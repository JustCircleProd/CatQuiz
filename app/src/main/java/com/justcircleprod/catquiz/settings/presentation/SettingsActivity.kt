package com.justcircleprod.catquiz.settings.presentation

import android.content.Intent
import android.os.Bundle
import androidx.activity.addCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory
import com.justcircleprod.catquiz.R
import com.justcircleprod.catquiz.core.data.dataStore.DataStoreConstants
import com.justcircleprod.catquiz.databinding.ActivitySettingsBinding
import com.justcircleprod.catquiz.developersAndLicenses.presentation.DevelopersAndLicensesActivity
import com.justcircleprod.catquiz.main.presentation.MainActivity
import com.justcircleprod.catquiz.resetProgress.presentation.ResetProgressConfirmationDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private val viewModel: SettingsViewModel by viewModels()

    private lateinit var reviewManager: ReviewManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)

        onBackPressedDispatcher.addCallback { startMainActivity() }
        binding.backBtn.setOnClickListener { onBackPressedDispatcher.onBackPressed() }

        setWithoutQuizHintsObserver()
        setOnResetProgressClickListener()

        setOnShareAppBtnClickListener()
        setOnRateAppBtnClickListener()

        setOnDevelopersAndLicensesBtnClickListener()

        setContentView(binding.root)
    }

    private fun setWithoutQuizHintsObserver() {
        viewModel.withoutQuizHints.observe(this) {
            binding.withoutQuizHintsSwitch.setOnCheckedChangeListener { _, _ -> }

            when (it) {
                DataStoreConstants.WITHOUT_QUIZ_HINTS -> {
                    binding.withoutQuizHintsSwitch.isChecked = true
                }

                DataStoreConstants.WITH_QUIZ_HINTS, null -> {
                    binding.withoutQuizHintsSwitch.isChecked = false
                }
            }

            setOnWithoutQuizHintsSwitchCheckedChangeListener()
        }
    }

    private fun setOnWithoutQuizHintsSwitchCheckedChangeListener() {
        binding.withoutQuizHintsSwitch.setOnCheckedChangeListener { _, isChecked ->
            viewModel.updateWithoutQuizHintsState(
                if (isChecked) {
                    DataStoreConstants.WITHOUT_QUIZ_HINTS
                } else {
                    DataStoreConstants.WITH_QUIZ_HINTS
                }
            )
        }
    }

    private fun setOnResetProgressClickListener() {
        binding.resetProgressBtn.setOnClickListener {
            ResetProgressConfirmationDialog().show(supportFragmentManager, null)
        }
    }

    private fun setOnRateAppBtnClickListener() {
        binding.rateAppBtn.setOnClickListener {
            reviewManager = ReviewManagerFactory.create(this)

            val request = reviewManager.requestReviewFlow()
            request.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    reviewManager.launchReviewFlow(this, task.result)
                }
            }
        }
    }

    private fun setOnShareAppBtnClickListener() {
        binding.shareAppBtn.setOnClickListener {
            lifecycleScope.launch {
                val passedQuestionCount = viewModel.getPassedQuestionCount()

                val resultStr =
                    getString(
                        R.string.for_sharing_result,
                        passedQuestionCount
                    )

                val playStoreLink = getString(R.string.play_store_link)

                val shareContentBuilder = StringBuilder()
                shareContentBuilder.append(resultStr, "\n\n", playStoreLink)

                val sendIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(
                        Intent.EXTRA_TEXT,
                        shareContentBuilder.toString()
                    )
                    type = "text/plain"
                }

                val shareIntent = Intent.createChooser(sendIntent, null)
                startActivity(shareIntent)
            }
        }
    }

    private fun setOnDevelopersAndLicensesBtnClickListener() {
        binding.developersAndLicensesBtn.setOnClickListener {
            val intent =
                Intent(this@SettingsActivity, DevelopersAndLicensesActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun startMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}