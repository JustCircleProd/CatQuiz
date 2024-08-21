package com.justcircleprod.catquiz.core.data.repositories

import com.justcircleprod.catquiz.core.data.dataStore.DataStoreConstants
import com.justcircleprod.catquiz.core.data.dataStore.DataStoreManager
import com.justcircleprod.catquiz.core.domain.repositories.SettingRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SettingRepositoryImpl @Inject constructor(
    private val dataStoreManager: DataStoreManager
) : SettingRepository {

    override fun isIntroductionShown() =
        dataStoreManager.get(DataStoreConstants.IS_INTRODUCTION_SHOWN_KEY)

    override suspend fun setIntroductionShown() {
        withContext(Dispatchers.IO) {
            dataStoreManager.save(
                DataStoreConstants.IS_INTRODUCTION_SHOWN_KEY,
                DataStoreConstants.INTRODUCTION_IS_SHOWN
            )
        }
    }

    override fun getWithoutQuizHintsState() =
        dataStoreManager.get(DataStoreConstants.WITHOUT_QUIZ_HINTS_KEY)

    override suspend fun editWithoutQuizHintsState(value: String) {
        withContext(Dispatchers.IO) {
            dataStoreManager.save(
                DataStoreConstants.WITHOUT_QUIZ_HINTS_KEY,
                value
            )
        }
    }
}