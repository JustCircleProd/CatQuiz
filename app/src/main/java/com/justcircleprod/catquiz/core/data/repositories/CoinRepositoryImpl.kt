package com.justcircleprod.catquiz.core.data.repositories

import com.justcircleprod.catquiz.core.data.dataStore.DataStoreConstants
import com.justcircleprod.catquiz.core.data.dataStore.DataStoreManager
import com.justcircleprod.catquiz.core.domain.repositories.CoinRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CoinRepositoryImpl @Inject constructor(
    private val dataStoreManager: DataStoreManager
) : CoinRepository {

    override fun getUserCoinsQuantity(): Flow<String?> =
        dataStoreManager.get(DataStoreConstants.USER_COINS_QUANTITY_KEY)

    override suspend fun editUserCoinsQuantity(value: Int) {
        withContext(Dispatchers.IO) {
            dataStoreManager.save(
                DataStoreConstants.USER_COINS_QUANTITY_KEY,
                value.toString()
            )
        }
    }

    override suspend fun addUserCoins(coinsToAdd: Int) {
        withContext(Dispatchers.IO) {
            val currentCoins = getUserCoinsQuantity().first()?.toInt() ?: return@withContext
            editUserCoinsQuantity(currentCoins + coinsToAdd)
        }
    }

    override suspend fun subtractUserCoins(coinsToSubtract: Int) {
        withContext(Dispatchers.IO) {
            val currentCoins = getUserCoinsQuantity().first()?.toInt() ?: return@withContext
            editUserCoinsQuantity(currentCoins - coinsToSubtract)
        }
    }
}