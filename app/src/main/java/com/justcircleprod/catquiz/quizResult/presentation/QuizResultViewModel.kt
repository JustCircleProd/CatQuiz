package com.justcircleprod.catquiz.quizResult.presentation

import androidx.annotation.ArrayRes
import androidx.annotation.RawRes
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.justcircleprod.catquiz.core.domain.repositories.CoinRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuizResultViewModel @Inject constructor(
    private val coinRepository: CoinRepository,
    state: SavedStateHandle
) : ViewModel() {

    val isBannerAdLoading = MutableStateFlow(true)
    val isInterstitialAdLoading = MutableStateFlow(true)

    private val areCoinsCalculating = MutableStateFlow(true)

    val isLoading =
        combine(
            areCoinsCalculating,
            isBannerAdLoading,
            isInterstitialAdLoading
        ) { areCoinsCalculating, isBannerAdLoading, isInterstitialAdLoading ->
            areCoinsCalculating || isBannerAdLoading || isInterstitialAdLoading
        }.asLiveData()

    // to show loading layout only when the activity is first created (not recreated)
    var isFirstLoadResultShown = false

    val earnedCoins =
        MutableLiveData(state.get<Int>(QuizResultActivity.EARNED_COINS_ARGUMENT_NAME)!!)

    val areEarnedCoinsDoubled = MutableLiveData(false)

    var isCongratulationViewsShown = false

    @ArrayRes
    var shownCongratulationTextArrayResId: Int? = null
    var shownCongratulationTextArrayIndex: Int? = null

    @RawRes
    var shownCongratulationAnimationRawResId: Int? = null

    init {
        viewModelScope.launch {
            coinRepository.addUserCoins(earnedCoins.value!!)
            areCoinsCalculating.value = false
        }
    }
}