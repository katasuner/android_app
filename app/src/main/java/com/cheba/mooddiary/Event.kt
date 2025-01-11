package com.cheba.mooddiary

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "events")
data class Event(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val description: String,
    val isProcessed: Boolean = false // Поле для статуса события
)
