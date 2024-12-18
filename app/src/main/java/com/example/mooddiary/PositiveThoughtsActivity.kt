package com.example.mooddiary

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class PositiveThoughtsActivity : AppCompatActivity() {

    private lateinit var positiveThoughtDao: PositiveThoughtDao
    private lateinit var distortionDao: DistortionDao
    private var negativeThoughtId: Int = -1
    private var afterValueFromNegative: Int = 0

    private val positiveThoughts = mutableListOf<PositiveThought>()
    private val distortions = mutableListOf<Distortion>()

    private lateinit var positiveThoughtsContainer: LinearLayout
    private lateinit var recyclerViewDistortions: RecyclerView
    private lateinit var sharedAfterValue: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_positive_thoughts)

        // Получаем ID и данные негативной мысли из Intent
        negativeThoughtId = intent.getIntExtra("negative_thought_id", -1)
        val negativeThoughtText = intent.getStringExtra("negative_thought") ?: "Негативная мысль"
        afterValueFromNegative = intent.getIntExtra("after_value", 0)

        if (negativeThoughtId == -1) {
            Toast.makeText(this, "Ошибка: некорректный ID негативной мысли", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Инициализация базы данных и DAO
        val database = AppDatabase.getDatabase(this)
        positiveThoughtDao = database.positiveThoughtDao()
        distortionDao = database.distortionDao()

        // Инициализация UI компонентов
        positiveThoughtsContainer = findViewById(R.id.positive_thoughts_container)
        recyclerViewDistortions = findViewById(R.id.recycler_view_distortions)
        sharedAfterValue = findViewById(R.id.shared_value_scale)
        val negativeThoughtTextView = findViewById<TextView>(R.id.negative_thought_text)
        val addPositiveThoughtButton = findViewById<Button>(R.id.add_positive_thought_button)

        // Устанавливаем текст негативной мысли и значение "После"
        negativeThoughtTextView.text = negativeThoughtText
        sharedAfterValue.setText(afterValueFromNegative.toString())

        // Обновляем значение "После" в базе данных при изменении
        sharedAfterValue.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val newValue = s?.toString()?.toIntOrNull() ?: 0
                updateAfterValue(newValue)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // Загружаем искажения и позитивные мысли
        setupDistortions()
        loadPositiveThoughts()

        // Добавление новой позитивной мысли
        addPositiveThoughtButton.setOnClickListener {
            showAddPositiveThoughtDialog()
        }
    }

    // Обновляем значение "После" в негативной мысли
    private fun updateAfterValue(newValue: Int) {
        afterValueFromNegative = newValue
    }

    // Загрузка искажений
    private fun setupDistortions() {
        lifecycleScope.launch {
            distortionDao.getDistortionsForNegativeThought(negativeThoughtId).collect { loadedDistortions ->
                distortions.clear()
                if (loadedDistortions.isEmpty()) {
                    val defaultDistortions = listOf(
                        "Все или ничего", "Сверхобобщение", "Негативный фильтр", "Чтение мыслей",
                        "Ошибка предсказаний", "Эмоциональное обоснование", "Преувеличение и преуменьшение",
                        "Обесценивание положительного", "Императивы", "Ярлыки", "Самообвинение", "Обвинение других"
                    ).map { text -> Distortion(negativeThoughtId = negativeThoughtId, distortionText = text) }
                    distortionDao.insertDistortions(defaultDistortions)
                    distortions.addAll(defaultDistortions)
                } else {
                    distortions.addAll(loadedDistortions)
                }

                val adapter = DistortionAdapter(distortions, onDistortionClick)
                recyclerViewDistortions.layoutManager = GridLayoutManager(this@PositiveThoughtsActivity, 2)
                recyclerViewDistortions.adapter = adapter
            }
        }
    }

    // Загрузка позитивных мыслей
    private fun loadPositiveThoughts() {
        lifecycleScope.launch {
            positiveThoughtDao.getThoughtsForNegativeThought(negativeThoughtId).collect { loadedThoughts ->
                positiveThoughts.clear()
                positiveThoughts.addAll(loadedThoughts)
                setupPositiveThoughtViews()
            }
        }
    }

    // Отображение позитивных мыслей
    private fun setupPositiveThoughtViews() {
        positiveThoughtsContainer.removeAllViews()
        positiveThoughts.forEach { thought ->
            val thoughtView = layoutInflater.inflate(R.layout.positive_thought_item, positiveThoughtsContainer, false)

            val thoughtTextView = thoughtView.findViewById<TextView>(R.id.thought_text)
            val confidenceScaleEditText = thoughtView.findViewById<EditText>(R.id.confidence_scale)

            thoughtTextView.text = thought.thoughtText
            confidenceScaleEditText.setText(thought.confidenceScale.toString())

            confidenceScaleEditText.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    val newValue = s?.toString()?.toIntOrNull() ?: 0
                    updatePositiveThought(thought.copy(confidenceScale = newValue))
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })

            thoughtView.setOnLongClickListener {
                showDeleteConfirmationDialog(thought)
                true
            }

            positiveThoughtsContainer.addView(thoughtView)
        }
    }

    private fun updatePositiveThought(updatedThought: PositiveThought) {
        lifecycleScope.launch { positiveThoughtDao.updateThought(updatedThought) }
    }

    private fun showDeleteConfirmationDialog(thought: PositiveThought) {
        AlertDialog.Builder(this)
            .setTitle("Удалить мысль")
            .setMessage("Вы уверены, что хотите удалить эту позитивную мысль?")
            .setPositiveButton("Удалить") { _, _ -> deletePositiveThought(thought) }
            .setNegativeButton("Отмена", null)
            .show()
    }

    private fun deletePositiveThought(thought: PositiveThought) {
        lifecycleScope.launch {
            positiveThoughtDao.deleteThought(thought)
            loadPositiveThoughts()
        }
    }

    private fun showAddPositiveThoughtDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_thought, null)
        val editText = dialogView.findViewById<EditText>(R.id.edit_negative_thought)
        val characterCount = dialogView.findViewById<TextView>(R.id.text_character_count)

        editText.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                characterCount.text = "${s?.length ?: 0}/100"
            }
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        })

        AlertDialog.Builder(this)
            .setView(dialogView)
            .setPositiveButton("Сохранить") { _, _ ->
                val text = editText.text.toString()
                if (text.isNotBlank()) savePositiveThought(text)
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    private fun savePositiveThought(text: String) {
        lifecycleScope.launch {
            val newThought = PositiveThought(
                negativeThoughtId = negativeThoughtId,
                thoughtText = text,
                confidenceScale = afterValueFromNegative
            )
            positiveThoughtDao.insertThought(newThought)
            loadPositiveThoughts()
        }
    }

    override fun onBackPressed() {
        val resultIntent = Intent()
        resultIntent.putExtra("updated_after_value", afterValueFromNegative)
        resultIntent.putExtra("negative_thought_id", negativeThoughtId)
        setResult(RESULT_OK, resultIntent)
        super.onBackPressed()
    }

    private val onDistortionClick: (Distortion) -> Unit = { distortion ->
        lifecycleScope.launch {
            val updatedDistortion = distortion.copy(isSelected = !distortion.isSelected)
            distortionDao.updateDistortion(updatedDistortion)
        }
    }
}
