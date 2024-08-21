package com.justcircleprod.catquiz.core.domain.repositories

import com.justcircleprod.catquiz.core.data.models.questions.PassedQuestion
import com.justcircleprod.catquiz.core.data.models.questions.Question
import kotlinx.coroutines.flow.Flow

interface PassedQuestionRepository {

    suspend fun getRandomPassedQuestions(): List<Question>

    suspend fun getPassedQuestionsCount(): Int

    fun getPassedQuestionsCountFlow(): Flow<Int>

    suspend fun insertPassedQuestion(passedQuestion: PassedQuestion)

    suspend fun deleteAllPassedQuestions()


}