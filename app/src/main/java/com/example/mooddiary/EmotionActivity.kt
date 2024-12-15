package com.example.mooddiary

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
            finish() // Завершаем, если eventId не передан
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
    }

    // Создание или загрузка эмоций
    private suspend fun getOrCreateEmotionsForEvent(eventId: Int): List<Emotion> {
        val existingEmotions = emotionDao.getEmotionsForEvent(eventId)
        if (existingEmotions.isNotEmpty()) {
            return existingEmotions
        }

        // Если эмоции ещё не созданы, создаём их
        val defaultEmotions = listOf(
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

        val newEmotions = defaultEmotions.flatMapIndexed { groupIndex, group ->
            group.map { emotionText ->
                Emotion(
                    eventId = eventId,
                    emotionText = emotionText,
                    groupIndex = groupIndex
                )
            }
        }

        emotionDao.insertEmotions(newEmotions)
        return emotionDao.getEmotionsForEvent(eventId)
    }

    // Настройка блоков эмоций
    private fun setupEmotionBlocks(emotions: List<Emotion>) {
        val container = findViewById<LinearLayout>(R.id.emotion_blocks_container)
        container.removeAllViews()

        // Группируем эмоции по индексам группы
        val groupedEmotions = emotions.groupBy { it.groupIndex }
        val inflater = layoutInflater

        groupedEmotions.forEach { (_, group) ->
            val blockView = inflater.inflate(R.layout.emotion_block, container, false)

            val recyclerView = blockView.findViewById<RecyclerView>(R.id.recycler_view_emotions)
            recyclerView.layoutManager = LinearLayoutManager(this)
            recyclerView.adapter = EmotionAdapter(group, onEmotionClick)

            val beforeValue = blockView.findViewById<EditText>(R.id.emotion_before_value)
            val afterValue = blockView.findViewById<EditText>(R.id.emotion_after_value)

            // Устанавливаем текущие значения "до" и "после" из первой эмоции группы
            beforeValue.setText(group.firstOrNull()?.beforeValue?.toString() ?: "0")
            afterValue.setText(group.firstOrNull()?.afterValue?.toString() ?: "0")

            // Сохранение значений "До" при каждом изменении
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

            // Сохранение значений "После" при каждом изменении
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

    // Обработка кликов на эмоции
    private val onEmotionClick: (Emotion) -> Unit = { emotion ->
        lifecycleScope.launch {
            // Переключаем состояние выделения эмоции
            val updatedEmotion = emotion.copy(isSelected = !emotion.isSelected)
            emotionDao.updateEmotion(updatedEmotion)

            // Обновляем локальный список
            val index = emotions.indexOfFirst { it.id == emotion.id }
            if (index != -1) {
                emotions[index] = updatedEmotion
                setupEmotionBlocks(emotions) // Перерисовываем блоки
            }
        }
    }
}
