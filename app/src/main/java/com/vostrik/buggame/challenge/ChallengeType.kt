package com.vostrik.buggame.challenge

enum class ChallengeType(
    val titleTemplate: String,
    val goalRange: IntRange,
    val xpRange: IntRange,
    val cumulative: Boolean
) {
    GOLDEN_BUGS(
        "Убить %d золотых тараканов",
        2..4,
        400..600,
        cumulative = true
    ),
    FAST_BUGS(
        "Убить %d быстрых тараканов",
        5..10,
        200..350,
        cumulative = true
    ),
    RARE_BUGS(
        "Убить %d редких тараканов",
        3..6,
        250..400,
        cumulative = true
    ),
    BONUSES(
        "Собрать %d бонусов",
        2..3,
        300..500,
        cumulative = true
    ),
    HITS(
        "Попасть по тараканам %d раз",
        30..60,
        150..300,
        cumulative = true
    ),
    HIGH_SCORE(
        "Набрать %d очков за раунд",
        300..800,
        300..500,
        cumulative = false
    ),
    ACCURACY(
        "Достичь точности %d%% за раунд",
        70..90,
        250..400,
        cumulative = false
    )
}
