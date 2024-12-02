package com.example.mooddiary

import android.graphics.Paint
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class EmotionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_emotion)

        // Получить данные из Intent
        val event = intent.getStringExtra("event")

        // Установить заголовок активности
        title = event ?: "Эмоции"

        // Настройка RecyclerView для эмоций
        val recyclerViewEmotions = findViewById<RecyclerView>(R.id.recycler_view_emotions)
        recyclerViewEmotions.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        val emotions = listOf("Грусть", "Подавленность", "Упадок настроения", "Печаль")
        val adapter = EmotionAdapter(emotions, this::onEmotionClick)
        recyclerViewEmotions.adapter = adapter
    }

    // Метод, который вызывается при нажатии на эмоцию
    fun onEmotionClick(view: View) {
        if (view is TextView) {
            // Переключение выделения
            if (view.paintFlags and Paint.UNDERLINE_TEXT_FLAG == Paint.UNDERLINE_TEXT_FLAG) {
                // Если текст уже подчеркнут, то снимаем выделение
                view.paintFlags = view.paintFlags and Paint.UNDERLINE_TEXT_FLAG.inv()
                view.setTypeface(view.typeface, Typeface.NORMAL)
            } else {
                // Если текст не подчеркнут, то выделяем его
                view.paintFlags = view.paintFlags or Paint.UNDERLINE_TEXT_FLAG
                view.setTypeface(view.typeface, Typeface.BOLD)
            }
        }
    }
}
