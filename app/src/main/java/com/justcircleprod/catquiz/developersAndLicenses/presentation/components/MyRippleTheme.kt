package com.justcircleprod.catquiz.developersAndLicenses.presentation.components

import androidx.compose.material.ripple.RippleAlpha
import androidx.compose.material.ripple.RippleTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

data class MyRippleTheme(private val color: Color) : RippleTheme {

    @Composable
    override fun defaultColor(): Color = color

    @Composable
    override fun rippleAlpha(): RippleAlpha = RippleAlpha(
        focusedAlpha = 0.6f,
        draggedAlpha = 0.6f,
        hoveredAlpha = 0.6f,
        pressedAlpha = 0.6f
    )
}