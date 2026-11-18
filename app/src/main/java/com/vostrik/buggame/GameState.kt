package com.vostrik.buggame

data class GameState(
    val score: Int = 0,
    val hits: Int = 0,
    val misses: Int = 0,
    val timeLeft: Int = 0,
    val isRunning: Boolean = false,
    val isGravityMode: Boolean = false,
    val roundDuration: Int = 30,
    val maxBugs: Int = 5,
    val gameSpeed: Float = 1f,
    val bonusInterval: Int = 5
)
