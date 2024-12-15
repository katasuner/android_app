package com.example.mooddiary

import com.example.mooddiary.Emotion
import android.graphics.Paint
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class EmotionAdapter(
    private val emotions: List<Emotion>,
    private val onEmotionClick: (Emotion) -> Unit
) : RecyclerView.Adapter<EmotionAdapter.EmotionViewHolder>() {

    inner class EmotionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val emotionText: TextView = itemView.findViewById(R.id.emotion_text)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmotionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_emotion, parent, false)
        return EmotionViewHolder(view)
    }

    override fun onBindViewHolder(holder: EmotionViewHolder, position: Int) {
        val emotion = emotions[position]

        holder.emotionText.text = emotion.emotionText

        holder.emotionText.paintFlags = if (emotion.isSelected) {
            holder.emotionText.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        } else {
            holder.emotionText.paintFlags and Paint.UNDERLINE_TEXT_FLAG.inv()
        }

        holder.emotionText.setTypeface(
            null,
            if (emotion.isSelected) Typeface.BOLD else Typeface.NORMAL
        )

        holder.emotionText.setOnClickListener {
            onEmotionClick(emotion)
        }
    }

    override fun getItemCount(): Int = emotions.size
}
