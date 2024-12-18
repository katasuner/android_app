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
import kotlinx.coroutines.launch

class NegativeThoughtsActivity : AppCompatActivity() {

    private lateinit var negativeThoughtDao: NegativeThoughtDao
    private var eventId: Int = -1
    private lateinit var thoughtsContainer: LinearLayout
    private lateinit var inflater: LayoutInflater
    private val thoughts = mutableListOf<NegativeThought>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_negative_thoughts)

        // Получаем ID события
        eventId = intent.getIntExtra("eventId", -1)
        if (eventId == -1) {
            finish()
            return
        }

        // Инициализация DAO и компонентов
        val database = AppDatabase.getDatabase(this)
        negativeThoughtDao = database.negativeThoughtDao()
        thoughtsContainer = findViewById(R.id.thoughts_container)
        inflater = LayoutInflater.from(this)

        // Загрузка мыслей
        loadNegativeThoughts()

        val addThoughtButton = findViewById<Button>(R.id.add_negative_thought_button)
        addThoughtButton.setOnClickListener {
            showAddNegativeThoughtDialog()
        }
    }

    private fun loadNegativeThoughts() {
        lifecycleScope.launch {
            negativeThoughtDao.getThoughtsForEvent(eventId).collect { loadedThoughts ->
                thoughts.clear()
                thoughts.addAll(loadedThoughts)
                setupThoughtViews()
            }
        }
    }

    private fun setupThoughtViews() {
        thoughtsContainer.removeAllViews()

        thoughts.forEach { thought ->
            val thoughtView = inflater.inflate(R.layout.thought_item, thoughtsContainer, false)

            val thoughtTextView = thoughtView.findViewById<TextView>(R.id.thought_text)
            val beforeEditText = thoughtView.findViewById<EditText>(R.id.thought_before_value)
            val afterEditText = thoughtView.findViewById<EditText>(R.id.thought_after_value)

            // Устанавливаем текст и значения
            thoughtTextView.text = thought.thoughtText

            // Если значение "До" = 0, поле будет пустым
            beforeEditText.setText(if (thought.beforeValue == 0) "" else thought.beforeValue.toString())
            afterEditText.setText(if (thought.afterValue == 0) "" else thought.afterValue.toString())

            // Обновление "До" при вводе
            beforeEditText.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    val newValue = s?.toString()?.toIntOrNull() ?: 0
                    if (thought.beforeValue != newValue) {
                        updateThought(thought.copy(beforeValue = newValue))
                    }
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })

            // Обновление "После" при вводе
            afterEditText.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    val newValue = s?.toString()?.toIntOrNull() ?: 0
                    if (thought.afterValue != newValue) {
                        updateThought(thought.copy(afterValue = newValue))
                    }
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })

            // Переход в PositiveThoughtsActivity
            thoughtTextView.setOnClickListener {
                val intent = Intent(this@NegativeThoughtsActivity, PositiveThoughtsActivity::class.java)
                intent.putExtra("negative_thought_id", thought.id)
                intent.putExtra("negative_thought", thought.thoughtText)
                intent.putExtra("after_value", thought.afterValue)
                startActivityForResult(intent, REQUEST_CODE)
            }

            // Удаление при долгом нажатии
            thoughtTextView.setOnLongClickListener {
                showDeleteConfirmationDialog(thought)
                true
            }

            thoughtsContainer.addView(thoughtView)
        }
    }

    // Обновление мысли в БД
    private fun updateThought(updatedThought: NegativeThought) {
        lifecycleScope.launch {
            negativeThoughtDao.updateThought(updatedThought)
        }
    }

    // Показ диалога для добавления новой мысли
    private fun showAddNegativeThoughtDialog() {
        val dialogView = inflater.inflate(R.layout.dialog_add_thought, null)
        val editText = dialogView.findViewById<EditText>(R.id.edit_negative_thought)
        val characterCount = dialogView.findViewById<TextView>(R.id.text_character_count)

        editText.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                characterCount.text = "${s?.length ?: 0}/100"
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}
        })

        AlertDialog.Builder(this)
            .setView(dialogView)
            .setPositiveButton("Сохранить") { _, _ ->
                val text = editText.text.toString()
                if (text.isNotBlank()) {
                    saveNegativeThought(text)
                }
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    private fun saveNegativeThought(text: String) {
        lifecycleScope.launch {
            val newThought = NegativeThought(
                eventId = eventId,
                thoughtText = text,
                beforeValue = 0,
                afterValue = 0
            )
            negativeThoughtDao.insertThought(newThought)
            loadNegativeThoughts()
        }
    }

    // Показ диалога для подтверждения удаления
    private fun showDeleteConfirmationDialog(thought: NegativeThought) {
        AlertDialog.Builder(this)
            .setTitle("Удалить мысль")
            .setMessage("Вы уверены, что хотите удалить эту мысль?")
            .setPositiveButton("Удалить") { _, _ ->
                deleteThought(thought)
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    private fun deleteThought(thought: NegativeThought) {
        lifecycleScope.launch {
            negativeThoughtDao.deleteThought(thought)
            loadNegativeThoughts()
        }
    }

    companion object {
        private const val REQUEST_CODE = 1001
    }
}
