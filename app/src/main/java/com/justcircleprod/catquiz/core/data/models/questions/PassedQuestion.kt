package com.justcircleprod.catquiz.core.data.models.questions

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.justcircleprod.catquiz.core.data.models.levels.LevelId

@Entity(tableName = "passed_questions")
data class PassedQuestion(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "question_id") val questionId: Int,
    @ColumnInfo(name = "level_id") val levelId: LevelId,
) {
    companion object {
        fun Question.toPassedQuestion(levelId: LevelId): PassedQuestion {
            return PassedQuestion(questionId = this.id, levelId = levelId)
        }
    }
}