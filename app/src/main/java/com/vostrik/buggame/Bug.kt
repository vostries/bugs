package com.vostrik.buggame

import java.util.UUID

data class Bug(
    val id: String = UUID.randomUUID().toString(),
    var x: Float,
    var y: Float,
    var speedX: Float,
    var speedY: Float,
    val size: Float,
    val type: BugType,
    val points: Int = type.points
) {
    fun update(deltaTime: Float, screenWidth: Int, screenHeight: Int, gravityX: Float = 0f, gravityY: Float = 0f) {
        x += (speedX + gravityX) * deltaTime
        y += (speedY + gravityY) * deltaTime

        val maxX = (screenWidth - size).coerceAtLeast(0f)
        val maxY = (screenHeight - size).coerceAtLeast(0f)
        if (x <= 0f || x >= maxX) speedX = -speedX
        if (y <= 0f || y >= maxY) speedY = -speedY

        x = x.coerceIn(0f, maxX)
        y = y.coerceIn(0f, maxY)
    }

    fun contains(touchX: Float, touchY: Float): Boolean {
        val centerX = x + size / 2
        val centerY = y + size / 2
        val dx = touchX - centerX
        val dy = touchY - centerY
        return dx * dx + dy * dy <= (size / 2) * (size / 2)
    }
}
