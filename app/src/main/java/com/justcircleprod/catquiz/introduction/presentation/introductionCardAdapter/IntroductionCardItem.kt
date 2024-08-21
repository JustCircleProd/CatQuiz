package com.justcircleprod.catquiz.introduction.presentation.introductionCardAdapter

import androidx.annotation.RawRes
import androidx.annotation.StringRes

data class IntroductionCardItem(
    @StringRes val titleStringResId: Int,
    @StringRes val textStringResId: Int,
    @RawRes val animationRawResId: Int
)