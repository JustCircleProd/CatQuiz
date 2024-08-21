package com.justcircleprod.catquiz.levels.presentation

import androidx.core.text.isDigitsOnly
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.justcircleprod.catquiz.core.data.models.levels.LevelId
import com.justcircleprod.catquiz.core.data.quizConstants.CoinConstants
import com.justcircleprod.catquiz.core.data.quizConstants.level.LevelPrices
import com.justcircleprod.catquiz.core.data.quizConstants.questions.Level1Questions
import com.justcircleprod.catquiz.core.data.quizConstants.questions.Level2Questions
import com.justcircleprod.catquiz.core.data.quizConstants.questions.Level3Questions
import com.justcircleprod.catquiz.core.data.quizConstants.questions.Level4Questions
import com.justcircleprod.catquiz.core.data.quizConstants.questions.Level5Questions
import com.justcircleprod.catquiz.core.data.quizConstants.questions.Level6Questions
import com.justcircleprod.catquiz.core.data.quizConstants.questions.Level7Questions
import com.justcircleprod.catquiz.core.domain.repositories.CoinRepository
import com.justcircleprod.catquiz.core.domain.repositories.LevelProgressRepository
import com.justcircleprod.catquiz.core.domain.repositories.LockedLevelRepository
import com.justcircleprod.catquiz.core.domain.repositories.PassedQuestionRepository
import com.justcircleprod.catquiz.levels.presentation.levelAdapter.LevelItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LevelsViewModel @Inject constructor(
    private val coinRepository: CoinRepository,
    passedQuestionRepository: PassedQuestionRepository,
    levelProgressRepository: LevelProgressRepository,
    private val lockedLevelRepository: LockedLevelRepository
) : ViewModel() {

    private val userCoinsQuantity = coinRepository.getUserCoinsQuantity()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), "NOT_INITIALIZED")

    val userCoinsQuantityLiveData = coinRepository.getUserCoinsQuantity().asLiveData()

    private val passedQuestionsCount = passedQuestionRepository.getPassedQuestionsCountFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)

    private val lockedLevels = lockedLevelRepository.getAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), listOf())

    private val levelProgress = levelProgressRepository.getAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), listOf())

    // conversion to LevelItem for RecyclerView in presentation
    val levelItems = combine(
        lockedLevels,
        levelProgress,
        passedQuestionsCount
    ) { lockedLevels, levelProgress, passedQuestionsCount ->

        if (lockedLevels.isEmpty() || levelProgress.isEmpty() || passedQuestionsCount == null) {
            return@combine LevelItem.getPlaceholders()
        }

        val tempLevelItems = mutableListOf<LevelItem>()

        if (passedQuestionsCount != 0) {
            tempLevelItems.add(
                LevelItem(
                    levelId = LevelId.LEVEL_PASSED_QUESTIONS,
                    nameStringResId = LevelItem.getNameStringResId(LevelId.LEVEL_PASSED_QUESTIONS),
                    isOpened = null,
                    price = 0,
                    progress = 0,
                    questionNumber = passedQuestionsCount
                )
            )
        }

        for (levelProgressItem in levelProgress) {
            val levelId = levelProgressItem.id
            val lockedLevel = lockedLevels.firstOrNull { levelProgressItem.id == it.id }

            tempLevelItems.add(
                LevelItem(
                    levelId = levelId,
                    nameStringResId = LevelItem.getNameStringResId(levelId),
                    isOpened = lockedLevel?.isOpened ?: true,
                    price = getLevelPrice(levelId),
                    progress = levelProgressItem.progress,
                    questionNumber = getLevelQuestionNumber(levelId)
                )
            )
        }

        tempLevelItems.toList()
    }.asLiveData()

    // set initial coins quantity
    init {
        viewModelScope.launch {
            userCoinsQuantity.collect { coinsQuantity ->
                if (coinsQuantity?.isDigitsOnly() == false || coinsQuantity != null) return@collect

                coinRepository.editUserCoinsQuantity(CoinConstants.INITIAL_COINS_QUANTITY)
                cancel()
            }
        }
    }

    private fun getLevelPrice(levelId: LevelId) =
        when (levelId) {
            LevelId.LEVEL_PASSED_QUESTIONS -> 0
            LevelId.LEVEL_1 -> 0
            LevelId.LEVEL_2 -> LevelPrices.LEVEL_2_PRICE
            LevelId.LEVEL_3 -> LevelPrices.LEVEL_3_PRICE
            LevelId.LEVEL_4 -> LevelPrices.LEVEL_4_PRICE
            LevelId.LEVEL_5 -> LevelPrices.LEVEL_5_PRICE
            LevelId.LEVEL_6 -> LevelPrices.LEVEL_6_PRICE
            LevelId.LEVEL_7 -> LevelPrices.LEVEL_7_PRICE
            LevelId.LEVEL_PLACEHOLDER -> 0
        }

    private fun getLevelQuestionNumber(levelId: LevelId) =
        when (levelId) {
            LevelId.LEVEL_PASSED_QUESTIONS -> 0
            LevelId.LEVEL_1 -> Level1Questions.size
            LevelId.LEVEL_2 -> Level2Questions.size
            LevelId.LEVEL_3 -> Level3Questions.size
            LevelId.LEVEL_4 -> Level4Questions.size
            LevelId.LEVEL_5 -> Level5Questions.size
            LevelId.LEVEL_6 -> Level6Questions.size
            LevelId.LEVEL_7 -> Level7Questions.size
            LevelId.LEVEL_PLACEHOLDER -> 0
        }

    fun unlockLevel(levelId: LevelId, levelPrice: Int) {
        viewModelScope.launch {
            coinRepository.subtractUserCoins(levelPrice)
            lockedLevelRepository.unlockLevel(levelId)
        }
    }
}