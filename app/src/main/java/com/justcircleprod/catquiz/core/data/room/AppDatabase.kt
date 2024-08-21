package com.justcircleprod.catquiz.core.data.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.justcircleprod.catquiz.core.data.models.levels.LevelProgress
import com.justcircleprod.catquiz.core.data.models.levels.LockedLevel
import com.justcircleprod.catquiz.core.data.models.questions.PassedQuestion
import com.justcircleprod.catquiz.core.data.room.convertes.Converters
import com.justcircleprod.catquiz.core.data.room.dao.LevelProgressDao
import com.justcircleprod.catquiz.core.data.room.dao.LockedLevelDao
import com.justcircleprod.catquiz.core.data.room.dao.PassedQuestionDao


@Database(
    version = 1,
    entities = [PassedQuestion::class, LockedLevel::class, LevelProgress::class]
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun passedQuestionDao(): PassedQuestionDao
    abstract fun lockedLevelDao(): LockedLevelDao
    abstract fun levelProgressDao(): LevelProgressDao

    companion object {
        fun getInstance(context: Context) =
            Room.databaseBuilder(context, AppDatabase::class.java, "app_database.db")
                .createFromAsset("app_database.db")
                .build()
    }
}