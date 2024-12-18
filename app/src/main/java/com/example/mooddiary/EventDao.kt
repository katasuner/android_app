package com.example.mooddiary

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Delete
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface EventDao {
    // Получение всех событий
    @Query("SELECT * FROM events ORDER BY id DESC")
    fun getAllEvents(): Flow<List<Event>> // Flow для реактивного получения данных

    // Вставка события
    @Insert
    suspend fun insertEvent(event: Event): Long

    // Удаление события
    @Delete
    suspend fun deleteEvent(event: Event): Int

    // Получение события по ID
    @Query("SELECT * FROM events WHERE id = :eventId LIMIT 1")
    suspend fun getEventById(eventId: Int): Event

    @Update
    suspend fun updateEvent(event: Event)
}
