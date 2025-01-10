package com.cheba.mooddiary

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "distortions")
data class Distortion(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val negativeThoughtId: Int,   // ID связанной негативной мысли
    val distortionText: String,   // Название искажения
    val isSelected: Boolean = false // Состояние выделения
)
