package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "equipment")
data class EquipmentEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val tag: String,                         // e.g. "BOM-01", "COMP-102"
    val name: String,                        // e.g. "Bomba Centrífuga de Alta Presión"
    val equipmentNumber: String = "1",       // e.g. "Unidad #1", "Equipo Nº 02", "Bomba 1" (para diferenciar varios del mismo modelo)
    val model: String = "",                  // e.g. "KSB Multitec 50/3", "Atlas Copco GA37"
    val category: String,                    // e.g. "Hidráulica", "Neumática", "Mecánica", "Eléctrica"
    val location: String,                    // e.g. "Planta 1 - Bahía A", "Cuarto de Máquinas"
    val status: String,                      // "OPERATIVO", "EN_MANTENIMIENTO", "FUERA_DE_SERVICIO", "EN_ALERTA"
    val criticality: String,                 // "ALTA", "MEDIA", "BAJA"
    val operatingHours: Int = 1200,
    val defaultToolsRequired: String = "Juego de llaves mixtas, Torquímetro, EPP completo",
    val notes: String = "",
    val photoUri: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "maintenance_schedules")
data class MaintenanceScheduleEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val equipmentId: Long,
    val equipmentTag: String,
    val equipmentName: String,
    val equipmentNumber: String = "1",       // Número de equipo para diferenciar unidades del mismo modelo
    val model: String = "",                  // Modelo del equipo
    val title: String,                       // e.g. "Cambio de rodamientos y lubricación ISO VG 68"
    val type: String,                        // "PREVENTIVO", "INSPECCION", "LUBRICACION", "CALIBRACION", "OVERHAUL"
    val scheduledEpochDay: Long,             // LocalDate.toEpochDay()
    val scheduledTime: String = "08:00",
    val frequencyDays: Int = 30,             // Cantidad de días en formato numérico (e.g. 7, 15, 30, 60, 90, 180, 365)
    val frequency: String = "Cada 30 días",  // Texto descriptivo de la frecuencia
    val requiredTools: String,               // Equipos y herramientas necesarios
    val assignedTechnician: String = "Ing. Carlos Mendoza",
    val estimatedDurationHours: Float = 3.5f,
    val notes: String = "",                  // Apartado para notas detalladas del mantenimiento
    val status: String = "PROGRAMADO",       // "PROGRAMADO", "EN_PROGRESO", "COMPLETADO", "VENCIDO"
    val completedAt: Long? = null,
    val completionNotes: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "corrective_maintenances")
data class CorrectiveMaintenanceEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val equipmentId: Long,
    val equipmentTag: String,
    val equipmentName: String,
    val equipmentNumber: String = "1",       // Número de equipo para diferenciar unidades del mismo modelo
    val model: String = "",                  // Modelo del equipo
    val failureDescription: String,          // Descripción detallada de la falla
    val requiredSpareParts: String,          // Repuestos necesarios para la reparación
    val severity: String = "ALTA",           // "CRITICA", "ALTA", "MEDIA", "BAJA"
    val status: String = "REPORTADO",        // "REPORTADO", "EN_DIAGNOSTICO", "ESPERANDO_REPUESTOS", "EN_REPARACION", "SOLUCIONADO"
    val reportedEpochDay: Long,
    val scheduledInterventionEpochDay: Long, // Programado en el cronograma
    val assignedTechnician: String = "Téc. Roberto Silva",
    val requiredTools: String = "Extractor de poleas, Multímetro Fluke, Llave dinamométrica",
    val downtimeHours: Float = 4.0f,
    val notes: String = "",
    val solutionSummary: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val resolvedAt: Long? = null
)

@Entity(tableName = "photo_reports")
data class PhotoReportEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val equipmentId: Long,
    val equipmentTag: String,
    val equipmentName: String,
    val equipmentNumber: String = "1",
    val maintenanceScheduleId: Long? = null,
    val correctiveId: Long? = null,
    val title: String,
    val stage: String,                       // "ANTES", "DURANTE", "DESPUES", "FALLA", "INSPECCION"
    val description: String,                 // Hallazgos y observaciones detalladas
    val imageUri: String,
    val technician: String = "Carlos Mendoza",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "attachments")
data class AttachmentEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val equipmentId: Long,
    val equipmentTag: String,
    val equipmentName: String,
    val equipmentNumber: String = "1",
    val maintenanceScheduleId: Long? = null,
    val title: String,
    val fileType: String,                    // "MANUAL_PDF", "FICHA_TECNICA", "CERTIFICADO_CALIBRACION", "CHECKLIST"
    val fileSizeDescription: String = "2.4 MB",
    val notes: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "app_notifications")
data class AppNotificationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val maintenanceScheduleId: Long? = null,
    val correctiveId: Long? = null,
    val equipmentName: String,
    val equipmentNumber: String = "1",
    val title: String,
    val message: String,
    val alertType: String,                   // "PREVIA_2_DIAS", "HOY", "VENCIDO", "CORRECTIVO_URGENTE"
    val scheduledEpochDay: Long,
    val isRead: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
