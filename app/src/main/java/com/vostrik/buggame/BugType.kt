package com.vostrik.buggame

enum class BugType(
    val speedMultiplier: Float,
    val sizeMultiplier: Float,
    val points: Int
) {
    NORMAL(1.0f, 1.0f, 10),
    FAST(2.0f, 0.7f, 20),
    RARE(0.5f, 1.5f, 50)
}
