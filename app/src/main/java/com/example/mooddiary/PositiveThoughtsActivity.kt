package com.example.mooddiary

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class PositiveThoughtsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_positive_thoughts)

        // Получение переданной отрицательной мысли
        val negativeThought = intent.getStringExtra("negativeThought")
        val textView = findViewById<TextView>(R.id.negative_thought_text)
        textView.text = negativeThought
    }
}
