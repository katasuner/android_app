package com.example.mooddiary

import com.example.mooddiary.Emotion
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Event::class, Emotion::class], version = 4, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun eventDao(): EventDao
    abstract fun emotionDao(): EmotionDao

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



