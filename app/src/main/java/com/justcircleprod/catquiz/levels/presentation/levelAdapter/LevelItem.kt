package com.justcircleprod.catquiz.levels.presentation.levelAdapter

import androidx.annotation.StringRes
import com.justcircleprod.catquiz.R
import com.justcircleprod.catquiz.core.data.models.levels.LevelId

// if isOpened == null, then price and progress == 0
// means it is LEVEL_PASSED_QUESTIONS
data class LevelItem(
    val levelId: LevelId,
    @StringRes val nameStringResId: Int,
    val isOpened: Boolean?,
    val price: Int,
    val progress: Int,
    val questionNumber: Int
) {
    companion object {
        // Field values do not matter
        fun getPlaceholders(): List<LevelItem> {
            val levelItemPlaceholders = mutableListOf<LevelItem>()

            levelItemPlaceholders.add(
                LevelItem(
                    levelId = LevelId.LEVEL_PLACEHOLDER,
                    nameStringResId = R.string.loading_levels,
                    isOpened = null,
                    price = 0,
                    progress = 0,
                    questionNumber = 0
                )
            )

            return levelItemPlaceholders.toList()
        }

        fun getNameStringResId(levelId: LevelId): Int {
            return when (levelId) {
                LevelId.LEVEL_PASSED_QUESTIONS -> R.string.passed_questions_level_name
                LevelId.LEVEL_1 -> R.string.level_1_name
                LevelId.LEVEL_2 -> R.string.level_2_name
                LevelId.LEVEL_3 -> R.string.level_3_name
                LevelId.LEVEL_4 -> R.string.level_4_name
                LevelId.LEVEL_5 -> R.string.level_5_name
                LevelId.LEVEL_6 -> R.string.level_6_name
                LevelId.LEVEL_7 -> R.string.level_7_name
                LevelId.LEVEL_PLACEHOLDER -> R.string.loading_levels
            }
        }
    }
}