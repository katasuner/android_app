package com.example.mooddiary

import android.graphics.Paint
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class EmotionAdapter(
    private val emotions: List<String>,
    private val onEmotionClick: (View) -> Unit
) : RecyclerView.Adapter<EmotionAdapter.EmotionViewHolder>() {

    // Набор для хранения выделенных эмоций
    private val selectedEmotions = mutableSetOf<String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmotionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_emotion, parent, false)
        return EmotionViewHolder(view)
    }

    override fun onBindViewHolder(holder: EmotionViewHolder, position: Int) {
        holder.bind(emotions[position])
    }

    override fun getItemCount(): Int = emotions.size

    inner class EmotionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val emotionTextView: TextView = itemView.findViewById(R.id.emotion_text)

        init {
            // Устанавливаем слушатель кликов
            emotionTextView.setOnClickListener {
                val emotion = emotions[adapterPosition]
                toggleSelection(emotion)
                onEmotionClick(it)
            }
        }

        fun bind(emotion: String) {
            emotionTextView.text = emotion

            if (selectedEmotions.contains(emotion)) {
                // Если эмоция выделена, устанавливаем жирный шрифт и подчеркивание
                emotionTextView.setTypeface(null, Typeface.BOLD)
                emotionTextView.paintFlags = emotionTextView.paintFlags or Paint.UNDERLINE_TEXT_FLAG
            } else {
                // Если эмоция не выделена, сбрасываем стили
                emotionTextView.setTypeface(null, Typeface.NORMAL)
                emotionTextView.paintFlags = emotionTextView.paintFlags and Paint.UNDERLINE_TEXT_FLAG.inv()
            }
        }

        private fun toggleSelection(emotion: String) {
            if (selectedEmotions.contains(emotion)) {
                selectedEmotions.remove(emotion)
            } else {
                selectedEmotions.add(emotion)
            }
            // Обновляем только этот элемент, чтобы избежать перерисовки всего списка
            notifyItemChanged(adapterPosition)
        }
    }
}
