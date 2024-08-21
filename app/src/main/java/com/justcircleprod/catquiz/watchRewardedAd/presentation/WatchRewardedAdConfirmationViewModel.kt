package com.justcircleprod.catquiz.watchRewardedAd.presentation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.justcircleprod.catquiz.core.data.models.levels.LevelId
import com.justcircleprod.catquiz.core.data.quizConstants.CoinConstants
import com.justcircleprod.catquiz.core.domain.repositories.CoinRepository
import com.justcircleprod.catquiz.core.domain.repositories.LockedLevelRepository
import com.justcircleprod.catquiz.core.presentation.RewardedAdState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WatchRewardedAdConfirmationViewModel @Inject constructor(
    private val coinRepository: CoinRepository,
    lockedLevelRepository: LockedLevelRepository
) : ViewModel() {

    var rewardReceived = false
    val rewardedAdState = MutableLiveData<RewardedAdState>(RewardedAdState.UserNotAgreedYet)
    val rewardedAdWorth = MutableLiveData(0)

    init {
        viewModelScope.launch {
            val mostExpensiveUnlockedLevelId =
                lockedLevelRepository.getMostExpensiveUnlockedLevelId() ?: LevelId.LEVEL_1
            rewardedAdWorth.postValue(CoinConstants.getRewardedAdWorth(mostExpensiveUnlockedLevelId))
        }
    }

    fun addReward() {
        viewModelScope.launch {
            val rewardedAdWorthValue = rewardedAdWorth.value

            if (rewardedAdWorthValue != null) {
                coinRepository.addUserCoins(rewardedAdWorthValue)
            }
        }
    }
}