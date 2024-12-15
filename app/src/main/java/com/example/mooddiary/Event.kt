package com.example.mooddiary

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "events")
data class Event(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0, // Автоматически генерируемый ID
    val description: String // Описание события
)
