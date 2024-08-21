package com.justcircleprod.catquiz.core.data.repositories

import com.justcircleprod.catquiz.core.data.models.levels.LevelId
import com.justcircleprod.catquiz.core.data.room.AppDatabase
import com.justcircleprod.catquiz.core.domain.repositories.LockedLevelRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class LockedLevelRepositoryImp @Inject constructor(
    private val db: AppDatabase
) : LockedLevelRepository {

    override fun getAll() = db.lockedLevelDao().getAll()

    override suspend fun getMostExpensiveUnlockedLevelId(): LevelId? {
        return withContext(Dispatchers.IO) {
            db.lockedLevelDao().getMostExpensiveUnlockedLevelId()
        }
    }

    override suspend fun unlockLevel(levelId: LevelId) {
        withContext(Dispatchers.IO) {
            db.lockedLevelDao().updateIsOpenedField(levelId, true)
        }
    }

    override suspend fun lockLevel(levelId: LevelId) {
        withContext(Dispatchers.IO) {
            db.lockedLevelDao().updateIsOpenedField(levelId, false)
        }
    }
}