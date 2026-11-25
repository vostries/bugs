package com.vostrik.buggame.challenge

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.vostrik.buggame.R
import com.vostrik.buggame.database.DayHistory
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class ChallengeHistoryAdapter : RecyclerView.Adapter<ChallengeHistoryAdapter.ViewHolder>() {
    private var history = listOf<DayHistory>()

    fun submitList(list: List<DayHistory>) {
        history = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_challenge_history, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(history[position])
    }

    override fun getItemCount() = history.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val dateView = itemView.findViewById<TextView>(R.id.textViewHistoryDate)
        private val summaryView = itemView.findViewById<TextView>(R.id.textViewHistorySummary)

        fun bind(day: DayHistory) {
            val date = LocalDate.parse(day.date)
            val formatter = DateTimeFormatter.ofPattern("dd MMM", Locale("ru"))
            dateView.text = date.format(formatter)

            summaryView.text = itemView.context.getString(
                R.string.challenges_history_summary,
                day.totalXp,
                day.completed,
                day.total
            )
        }
    }
}
