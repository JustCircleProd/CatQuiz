package com.justcircleprod.catquiz.levels.presentation.levelAdapter

import com.airbnb.lottie.LottieAnimationView
import com.justcircleprod.catquiz.core.data.models.levels.LevelId

interface LevelItemAdapterActions {

    fun tryToUnlockLevel(
        levelId: LevelId,
        levelPrice: Int,
        confettiAnimationView: LottieAnimationView
    )

    fun startQuizActivity(levelId: LevelId)
}