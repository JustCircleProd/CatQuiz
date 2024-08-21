package com.justcircleprod.catquiz.core.data.room.convertes

import androidx.room.TypeConverter
import com.justcircleprod.catquiz.core.data.models.levels.LevelId

class Converters {

    @TypeConverter
    fun toLevelId(value: Int) = LevelId.fromInt(value)

    @TypeConverter
    fun fromLevelId(levelId: LevelId) = levelId.value
}