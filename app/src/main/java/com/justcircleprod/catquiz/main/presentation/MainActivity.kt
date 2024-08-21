package com.justcircleprod.catquiz.main.presentation

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.justcircleprod.catquiz.core.data.dataStore.DataStoreConstants
import com.justcircleprod.catquiz.databinding.ActivityMainBinding
import com.justcircleprod.catquiz.introduction.presentation.IntroductionActivity
import com.justcircleprod.catquiz.levels.presentation.LevelsActivity
import com.justcircleprod.catquiz.settings.presentation.SettingsActivity
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        setIsIntroductionShownObserver()
    }

    private fun setIsIntroductionShownObserver() {
        viewModel.isIntroductionShown.observe(this) { isIntroductionShown ->
            when (isIntroductionShown) {
                DataStoreConstants.INTRODUCTION_IS_SHOWN -> {
                    setOnClickListeners()
                    setContentView(binding.root)

                    viewModel.isIntroductionShown.removeObservers(this)
                }

                null -> {
                    startIntroductionActivity()
                }
            }
        }
    }

    private fun setOnClickListeners() {
        binding.playBtn.setOnClickListener { startLevelsActivity() }
        binding.settingsBtn.setOnClickListener { startSettingActivity() }
    }

    private fun startIntroductionActivity() {
        val intent = Intent(this, IntroductionActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun startLevelsActivity() {
        val intent = Intent(this, LevelsActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun startSettingActivity() {
        val intent = Intent(this, SettingsActivity::class.java)
        startActivity(intent)
        finish()
    }
}