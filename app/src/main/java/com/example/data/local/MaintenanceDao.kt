package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface MaintenanceDao {

    // === EQUIPOS ===
    @Query("SELECT * FROM equipment ORDER BY tag ASC")
    fun getAllEquipment(): Flow<List<EquipmentEntity>>

    @Query("SELECT * FROM equipment WHERE id = :id LIMIT 1")
    suspend fun getEquipmentById(id: Long): EquipmentEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEquipment(equipment: EquipmentEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEquipmentList(list: List<EquipmentEntity>)

    @Update
    suspend fun updateEquipment(equipment: EquipmentEntity)

    @Query("UPDATE equipment SET status = :newStatus WHERE id = :id")
    suspend fun updateEquipmentStatus(id: Long, newStatus: String)

    @Delete
    suspend fun deleteEquipment(equipment: EquipmentEntity)

    // === CRONOGRAMA DE MANTENIMIENTOS ===
    @Query("SELECT * FROM maintenance_schedules ORDER BY scheduledEpochDay ASC, scheduledTime ASC")
    fun getAllSchedules(): Flow<List<MaintenanceScheduleEntity>>

    @Query("SELECT * FROM maintenance_schedules WHERE equipmentId = :equipmentId ORDER BY scheduledEpochDay ASC")
    fun getSchedulesByEquipment(equipmentId: Long): Flow<List<MaintenanceScheduleEntity>>

    @Query("SELECT * FROM maintenance_schedules WHERE scheduledEpochDay = :epochDay ORDER BY scheduledTime ASC")
    fun getSchedulesForDay(epochDay: Long): Flow<List<MaintenanceScheduleEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSchedule(schedule: MaintenanceScheduleEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScheduleList(list: List<MaintenanceScheduleEntity>)

    @Update
    suspend fun updateSchedule(schedule: MaintenanceScheduleEntity)

    @Query("UPDATE maintenance_schedules SET status = :status, completedAt = :completedAt, completionNotes = :notes WHERE id = :id")
    suspend fun markScheduleCompleted(id: Long, status: String, completedAt: Long, notes: String?)

    @Delete
    suspend fun deleteSchedule(schedule: MaintenanceScheduleEntity)

    // === MANTENIMIENTOS CORRECTIVOS ===
    @Query("SELECT * FROM corrective_maintenances ORDER BY scheduledInterventionEpochDay ASC")
    fun getAllCorrectives(): Flow<List<CorrectiveMaintenanceEntity>>

    @Query("SELECT * FROM corrective_maintenances WHERE scheduledInterventionEpochDay = :epochDay")
    fun getCorrectivesForDay(epochDay: Long): Flow<List<CorrectiveMaintenanceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCorrective(corrective: CorrectiveMaintenanceEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCorrectiveList(list: List<CorrectiveMaintenanceEntity>)

    @Update
    suspend fun updateCorrective(corrective: CorrectiveMaintenanceEntity)

    @Query("UPDATE corrective_maintenances SET status = :status, solutionSummary = :solution, resolvedAt = :resolvedAt WHERE id = :id")
    suspend fun resolveCorrective(id: Long, status: String, solution: String, resolvedAt: Long)

    @Delete
    suspend fun deleteCorrective(corrective: CorrectiveMaintenanceEntity)

    // === REPORTES FOTOGRÁFICOS ===
    @Query("SELECT * FROM photo_reports ORDER BY timestamp DESC")
    fun getAllPhotoReports(): Flow<List<PhotoReportEntity>>

    @Query("SELECT * FROM photo_reports WHERE equipmentId = :equipmentId ORDER BY timestamp DESC")
    fun getPhotoReportsForEquipment(equipmentId: Long): Flow<List<PhotoReportEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhotoReport(report: PhotoReportEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhotoReportList(list: List<PhotoReportEntity>)

    @Delete
    suspend fun deletePhotoReport(report: PhotoReportEntity)

    // === ARCHIVOS ADJUNTOS ===
    @Query("SELECT * FROM attachments ORDER BY timestamp DESC")
    fun getAllAttachments(): Flow<List<AttachmentEntity>>

    @Query("SELECT * FROM attachments WHERE equipmentId = :equipmentId ORDER BY timestamp DESC")
    fun getAttachmentsForEquipment(equipmentId: Long): Flow<List<AttachmentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttachment(attachment: AttachmentEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttachmentList(list: List<AttachmentEntity>)

    @Delete
    suspend fun deleteAttachment(attachment: AttachmentEntity)

    // === NOTIFICACIONES ===
    @Query("SELECT * FROM app_notifications ORDER BY createdAt DESC")
    fun getAllNotifications(): Flow<List<AppNotificationEntity>>

    @Query("SELECT COUNT(*) FROM app_notifications WHERE isRead = 0")
    fun getUnreadNotificationsCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: AppNotificationEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotificationList(list: List<AppNotificationEntity>)

    @Query("UPDATE app_notifications SET isRead = 1 WHERE id = :id")
    suspend fun markNotificationAsRead(id: Long)

    @Query("UPDATE app_notifications SET isRead = 1")
    suspend fun markAllNotificationsAsRead()

    @Query("DELETE FROM app_notifications WHERE id = :id")
    suspend fun deleteNotification(id: Long)

    @Query("DELETE FROM app_notifications")
    suspend fun clearAllNotifications()
}
