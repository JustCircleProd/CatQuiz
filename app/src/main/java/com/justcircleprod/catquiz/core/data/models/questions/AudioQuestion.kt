package com.justcircleprod.catquiz.core.data.models.questions

import androidx.annotation.RawRes
import androidx.annotation.StringRes

data class AudioQuestion(
    override val id: Int,
    @RawRes val audioRawResId: Int,
    @StringRes override var firstOptionStringResId: Int,
    @StringRes override var secondOptionStringResId: Int,
    @StringRes override var thirdOptionStringResId: Int,
    @StringRes override var fourthOptionStringResId: Int,
    override val answerNumStringResId: Int
) : Question