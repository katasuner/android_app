package com.example.mooddiary

import android.graphics.Paint
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class DistortionAdapter(
    private val distortions: List<String>
) : RecyclerView.Adapter<DistortionAdapter.DistortionViewHolder>() {

    // Список для отслеживания выделенных элементов
    private val selectedItems = mutableSetOf<Int>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DistortionViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_distortion, parent, false)
        return DistortionViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: DistortionViewHolder, position: Int) {
        val distortion = distortions[position]
        holder.bind(distortion, position)
    }

    override fun getItemCount(): Int = distortions.size

    inner class DistortionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val distortionText: TextView = itemView.findViewById(R.id.distortion_text)

        fun bind(distortion: String, position: Int) {
            distortionText.text = distortion

            // Обновляем стиль в зависимости от состояния выделения
            if (selectedItems.contains(position)) {
                distortionText.paintFlags = distortionText.paintFlags or Paint.UNDERLINE_TEXT_FLAG
                distortionText.setTypeface(null, Typeface.BOLD)
            } else {
                distortionText.paintFlags = distortionText.paintFlags and Paint.UNDERLINE_TEXT_FLAG.inv()
                distortionText.setTypeface(null, Typeface.NORMAL)
            }

            // Устанавливаем слушатель кликов
            itemView.setOnClickListener {
                if (selectedItems.contains(position)) {
                    // Если уже выделено, снимаем выделение
                    selectedItems.remove(position)
                } else {
                    // Если не выделено, добавляем в список выделенных
                    selectedItems.add(position)
                }
                // Уведомляем адаптер об изменении элемента
                notifyItemChanged(position)
            }
        }
    }
}
