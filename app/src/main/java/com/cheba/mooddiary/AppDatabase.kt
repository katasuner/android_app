package com.cheba.mooddiary

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [Event::class, Emotion::class, NegativeThought::class, PositiveThought::class, Distortion::class],
    version = 8,
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

        // Миграция с версии 7 на 8
        private val MIGRATION_7_8 = object : Migration(7, 8) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Добавляем новое поле в таблицу "events"
                database.execSQL("ALTER TABLE events ADD COLUMN isProcessed INTEGER NOT NULL DEFAULT 0")
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "event_database" // Имя базы данных
                )
                    .addMigrations(MIGRATION_7_8) // Подключаем миграцию
                    .fallbackToDestructiveMigration() // Удалить после отладки
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

}



