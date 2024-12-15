package com.example.mooddiary

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class PositiveThoughtsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_positive_thoughts)

        // Настройка RecyclerView для искажений
        val recyclerViewDistortions = findViewById<RecyclerView>(R.id.recycler_view_distortions)
        recyclerViewDistortions.layoutManager = GridLayoutManager(this, 2) // 2 колонки

        // Список искажений
        val distortions = listOf(
            "Все или ничего",
            "Сверхобобщение",
            "Негативный фильтр",
            "Чтение мыслей",
            "Ошибка предсказаний",
            "Эмоциональное обоснование",
            "Преувеличение и преуменьшение",
            "Обесценивание положительного",
            "Императивы",
            "Ярлыки",
            "Самообвинение",
            "Обвинение других"
        )
        val negativeThought = intent.getStringExtra("negative_thought")
        val negativeThoughtTextView = findViewById<TextView>(R.id.negative_thought_text)
        negativeThoughtTextView.text = negativeThought

        val adapter = DistortionAdapter(distortions)
        recyclerViewDistortions.adapter = adapter

        // Контейнер для позитивных мыслей
        val positiveThoughtsContainer = findViewById<LinearLayout>(R.id.positive_thoughts_container)
        val inflater = LayoutInflater.from(this)

        // Кнопка для добавления позитивной мысли
        val addPositiveThoughtButton = findViewById<Button>(R.id.add_positive_thought_button)
        addPositiveThoughtButton.setOnClickListener {
            showAddThoughtDialog(positiveThoughtsContainer, inflater)
        }
    }

    private fun showAddThoughtDialog(container: LinearLayout, inflater: LayoutInflater) {
        val dialogView = inflater.inflate(R.layout.dialog_add_thought, null)
        val editText = dialogView.findViewById<EditText>(R.id.edit_negative_thought)
        val characterCount = dialogView.findViewById<TextView>(R.id.text_character_count)
        val title = dialogView.findViewById<TextView>(R.id.dialog_title)

        // Настройка заголовка
        title.text = "Добавить позитивную мысль"

        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val currentCount = s?.length ?: 0
                characterCount.text = "$currentCount/100"
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        dialogView.findViewById<Button>(R.id.button_cancel).setOnClickListener {
            dialog.dismiss()
        }

        dialogView.findViewById<Button>(R.id.button_save).setOnClickListener {
            val thoughtText = editText.text.toString()
            if (thoughtText.isNotBlank()) {
                addPositiveThoughtView(thoughtText, container, inflater)
                dialog.dismiss()
            }
        }

        dialog.show()
    }

    private fun addPositiveThoughtView(thoughtText: String, container: LinearLayout, inflater: LayoutInflater) {
        val thoughtView = inflater.inflate(R.layout.positive_thought_item, container, false)
        val textView = thoughtView.findViewById<TextView>(R.id.thought_text)
        textView.text = thoughtText

        // Убедитесь, что значение вводится корректно
        val confidenceScale = thoughtView.findViewById<EditText>(R.id.confidence_scale)
        confidenceScale.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val value = confidenceScale.text.toString().toIntOrNull() ?: 0
                if (value < 0 || value > 100) {
                    confidenceScale.setText("0") // Сброс значения, если оно некорректное
                }
            }
        }

        container.addView(thoughtView)
    }

}
