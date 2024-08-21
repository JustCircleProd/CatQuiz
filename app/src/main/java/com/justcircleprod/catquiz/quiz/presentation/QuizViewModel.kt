package com.justcircleprod.catquiz.quiz.presentation

import androidx.annotation.StringRes
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.justcircleprod.catquiz.core.data.models.levels.LevelId
import com.justcircleprod.catquiz.core.data.models.questions.AudioQuestion
import com.justcircleprod.catquiz.core.data.models.questions.ImageQuestion
import com.justcircleprod.catquiz.core.data.models.questions.PassedQuestion
import com.justcircleprod.catquiz.core.data.models.questions.PassedQuestion.Companion.toPassedQuestion
import com.justcircleprod.catquiz.core.data.models.questions.Question
import com.justcircleprod.catquiz.core.data.models.questions.TextQuestion
import com.justcircleprod.catquiz.core.data.models.questions.VideoQuestion
import com.justcircleprod.catquiz.core.data.quizConstants.CoinConstants
import com.justcircleprod.catquiz.core.data.quizConstants.level.LevelsQuestionWorth
import com.justcircleprod.catquiz.core.domain.repositories.CoinRepository
import com.justcircleprod.catquiz.core.domain.repositories.LevelProgressRepository
import com.justcircleprod.catquiz.core.domain.repositories.PassedQuestionRepository
import com.justcircleprod.catquiz.core.domain.repositories.QuestionRepository
import com.justcircleprod.catquiz.core.domain.repositories.SettingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class QuizViewModel @Inject constructor(
    private val coinRepository: CoinRepository,
    settingRepository: SettingRepository,
    private val passedQuestionRepository: PassedQuestionRepository,
    private val questionRepository: QuestionRepository,
    private val levelProgressRepository: LevelProgressRepository,
    state: SavedStateHandle
) : ViewModel() {

    val isLoading = MutableLiveData(true)

    // to show loading layout only when the activity is first created (not recreated)
    var isFirstLoadResultShown = false

    val userCoinsQuantity = coinRepository.getUserCoinsQuantity().asLiveData()

    val levelId = LevelId.fromInt(state.get<Int>(QuizActivity.LEVEL_ARGUMENT_NAME)!!)

    val questionWorth = MutableLiveData(LevelsQuestionWorth.getQuestionWorth(levelId))

    val hint5050Price = CoinConstants.getHint5050Price(levelId)

    val hintCorrectAnswerPrice = CoinConstants.getHintCorrectAnswerPrice(levelId)

    var earnedCoins = 0
    var correctlyAnsweredQuestionsCount = 0

    private var questions = mutableListOf<Question>()
    var questionsCount = 0

    var questionPosition = -1

    // the current question or a sign of the end of questions is placed here
    val question = MutableLiveData<Question?>(null)

    @StringRes
    var correctAnswerStringResId = 0

    val withoutQuizHints = settingRepository.getWithoutQuizHintsState().asLiveData()

    // for current question
    val hint5050UsedWithOptionsToShow =
        MutableLiveData<Pair<Boolean, List<@StringRes Int>>>(Pair(false, listOf()))
    val hintCorrectAnswerUsed = MutableLiveData(false)

    // to save a question that has been answered,
    // but the question has not yet had time to change to the next
    // (helps to avoid cheating when changing the orientation of the phone)
    var questionForWhichAnswerWasShown: Question? = null

    private val passedQuestions = mutableListOf<PassedQuestion>()

    init {
        viewModelScope.launch(Dispatchers.Default) {
            when (levelId) {
                LevelId.LEVEL_PASSED_QUESTIONS -> {
                    questions.addAll(passedQuestionRepository.getRandomPassedQuestions())
                }

                else -> {
                    questions.addAll(questionRepository.getRandomQuestions(levelId))
                }
            }

            questions = questions.shuffled() as MutableList<Question>
            questionsCount = questions.size
            isLoading.postValue(false)
        }
    }

    private fun setCorrectAnswer(question: Question) {
        correctAnswerStringResId = listOf(
            question.firstOptionStringResId,
            question.secondOptionStringResId,
            question.thirdOptionStringResId,
            question.fourthOptionStringResId,
        )[question.answerNumStringResId - 1]
    }

    private fun getQuestionWithShuffledOptions(question: Question): Question {
        val options = listOf(
            question.firstOptionStringResId,
            question.secondOptionStringResId,
            question.thirdOptionStringResId,
            question.fourthOptionStringResId,
        ).shuffled()

        return when (question) {
            is TextQuestion -> {
                question.copy(
                    firstOptionStringResId = options[0],
                    secondOptionStringResId = options[1],
                    thirdOptionStringResId = options[2],
                    fourthOptionStringResId = options[3]
                )
            }

            is ImageQuestion -> {
                question.copy(
                    firstOptionStringResId = options[0],
                    secondOptionStringResId = options[1],
                    thirdOptionStringResId = options[2],
                    fourthOptionStringResId = options[3]
                )
            }

            is AudioQuestion -> {
                question.copy(
                    firstOptionStringResId = options[0],
                    secondOptionStringResId = options[1],
                    thirdOptionStringResId = options[2],
                    fourthOptionStringResId = options[3]
                )
            }

            is VideoQuestion -> {
                question.copy(
                    firstOptionStringResId = options[0],
                    secondOptionStringResId = options[1],
                    thirdOptionStringResId = options[2],
                    fourthOptionStringResId = options[3]
                )
            }

            else -> question
        }
    }

    fun setQuestionOnCurrentPosition() {
        if (questionPosition < questions.size) {
            // If this is the first call, shuffle the question options,
            // otherwise they are already shuffled
            if (questionPosition == -1) {
                questionPosition = 0
                question.value = getQuestionWithShuffledOptions(questions[questionPosition])
            }

            setCorrectAnswer(questions[questionPosition])
        } else {
            question.value = null
        }
    }

    fun setQuestionOnNextPosition() {
        viewModelScope.launch {
            questionPosition++
            hint5050UsedWithOptionsToShow.postValue(Pair(false, listOf()))
            hintCorrectAnswerUsed.postValue(false)

            if (questionPosition < questions.size) {
                setCorrectAnswer(questions[questionPosition])
                question.postValue(getQuestionWithShuffledOptions(questions[questionPosition]))
            } else {
                savePassedQuestions()
                question.postValue(null)
            }
        }
    }

    fun onCorrectAnswer() {
        if (levelId != LevelId.LEVEL_PASSED_QUESTIONS) {
            val passedQuestion = question.value!!.toPassedQuestion(levelId)
            passedQuestions.add(passedQuestion)
        }

        earnedCoins += questionWorth.value!!
        correctlyAnsweredQuestionsCount++
    }

    fun useHint5050() {
        viewModelScope.launch {
            val questionValue = question.value ?: return@launch

            var optionStringResIdsToShow = mutableListOf(
                questionValue.firstOptionStringResId,
                questionValue.secondOptionStringResId,
                questionValue.thirdOptionStringResId,
                questionValue.fourthOptionStringResId
            )
            optionStringResIdsToShow.remove(correctAnswerStringResId)
            optionStringResIdsToShow =
                optionStringResIdsToShow.shuffled().toMutableList().take(2).toMutableList()

            coinRepository.subtractUserCoins(hint5050Price)
            hint5050UsedWithOptionsToShow.postValue(Pair(true, optionStringResIdsToShow))
        }
    }

    fun useHintCorrectAnswer() {
        viewModelScope.launch {
            coinRepository.subtractUserCoins(hintCorrectAnswerPrice)
            hintCorrectAnswerUsed.postValue(true)
        }
    }

    private suspend fun savePassedQuestions() {
        if (passedQuestions.size == 0) return

        passedQuestions.forEach {
            withContext(Dispatchers.IO) {
                passedQuestionRepository.insertPassedQuestion(it)
            }
        }

        withContext(Dispatchers.IO) {
            levelProgressRepository.addLevelProgress(levelId, passedQuestions.size)
        }
    }

    fun setQuestionForWhichAnswerWasShown() {
        questionForWhichAnswerWasShown = question.value!!
    }

    fun clearQuestionForWhichAnswerWasShown() {
        questionForWhichAnswerWasShown = null
    }
}