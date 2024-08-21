package com.justcircleprod.catquiz.doubleCoins.presentation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.justcircleprod.catquiz.core.domain.repositories.CoinRepository
import com.justcircleprod.catquiz.core.presentation.RewardedAdState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DoubleCoinsConfirmationViewModel @Inject constructor(
    private val coinRepository: CoinRepository,
    state: SavedStateHandle
) : ViewModel() {

    var rewardReceived = false
    val rewardedAdState = MutableLiveData<RewardedAdState>(RewardedAdState.UserNotAgreedYet)

    val earnedCoins = state.get<Int>(DoubleCoinsConfirmationDialog.EARNED_COINS_NAME_ARGUMENT)!!

    fun doubleEarnedCoins() {
        viewModelScope.launch {
            coinRepository.addUserCoins(earnedCoins)
        }
    }
}