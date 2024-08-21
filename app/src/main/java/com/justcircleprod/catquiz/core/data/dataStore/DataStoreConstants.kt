package com.justcircleprod.catquiz.core.data.dataStore

import androidx.datastore.preferences.core.stringPreferencesKey

object DataStoreConstants {

    val USER_COINS_QUANTITY_KEY = stringPreferencesKey("COINS_QUANTITY")

    val WITHOUT_QUIZ_HINTS_KEY = stringPreferencesKey("WITHOUT_QUIZ_HINTS")
    const val WITH_QUIZ_HINTS = "WITH_QUIZ_HINTS"
    const val WITHOUT_QUIZ_HINTS = "WITHOUT_QUIZ_HINTS"

    val IS_INTRODUCTION_SHOWN_KEY = stringPreferencesKey("IS_INTRODUCTION_SHOWN_KEY")
    const val INTRODUCTION_IS_SHOWN = "INTRODUCTION_IS_SHOWN"
}