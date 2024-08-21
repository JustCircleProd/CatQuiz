package com.justcircleprod.catquiz.core.data.quizConstants.level

import com.justcircleprod.catquiz.core.data.models.levels.LevelId

object LevelsQuestionWorth {

    private const val LEVEL_PASSED_QUESTIONS_QUESTION_WORTH = 20
    private const val LEVEL_1_QUESTION_WORTH = 30
    private const val LEVEL_2_QUESTION_WORTH = 40
    private const val LEVEL_3_QUESTION_WORTH = 50
    private const val LEVEL_4_QUESTION_WORTH = 60
    private const val LEVEL_5_QUESTION_WORTH = 70
    private const val LEVEL_6_QUESTION_WORTH = 80
    private const val LEVEL_7_QUESTION_WORTH = 90

    fun getQuestionWorth(levelId: LevelId): Int {
        return when (levelId) {
            LevelId.LEVEL_PASSED_QUESTIONS -> LEVEL_PASSED_QUESTIONS_QUESTION_WORTH
            LevelId.LEVEL_1 -> LEVEL_1_QUESTION_WORTH
            LevelId.LEVEL_2 -> LEVEL_2_QUESTION_WORTH
            LevelId.LEVEL_3 -> LEVEL_3_QUESTION_WORTH
            LevelId.LEVEL_4 -> LEVEL_4_QUESTION_WORTH
            LevelId.LEVEL_5 -> LEVEL_5_QUESTION_WORTH
            LevelId.LEVEL_6 -> LEVEL_6_QUESTION_WORTH
            LevelId.LEVEL_7 -> LEVEL_7_QUESTION_WORTH
            else -> LEVEL_PASSED_QUESTIONS_QUESTION_WORTH
        }
    }
}