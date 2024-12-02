package com.example.mooddiary

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class EmotionAdapter(
    private val emotions: List<String>,
    private val clickListener: (View) -> Unit
) : RecyclerView.Adapter<EmotionAdapter.EmotionViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmotionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_emotion, parent, false)
        return EmotionViewHolder(view)
    }

    override fun onBindViewHolder(holder: EmotionViewHolder, position: Int) {
        holder.bind(emotions[position], clickListener)
    }

    override fun getItemCount(): Int = emotions.size

    class EmotionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val emotionTextView: TextView = itemView.findViewById(R.id.emotion_text)

        fun bind(emotion: String, clickListener: (View) -> Unit) {
            emotionTextView.text = emotion
            emotionTextView.setOnClickListener(clickListener)
        }
    }
}
