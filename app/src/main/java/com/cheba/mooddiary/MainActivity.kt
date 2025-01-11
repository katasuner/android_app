package com.cheba.mooddiary

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch

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
                val intent = Intent(this, EmotionActivity::class.java)
                intent.putExtra("eventId", event.id)
                startActivity(intent)
            },
            onDeleteClick = { event ->
                lifecycleScope.launch {
                    eventViewModel.deleteEvent(event)
                }
            },
            onLongClick = { event ->
                showProcessEventDialog(event)
            },
            onResetClick = { event ->
                showResetEventDialog(event)
            }
        )

        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.adapter = eventAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Подписка на обновления списка событий
        lifecycleScope.launch {
            eventViewModel.getAllEvents().collect { events ->
                eventAdapter.setEvents(events)
            }
        }

        // Обработка кнопки добавления события
        findViewById<Button>(R.id.add_event_button).setOnClickListener {
            showAddEventDialog()
        }
    }

    // Диалог для обработки события
    private fun showProcessEventDialog(event: Event) {
        AlertDialog.Builder(this)
            .setTitle("Обработка события")
            .setMessage("Отметить событие как обработанное?")
            .setPositiveButton("Да") { _, _ ->
                lifecycleScope.launch {
                    val updatedEvent = event.copy(isProcessed = true)
                    eventViewModel.updateEvent(updatedEvent)
                }
            }
            .setNegativeButton("Отмена", null)
            .create()
            .show()
    }

    // Диалог для сброса статуса события
    private fun showResetEventDialog(event: Event) {
        AlertDialog.Builder(this)
            .setTitle("Сброс статуса")
            .setMessage("Хотите еще поработать над событием?")
            .setPositiveButton("Да") { _, _ ->
                lifecycleScope.launch {
                    val updatedEvent = event.copy(isProcessed = false)
                    eventViewModel.updateEvent(updatedEvent)
                }
            }
            .setNegativeButton("Отмена", null)
            .create()
            .show()
    }

    // Диалог для добавления нового события
    private fun showAddEventDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_event, null)
        val editTextEvent = dialogView.findViewById<android.widget.EditText>(R.id.edit_text_event)
        val characterCount = dialogView.findViewById<android.widget.TextView>(R.id.character_count)

        editTextEvent.addTextChangedListener { text ->
            val length = text?.length ?: 0
            characterCount.text = "$length/100"
        }

        AlertDialog.Builder(this)
            .setView(dialogView)
            .setPositiveButton("Добавить") { _, _ ->
                val eventText = editTextEvent.text.toString()
                if (eventText.isNotBlank()) {
                    addNewEvent(eventText)
                }
            }
            .setNegativeButton("Отмена", null)
            .create()
            .show()
    }

    // Добавление нового события
    private fun addNewEvent(description: String) {
        lifecycleScope.launch {
            val newEvent = Event(description = description)
            eventViewModel.addEvent(newEvent)
        }
    }
}
