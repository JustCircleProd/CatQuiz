package com.justcircleprod.catquiz

import android.app.Application
import com.yandex.mobile.ads.common.MobileAds
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App : Application() {

    // to determine the display of ads after each 3 passed the test
    var passedQuizNum = 0
    val passedQuizNumForShowingAd = 2

    override fun onCreate() {
        super.onCreate()

        MobileAds.initialize(this) { }
    }

    // increase passedTestNum
    // if TestNum > passedTestNumForShowingAd then reset to 1
    fun onQuizPassed() {
        passedQuizNum++

        if (passedQuizNum > passedQuizNumForShowingAd) {
            passedQuizNum = 1
        }
    }
}