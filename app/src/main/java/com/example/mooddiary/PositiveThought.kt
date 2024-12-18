package com.example.mooddiary

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "positive_thoughts")
data class PositiveThought(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val negativeThoughtId: Int,  // ID связанной негативной мысли
    val thoughtText: String,     // Текст позитивной мысли
    val confidenceScale: Int = 0 // Шкала уверенности (0-100)
)
