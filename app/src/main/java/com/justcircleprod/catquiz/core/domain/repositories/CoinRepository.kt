package com.justcircleprod.catquiz.core.domain.repositories


import kotlinx.coroutines.flow.Flow

interface CoinRepository {

    fun getUserCoinsQuantity(): Flow<String?>

    suspend fun editUserCoinsQuantity(value: Int)

    suspend fun addUserCoins(coinsToAdd: Int)

    suspend fun subtractUserCoins(coinsToSubtract: Int)
}