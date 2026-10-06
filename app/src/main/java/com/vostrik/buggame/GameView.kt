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
    private val bugPaint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.FILL
    }

    var onBugHit: ((Bug) -> Unit)? = null
    var onMiss: (() -> Unit)? = null

    fun setBugs(newBugs: List<Bug>) {
        bugs.clear()
        bugs.addAll(newBugs)
        invalidate()
    }

    fun updateBugs(deltaTime: Float) {
        bugs.forEach { bug ->
            bug.update(deltaTime, width, height)
        }
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        bugs.forEach { bug ->
            bugPaint.color = when (bug.type) {
                BugType.NORMAL -> Color.parseColor("#8B4513")
                BugType.FAST -> Color.parseColor("#FF4500")
                BugType.RARE -> Color.parseColor("#FFD700")
            }

            // тело жука
            canvas.drawOval(
                bug.x,
                bug.y,
                bug.x + bug.size,
                bug.y + bug.size,
                bugPaint
            )

            // голова
            val headSize = bug.size * 0.3f
            canvas.drawCircle(
                bug.x + bug.size / 2,
                bug.y,
                headSize,
                bugPaint
            )
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val x = event.x
            val y = event.y

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
