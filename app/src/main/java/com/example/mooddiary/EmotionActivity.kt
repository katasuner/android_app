package com.example.mooddiary

import android.graphics.Paint
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
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

        // Список списков эмоций
        val emotionsList = listOf(
            listOf("Грусть", "Подавленность", "Упадок настроения", "Печаль"),
            listOf("Тревога", "Беспокойство", "Паника", "Нервозность", "Страх"),
            listOf("Вина", "Угрызение совести", "Стыд", "Сожаление"),
            listOf("Чувство собественной неполноценности", "Никчемности", "Непригодности", "Ущербности","Некомпетентности"),
            listOf("Одиночество", "Никому не нужен", "Чувство себя отвергнутым", "Брошенным", "Оставленным"),
            listOf("Смущение", "Чувство себя глупо", "Унизительно", "Зациклен на себе"),
            listOf("Беспомощность", "Уныние", "Пессимизм", "Отчаяние"),
            listOf("Досада", "Чувство тупиковости", "Повержености", "Постоянных преград на пути"),
            listOf("Злость", "Гнев", "Обида", "Раздражение", "Рассержен", "Расстроен", "В бешенстве")

        )

        val container = findViewById<LinearLayout>(R.id.emotion_blocks_container)
        val inflater = LayoutInflater.from(this)

        // Проход по каждому списку эмоций и добавление блока
        emotionsList.forEach { emotions ->
            val blockView = inflater.inflate(R.layout.emotion_block, container, false)

            // Настройка RecyclerView внутри блока
            val recyclerViewEmotions = blockView.findViewById<RecyclerView>(R.id.recycler_view_emotions)
            recyclerViewEmotions.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            val adapter = EmotionAdapter(emotions, this::onEmotionClick)
            recyclerViewEmotions.adapter = adapter

            container.addView(blockView)
        }

        // Настройка кнопки
        val negativeThoughtsButton = findViewById<Button>(R.id.negative_thoughts_button)
        negativeThoughtsButton.setOnClickListener {
            // Обработка нажатия кнопки
        }
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
