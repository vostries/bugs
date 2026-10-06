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
    fun update(deltaTime: Float, screenWidth: Int, screenHeight: Int) {
        x += speedX * deltaTime
        y += speedY * deltaTime

        // отскок от границ
        if (x <= 0 || x >= screenWidth - size) speedX = -speedX
        if (y <= 0 || y >= screenHeight - size) speedY = -speedY

        x = x.coerceIn(0f, (screenWidth - size).toFloat())
        y = y.coerceIn(0f, (screenHeight - size).toFloat())
    }

    fun contains(touchX: Float, touchY: Float): Boolean {
        val centerX = x + size / 2
        val centerY = y + size / 2
        val dx = touchX - centerX
        val dy = touchY - centerY
        return dx * dx + dy * dy <= (size / 2) * (size / 2)
    }
}
