package com.justcircleprod.catquiz.core.data.repositories

import com.justcircleprod.catquiz.core.data.models.levels.LevelId
import com.justcircleprod.catquiz.core.data.room.AppDatabase
import com.justcircleprod.catquiz.core.domain.repositories.LevelProgressRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class LevelProgressRepositoryImpl @Inject constructor(
    private val db: AppDatabase
) : LevelProgressRepository {

    override fun getAll() = db.levelProgressDao().getAll()

    override suspend fun addLevelProgress(levelId: LevelId, progressToAdd: Int) {
        withContext(Dispatchers.IO) {
            val currentProgress = db.levelProgressDao().getLevelProgress(levelId)
            db.levelProgressDao().updateProgressField(levelId, currentProgress + progressToAdd)
        }
    }

    override suspend fun resetLevelProgress(levelId: LevelId) {
        withContext(Dispatchers.IO) {
            db.levelProgressDao().updateProgressField(levelId, 0)
        }
    }
}