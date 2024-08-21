package com.justcircleprod.catquiz.core.data.models.levels

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "locked_levels")
data class LockedLevel(
    @PrimaryKey val id: LevelId,
    @ColumnInfo(name = "is_opened") val isOpened: Boolean
)
