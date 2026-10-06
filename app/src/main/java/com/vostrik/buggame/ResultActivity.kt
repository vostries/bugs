package com.vostrik.buggame

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ResultActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        val score = intent.getIntExtra("SCORE", 0)
        val hits = intent.getIntExtra("HITS", 0)
        val misses = intent.getIntExtra("MISSES", 0)

        val totalClicks = hits + misses
        val accuracy = if (totalClicks > 0) {
            (hits.toFloat() / totalClicks * 100).toInt()
        } else {
            0
        }

        findViewById<TextView>(R.id.textViewFinalScore).text = "Очки: $score"
        findViewById<TextView>(R.id.textViewHits).text = "Попадания: $hits"
        findViewById<TextView>(R.id.textViewMisses).text = "Промахи: $misses"
        findViewById<TextView>(R.id.textViewAccuracy).text = "Точность: $accuracy%"

        findViewById<Button>(R.id.buttonPlayAgain).setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }
    }
}
