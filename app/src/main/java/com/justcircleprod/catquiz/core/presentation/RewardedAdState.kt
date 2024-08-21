package com.justcircleprod.catquiz.core.presentation

sealed class RewardedAdState {

    object UserNotAgreedYet : RewardedAdState()
    object Loading : RewardedAdState()
    object FailedToLoad : RewardedAdState()
    object RewardReceived : RewardedAdState()
}
