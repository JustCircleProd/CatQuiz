package com.justcircleprod.catquiz.core.domain.repositories

import com.justcircleprod.catquiz.core.data.models.levels.LevelId
import com.justcircleprod.catquiz.core.data.models.questions.Question

interface QuestionRepository {

    suspend fun getRandomQuestions(levelId: LevelId): List<Question>
}