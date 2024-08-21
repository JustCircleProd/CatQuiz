package com.justcircleprod.catquiz.resetProgress.presentation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.justcircleprod.catquiz.core.data.models.levels.LevelId
import com.justcircleprod.catquiz.core.data.quizConstants.CoinConstants
import com.justcircleprod.catquiz.core.domain.repositories.CoinRepository
import com.justcircleprod.catquiz.core.domain.repositories.LevelProgressRepository
import com.justcircleprod.catquiz.core.domain.repositories.LockedLevelRepository
import com.justcircleprod.catquiz.core.domain.repositories.PassedQuestionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ResetProgressConfirmationViewModel @Inject constructor(
    private val coinRepository: CoinRepository,
    private val passedQuestionRepository: PassedQuestionRepository,
    private val lockedLevelRepository: LockedLevelRepository,
    private val levelProgressRepository: LevelProgressRepository
) : ViewModel() {

    val isProgressReset = MutableLiveData(false)

    fun resetProgress() {
        viewModelScope.launch {
            coinRepository.editUserCoinsQuantity(CoinConstants.INITIAL_COINS_QUANTITY)

            passedQuestionRepository.deleteAllPassedQuestions()

            lockedLevelRepository.lockLevel(LevelId.LEVEL_2)
            lockedLevelRepository.lockLevel(LevelId.LEVEL_3)
            lockedLevelRepository.lockLevel(LevelId.LEVEL_4)
            lockedLevelRepository.lockLevel(LevelId.LEVEL_5)
            lockedLevelRepository.lockLevel(LevelId.LEVEL_6)
            lockedLevelRepository.lockLevel(LevelId.LEVEL_7)

            levelProgressRepository.resetLevelProgress(LevelId.LEVEL_1)
            levelProgressRepository.resetLevelProgress(LevelId.LEVEL_2)
            levelProgressRepository.resetLevelProgress(LevelId.LEVEL_3)
            levelProgressRepository.resetLevelProgress(LevelId.LEVEL_4)
            levelProgressRepository.resetLevelProgress(LevelId.LEVEL_5)
            levelProgressRepository.resetLevelProgress(LevelId.LEVEL_6)
            levelProgressRepository.resetLevelProgress(LevelId.LEVEL_7)

            isProgressReset.postValue(true)
        }
    }
}