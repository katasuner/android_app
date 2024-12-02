package com.example.mooddiary

import EventAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.graphics.Paint
import android.graphics.Typeface

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Настройка RecyclerView
        val events = mutableListOf<String>()
        val eventAdapter = EventAdapter(events)

        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.adapter = eventAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        val addEventButton = findViewById<Button>(R.id.add_event_button)
        addEventButton.setOnClickListener {
            showAddEventDialog(events, eventAdapter, recyclerView)
        }
    }

    private fun showAddEventDialog(events: MutableList<String>, eventAdapter: EventAdapter, recyclerView: RecyclerView) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_event, null)
        val editTextEvent = dialogView.findViewById<EditText>(R.id.edit_text_event)
        val characterCount = dialogView.findViewById<TextView>(R.id.character_count)

        editTextEvent.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val length = s?.length ?: 0
                characterCount.text = "$length/100"
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setPositiveButton("Сохранить") { _, _ ->
                val newEvent = editTextEvent.text.toString().trim()
                if (newEvent.isNotEmpty()) {
                    events.add(newEvent)
                    eventAdapter.notifyItemInserted(events.size - 1)
                    recyclerView.scrollToPosition(events.size - 1)
                }
            }
            .setNegativeButton("Отмена", null)
            .create()

        dialog.show()
    }

    // Метод для обработки нажатий на эмоции
    fun onEmotionClick(view: View) {
        if (view is TextView) {
            // Сделать текст жирным и подчеркнутым при клике
            view.paintFlags = view.paintFlags or Paint.UNDERLINE_TEXT_FLAG
            view.setTypeface(view.typeface, Typeface.BOLD)
        }
    }
}
