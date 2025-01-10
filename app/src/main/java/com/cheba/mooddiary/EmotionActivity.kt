package com.cheba.mooddiary

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch

class EmotionActivity : AppCompatActivity() {

    private lateinit var emotionDao: EmotionDao
    private var eventId: Int = -1
    private val emotions = mutableListOf<Emotion>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_emotion)

        // Получаем eventId из Intent
        eventId = intent.getIntExtra("eventId", -1)
        if (eventId == -1) {
            finish()
            return
        }

        // Инициализация базы данных
        val database = AppDatabase.getDatabase(this)
        emotionDao = database.emotionDao()

        // Загрузка эмоций
        lifecycleScope.launch {
            emotions.clear()
            emotions.addAll(getOrCreateEmotionsForEvent(eventId))
            setupEmotionBlocks(emotions)
        }

        // Кнопка перехода к негативным мыслям
        val negativeThoughtsButton = findViewById<Button>(R.id.negative_thoughts_button)
        negativeThoughtsButton.setOnClickListener {
            val intent = Intent(this, NegativeThoughtsActivity::class.java)
            intent.putExtra("eventId", eventId)
            startActivity(intent)
        }
    }

    // Создание или загрузка эмоций
    private suspend fun getOrCreateEmotionsForEvent(eventId: Int): List<Emotion> {
        val existingEmotions = emotionDao.getEmotionsForEvent(eventId)
        if (existingEmotions.isNotEmpty()) {
            return existingEmotions
        }

        val defaultEmotions = listOf(
            listOf("Грусть", "Подавленность", "Упадок настроения", "Печаль"),
            listOf("Тревога", "Беспокойство", "Паника", "Нервозность", "Страх"),
            listOf("Вина", "Угрызение совести", "Стыд", "Сожаление"),
            listOf("Чувство собственной неполноценности", "Никчемности", "Непригодности", "Ущербности", "Некомпетентности"),
            listOf("Одиночество", "Никому не нужен", "Чувство себя отвергнутым", "Брошенным", "Оставленным"),
            listOf("Смущение", "Чувство себя глупо", "Унизительно", "Зациклен на себе"),
            listOf("Беспомощность", "Уныние", "Пессимизм", "Отчаяние"),
            listOf("Досада", "Чувство тупиковости", "Поверженности", "Постоянных преград на пути"),
            listOf("Злость", "Гнев", "Обида", "Раздражение", "Рассержен", "Расстроен", "В бешенстве")
        )

        val newEmotions = defaultEmotions.flatMapIndexed { groupIndex, group ->
            group.map { emotionText ->
                Emotion(eventId = eventId, emotionText = emotionText, groupIndex = groupIndex)
            }
        }

        emotionDao.insertEmotions(newEmotions)
        return emotionDao.getEmotionsForEvent(eventId)
    }

    // Настройка блоков эмоций
    private fun setupEmotionBlocks(emotions: List<Emotion>) {
        val container = findViewById<LinearLayout>(R.id.emotion_blocks_container)
        container.removeAllViews()

        val groupedEmotions = emotions.groupBy { it.groupIndex }
        val inflater = layoutInflater

        groupedEmotions.forEach { (_, group) ->
            val blockView = inflater.inflate(R.layout.emotion_block, container, false)

            val recyclerView = blockView.findViewById<RecyclerView>(R.id.recycler_view_emotions)
            recyclerView.layoutManager = LinearLayoutManager(this)

            val adapter = EmotionAdapter(group.toMutableList(), onEmotionClick)
            recyclerView.adapter = adapter

            val beforeValue = blockView.findViewById<EditText>(R.id.emotion_before_value)
            val afterValue = blockView.findViewById<EditText>(R.id.emotion_after_value)

            beforeValue.setText(group.firstOrNull()?.beforeValue?.takeIf { it != 0 }?.toString() ?: "")
            afterValue.setText(group.firstOrNull()?.afterValue?.takeIf { it != 0 }?.toString() ?: "")

            beforeValue.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    val value = s?.toString()?.toIntOrNull() ?: 0
                    lifecycleScope.launch {
                        group.forEach { emotion ->
                            val updatedEmotion = emotion.copy(beforeValue = value)
                            emotionDao.updateEmotion(updatedEmotion)
                        }
                    }
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })

            afterValue.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    val value = s?.toString()?.toIntOrNull() ?: 0
                    lifecycleScope.launch {
                        group.forEach { emotion ->
                            val updatedEmotion = emotion.copy(afterValue = value)
                            emotionDao.updateEmotion(updatedEmotion)
                        }
                    }
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })

            container.addView(blockView)
        }
    }

    private val onEmotionClick: (Emotion, EmotionAdapter) -> Unit = { emotion, adapter ->
        lifecycleScope.launch {
            val updatedEmotion = emotion.copy(isSelected = !emotion.isSelected)
            emotionDao.updateEmotion(updatedEmotion)
            adapter.updateEmotion(updatedEmotion)
        }
    }
}
