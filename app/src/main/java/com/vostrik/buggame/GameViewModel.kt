package com.vostrik.buggame

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class GameViewModel : ViewModel() {
    private val _gameState = MutableStateFlow(GameState())
    val gameState: StateFlow<GameState> = _gameState.asStateFlow()

    var gameManager: GameManager? = null

    override fun onCleared() {
        gameManager?.stop()
        gameManager = null
        super.onCleared()
    }

    fun updateScore(score: Int) {
        _gameState.value = _gameState.value.copy(score = score)
    }

    fun updateHits(hits: Int) {
        _gameState.value = _gameState.value.copy(hits = hits)
    }

    fun updateMisses(misses: Int) {
        _gameState.value = _gameState.value.copy(misses = misses)
    }

    fun updateTimeLeft(timeLeft: Int) {
        _gameState.value = _gameState.value.copy(timeLeft = timeLeft)
    }

    fun updateIsRunning(isRunning: Boolean) {
        _gameState.value = _gameState.value.copy(isRunning = isRunning)
    }

    fun updateIsGravityMode(isGravityMode: Boolean) {
        _gameState.value = _gameState.value.copy(isGravityMode = isGravityMode)
    }

    fun restoreState(state: GameState) {
        _gameState.value = state
    }

    fun resetState() {
        _gameState.value = GameState()
    }

    fun setGameSettings(roundDuration: Int, maxBugs: Int, gameSpeed: Float, bonusInterval: Int) {
        _gameState.value = _gameState.value.copy(
            roundDuration = roundDuration,
            maxBugs = maxBugs,
            gameSpeed = gameSpeed,
            bonusInterval = bonusInterval
        )
    }
}
