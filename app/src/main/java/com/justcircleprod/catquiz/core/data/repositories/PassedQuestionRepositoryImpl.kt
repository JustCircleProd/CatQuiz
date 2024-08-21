package com.justcircleprod.catquiz.core.data.repositories

import com.justcircleprod.catquiz.core.data.models.levels.LevelId
import com.justcircleprod.catquiz.core.data.models.questions.PassedQuestion
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
import com.justcircleprod.catquiz.core.domain.repositories.PassedQuestionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PassedQuestionRepositoryImpl @Inject constructor(
    private val db: AppDatabase
) : PassedQuestionRepository {

    override suspend fun getRandomPassedQuestions(): List<Question> {
        return withContext(Dispatchers.IO) {
            val count = db.passedQuestionDao().getCount()
            val ids =
                (1..count).shuffled().take(QuizConstants.COUNT_OF_QUESTIONS_IN_TEST).toIntArray()

            val passedQuestions = db.passedQuestionDao().getByIds(ids)
            val questions: MutableList<Question> = mutableListOf()

            passedQuestions.forEach { passedQuestion ->
                when (passedQuestion.levelId) {

                    LevelId.LEVEL_1 -> {
                        questions.addAll(Level1Questions.filter { it.id == passedQuestion.questionId })
                    }

                    LevelId.LEVEL_2 -> {
                        questions.addAll(Level2Questions.filter { it.id == passedQuestion.questionId })
                    }

                    LevelId.LEVEL_3 -> {
                        questions.addAll(Level3Questions.filter { it.id == passedQuestion.questionId })
                    }

                    LevelId.LEVEL_4 -> {
                        questions.addAll(Level4Questions.filter { it.id == passedQuestion.questionId })
                    }

                    LevelId.LEVEL_5 -> {
                        questions.addAll(Level5Questions.filter { it.id == passedQuestion.questionId })
                    }

                    LevelId.LEVEL_6 -> {
                        questions.addAll(Level6Questions.filter { it.id == passedQuestion.questionId })
                    }

                    LevelId.LEVEL_7 -> {
                        questions.addAll(Level7Questions.filter { it.id == passedQuestion.questionId })
                    }

                    LevelId.LEVEL_PASSED_QUESTIONS -> {}

                    LevelId.LEVEL_PLACEHOLDER -> {}
                }
            }

            questions.toList()
        }
    }

    override suspend fun getPassedQuestionsCount(): Int {
        return withContext(Dispatchers.IO) {
            db.passedQuestionDao().getCount()
        }
    }

    override fun getPassedQuestionsCountFlow() =
        db.passedQuestionDao().getCountFlow()

    override suspend fun insertPassedQuestion(passedQuestion: PassedQuestion) {
        withContext(Dispatchers.IO) {
            db.passedQuestionDao().insert(passedQuestion)
        }
    }

    override suspend fun deleteAllPassedQuestions() {
        withContext(Dispatchers.IO) {
            db.passedQuestionDao().deleteAll()
        }
    }
}