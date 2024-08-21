package com.justcircleprod.catquiz.core.domain.repositories

import com.justcircleprod.catquiz.core.data.models.levels.LevelId
import com.justcircleprod.catquiz.core.data.models.levels.LevelProgress
import kotlinx.coroutines.flow.Flow

interface LevelProgressRepository {

    fun getAll(): Flow<List<LevelProgress>>

    suspend fun addLevelProgress(levelId: LevelId, progressToAdd: Int)

    suspend fun resetLevelProgress(levelId: LevelId)
}