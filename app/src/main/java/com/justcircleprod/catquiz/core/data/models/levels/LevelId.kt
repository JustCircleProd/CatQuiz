package com.justcircleprod.catquiz.core.data.models.levels

enum class LevelId(val value: Int) {
    LEVEL_PASSED_QUESTIONS(0),
    LEVEL_1(1),
    LEVEL_2(2),
    LEVEL_3(3),
    LEVEL_4(4),
    LEVEL_5(5),
    LEVEL_6(6),
    LEVEL_7(7),
    LEVEL_PLACEHOLDER(8);

    companion object {
        fun fromInt(value: Int) = values().first { it.value == value }
    }
}