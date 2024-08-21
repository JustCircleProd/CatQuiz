package com.justcircleprod.catquiz.core.data.room.dao

import androidx.room.Dao
import androidx.room.Query
import com.justcircleprod.catquiz.core.data.models.levels.LevelId
import com.justcircleprod.catquiz.core.data.models.levels.LockedLevel
import kotlinx.coroutines.flow.Flow

@Dao
interface LockedLevelDao {

    @Query("SELECT * FROM locked_levels")
    fun getAll(): Flow<List<LockedLevel>>

    @Query("SELECT id FROM locked_levels WHERE is_opened = 1 ORDER BY id DESC LIMIT 1")
    suspend fun getMostExpensiveUnlockedLevelId(): LevelId?

    @Query("UPDATE locked_levels SET is_opened = :value WHERE id = :levelId")
    suspend fun updateIsOpenedField(levelId: LevelId, value: Boolean)
}