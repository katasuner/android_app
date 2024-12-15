package com.example.mooddiary

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "emotions")
data class Emotion(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0, // Уникальный идентификатор эмоции
    val eventId: Int, // Связь с событием (ID события)
    val emotionText: String, // Текст эмоции
    val groupIndex: Int, // Индекс группы эмоций
    val isSelected: Boolean = false, // Состояние выделения эмоции
    val beforeValue: Int = 0, // Оценка "до"
    val afterValue: Int = 0 // Оценка "после"
)


