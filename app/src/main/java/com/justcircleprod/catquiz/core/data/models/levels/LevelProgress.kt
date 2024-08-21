package com.justcircleprod.catquiz.core.data.models.levels

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "level_progress")
data class LevelProgress(
    @PrimaryKey val id: LevelId,
    @ColumnInfo(name = "progress") val progress: Int
)
