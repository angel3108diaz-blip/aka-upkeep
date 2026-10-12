package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.AppNotificationEntity
import com.example.data.local.AttachmentEntity
import com.example.data.local.CorrectiveMaintenanceEntity
import com.example.data.local.EquipmentEntity
import com.example.data.local.InitialDataSeeder
import com.example.data.local.MaintenanceScheduleEntity
import com.example.data.local.PhotoReportEntity
import com.example.data.repository.MaintenanceRepository
import com.example.notifications.NotificationHelper
import com.example.ui.components.DateUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class OperationalStats(
    val totalEquipment: Int = 0,
    val operativosCount: Int = 0,
    val enMantenimientoCount: Int = 0,
    val fueraDeServicioCount: Int = 0,
    val enAlertaCount: Int = 0,
    val availabilityPercentage: Int = 100,
    val upcoming48HoursCount: Int = 0,
    val overdueCount: Int = 0,
    val activeCorrectivesCount: Int = 0,
    val criticalCorrectivesCount: Int = 0
)

class MaintenanceViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: MaintenanceRepository
    val selectedCalendarEpochDay = MutableStateFlow(DateUtils.todayEpochDay())
    val searchQuery = MutableStateFlow("")

    val equipmentList: StateFlow<List<EquipmentEntity>>
    val scheduleList: StateFlow<List<MaintenanceScheduleEntity>>
    val correctiveList: StateFlow<List<CorrectiveMaintenanceEntity>>
    val photoReports: StateFlow<List<PhotoReportEntity>>
    val attachments: StateFlow<List<AttachmentEntity>>
    val notifications: StateFlow<List<AppNotificationEntity>>
    val unreadNotificationsCount: StateFlow<Int>
    val operationalStats: StateFlow<OperationalStats>

    init {
        val database = AppDatabase.getInstance(application)
        val dao = database.maintenanceDao()
        repository = MaintenanceRepository(dao)

        NotificationHelper.createNotificationChannel(application)

        // Seed initial data if empty
        viewModelScope.launch {
            InitialDataSeeder.seedIfNeeded(dao)
            // Trigger 2-day advance alerts check
            val newAlerts = repository.evaluateAdvanceAlerts()
            for (alert in newAlerts) {
                NotificationHelper.sendMaintenanceAlert(
                    application,
                    alert.id.toInt(),
                    alert.title,
                    alert.message,
                    isUrgent = alert.alertType == "PREVIA_2_DIAS" || alert.alertType == "CORRECTIVO_URGENTE"
                )
            }
        }

        equipmentList = repository.allEquipment
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        scheduleList = repository.allSchedules
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        correctiveList = repository.allCorrectives
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        photoReports = repository.allPhotoReports
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        attachments = repository.allAttachments
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        notifications = repository.allNotifications
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        unreadNotificationsCount = repository.unreadNotificationsCount
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

        operationalStats = combine(equipmentList, scheduleList, correctiveList) { eqList, schList, corList ->
            val total = eqList.size
            val op = eqList.count { it.status == "OPERATIVO" }
            val mant = eqList.count { it.status == "EN_MANTENIMIENTO" }
            val fuera = eqList.count { it.status == "FUERA_DE_SERVICIO" }
            val alerta = eqList.count { it.status == "EN_ALERTA" }
            val avail = if (total > 0) ((op.toDouble() / total.toDouble()) * 100).toInt() else 100

            val upcoming = schList.count { it.status != "COMPLETADO" && DateUtils.isWithinNext48Hours(it.scheduledEpochDay) }
            val overdue = schList.count { it.status != "COMPLETADO" && DateUtils.isOverdue(it.scheduledEpochDay) }

            val activeCor = corList.count { it.status != "SOLUCIONADO" }
            val critCor = corList.count { it.status != "SOLUCIONADO" && it.severity == "CRITICA" }

            OperationalStats(
                totalEquipment = total,
                operativosCount = op,
                enMantenimientoCount = mant,
                fueraDeServicioCount = fuera,
                enAlertaCount = alerta,
                availabilityPercentage = avail,
                upcoming48HoursCount = upcoming,
                overdueCount = overdue,
                activeCorrectivesCount = activeCor,
                criticalCorrectivesCount = critCor
            )
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), OperationalStats())
    }

    // Equipment operations
    fun addEquipment(equipment: EquipmentEntity) {
        viewModelScope.launch {
            repository.insertEquipment(equipment)
        }
    }

    fun updateEquipment(equipment: EquipmentEntity) {
        viewModelScope.launch {
            repository.updateEquipment(equipment)
        }
    }

    fun updateEquipmentStatus(id: Long, newStatus: String) {
        viewModelScope.launch {
            repository.updateEquipmentStatus(id, newStatus)
        }
    }

    fun deleteEquipment(equipment: EquipmentEntity) {
        viewModelScope.launch {
            repository.deleteEquipment(equipment)
        }
    }

    // Schedule operations
    fun addSchedule(schedule: MaintenanceScheduleEntity) {
        viewModelScope.launch {
            val id = repository.insertSchedule(schedule)
            // If in next 48h, notify directly
            if (DateUtils.isWithinNext48Hours(schedule.scheduledEpochDay)) {
                NotificationHelper.sendMaintenanceAlert(
                    getApplication(),
                    id.toInt(),
                    "⚠️ Mantenimiento programado: ${schedule.equipmentTag}",
                    "Programado para ${DateUtils.formatEpochDay(schedule.scheduledEpochDay)} a las ${schedule.scheduledTime}. Herramientas: ${schedule.requiredTools}",
                    isUrgent = true
                )
            }
        }
    }

    fun updateSchedule(schedule: MaintenanceScheduleEntity) {
        viewModelScope.launch {
            repository.updateSchedule(schedule)
        }
    }

    fun markScheduleCompleted(id: Long, notes: String?) {
        viewModelScope.launch {
            repository.markScheduleCompleted(id, notes)
        }
    }

    fun deleteSchedule(schedule: MaintenanceScheduleEntity) {
        viewModelScope.launch {
            repository.deleteSchedule(schedule)
        }
    }

    // Corrective operations
    fun addCorrective(corrective: CorrectiveMaintenanceEntity) {
        viewModelScope.launch {
            val id = repository.insertCorrective(corrective)
            NotificationHelper.sendMaintenanceAlert(
                getApplication(),
                id.toInt() + 5000,
                "🚨 Falla Correctiva: ${corrective.equipmentTag} (${corrective.severity})",
                "Falla: ${corrective.failureDescription}. Repuestos: ${corrective.requiredSpareParts}",
                isUrgent = true
            )
        }
    }

    fun updateCorrective(corrective: CorrectiveMaintenanceEntity) {
        viewModelScope.launch {
            repository.updateCorrective(corrective)
        }
    }

    fun resolveCorrective(id: Long, solution: String) {
        viewModelScope.launch {
            repository.resolveCorrective(id, solution)
        }
    }

    fun deleteCorrective(corrective: CorrectiveMaintenanceEntity) {
        viewModelScope.launch {
            repository.deleteCorrective(corrective)
        }
    }

    // Photo Reports
    fun addPhotoReport(report: PhotoReportEntity) {
        viewModelScope.launch {
            repository.insertPhotoReport(report)
        }
    }

    fun deletePhotoReport(report: PhotoReportEntity) {
        viewModelScope.launch {
            repository.deletePhotoReport(report)
        }
    }

    // Attachments
    fun addAttachment(attachment: AttachmentEntity) {
        viewModelScope.launch {
            repository.insertAttachment(attachment)
        }
    }

    fun deleteAttachment(attachment: AttachmentEntity) {
        viewModelScope.launch {
            repository.deleteAttachment(attachment)
        }
    }

    // Notifications
    fun markNotificationAsRead(id: Long) {
        viewModelScope.launch {
            repository.markNotificationAsRead(id)
        }
    }

    fun markAllNotificationsAsRead() {
        viewModelScope.launch {
            repository.markAllNotificationsAsRead()
        }
    }

    fun deleteNotification(id: Long) {
        viewModelScope.launch {
            repository.deleteNotification(id)
        }
    }

    fun testSimulateAdvanceNotification() {
        viewModelScope.launch {
            val today = DateUtils.todayEpochDay()
            val notif = AppNotificationEntity(
                equipmentName = "BOM-01 (Bomba Centrífuga Multietapa KSB)",
                title = "⚠️ Aviso con 2 días de antelación (Prueba)",
                message = "Alerta automática: Mantenimiento programado para el ${DateUtils.formatEpochDay(today + 2)}. Requiere: Extractor de rodamientos y alineador láser.",
                alertType = "PREVIA_2_DIAS",
                scheduledEpochDay = today + 2,
                isRead = false
            )
            val id = repository.insertNotification(notif)
            NotificationHelper.sendMaintenanceAlert(
                getApplication(),
                id.toInt() + 1000,
                notif.title,
                notif.message,
                isUrgent = true
            )
        }
    }
}
