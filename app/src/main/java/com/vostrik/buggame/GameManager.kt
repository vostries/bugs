package com.vostrik.buggame

import android.os.CountDownTimer
import kotlin.random.Random

class GameManager(
    private val gameView: GameView,
    private val roundDuration: Int,
    private val maxBugs: Int,
    private val gameSpeed: Float,
    private val bonusInterval: Int,
    private val onScoreUpdate: (Int) -> Unit,
    private val onTimeUpdate: (Int) -> Unit,
    private val onGameEnd: (GameResult) -> Unit,
    private val onGravityModeChanged: (Boolean) -> Unit = {}
) {
    private val bugs = mutableListOf<Bug>()
    private var bonus: Bonus? = null
    private var isGravityMode = false
    private var gravityEndTime = 0L
    private val gravityDuration = 15000L
    private var currentGravityX = 0f
    private var currentGravityY = 0f
    private var score = 0
    private var hits = 0
    private var misses = 0
    private var isRunning = false
    private var timer: CountDownTimer? = null
    private var bonusTimer: CountDownTimer? = null
    private var lastUpdateTime = System.currentTimeMillis()

    init {
        gameView.onBugHit = { bug ->
            handleBugHit(bug)
        }
        gameView.onMiss = {
            handleMiss()
        }
        gameView.onBonusCollected = {
            handleBonusCollected()
        }
    }

    fun start() {
        isRunning = true
        score = 0
        hits = 0
        misses = 0
        bugs.clear()
        bonus = null
        lastUpdateTime = System.currentTimeMillis()

        spawnInitialBugs()
        startGameLoop()
        startTimer()
        startBonusTimer()
    }

    fun stop() {
        isRunning = false
        timer?.cancel()
        bonusTimer?.cancel()
    }

    private fun spawnInitialBugs() {
        val screenWidth = gameView.width
        val screenHeight = gameView.height
        if (screenWidth == 0 || screenHeight == 0) return

        repeat(maxBugs.coerceAtMost(5)) {
            spawnBug(screenWidth, screenHeight)
        }
        gameView.setBugs(bugs)
    }

    private fun spawnBug(screenWidth: Int, screenHeight: Int) {
        val type = when (Random.nextInt(100)) {
            in 0..9 -> BugType.RARE
            in 10..39 -> BugType.FAST
            else -> BugType.NORMAL
        }

        val baseSize = 120f
        val size = baseSize * type.sizeMultiplier
        val baseSpeed = 100f * gameSpeed
        val speed = baseSpeed * type.speedMultiplier

        val bug = Bug(
            x = Random.nextFloat() * (screenWidth - size),
            y = Random.nextFloat() * (screenHeight - size),
            speedX = (Random.nextFloat() * 2 - 1) * speed,
            speedY = (Random.nextFloat() * 2 - 1) * speed,
            size = size,
            type = type
        )
        bugs.add(bug)
    }

    private fun startGameLoop() {
        gameView.post(object : Runnable {
            override fun run() {
                if (!isRunning) return

                val currentTime = System.currentTimeMillis()
                val deltaTime = (currentTime - lastUpdateTime) / 1000f
                lastUpdateTime = currentTime

                if (isGravityMode && currentTime >= gravityEndTime) {
                    isGravityMode = false
                    onGravityModeChanged(false)
                }

                val gravityX = if (isGravityMode) currentGravityX * 300f else 0f
                val gravityY = if (isGravityMode) currentGravityY * 300f else 0f
                gameView.updateBugs(deltaTime, gravityX, gravityY)

                val screenWidth = gameView.width
                val screenHeight = gameView.height
                while (bugs.size < maxBugs) {
                    spawnBug(screenWidth, screenHeight)
                }
                gameView.setBugs(bugs)

                gameView.postDelayed(this, 16)
            }
        })
    }

    private fun startTimer() {
        timer = object : CountDownTimer((roundDuration * 1000).toLong(), 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsLeft = (millisUntilFinished / 1000).toInt()
                onTimeUpdate(secondsLeft)
            }

            override fun onFinish() {
                endGame()
            }
        }.start()
    }

    private fun startBonusTimer() {
        bonusTimer = object : CountDownTimer((bonusInterval * 1000).toLong(), (bonusInterval * 1000).toLong()) {
            override fun onTick(millisUntilFinished: Long) = Unit

            override fun onFinish() {
                spawnBonus()
                if (isRunning) {
                    start()
                }
            }
        }.start()
    }

    private fun spawnBonus() {
        if (bonus == null && !isGravityMode) {
            val screenWidth = gameView.width
            val screenHeight = gameView.height
            if (screenWidth > 0 && screenHeight > 0) {
                val size = 80f
                bonus = Bonus(
                    x = Random.nextFloat() * (screenWidth - size),
                    y = Random.nextFloat() * (screenHeight - size),
                    size = size
                )
                gameView.setBonus(bonus)
            }
        }
    }

    private fun handleBugHit(bug: Bug) {
        score += bug.points
        hits++
        bugs.remove(bug)
        onScoreUpdate(score)
    }

    private fun handleMiss() {
        score -= 5
        misses++
        onScoreUpdate(score)
    }

    fun updateGravity(x: Float, y: Float) {
        currentGravityX = x
        currentGravityY = y
    }

    private fun handleBonusCollected() {
        isGravityMode = true
        gravityEndTime = System.currentTimeMillis() + gravityDuration
        bonus = null
        gameView.setBonus(null)
        onGravityModeChanged(true)
    }

    private fun endGame() {
        isRunning = false
        val accuracy = if (hits + misses > 0) {
            (hits.toFloat() / (hits + misses) * 100).toInt()
        } else {
            0
        }
        onGameEnd(GameResult(score, hits, misses, accuracy))
    }
}

data class GameResult(
    val score: Int,
    val hits: Int,
    val misses: Int,
    val accuracy: Int
)
