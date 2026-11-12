package com.vostrik.buggame

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class GameView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val bugs = mutableListOf<Bug>()
    private var bonus: Bonus? = null
    private val bugPaint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.FILL
    }
    private val legPaint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.STROKE
        strokeWidth = 4f
        color = Color.BLACK
    }
    private val bonusPaint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.FILL
        color = Color.parseColor("#FFD700")
    }
    private val bonusStrokePaint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.STROKE
        strokeWidth = 6f
        color = Color.parseColor("#FFA500")
    }
    private var animationTime = 0f

    var onBugHit: ((Bug) -> Unit)? = null
    var onMiss: (() -> Unit)? = null
    var onBonusCollected: (() -> Unit)? = null

    fun setBugs(newBugs: List<Bug>) {
        bugs.clear()
        bugs.addAll(newBugs)
        invalidate()
    }

    fun setBonus(newBonus: Bonus?) {
        bonus = newBonus
        invalidate()
    }

    fun updateBugs(deltaTime: Float, gravityX: Float = 0f, gravityY: Float = 0f) {
        bugs.forEach { bug ->
            bug.update(deltaTime, width, height, gravityX, gravityY)
        }
        animationTime += deltaTime
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        bugs.forEach { bug ->
            val centerX = bug.x + bug.size / 2
            val centerY = bug.y + bug.size / 2
            val legWave = Math.sin(animationTime * 10.0 + bug.x.toDouble()).toFloat() * 5f

            bugPaint.color = when (bug.type) {
                BugType.NORMAL -> Color.parseColor("#8B4513")
                BugType.FAST -> Color.parseColor("#FF4500")
                BugType.RARE -> Color.parseColor("#FFD700")
                BugType.GOLDEN -> Color.parseColor("#FFD700")
            }

            // ножки слева
            for (i in 0..2) {
                val legY = bug.y + bug.size * (0.3f + i * 0.2f)
                canvas.drawLine(
                    centerX - bug.size * 0.2f,
                    legY,
                    centerX - bug.size * 0.4f,
                    legY + legWave * (if (i % 2 == 0) 1 else -1),
                    legPaint
                )
            }

            // ножки справа
            for (i in 0..2) {
                val legY = bug.y + bug.size * (0.3f + i * 0.2f)
                canvas.drawLine(
                    centerX + bug.size * 0.2f,
                    legY,
                    centerX + bug.size * 0.4f,
                    legY - legWave * (if (i % 2 == 0) 1 else -1),
                    legPaint
                )
            }

            // тело
            canvas.drawOval(
                bug.x + bug.size * 0.1f,
                bug.y + bug.size * 0.2f,
                bug.x + bug.size * 0.9f,
                bug.y + bug.size * 0.9f,
                bugPaint
            )

            // голова
            canvas.drawCircle(
                centerX,
                bug.y + bug.size * 0.15f,
                bug.size * 0.25f,
                bugPaint
            )

            // усики
            val antennaAngle = Math.sin(animationTime * 8.0 + bug.x.toDouble()).toFloat() * 0.2f
            canvas.drawLine(
                centerX - bug.size * 0.1f,
                bug.y + bug.size * 0.05f,
                centerX - bug.size * 0.2f,
                bug.y - bug.size * 0.1f + antennaAngle * 10,
                legPaint
            )
            canvas.drawLine(
                centerX + bug.size * 0.1f,
                bug.y + bug.size * 0.05f,
                centerX + bug.size * 0.2f,
                bug.y - bug.size * 0.1f - antennaAngle * 10,
                legPaint
            )

            // глаза
            bugPaint.color = Color.BLACK
            canvas.drawCircle(centerX - bug.size * 0.08f, bug.y + bug.size * 0.12f, bug.size * 0.04f, bugPaint)
            canvas.drawCircle(centerX + bug.size * 0.08f, bug.y + bug.size * 0.12f, bug.size * 0.04f, bugPaint)

            if (bug.type == BugType.GOLDEN) {
                val sparkle = (Math.sin(animationTime * 10.0 + bug.x.toDouble()) * 0.5 + 0.5).toFloat()
                bugPaint.color = Color.argb((255 * sparkle).toInt(), 255, 255, 255)
                canvas.drawCircle(bug.x + bug.size * 0.3f, bug.y + bug.size * 0.4f, bug.size * 0.08f, bugPaint)
                canvas.drawCircle(bug.x + bug.size * 0.7f, bug.y + bug.size * 0.6f, bug.size * 0.06f, bugPaint)
            }
        }

        bonus?.let { b ->
            val pulse = (Math.sin(animationTime * 5.0) * 10f).toFloat()
            val bonusRadius = b.size / 2 + pulse
            canvas.drawCircle(b.x + b.size / 2, b.y + b.size / 2, bonusRadius, bonusPaint)

            bonusPaint.color = Color.parseColor("#FFA500")
            canvas.drawCircle(b.x + b.size / 2, b.y + b.size / 2, bonusRadius * 0.6f, bonusPaint)
            bonusPaint.color = Color.parseColor("#FFD700")
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val x = event.x
            val y = event.y

            bonus?.let { b ->
                if (b.contains(x, y)) {
                    onBonusCollected?.invoke()
                    bonus = null
                    invalidate()
                    return true
                }
            }

            val hitBug = bugs.firstOrNull { it.contains(x, y) }
            if (hitBug != null) {
                onBugHit?.invoke(hitBug)
                bugs.remove(hitBug)
                invalidate()
            } else {
                onMiss?.invoke()
            }
            return true
        }
        return super.onTouchEvent(event)
    }
}

