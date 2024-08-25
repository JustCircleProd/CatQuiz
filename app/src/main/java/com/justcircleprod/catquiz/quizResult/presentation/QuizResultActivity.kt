package com.justcircleprod.catquiz.quizResult.presentation

import android.animation.LayoutTransition
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Base64
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.activity.addCallback
import androidx.activity.viewModels
import androidx.annotation.RawRes
import androidx.appcompat.app.AppCompatActivity
import com.justcircleprod.catquiz.App
import com.justcircleprod.catquiz.R
import com.justcircleprod.catquiz.core.data.models.levels.LevelId
import com.justcircleprod.catquiz.core.presentation.hideWithAnimation
import com.justcircleprod.catquiz.databinding.ActivityQuizResultBinding
import com.justcircleprod.catquiz.doubleCoins.presentation.DoubleCoinsConfirmationDialog
import com.justcircleprod.catquiz.doubleCoins.presentation.DoubleCoinsConfirmationDialogCallback
import com.justcircleprod.catquiz.levels.presentation.LevelsActivity
import com.justcircleprod.catquiz.quiz.presentation.QuizActivity
import com.yandex.mobile.ads.banner.BannerAdEventListener
import com.yandex.mobile.ads.banner.BannerAdSize
import com.yandex.mobile.ads.banner.BannerAdView
import com.yandex.mobile.ads.common.AdError
import com.yandex.mobile.ads.common.AdRequest
import com.yandex.mobile.ads.common.AdRequestConfiguration
import com.yandex.mobile.ads.common.AdRequestError
import com.yandex.mobile.ads.common.ImpressionData
import com.yandex.mobile.ads.interstitial.InterstitialAd
import com.yandex.mobile.ads.interstitial.InterstitialAdEventListener
import com.yandex.mobile.ads.interstitial.InterstitialAdLoadListener
import com.yandex.mobile.ads.interstitial.InterstitialAdLoader
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.roundToInt

@AndroidEntryPoint
class QuizResultActivity : AppCompatActivity(), DoubleCoinsConfirmationDialogCallback {

    companion object {
        const val LEVEL_ARGUMENT_NAME = "LEVEL"
        const val EARNED_COINS_ARGUMENT_NAME = "EARNED_COINS"
        const val CORRECTLY_ANSWERED_QUESTIONS_COUNT_ARGUMENT_NAME =
            "CORRECTLY_ANSWERED_QUESTIONS_COUNT"
        const val QUESTIONS_COUNT_ARGUMENT_NAME = "QUESTIONS_COUNT"
    }

    private lateinit var binding: ActivityQuizResultBinding
    private val viewModel: QuizResultViewModel by viewModels()

    private var bannerAdView: BannerAdView? = null
    private val adSize: BannerAdSize
        get() {
            // Calculate the width of the ad, taking into account the padding in the ad container.
            var adWidthPixels = binding.bannerAdView.width
            if (adWidthPixels == 0) {
                // If the ad hasn't been laid out, default to the full screen width
                adWidthPixels = resources.displayMetrics.widthPixels
            }
            val adWidth =
                ((adWidthPixels - resources.getDimensionPixelSize(R.dimen.banner_ad_horizontal_margin) * 2) / resources.displayMetrics.density).roundToInt()
            val maxAdHeight =
                (resources.getDimensionPixelSize(R.dimen.banner_ad_height) / resources.displayMetrics.density).roundToInt()

            return BannerAdSize.inlineSize(this, adWidth, maxAdHeight)
        }

    private var bannerAdLoadTimer: CountDownTimer? = null
    private var refreshAdTimer: CountDownTimer? = null

    private var interstitialAd: InterstitialAd? = null
    private var interstitialAdLoader: InterstitialAdLoader? = null
    private var interstitialAdLoadTimer: CountDownTimer? = null

    private lateinit var resultPlayer: MediaPlayer
    private var isResultPlayerPrepared = false
    private var isResultPlayerPlaying = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuizResultBinding.inflate(layoutInflater)

        enableAnimations()

        setLoadingObserver()

        initBannerAd()
        workWithInterstitialAd()

        setEarnedCoinsObserver()
        setEarnedCoinsDoubledObserver()
        setOnDoubleCoinsBtnClickListener()

        setOnRepeatQuizBtnClickListener()
        setOnToLevelsBtnClicked()

        setContentView(binding.root)
    }

    override fun onResume() {
        super.onResume()

        refreshAdTimer?.start()
        if (isResultPlayerPlaying) {
            resultPlayer.start()
        }
    }

    override fun onPause() {
        super.onPause()

        refreshAdTimer?.cancel()
        if (isResultPlayerPlaying) {
            resultPlayer.pause()
        }
    }

    private fun enableAnimations() {
        binding.rootLayout.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
        binding.contentLayout.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
    }

    private fun initBannerAd() {
        binding.bannerAdView.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                binding.bannerAdView.viewTreeObserver.removeOnGlobalLayoutListener(this)

                binding.bannerAdView.apply {
                    setAdSize(this@QuizResultActivity.adSize)

                    val adUnitId =
                        String(
                            Base64.decode("Ui1NLTExNDcwMjc0LTE=", Base64.DEFAULT),
                            Charsets.UTF_8
                        )
                    binding.bannerAdView.setAdUnitId(adUnitId)
                }

                bannerAdView = loadBannerAd()

                // to limit the time for first loading of an ad
                bannerAdLoadTimer = object : CountDownTimer(1750, 1750) {
                    override fun onTick(mills: Long) {}

                    override fun onFinish() {
                        viewModel.isBannerAdLoading.value = false
                    }
                }.start()
            }
        })
    }

    private fun loadBannerAd(): BannerAdView {
        return binding.bannerAdView.apply {
            setBannerAdEventListener(object : BannerAdEventListener {
                override fun onAdLoaded() {
                    // If this callback occurs after the activity is destroyed, you
                    // must call destroy and return or you may get a memory leak.
                    // Note `isDestroyed` is a method on Activity.
                    if (isDestroyed) {
                        bannerAdView?.destroy()
                        return
                    }

                    // to update ad every n seconds
                    refreshAdTimer = object : CountDownTimer(30000, 30000) {
                        override fun onTick(mills: Long) {}

                        override fun onFinish() {
                            bannerAdView = loadBannerAd()
                        }
                    }.start()
                }

                override fun onAdFailedToLoad(error: AdRequestError) {
                    viewModel.isBannerAdLoading.value = false
                    destroyBannerAd()
                }

                override fun onAdClicked() {}

                override fun onLeftApplication() {}

                override fun onReturnedToApplication() {}

                override fun onImpression(impressionData: ImpressionData?) {}
            })

            loadAd(AdRequest.Builder().build())
        }
    }

    private fun setLoadingObserver() {
        viewModel.isLoading.observe(this) { isLoading ->
            if (isLoading && !viewModel.isFirstLoadResultShown) {
                binding.loadingLayout.visibility = View.VISIBLE
                return@observe
            }

            if (!viewModel.isFirstLoadResultShown) {
                binding.loadingLayout.hideWithAnimation(
                    onComplete = {
                        viewModel.isFirstLoadResultShown = true
                        binding.contentLayout.visibility = View.VISIBLE
                    }
                )
            } else {
                binding.contentLayout.visibility = View.VISIBLE
            }

            onBackPressedDispatcher.addCallback { startLevelsActivity() }
            showResult()
        }
    }

    private fun workWithInterstitialAd() {
        // if an ad has already been shown
        if (!viewModel.isInterstitialAdLoading.value) {
            return
        }

        (application as App).onQuizPassed()

        val showAd =
            (application as App).passedQuizNum >= (application as App).passedQuizNumForShowingAd

        if (!showAd) {
            viewModel.isInterstitialAdLoading.value = false
            return
        }

        interstitialAdLoader = InterstitialAdLoader(this).apply {
            setAdLoadListener(object : InterstitialAdLoadListener {
                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    interstitialAdLoadTimer?.cancel()
                    this@QuizResultActivity.interstitialAd = interstitialAd
                    showAd()
                }

                override fun onAdFailedToLoad(error: AdRequestError) {
                    interstitialAdLoadTimer?.cancel()
                    viewModel.isInterstitialAdLoading.value = false
                    destroyInterstitialAd()
                }
            })
        }

        val adUnitId =
            String(Base64.decode("Ui1NLTExNDcwMjc0LTU=", Base64.DEFAULT), Charsets.UTF_8)
        val adRequestConfiguration = AdRequestConfiguration.Builder(adUnitId).build()

        interstitialAdLoader?.loadAd(adRequestConfiguration)

        // to limit the time to load an ad
        interstitialAdLoadTimer = object : CountDownTimer(5000, 5000) {
            override fun onTick(mills: Long) {}

            override fun onFinish() {
                interstitialAdLoader?.cancelLoading()
                viewModel.isInterstitialAdLoading.value = false
                destroyInterstitialAd()
            }
        }.start()
    }

    private fun showAd() {
        interstitialAd?.apply {
            setAdEventListener(object : InterstitialAdEventListener {
                override fun onAdShown() {}

                override fun onAdFailedToShow(adError: AdError) {
                    viewModel.isInterstitialAdLoading.value = false
                    destroyInterstitialAd()
                }

                override fun onAdDismissed() {
                    viewModel.isInterstitialAdLoading.value = false
                    destroyInterstitialAd()
                }

                override fun onAdClicked() {}

                override fun onAdImpression(impressionData: ImpressionData?) {}
            })

            show(this@QuizResultActivity)
        }
    }

    private fun setEarnedCoinsObserver() {
        viewModel.earnedCoins.observe(this) {
            binding.earnedCoins.text = it.toString()
        }
    }

    private fun showResult() {
        if (viewModel.isCongratulationViewsShown) {
            binding.congratulationText.text =
                resources.getStringArray(viewModel.shownCongratulationTextArrayResId!!)[viewModel.shownCongratulationTextArrayIndex!!]

            binding.congratulationAnimation.setAnimation(viewModel.shownCongratulationAnimationRawResId!!)

            return
        }

        val correctlyAnsweredQuestionsCount =
            intent.extras!!.getInt(CORRECTLY_ANSWERED_QUESTIONS_COUNT_ARGUMENT_NAME)

        val questionsCount = intent.extras!!.getInt(QUESTIONS_COUNT_ARGUMENT_NAME)

        when {
            questionsCount == 1 -> {
                if (correctlyAnsweredQuestionsCount == 0) {
                    onBadResult()
                } else {
                    onBestResult()
                }
            }

            correctlyAnsweredQuestionsCount > (questionsCount * 0.66).roundToInt() -> {
                onBestResult()
            }

            correctlyAnsweredQuestionsCount in (questionsCount * 0.33).roundToInt()..(questionsCount * 0.66).roundToInt() -> {
                onGoodResult()
            }

            else -> {
                onBadResult()
            }
        }

        viewModel.isCongratulationViewsShown = true
    }

    private fun onBestResult() {
        viewModel.shownCongratulationTextArrayResId = R.array.texts_for_best_result

        val stringArray = resources.getStringArray(viewModel.shownCongratulationTextArrayResId!!)

        viewModel.shownCongratulationTextArrayIndex =
            (0 until stringArray.toList().size).shuffled()[0]

        binding.congratulationText.text = stringArray[viewModel.shownCongratulationTextArrayIndex!!]

        viewModel.shownCongratulationAnimationRawResId = listOf(
            R.raw.quiz_result_best_congratulation_animation_1,
            R.raw.introduction_card_item_animation_3_and_quiz_result_best_congratulation_animation_2,
            R.raw.quiz_result_best_congratulation_animation_3
        ).shuffled().first()

        binding.congratulationAnimation.setAnimation(viewModel.shownCongratulationAnimationRawResId!!)

        startResultPlayer(R.raw.quiz_result_best_sound)
    }

    private fun onGoodResult() {
        viewModel.shownCongratulationTextArrayResId = R.array.texts_for_good_result

        val stringArray = resources.getStringArray(viewModel.shownCongratulationTextArrayResId!!)

        viewModel.shownCongratulationTextArrayIndex =
            (0 until stringArray.toList().size).shuffled()[0]

        binding.congratulationText.text = stringArray[viewModel.shownCongratulationTextArrayIndex!!]

        viewModel.shownCongratulationAnimationRawResId = listOf(
            R.raw.quiz_result_good_congratulation_animation_1,
            R.raw.quiz_result_good_congratulation_animation_2,
            R.raw.introduction_card_item_animation_4_and_quiz_result_good_congratulation_animation_3

        ).shuffled().first()

        binding.congratulationAnimation.setAnimation(viewModel.shownCongratulationAnimationRawResId!!)

        startResultPlayer(R.raw.quiz_result_good_sound)
    }

    private fun onBadResult() {
        viewModel.shownCongratulationTextArrayResId = R.array.texts_for_bad_result

        val stringArray = resources.getStringArray(viewModel.shownCongratulationTextArrayResId!!)

        viewModel.shownCongratulationTextArrayIndex =
            (0 until stringArray.toList().size).shuffled()[0]

        binding.congratulationText.text = stringArray[viewModel.shownCongratulationTextArrayIndex!!]

        viewModel.shownCongratulationAnimationRawResId = listOf(
            R.raw.quiz_result_bad_congratulation_animation_1,
            R.raw.quiz_result_bad_congratulation_animation_2,
            R.raw.quiz_result_bad_congratulation_animation_3
        ).shuffled().first()

        binding.congratulationAnimation.setAnimation(viewModel.shownCongratulationAnimationRawResId!!)

        startResultPlayer(R.raw.quiz_result_bad_sound)
    }

    private fun startResultPlayer(@RawRes audioResId: Int) {
        resultPlayer = MediaPlayer()

        resultPlayer.setOnPreparedListener {
            isResultPlayerPrepared = true

            it.start()
            isResultPlayerPlaying = true

            it.setOnPreparedListener(null)
        }

        resultPlayer.setOnCompletionListener {
            isResultPlayerPlaying = false
            isResultPlayerPrepared = false

            it.setOnCompletionListener(null)
            it.release()
        }

        resultPlayer.setDataSource(
            this@QuizResultActivity,
            Uri.parse("android.resource://$packageName/raw/$audioResId")
        )
        resultPlayer.prepareAsync()
    }

    private fun setEarnedCoinsDoubledObserver() {
        viewModel.areEarnedCoinsDoubled.observe(this) {
            if (it || viewModel.earnedCoins.value == 0) {
                binding.doubleCoinsBtn.visibility = View.GONE
                changeLineTopMargin(isTextAbove = true)
            } else {
                binding.doubleCoinsBtn.visibility = View.VISIBLE
                changeLineTopMargin(isTextAbove = false)
            }
        }
    }

    private fun changeLineTopMargin(isTextAbove: Boolean) {
        if (binding.line.layoutParams is ViewGroup.MarginLayoutParams) {
            val layoutParams = binding.line.layoutParams as ViewGroup.MarginLayoutParams
            val bottomMargin =
                resources.getDimension(R.dimen.line_with_text_under_bottom_margin).roundToInt()

            val topMargin = if (isTextAbove) {
                0
            } else {
                resources.getDimension(R.dimen.default_line_top_margin).roundToInt()
            }

            layoutParams.setMargins(0, topMargin, 0, bottomMargin)
            binding.line.requestLayout()
        }
    }

    private fun setOnDoubleCoinsBtnClickListener() {
        binding.doubleCoinsBtn.setOnClickListener {
            val earnedCoins = intent.extras?.getInt(EARNED_COINS_ARGUMENT_NAME)

            if (earnedCoins != null) {
                DoubleCoinsConfirmationDialog.newInstance(earnedCoins)
                    .show(supportFragmentManager, null)
            }
        }
    }

    override fun onCoinsDoublingConfirmed() {
        viewModel.areEarnedCoinsDoubled.value = true
        val earnedCoins = viewModel.earnedCoins.value!!

        viewModel.earnedCoins.value = earnedCoins * 2
    }

    private fun setOnRepeatQuizBtnClickListener() {
        binding.continueQuizBtn.setOnClickListener {
            val intent = Intent(this, QuizActivity::class.java)
            val levelId = LevelId.fromInt(this.intent.extras!!.getInt(LEVEL_ARGUMENT_NAME))
            intent.putExtra(QuizActivity.LEVEL_ARGUMENT_NAME, levelId.value)
            startActivity(intent)
            finish()
        }
    }

    private fun setOnToLevelsBtnClicked() {
        binding.toLevelsBtn.setOnClickListener {
            startLevelsActivity()
        }
    }

    private fun startLevelsActivity() {
        val intent = Intent(this, LevelsActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun destroyBannerAd() {
        bannerAdLoadTimer?.cancel()
        bannerAdView = null

        refreshAdTimer?.cancel()
        refreshAdTimer = null

        bannerAdView?.destroy()
        bannerAdView = null
    }

    private fun destroyInterstitialAd() {
        interstitialAdLoadTimer?.cancel()
        interstitialAdLoadTimer = null

        interstitialAdLoader?.setAdLoadListener(null)
        interstitialAdLoader = null

        interstitialAd?.setAdEventListener(null)
        interstitialAd = null
    }

    override fun onDestroy() {
        super.onDestroy()

        destroyBannerAd()
        destroyInterstitialAd()
    }
}