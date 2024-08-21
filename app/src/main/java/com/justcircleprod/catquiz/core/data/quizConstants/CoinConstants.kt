package com.justcircleprod.catquiz.core.data.quizConstants

import com.justcircleprod.catquiz.core.data.models.levels.LevelId

object CoinConstants {

    const val INITIAL_COINS_QUANTITY = 200

    private const val HINT_50_50_PRICE_LEVEL_PASSED_QUESTIONS = 30
    private const val HINT_50_50_PRICE_LEVEL_1 = 40
    private const val HINT_50_50_PRICE_LEVEL_2 = 50
    private const val HINT_50_50_PRICE_LEVEL_3 = 60
    private const val HINT_50_50_PRICE_LEVEL_4 = 70
    private const val HINT_50_50_PRICE_LEVEL_5 = 80
    private const val HINT_50_50_PRICE_LEVEL_6 = 90
    private const val HINT_50_50_PRICE_LEVEL_7 = 100

    private const val HINT_CORRECT_ANSWER_PRICE_LEVEL_PASSED_QUESTIONS = 60
    private const val HINT_CORRECT_ANSWER_PRICE_LEVEL_1 = 80
    private const val HINT_CORRECT_ANSWER_PRICE_LEVEL_2 = 100
    private const val HINT_CORRECT_ANSWER_PRICE_LEVEL_3 = 120
    private const val HINT_CORRECT_ANSWER_PRICE_LEVEL_4 = 140
    private const val HINT_CORRECT_ANSWER_PRICE_LEVEL_5 = 160
    private const val HINT_CORRECT_ANSWER_PRICE_LEVEL_6 = 180
    private const val HINT_CORRECT_ANSWER_PRICE_LEVEL_7 = 200

    private const val REWARDED_AD_WORTH_LEVEL_1 = 110
    private const val REWARDED_AD_WORTH_LEVEL_2 = 150
    private const val REWARDED_AD_WORTH_LEVEL_3 = 180
    private const val REWARDED_AD_WORTH_LEVEL_4 = 220
    private const val REWARDED_AD_WORTH_LEVEL_5 = 250
    private const val REWARDED_AD_WORTH_LEVEL_6 = 290
    private const val REWARDED_AD_WORTH_LEVEL_7 = 330

    fun getHint5050Price(levelId: LevelId): Int {
        return when (levelId) {
            LevelId.LEVEL_PASSED_QUESTIONS -> HINT_50_50_PRICE_LEVEL_PASSED_QUESTIONS
            LevelId.LEVEL_1 -> HINT_50_50_PRICE_LEVEL_1
            LevelId.LEVEL_2 -> HINT_50_50_PRICE_LEVEL_2
            LevelId.LEVEL_3 -> HINT_50_50_PRICE_LEVEL_3
            LevelId.LEVEL_4 -> HINT_50_50_PRICE_LEVEL_4
            LevelId.LEVEL_5 -> HINT_50_50_PRICE_LEVEL_5
            LevelId.LEVEL_6 -> HINT_50_50_PRICE_LEVEL_6
            LevelId.LEVEL_7 -> HINT_50_50_PRICE_LEVEL_7
            else -> HINT_50_50_PRICE_LEVEL_PASSED_QUESTIONS
        }
    }

    fun getHintCorrectAnswerPrice(levelId: LevelId): Int {
        return when (levelId) {
            LevelId.LEVEL_PASSED_QUESTIONS -> HINT_CORRECT_ANSWER_PRICE_LEVEL_PASSED_QUESTIONS
            LevelId.LEVEL_1 -> HINT_CORRECT_ANSWER_PRICE_LEVEL_1
            LevelId.LEVEL_2 -> HINT_CORRECT_ANSWER_PRICE_LEVEL_2
            LevelId.LEVEL_3 -> HINT_CORRECT_ANSWER_PRICE_LEVEL_3
            LevelId.LEVEL_4 -> HINT_CORRECT_ANSWER_PRICE_LEVEL_4
            LevelId.LEVEL_5 -> HINT_CORRECT_ANSWER_PRICE_LEVEL_5
            LevelId.LEVEL_6 -> HINT_CORRECT_ANSWER_PRICE_LEVEL_6
            LevelId.LEVEL_7 -> HINT_CORRECT_ANSWER_PRICE_LEVEL_7
            else -> HINT_CORRECT_ANSWER_PRICE_LEVEL_PASSED_QUESTIONS
        }
    }

    fun getRewardedAdWorth(levelId: LevelId): Int {
        return when (levelId) {
            LevelId.LEVEL_1 -> REWARDED_AD_WORTH_LEVEL_1
            LevelId.LEVEL_2 -> REWARDED_AD_WORTH_LEVEL_2
            LevelId.LEVEL_3 -> REWARDED_AD_WORTH_LEVEL_3
            LevelId.LEVEL_4 -> REWARDED_AD_WORTH_LEVEL_4
            LevelId.LEVEL_5 -> REWARDED_AD_WORTH_LEVEL_5
            LevelId.LEVEL_6 -> REWARDED_AD_WORTH_LEVEL_6
            LevelId.LEVEL_7 -> REWARDED_AD_WORTH_LEVEL_7
            else -> REWARDED_AD_WORTH_LEVEL_1
        }
    }
}