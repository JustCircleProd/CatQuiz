package com.justcircleprod.catquiz.core.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.justcircleprod.catquiz.core.data.models.levels.LevelId
import com.justcircleprod.catquiz.core.data.models.questions.PassedQuestion
import kotlinx.coroutines.flow.Flow

@Dao
interface PassedQuestionDao {

    @Query("SELECT * FROM passed_questions WHERE id IN (:ids)")
    suspend fun getByIds(ids: IntArray): List<PassedQuestion>

    @Query("SELECT question_id FROM passed_questions WHERE level_id = :levelId")
    suspend fun getIdsByLevelId(levelId: LevelId): List<Int>

    @Query("SELECT COUNT(id) FROM passed_questions")
    suspend fun getCount(): Int

    @Query("SELECT COUNT(id) FROM passed_questions")
    fun getCountFlow(): Flow<Int>

    @Insert
    suspend fun insert(passedQuestions: PassedQuestion)

    @Query("DELETE FROM passed_questions")
    suspend fun deleteAll(): Int
}