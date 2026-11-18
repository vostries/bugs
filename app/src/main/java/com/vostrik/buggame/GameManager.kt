package com.vostrik.buggame

import android.os.CountDownTimer
import com.vostrik.buggame.api.GoldRateRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.random.Random

class GameManager(
    private val roundDuration: Int,
    private val maxBugs: Int,
    private val gameSpeed: Float,
    private val bonusInterval: Int
) {
    private var gameView: GameView? = null
    var onScoreUpdate: (Int) -> Unit = {}
    var onTimeUpdate: (Int) -> Unit = {}
    var onGameEnd: (GameResult) -> Unit = {}
    var onGravityModeChanged: (Boolean) -> Unit = {}

    private val bugs = mutableListOf<Bug>()
    private var bonus: Bonus? = null
    private var goldenBug: Bug? = null
    private var goldRate: Double? = null
    private var isGravityMode = false
    private var gravityEndTime = 0L
    private val gravityDuration = 15000L
    private var currentGravityX = 0f
    private var currentGravityY = 0f
    private var score = 0
    private var hits = 0
    private var misses = 0
    var isRunning = false
        private set
    private var timer: CountDownTimer? = null
    private var bonusTimer: CountDownTimer? = null
    private var goldenBugTimer: CountDownTimer? = null
    private var lastUpdateTime = System.currentTimeMillis()

    fun attachView(view: GameView) {
        gameView = view
        view.onBugHit = { handleBugHit(it) }
        view.onMiss = { handleMiss() }
        view.onBonusCollected = { handleBonusCollected() }
        view.setBugs(bugs)
        view.setBonus(bonus)
        if (isRunning) {
            lastUpdateTime = System.currentTimeMillis()
            startGameLoop()
        }
    }

    fun detachView() {
        gameView?.removeCallbacks(gameLoop)
        gameView?.onBugHit = null
        gameView?.onMiss = null
        gameView?.onBonusCollected = null
        gameView = null
    }

    fun start() {
        isRunning = true
        score = 0
        hits = 0
        misses = 0
        bugs.clear()
        bonus = null
        goldenBug = null
        lastUpdateTime = System.currentTimeMillis()

        loadGoldRate()
        spawnInitialBugs()
        startGameLoop()
        startTimer()
        startBonusTimer()
        startGoldenBugTimer()
    }

    fun stop() {
        isRunning = false
        timer?.cancel()
        bonusTimer?.cancel()
        goldenBugTimer?.cancel()
    }

    private fun spawnInitialBugs() {
        val view = gameView ?: return
        val screenWidth = view.width
        val screenHeight = view.height
        if (screenWidth == 0 || screenHeight == 0) return

        repeat(maxBugs.coerceAtMost(5)) {
            spawnBug(screenWidth, screenHeight)
        }
        view.setBugs(bugs)
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

    private val gameLoop = object : Runnable {
        override fun run() {
            val view = gameView ?: return
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
            view.updateBugs(deltaTime, gravityX, gravityY)

            val screenWidth = view.width
            val screenHeight = view.height
            while (bugs.size < maxBugs) {
                spawnBug(screenWidth, screenHeight)
            }
            view.setBugs(bugs)

            view.postDelayed(this, 16)
        }
    }

    private fun startGameLoop() {
        gameView?.removeCallbacks(gameLoop)
        gameView?.post(gameLoop)
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
        bonusTimer?.cancel()
        bonusTimer = object : CountDownTimer((bonusInterval * 1000).toLong(), (bonusInterval * 1000).toLong()) {
            override fun onTick(millisUntilFinished: Long) = Unit

            override fun onFinish() {
                spawnBonus()
                if (isRunning) startBonusTimer()
            }
        }.start()
    }

    private fun spawnBonus() {
        val view = gameView ?: return
        if (bonus == null && !isGravityMode) {
            val screenWidth = view.width
            val screenHeight = view.height
            if (screenWidth > 0 && screenHeight > 0) {
                val size = 80f
                bonus = Bonus(
                    x = Random.nextFloat() * (screenWidth - size),
                    y = Random.nextFloat() * (screenHeight - size),
                    size = size
                )
                view.setBonus(bonus)
            }
        }
    }

    private fun handleBugHit(bug: Bug) {
        val points = if (bug.type == BugType.GOLDEN) {
            val rate = goldRate ?: 8000.0
            (rate / 100.0).toInt().coerceAtLeast(50)
        } else {
            bug.points
        }

        score += points
        hits++

        if (bug == goldenBug) {
            goldenBug = null
        }
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
        gameView?.setBonus(null)
        onGravityModeChanged(true)
    }

    private fun endGame() {
        stop()
        val accuracy = if (hits + misses > 0) {
            (hits.toFloat() / (hits + misses) * 100).toInt()
        } else {
            0
        }
        onGameEnd(GameResult(score, hits, misses, accuracy))
    }

    private fun loadGoldRate() {
        CoroutineScope(Dispatchers.IO).launch {
            val rate = GoldRateRepository.getInstance().getGoldRate()
            goldRate = rate?.ratePerGram
        }
    }

    private fun startGoldenBugTimer() {
        goldenBugTimer?.cancel()
        goldenBugTimer = object : CountDownTimer(20000, 20000) {
            override fun onTick(millisUntilFinished: Long) = Unit

            override fun onFinish() {
                spawnGoldenBug()
                if (isRunning) startGoldenBugTimer()
            }
        }.start()
    }

    private fun spawnGoldenBug() {
        val view = gameView ?: return
        if (goldenBug == null) {
            val screenWidth = view.width
            val screenHeight = view.height
            if (screenWidth > 0 && screenHeight > 0) {
                val baseSize = 120f
                val size = baseSize * BugType.GOLDEN.sizeMultiplier
                val baseSpeed = 100f * gameSpeed
                val speed = baseSpeed * BugType.GOLDEN.speedMultiplier

                goldenBug = Bug(
                    x = Random.nextFloat() * (screenWidth - size),
                    y = Random.nextFloat() * (screenHeight - size),
                    speedX = (Random.nextFloat() * 2 - 1) * speed,
                    speedY = (Random.nextFloat() * 2 - 1) * speed,
                    size = size,
                    type = BugType.GOLDEN
                )
                bugs.add(goldenBug!!)
                view.setBugs(bugs)
            }
        }
    }
}

data class GameResult(
    val score: Int,
    val hits: Int,
    val misses: Int,
    val accuracy: Int
)
