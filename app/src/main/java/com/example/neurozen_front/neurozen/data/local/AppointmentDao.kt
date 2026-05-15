package com.example.neurozen_front.neurozen.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AppointmentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAppointment(appointment: AppointmentEntity)

    @Query("SELECT * FROM appointments ORDER BY dateMillis ASC")
    fun getAllAppointments(): Flow<List<AppointmentEntity>>

    @Query("DELETE FROM appointments WHERE id = :appointmentId")
    suspend fun deleteAppointment(appointmentId: Long)
}
