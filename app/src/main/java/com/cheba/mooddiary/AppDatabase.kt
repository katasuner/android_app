package com.cheba.mooddiary

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [Event::class, Emotion::class, NegativeThought::class, PositiveThought::class, Distortion::class],
    version = 7,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun eventDao(): EventDao
    abstract fun emotionDao(): EmotionDao
    abstract fun negativeThoughtDao(): NegativeThoughtDao
    abstract fun positiveThoughtDao(): PositiveThoughtDao
    abstract fun distortionDao(): DistortionDao


    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "event_database"
                )
                    .fallbackToDestructiveMigration() // Пересоздаёт базу при изменении схемы
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}



