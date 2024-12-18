package com.example.mooddiary

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "negative_thoughts")
data class NegativeThought(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val eventId: Int,         // ID связанного события
    val thoughtText: String,  // Текст мысли
    val beforeValue: Int = 0, // Значение "До"
    val afterValue: Int = 0   // Значение "После"
)

