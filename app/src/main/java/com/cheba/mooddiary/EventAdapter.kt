package com.cheba.mooddiary

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cheba.mooddiary.R

class EventAdapter(
    private val events: MutableList<Event>,
    private val onEventClick: (Event) -> Unit,
    private val onDeleteClick: (Event) -> Unit,
    private val onLongClick: (Event) -> Unit,
    private val onResetClick: (Event) -> Unit // Новый колбэк для сброса статуса
) : RecyclerView.Adapter<EventAdapter.EventViewHolder>() {

    inner class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val eventDescription: TextView = itemView.findViewById(R.id.event_text)
        val deleteButton: ImageButton = itemView.findViewById(R.id.delete_button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_event, parent, false)
        return EventViewHolder(view)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event = events[position]
        holder.eventDescription.text = event.description

        // Изменяем фон на основе статуса
        val backgroundResource = if (event.isProcessed) {
            R.drawable.rounded_background_processed
        } else {
            R.drawable.rounded_background
        }
        holder.itemView.setBackgroundResource(backgroundResource)

        holder.eventDescription.setOnClickListener {
            onEventClick(event)
        }

        holder.eventDescription.setOnLongClickListener {
            if (event.isProcessed) {
                onResetClick(event) // Показываем окно для сброса
            } else {
                onLongClick(event) // Показываем окно для обработки
            }
            true
        }

        holder.deleteButton.setOnClickListener {
            onDeleteClick(event)
        }
    }

    override fun getItemCount(): Int = events.size

    fun setEvents(newEvents: List<Event>) {
        events.clear()
        events.addAll(newEvents)
        notifyDataSetChanged()
    }
}

