package com.cheba.mooddiary

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class EventAdapter(
    private val events: MutableList<Event>,
    private val onEventClick: (Event) -> Unit, // Колбэк для клика по событию
    private val onDeleteClick: (Event) -> Unit // Колбэк для удаления события
) : RecyclerView.Adapter<EventAdapter.EventViewHolder>() {

    inner class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val eventDescription: TextView = itemView.findViewById(R.id.event_text) // Текст события
        val deleteButton: ImageButton = itemView.findViewById(R.id.delete_button) // Кнопка удаления
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_event, parent, false)
        return EventViewHolder(view)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event = events[position]
        holder.eventDescription.text = event.description

        // Обработка клика на текст события (переход на другую страницу)
        holder.eventDescription.setOnClickListener {
            onEventClick(event)
        }

        // Обработка клика на крестик (удаление события)
        holder.deleteButton.setOnClickListener {
            onDeleteClick(event)
        }
    }

    override fun getItemCount(): Int = events.size

    // Метод для обновления списка событий
    fun setEvents(newEvents: List<Event>) {
        events.clear()
        events.addAll(newEvents)
        notifyDataSetChanged()
    }
}




