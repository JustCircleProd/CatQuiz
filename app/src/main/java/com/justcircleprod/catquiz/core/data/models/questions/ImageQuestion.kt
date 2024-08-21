package com.justcircleprod.catquiz.core.data.models.questions

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class ImageQuestion(
    override val id: Int,
    @DrawableRes val imageDrawableResId: Int,
    @StringRes override var firstOptionStringResId: Int,
    @StringRes override var secondOptionStringResId: Int,
    @StringRes override var thirdOptionStringResId: Int,
    @StringRes override var fourthOptionStringResId: Int,
    override val answerNumStringResId: Int
) : Question