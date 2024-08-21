package com.justcircleprod.catquiz.core.presentation

import android.animation.ObjectAnimator
import android.view.View
import android.view.animation.DecelerateInterpolator
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.progressindicator.LinearProgressIndicator

fun MaterialButton.disableWithTransparency() {
    this.isEnabled = false
    this.alpha = 0.4f
}

fun MaterialButton.enable() {
    this.isEnabled = true
    this.alpha = 1f
}

fun MaterialCardView.disableWithTransparency() {
    this.isEnabled = false
    this.alpha = 0.4f
}

fun MaterialCardView.enable() {
    this.isEnabled = true
    this.alpha = 1f
}

fun LinearProgressIndicator.animateProgress(currentProgress: Int, newProgress: Int) {
    ObjectAnimator.ofInt(
        this,
        "progress",
        currentProgress,
        newProgress
    ).apply {
        interpolator = DecelerateInterpolator()
    }.start()
}

fun View.hideWithAnimation(onComplete: () -> Unit) {
    this.animate()
        .alpha(0f)
        .withEndAction {
            this.visibility = View.GONE
            onComplete()
        }
        .start()
}