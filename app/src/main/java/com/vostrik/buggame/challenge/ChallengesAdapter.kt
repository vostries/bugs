package com.vostrik.buggame.challenge

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.vostrik.buggame.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ChallengesAdapter : RecyclerView.Adapter<ChallengesAdapter.ViewHolder>() {
    private var challenges = listOf<DailyChallenge>()

    fun submitList(list: List<DailyChallenge>) {
        challenges = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_challenge, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(challenges[position])
    }

    override fun getItemCount() = challenges.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val statusView = itemView.findViewById<TextView>(R.id.textViewChallengeStatus)
        private val descView = itemView.findViewById<TextView>(R.id.textViewChallengeDesc)
        private val progressBar = itemView.findViewById<ProgressBar>(R.id.progressBarChallenge)
        private val progressText = itemView.findViewById<TextView>(R.id.textViewChallengeProgress)
        private val xpView = itemView.findViewById<TextView>(R.id.textViewChallengeXp)
        private val completedView = itemView.findViewById<TextView>(R.id.textViewChallengeCompleted)
        private val completedTimeView = itemView.findViewById<TextView>(R.id.textViewChallengeCompletedTime)

        fun bind(challenge: DailyChallenge) {
            descView.text = challenge.description
            progressText.text = itemView.context.getString(
                R.string.challenges_progress,
                challenge.progress,
                challenge.goal
            )
            xpView.text = "+${challenge.xpReward} XP"

            val progress = (challenge.progress.toFloat() / challenge.goal.toFloat() * 100).toInt().coerceIn(0, 100)
            progressBar.progress = progress

            if (challenge.isCompleted) {
                statusView.text = "✓"
                statusView.setTextColor(0xFF4CAF50.toInt())
                completedView.visibility = View.VISIBLE
                val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
                completedTimeView.text = itemView.context.getString(
                    R.string.challenges_completed,
                    sdf.format(Date(challenge.completedAt!!))
                )
                completedTimeView.visibility = View.VISIBLE
            } else {
                statusView.text = "○"
                statusView.setTextColor(0xFFFFB800.toInt())
                completedView.visibility = View.GONE
                completedTimeView.visibility = View.GONE
            }
        }
    }
}
