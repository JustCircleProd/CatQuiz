package com.justcircleprod.catquiz.core.data.repositories

import com.justcircleprod.catquiz.core.data.models.levels.LevelId
import com.justcircleprod.catquiz.core.data.models.questions.Question
import com.justcircleprod.catquiz.core.data.quizConstants.questions.Level1Questions
import com.justcircleprod.catquiz.core.data.quizConstants.questions.Level2Questions
import com.justcircleprod.catquiz.core.data.quizConstants.questions.Level3Questions
import com.justcircleprod.catquiz.core.data.quizConstants.questions.Level4Questions
import com.justcircleprod.catquiz.core.data.quizConstants.questions.Level5Questions
import com.justcircleprod.catquiz.core.data.quizConstants.questions.Level6Questions
import com.justcircleprod.catquiz.core.data.quizConstants.questions.Level7Questions
import com.justcircleprod.catquiz.core.data.room.AppDatabase
import com.justcircleprod.catquiz.core.domain.constants.QuizConstants
import com.justcircleprod.catquiz.core.domain.repositories.QuestionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class QuestionRepositoryImpl @Inject constructor(
    private val db: AppDatabase
) : QuestionRepository {

    override suspend fun getRandomQuestions(levelId: LevelId): List<Question> {
        return withContext(Dispatchers.IO) {
            val levelQuestions = when (levelId) {
                LevelId.LEVEL_1 -> Level1Questions
                LevelId.LEVEL_2 -> Level2Questions
                LevelId.LEVEL_3 -> Level3Questions
                LevelId.LEVEL_4 -> Level4Questions
                LevelId.LEVEL_5 -> Level5Questions
                LevelId.LEVEL_6 -> Level6Questions
                LevelId.LEVEL_7 -> Level7Questions
                LevelId.LEVEL_PASSED_QUESTIONS -> emptyList()
                LevelId.LEVEL_PLACEHOLDER -> emptyList()
            }

            val passedLevelQuestionIds = db.passedQuestionDao().getIdsByLevelId(levelId)

            levelQuestions.filter { it.id !in passedLevelQuestionIds }
                .shuffled()
                .take(QuizConstants.COUNT_OF_QUESTIONS_IN_TEST)
        }
    }
}