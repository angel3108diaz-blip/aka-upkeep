package com.example.data.repository

import com.example.data.local.AppNotificationEntity
import com.example.data.local.AttachmentEntity
import com.example.data.local.CorrectiveMaintenanceEntity
import com.example.data.local.EquipmentEntity
import com.example.data.local.MaintenanceDao
import com.example.data.local.MaintenanceScheduleEntity
import com.example.data.local.PhotoReportEntity
import com.example.ui.components.DateUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class MaintenanceRepository(private val dao: MaintenanceDao) {

    // Equipment
    val allEquipment: Flow<List<EquipmentEntity>> = dao.getAllEquipment()
    suspend fun getEquipmentById(id: Long) = dao.getEquipmentById(id)
    suspend fun insertEquipment(equipment: EquipmentEntity) = dao.insertEquipment(equipment)
    suspend fun updateEquipment(equipment: EquipmentEntity) = dao.updateEquipment(equipment)
    suspend fun updateEquipmentStatus(id: Long, newStatus: String) = dao.updateEquipmentStatus(id, newStatus)
    suspend fun deleteEquipment(equipment: EquipmentEntity) = dao.deleteEquipment(equipment)

    // Schedules
    val allSchedules: Flow<List<MaintenanceScheduleEntity>> = dao.getAllSchedules()
    fun getSchedulesForDay(epochDay: Long): Flow<List<MaintenanceScheduleEntity>> = dao.getSchedulesForDay(epochDay)
    suspend fun insertSchedule(schedule: MaintenanceScheduleEntity): Long {
        val id = dao.insertSchedule(schedule)
        // Check if new schedule warrants immediate 2-day advance or today notification
        checkAndCreateNotificationForSchedule(schedule.copy(id = id))
        return id
    }
    suspend fun updateSchedule(schedule: MaintenanceScheduleEntity) = dao.updateSchedule(schedule)
    suspend fun markScheduleCompleted(id: Long, notes: String?) {
        dao.markScheduleCompleted(id, "COMPLETADO", System.currentTimeMillis(), notes)
    }
    suspend fun deleteSchedule(schedule: MaintenanceScheduleEntity) = dao.deleteSchedule(schedule)

    // Correctives
    val allCorrectives: Flow<List<CorrectiveMaintenanceEntity>> = dao.getAllCorrectives()
    fun getCorrectivesForDay(epochDay: Long): Flow<List<CorrectiveMaintenanceEntity>> = dao.getCorrectivesForDay(epochDay)
    suspend fun insertCorrective(corrective: CorrectiveMaintenanceEntity): Long {
        val id = dao.insertCorrective(corrective)
        // Also update equipment status to FUERA_DE_SERVICIO or EN_MANTENIMIENTO if severe
        if (corrective.severity == "CRITICA") {
            dao.updateEquipmentStatus(corrective.equipmentId, "FUERA_DE_SERVICIO")
        } else {
            dao.updateEquipmentStatus(corrective.equipmentId, "EN_ALERTA")
        }
        // Insert alert notification
        dao.insertNotification(
            AppNotificationEntity(
                correctiveId = id,
                equipmentName = "${corrective.equipmentTag} - ${corrective.equipmentName}",
                equipmentNumber = corrective.equipmentNumber,
                title = "🚨 Falla: ${corrective.equipmentTag} [${corrective.equipmentNumber}]",
                message = "Severidad ${corrective.severity}: ${corrective.failureDescription.take(80)}... Repuestos: ${corrective.requiredSpareParts.take(60)}",
                alertType = "CORRECTIVO_URGENTE",
                scheduledEpochDay = corrective.scheduledInterventionEpochDay,
                isRead = false
            )
        )
        return id
    }
    suspend fun updateCorrective(corrective: CorrectiveMaintenanceEntity) = dao.updateCorrective(corrective)
    suspend fun resolveCorrective(id: Long, solution: String) {
        val now = System.currentTimeMillis()
        dao.resolveCorrective(id, "SOLUCIONADO", solution, now)
    }
    suspend fun deleteCorrective(corrective: CorrectiveMaintenanceEntity) = dao.deleteCorrective(corrective)

    // Photo Reports
    val allPhotoReports: Flow<List<PhotoReportEntity>> = dao.getAllPhotoReports()
    fun getPhotoReportsForEquipment(equipmentId: Long) = dao.getPhotoReportsForEquipment(equipmentId)
    suspend fun insertPhotoReport(report: PhotoReportEntity) = dao.insertPhotoReport(report)
    suspend fun deletePhotoReport(report: PhotoReportEntity) = dao.deletePhotoReport(report)

    // Attachments
    val allAttachments: Flow<List<AttachmentEntity>> = dao.getAllAttachments()
    fun getAttachmentsForEquipment(equipmentId: Long) = dao.getAttachmentsForEquipment(equipmentId)
    suspend fun insertAttachment(attachment: AttachmentEntity) = dao.insertAttachment(attachment)
    suspend fun deleteAttachment(attachment: AttachmentEntity) = dao.deleteAttachment(attachment)

    // Notifications
    val allNotifications: Flow<List<AppNotificationEntity>> = dao.getAllNotifications()
    val unreadNotificationsCount: Flow<Int> = dao.getUnreadNotificationsCount()
    suspend fun insertNotification(notification: AppNotificationEntity) = dao.insertNotification(notification)
    suspend fun markNotificationAsRead(id: Long) = dao.markNotificationAsRead(id)
    suspend fun markAllNotificationsAsRead() = dao.markAllNotificationsAsRead()
    suspend fun deleteNotification(id: Long) = dao.deleteNotification(id)
    suspend fun clearAllNotifications() = dao.clearAllNotifications()

    // Notification automation check: evaluates all active schedules and generates advance 2-day alerts
    suspend fun evaluateAdvanceAlerts(): List<AppNotificationEntity> {
        val today = DateUtils.todayEpochDay()
        val schedules = dao.getAllSchedules().first()
        val existingNotifications = dao.getAllNotifications().first()
        val created = mutableListOf<AppNotificationEntity>()

        for (item in schedules) {
            if (item.status == "COMPLETADO") continue
            val diff = item.scheduledEpochDay - today

            // Advance alert with 2 days notice:
            if (diff in 1..2) {
                val alreadyNotified = existingNotifications.any {
                    it.maintenanceScheduleId == item.id && it.alertType == "PREVIA_2_DIAS"
                }
                if (!alreadyNotified) {
                    val notif = AppNotificationEntity(
                        maintenanceScheduleId = item.id,
                        equipmentName = "${item.equipmentTag} (${item.equipmentName})",
                        equipmentNumber = item.equipmentNumber,
                        title = "⚠️ Aviso con ${diff} días de antelación: ${item.equipmentTag} [${item.equipmentNumber}]",
                        message = "El mantenimiento '${item.title}' está programado para el ${DateUtils.formatEpochDay(item.scheduledEpochDay)} a las ${item.scheduledTime}. Equipos/herramientas requeridos: ${item.requiredTools}.",
                        alertType = "PREVIA_2_DIAS",
                        scheduledEpochDay = item.scheduledEpochDay,
                        isRead = false
                    )
                    dao.insertNotification(notif)
                    created.add(notif)
                }
            } else if (diff == 0L) {
                // Today notification
                val alreadyNotified = existingNotifications.any {
                    it.maintenanceScheduleId == item.id && it.alertType == "HOY"
                }
                if (!alreadyNotified) {
                    val notif = AppNotificationEntity(
                        maintenanceScheduleId = item.id,
                        equipmentName = "${item.equipmentTag} (${item.equipmentName})",
                        equipmentNumber = item.equipmentNumber,
                        title = "🔔 Mantenimiento programado para HOY: ${item.equipmentTag} [${item.equipmentNumber}]",
                        message = "Hoy corresponde '${item.title}' asignado a ${item.assignedTechnician}. Herramientas: ${item.requiredTools}.",
                        alertType = "HOY",
                        scheduledEpochDay = item.scheduledEpochDay,
                        isRead = false
                    )
                    dao.insertNotification(notif)
                    created.add(notif)
                }
            }
        }
        return created
    }

    private suspend fun checkAndCreateNotificationForSchedule(item: MaintenanceScheduleEntity) {
        val today = DateUtils.todayEpochDay()
        val diff = item.scheduledEpochDay - today
        if (diff in 0..2) {
            val alertType = if (diff == 0L) "HOY" else "PREVIA_2_DIAS"
            val title = if (diff == 0L) "🔔 Mantenimiento HOY: ${item.equipmentTag} [${item.equipmentNumber}]" else "⚠️ Aviso previo (2 días): ${item.equipmentTag} [${item.equipmentNumber}]"
            dao.insertNotification(
                AppNotificationEntity(
                    maintenanceScheduleId = item.id,
                    equipmentName = "${item.equipmentTag} (${item.equipmentName})",
                    equipmentNumber = item.equipmentNumber,
                    title = title,
                    message = "Programado para el ${DateUtils.formatEpochDay(item.scheduledEpochDay)}. Requiere: ${item.requiredTools}",
                    alertType = alertType,
                    scheduledEpochDay = item.scheduledEpochDay,
                    isRead = false
                )
            )
        }
    }
}
