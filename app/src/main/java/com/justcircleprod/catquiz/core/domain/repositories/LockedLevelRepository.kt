package com.justcircleprod.catquiz.core.domain.repositories

import com.justcircleprod.catquiz.core.data.models.levels.LevelId
import com.justcircleprod.catquiz.core.data.models.levels.LockedLevel
import kotlinx.coroutines.flow.Flow

interface LockedLevelRepository {

    fun getAll(): Flow<List<LockedLevel>>

    suspend fun getMostExpensiveUnlockedLevelId(): LevelId?

    suspend fun unlockLevel(levelId: LevelId)

    suspend fun lockLevel(levelId: LevelId)
}