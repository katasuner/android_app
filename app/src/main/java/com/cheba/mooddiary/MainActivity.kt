package com.cheba.mooddiary

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import androidx.core.widget.addTextChangedListener
import android.app.AlertDialog
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    private lateinit var eventViewModel: EventViewModel
    private lateinit var eventAdapter: EventAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Инициализация базы данных и ViewModel
        val database = AppDatabase.getDatabase(this)
        val eventDao = database.eventDao()
        val factory = EventViewModelFactory(eventDao)
        eventViewModel = ViewModelProvider(this, factory).get(EventViewModel::class.java)

        // Настройка RecyclerView и адаптера
        eventAdapter = EventAdapter(
            events = mutableListOf(),
            onEventClick = { event ->
                // Переход к EmotionActivity
                val intent = Intent(this, EmotionActivity::class.java)
                intent.putExtra("eventId", event.id)
                intent.putExtra("event", event.description)
                startActivity(intent)
            },
            onDeleteClick = { event ->
                // Удаление события
                lifecycleScope.launch {
                    eventViewModel.deleteEvent(event) // Удаление события через ViewModel
                }
            }
        )

        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.adapter = eventAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Подписка на обновления списка событий
        lifecycleScope.launch {
            eventViewModel.getAllEvents().collect { events ->
                eventAdapter.setEvents(events) // Обновляем список событий
            }
        }

        // Обработка кнопки добавления события
        findViewById<Button>(R.id.add_event_button).setOnClickListener {
            showAddEventDialog() // Показ диалога для добавления нового события
        }
    }

    // Метод для показа диалога добавления события
    private fun showAddEventDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_event, null)
        val editTextEvent = dialogView.findViewById<EditText>(R.id.edit_text_event)
        val characterCount = dialogView.findViewById<TextView>(R.id.character_count)

        // Обновляем счётчик символов в режиме реального времени
        editTextEvent.addTextChangedListener { text ->
            val length = text?.length ?: 0
            characterCount.text = "$length/100"
        }

        // Создание AlertDialog
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setPositiveButton("Добавить") { _, _ ->
                val eventText = editTextEvent.text.toString()
                if (eventText.isNotBlank()) {
                    addNewEvent(eventText) // Добавляем новое событие
                }
            }
            .setNegativeButton("Отмена", null)
            .create()

        dialog.show()
    }

    // Метод для добавления нового события
    private fun addNewEvent(description: String) {
        lifecycleScope.launch {
            val newEvent = Event(description = description)
            eventViewModel.addEvent(newEvent) // Добавляем событие через ViewModel
        }
    }
}

