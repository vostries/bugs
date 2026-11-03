package com.vostrik.buggame

import java.util.UUID

data class Bonus(
    val id: String = UUID.randomUUID().toString(),
    var x: Float,
    var y: Float,
    val size: Float = 80f
) {
    fun contains(touchX: Float, touchY: Float): Boolean {
        val centerX = x + size / 2
        val centerY = y + size / 2
        val dx = touchX - centerX
        val dy = touchY - centerY
        return dx * dx + dy * dy <= (size / 2) * (size / 2)
    }
}
