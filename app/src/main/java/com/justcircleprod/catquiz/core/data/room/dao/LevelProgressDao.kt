package com.justcircleprod.catquiz.core.data.room.dao

import androidx.room.Dao
import androidx.room.Query
import com.justcircleprod.catquiz.core.data.models.levels.LevelId
import com.justcircleprod.catquiz.core.data.models.levels.LevelProgress
import kotlinx.coroutines.flow.Flow

@Dao
interface LevelProgressDao {

    @Query("SELECT * FROM level_progress")
    fun getAll(): Flow<List<LevelProgress>>

    @Query("SELECT progress FROM level_progress WHERE id = :levelId")
    suspend fun getLevelProgress(levelId: LevelId): Int

    @Query("UPDATE level_progress SET progress = :progress WHERE id = :levelId")
    suspend fun updateProgressField(levelId: LevelId, progress: Int)
}