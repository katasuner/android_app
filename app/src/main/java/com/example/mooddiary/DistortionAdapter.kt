package com.example.mooddiary

import android.graphics.Paint
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class DistortionAdapter(
    private val distortions: List<Distortion>,
    private val onDistortionClick: (Distortion) -> Unit
) : RecyclerView.Adapter<DistortionAdapter.DistortionViewHolder>() {

    inner class DistortionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val distortionText: TextView = itemView.findViewById(R.id.distortion_text)

        fun bind(distortion: Distortion) {
            distortionText.text = distortion.distortionText
            distortionText.paintFlags = if (distortion.isSelected) {
                distortionText.paintFlags or Paint.UNDERLINE_TEXT_FLAG
            } else {
                distortionText.paintFlags and Paint.UNDERLINE_TEXT_FLAG.inv()
            }

            itemView.setOnClickListener {
                onDistortionClick(distortion)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DistortionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_distortion, parent, false)
        return DistortionViewHolder(view)
    }

    override fun onBindViewHolder(holder: DistortionViewHolder, position: Int) {
        holder.bind(distortions[position])
    }

    override fun getItemCount(): Int = distortions.size
}


