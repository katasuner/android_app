package com.example.mooddiary

import android.content.Intent
import android.graphics.Paint
import android.graphics.Typeface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class NegativeThoughtsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_negative_thoughts)

        // Контейнер для негативных мыслей
        val thoughtsContainer = findViewById<LinearLayout>(R.id.thoughts_container)
        val inflater = LayoutInflater.from(this)

        // Настройка кнопки добавления негативной мысли
        val addNegativeThoughtButton = findViewById<Button>(R.id.add_negative_thought_button)
        addNegativeThoughtButton.setOnClickListener {
            showAddNegativeThoughtDialog(thoughtsContainer, inflater)
        }

    }

    private fun showAddNegativeThoughtDialog(container: LinearLayout, inflater: LayoutInflater) {
        val dialogView = inflater.inflate(R.layout.dialog_add_thought, null)
        val editText = dialogView.findViewById<EditText>(R.id.edit_negative_thought)
        val characterCount = dialogView.findViewById<TextView>(R.id.text_character_count)

        // Слушатель изменений текста для обновления счетчика символов
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Можно оставить пустым
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Обновляем счетчик символов при каждом изменении текста
                val currentCount = s?.length ?: 0
                characterCount.text = "$currentCount/100"
            }

            override fun afterTextChanged(s: Editable?) {
                // Можно оставить пустым
            }
        })

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        // Настройка кнопок внутри диалога
        dialogView.findViewById<Button>(R.id.button_cancel).setOnClickListener {
            dialog.dismiss()
        }

        dialogView.findViewById<Button>(R.id.button_save).setOnClickListener {
            val thoughtText = editText.text.toString()
            if (thoughtText.isNotBlank()) {
                addNegativeThoughtView(thoughtText, container, inflater)
                dialog.dismiss()
            }
        }

        dialog.show()
    }



    private fun addNegativeThoughtView(thoughtText: String, container: LinearLayout, inflater: LayoutInflater) {
        val thoughtView = inflater.inflate(R.layout.thought_item, container, false)
        val textView = thoughtView.findViewById<TextView>(R.id.thought_text)
        textView.text = thoughtText

        // Добавляем переход на экран с положительными мыслями
        textView.setOnClickListener {
            val intent = Intent(this, PositiveThoughtsActivity::class.java)
            intent.putExtra("negative_thought", thoughtText) // Передаем текст негативной мысли
            startActivity(intent)
        }

        container.addView(thoughtView)
    }

}
