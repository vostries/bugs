package com.vostrik.buggame.challenge

data class DailyChallenge(
    val type: ChallengeType,
    val goal: Int,
    val xpReward: Int,
    val progress: Int = 0,
    val completedAt: Long? = null
) {
    val isCompleted: Boolean get() = progress >= goal
    val description: String get() = type.titleTemplate.format(goal)
}
