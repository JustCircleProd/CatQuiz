package com.justcircleprod.catquiz.core.domain.repositories

import kotlinx.coroutines.flow.Flow

interface SettingRepository {

    fun isIntroductionShown(): Flow<String?>

    suspend fun setIntroductionShown()

    fun getWithoutQuizHintsState(): Flow<String?>

    suspend fun editWithoutQuizHintsState(value: String)
}