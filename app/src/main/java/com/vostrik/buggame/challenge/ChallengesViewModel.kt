package com.vostrik.buggame.challenge

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vostrik.buggame.BugType
import com.vostrik.buggame.GameResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ChallengesUiState(
    val loading: Boolean = true,
    val hasProfile: Boolean = false,
    val active: List<DailyChallenge> = emptyList(),
    val todayXpEarned: Int = 0,
    val todayXpMax: Int = 0,
    val lifetimeXp: Int = 0,
    val history: List<com.vostrik.buggame.database.DayHistory> = emptyList()
)

class ChallengesViewModel(
    private val repository: ChallengeRepository
) : ViewModel() {
    private val _state = MutableStateFlow(ChallengesUiState())
    val state: StateFlow<ChallengesUiState> = _state.asStateFlow()

    fun load(profileId: Long?) {
        if (profileId == null) {
            _state.value = ChallengesUiState(loading = false, hasProfile = false)
            return
        }
        viewModelScope.launch { refresh(profileId) }
    }

    fun onBugKilled(profileId: Long?, bugType: BugType) {
        val id = profileId ?: return
        viewModelScope.launch { repository.applyBugKill(id, bugType) }
    }

    fun onBonusCollected(profileId: Long?) {
        val id = profileId ?: return
        viewModelScope.launch { repository.applyBonus(id) }
    }

    fun onRoundEnd(profileId: Long?, result: GameResult) {
        val id = profileId ?: return
        viewModelScope.launch {
            val active = repository.applyRoundResult(id, result)
            refresh(id, active)
        }
    }

    private suspend fun refresh(profileId: Long, activeOverride: List<DailyChallenge>? = null) {
        val snapshot = repository.getSnapshot(profileId)
        val active = activeOverride ?: snapshot.active
        _state.value = ChallengesUiState(
            loading = false,
            hasProfile = true,
            active = active,
            todayXpEarned = active.filter { it.isCompleted }.sumOf { it.xpReward },
            todayXpMax = active.sumOf { it.xpReward },
            lifetimeXp = snapshot.lifetimeXp,
            history = snapshot.history
        )
    }
}
