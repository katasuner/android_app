package com.example.mooddiary

import com.example.mooddiary.Emotion
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface EmotionDao {

    @Query("SELECT * FROM emotions WHERE eventId = :eventId")
    suspend fun getEmotionsForEvent(eventId: Int): List<Emotion>

    @Insert
    suspend fun insertEmotions(emotions: List<Emotion>)

    @Update
    suspend fun updateEmotion(emotion: Emotion)

    // Метод для обновления только полей beforeValue и afterValue
    @Query("UPDATE emotions SET beforeValue = :beforeValue, afterValue = :afterValue WHERE id = :emotionId")
    suspend fun updateEmotionValues(emotionId: Int, beforeValue: Int, afterValue: Int)
}


