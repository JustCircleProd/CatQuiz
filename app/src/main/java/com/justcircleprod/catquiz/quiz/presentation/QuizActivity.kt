package com.justcircleprod.catquiz.quiz.presentation

import android.animation.LayoutTransition
import android.content.Intent
import android.content.res.ColorStateList
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Base64
import android.view.View
import android.view.ViewTreeObserver
import androidx.activity.addCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.text.isDigitsOnly
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.button.MaterialButton
import com.justcircleprod.catquiz.R
import com.justcircleprod.catquiz.core.data.dataStore.DataStoreConstants
import com.justcircleprod.catquiz.core.data.models.questions.AudioQuestion
import com.justcircleprod.catquiz.core.data.models.questions.ImageQuestion
import com.justcircleprod.catquiz.core.data.models.questions.TextQuestion
import com.justcircleprod.catquiz.core.data.models.questions.VideoQuestion
import com.justcircleprod.catquiz.core.presentation.animateProgress
import com.justcircleprod.catquiz.core.presentation.disableWithTransparency
import com.justcircleprod.catquiz.core.presentation.enable
import com.justcircleprod.catquiz.core.presentation.hideWithAnimation
import com.justcircleprod.catquiz.databinding.ActivityQuizBinding
import com.justcircleprod.catquiz.levels.presentation.LevelsActivity
import com.justcircleprod.catquiz.quizResult.presentation.QuizResultActivity
import com.justcircleprod.catquiz.watchRewardedAd.presentation.WatchRewardedAdConfirmationDialog
import com.yandex.mobile.ads.banner.BannerAdEventListener
import com.yandex.mobile.ads.banner.BannerAdSize
import com.yandex.mobile.ads.banner.BannerAdView
import com.yandex.mobile.ads.common.AdRequest
import com.yandex.mobile.ads.common.AdRequestError
import com.yandex.mobile.ads.common.ImpressionData
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.roundToInt


@AndroidEntryPoint
class QuizActivity : AppCompatActivity() {

    companion object {
        const val LEVEL_ARGUMENT_NAME = "LEVEL"
    }

    private lateinit var binding: ActivityQuizBinding
    private val viewModel: QuizViewModel by viewModels()

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
    private var refreshAdTimer: CountDownTimer? = null

    private lateinit var correctAnswerPlayer: MediaPlayer
    private var isCorrectAnswerPlayerPrepared = false
    private var isCorrectAnswerPlayerPlaying = false

    private lateinit var wrongAnswerPlayer: MediaPlayer
    private var isWrongAnswerPlayerPrepared = false
    private var isWrongAnswerPlayerPlaying = false

    private var audioQuestionPlayer: MediaPlayer? = null
    private var isAudioQuestionPlayerPlaying = false

    private var isVideoQuestionPlayerPlaying = false

    private lateinit var hint5050Player: MediaPlayer
    private var isHint5050PlayerPrepared = false
    private var isHint5050PlayerPlaying = false

    private var positionOfVideoPlayer = 0

    private var correctAnswerTimer: CountDownTimer? = null
    private var wrongAnswerTimer: CountDownTimer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuizBinding.inflate(layoutInflater)

        onBackPressedDispatcher.addCallback { startLevelsActivity() }

        enableAnimation()
        binding.videoQuestionLayout.clipToOutline = true
        initAd()

        initCorrectAnswerPlayer()
        initWrongAnswerPlayer()

        setLoadingObserver()
        setOnOptionsClickListeners()

        setContentView(binding.root)
    }

    override fun onResume() {
        super.onResume()

        refreshAdTimer?.start()
        resumePlayers()
    }

    override fun onPause() {
        super.onPause()

        refreshAdTimer?.cancel()
        pausePlayers()
    }

    private fun resumePlayers() {
        // VideoView becomes black after minimizing the application
        // if you don't set the position manually
        if (isVideoQuestionPlayerPlaying) {
            binding.videoQuestion.seekTo(positionOfVideoPlayer)
            binding.videoQuestion.start()
            // if VideoView has already finished playing, but is still on the screen
        } else if (binding.videoQuestion.visibility == View.VISIBLE) {
            binding.videoQuestion.seekTo(positionOfVideoPlayer)
        }

        if (isAudioQuestionPlayerPlaying) {
            audioQuestionPlayer?.start()
        }

        if (isCorrectAnswerPlayerPlaying) {
            correctAnswerPlayer.start()
        }

        if (isWrongAnswerPlayerPlaying) {
            wrongAnswerPlayer.start()
        }

        if (isHint5050PlayerPlaying) {
            hint5050Player.start()
        }
    }

    private fun pausePlayers() {
        if (isVideoQuestionPlayerPlaying) {
            binding.videoQuestion.pause()
            positionOfVideoPlayer = binding.videoQuestion.currentPosition
        }

        if (isAudioQuestionPlayerPlaying) {
            audioQuestionPlayer?.pause()
        }

        if (isCorrectAnswerPlayerPlaying) {
            correctAnswerPlayer.pause()
        }

        if (isWrongAnswerPlayerPlaying) {
            wrongAnswerPlayer.pause()
        }

        if (isHint5050PlayerPlaying) {
            hint5050Player.pause()
        }
    }

    private fun enableAnimation() {
        binding.rootLayout.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
        binding.contentLayout.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
        binding.optionButtonsLayout.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
    }

    private fun initAd() {
        binding.bannerAdView.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                binding.bannerAdView.viewTreeObserver.removeOnGlobalLayoutListener(this)

                binding.bannerAdView.apply {
                    setAdSize(this@QuizActivity.adSize)

                    val adUnitId =
                        String(
                            Base64.decode("Ui1NLTExNDcwMjc0LTY=", Base64.DEFAULT),
                            Charsets.UTF_8
                        )
                    binding.bannerAdView.setAdUnitId(adUnitId)
                }

                bannerAdView = loadBannerAd()
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
                    destroyBannerAdView()
                }

                override fun onAdClicked() {}

                override fun onLeftApplication() {}

                override fun onReturnedToApplication() {}

                override fun onImpression(impressionData: ImpressionData?) {}
            })

            loadAd(AdRequest.Builder().build())
        }
    }

    private fun initCorrectAnswerPlayer() {
        correctAnswerPlayer = MediaPlayer()

        correctAnswerPlayer.setOnPreparedListener {
            isCorrectAnswerPlayerPrepared = true
            it.setOnPreparedListener(null)
        }

        correctAnswerPlayer.setOnCompletionListener {
            isCorrectAnswerPlayerPlaying = false
        }

        correctAnswerPlayer.setDataSource(
            this,
            Uri.parse("android.resource://$packageName/raw/${R.raw.answer_correct_sound}")
        )
        correctAnswerPlayer.prepareAsync()
    }

    private fun initWrongAnswerPlayer() {
        wrongAnswerPlayer = MediaPlayer()

        wrongAnswerPlayer.setOnPreparedListener {
            isWrongAnswerPlayerPrepared = true
            it.setOnPreparedListener(null)
        }

        wrongAnswerPlayer.setOnCompletionListener {
            isWrongAnswerPlayerPlaying = false
        }

        wrongAnswerPlayer.setDataSource(
            this,
            Uri.parse("android.resource://$packageName/raw/${R.raw.answer_wrong_sound}")
        )
        wrongAnswerPlayer.prepareAsync()
    }

    private fun setLoadingObserver() {
        viewModel.isLoading.observe(this) { isLoading ->
            if (isLoading && !viewModel.isFirstLoadResultShown) {
                binding.loadingLayout.visibility = View.VISIBLE
                return@observe
            }

            if (viewModel.questionsCount != 0) {
                if (viewModel.questionForWhichAnswerWasShown == null) {
                    viewModel.setQuestionOnCurrentPosition()
                } else {
                    viewModel.setQuestionOnNextPosition()
                }

                setQuestionWorthObserver()
                setUserCoinsQuantityObserver()
                setWithoutQuizHintsObserver()
                setQuestionObserver()

                binding.quizProgress.max = viewModel.questionsCount * 100

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
            } else {
                // if the loading was successful, but there are no questions left for the user
                if (!viewModel.isFirstLoadResultShown) {
                    binding.loadingLayout.hideWithAnimation(
                        onComplete = {
                            viewModel.isFirstLoadResultShown = true
                            setOutOfQuestionsLayout()
                        }
                    )
                } else {
                    setOutOfQuestionsLayout()
                }
            }
        }
    }

    private fun setOutOfQuestionsLayout() {
        binding.toLevelsBtn.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
        binding.outOfQuestionsLayout.visibility = View.VISIBLE
    }

    private fun startLevelsActivity() {
        val intent = Intent(this, LevelsActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun setOnOptionsClickListeners() {
        binding.firstOption.setOnClickListener { onOptionClicked(it as MaterialButton) }
        binding.secondOption.setOnClickListener { onOptionClicked(it as MaterialButton) }
        binding.thirdOption.setOnClickListener { onOptionClicked(it as MaterialButton) }
        binding.fourthOption.setOnClickListener { onOptionClicked(it as MaterialButton) }
    }

    private fun onOptionClicked(btn: MaterialButton) {
        disableButtons()
        stopQuestionPlayers()

        val isAnswerCorrect =
            if (btn.text.toString() == getString(viewModel.correctAnswerStringResId)) {
                viewModel.onCorrectAnswer()
                true
            } else {
                false
            }

        if (isAnswerCorrect) {
            onCorrectAnswer(btn)
        } else {
            onWrongAnswer(btn)
        }
    }

    private fun setQuestionWorthObserver() {
        if (viewModel.questionWorth.hasActiveObservers()) return

        viewModel.questionWorth.observe(this) {
            binding.questionWorth.text = getString(R.string.quiz_question_worth_label, it)
            binding.questionWorthLayout.visibility = View.VISIBLE
        }
    }

    private fun setUserCoinsQuantityObserver() {
        if (viewModel.userCoinsQuantity.hasActiveObservers()) return

        viewModel.userCoinsQuantity.observe(this) {
            if (it == null || !it.isDigitsOnly()) return@observe

            binding.userCoinsQuantity.text =
                getString(R.string.quiz_users_coins_quantity, it.toInt())

            binding.userCoinsQuantityLayout.visibility = View.VISIBLE
        }
    }

    private fun setWithoutQuizHintsObserver() {
        if (viewModel.withoutQuizHints.hasActiveObservers()) return

        viewModel.withoutQuizHints.observe(this) {
            when (it) {
                DataStoreConstants.WITHOUT_QUIZ_HINTS -> {
                    binding.hintDivider.visibility = View.GONE
                    binding.hint5050.visibility = View.GONE
                    binding.hintCorrectAnswer.visibility = View.GONE
                }

                DataStoreConstants.WITH_QUIZ_HINTS, null -> {
                    initHint5050Player()
                    setHintPrices()
                    setOnHintsClickListeners()

                    binding.hintDivider.visibility = View.VISIBLE
                    binding.hint5050.visibility = View.VISIBLE
                    binding.hintCorrectAnswer.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun setQuestionObserver() {
        if (viewModel.question.hasActiveObservers()) return

        viewModel.question.observe(this) { question ->
            if (question == null) {
                // if the orientation of the device was changed when the last question was displayed
                if (viewModel.questionForWhichAnswerWasShown != null) {
                    binding.rootLayout.layoutTransition.disableTransitionType(LayoutTransition.CHANGING)
                    binding.contentLayout.visibility = View.GONE
                }
                startResultActivity()
                return@observe
            }

            if (viewModel.questionForWhichAnswerWasShown == question) {
                return@observe
            }

            viewModel.clearQuestionForWhichAnswerWasShown()
            hidePreviousQuestion()
            animateQuizProgress()
            setDefaultButtonColors()

            when (question) {
                is TextQuestion -> {
                    setTextQuestionData(question)
                }

                is ImageQuestion -> {
                    setImageQuestionData(question)
                }

                is VideoQuestion -> {
                    setVideoQuestionData(question)
                }

                is AudioQuestion -> {
                    setAudioQuestionData(question)
                }
            }

            binding.firstOption.text = getString(question.firstOptionStringResId)
            binding.secondOption.text = getString(question.secondOptionStringResId)
            binding.thirdOption.text = getString(question.thirdOptionStringResId)
            binding.fourthOption.text = getString(question.fourthOptionStringResId)
        }
    }

    private fun initHint5050Player() {
        if (isHint5050PlayerPrepared) return

        hint5050Player = MediaPlayer()

        hint5050Player.setOnPreparedListener {
            isHint5050PlayerPrepared = true
            it.setOnPreparedListener(null)
        }

        hint5050Player.setOnCompletionListener {
            isHint5050PlayerPlaying = false
        }

        hint5050Player.setDataSource(
            this,
            Uri.parse("android.resource://$packageName/raw/${R.raw.hint_50_50_sound}")
        )
        hint5050Player.prepareAsync()
    }

    private fun setHintPrices() {
        binding.hint5050Price.text = viewModel.hint5050Price.toString()
        binding.hintCorrectAnswerPrice.text = viewModel.hintCorrectAnswerPrice.toString()
    }

    private fun setOnHintsClickListeners() {
        binding.hint5050.setOnClickListener {
            val userCoinsQuantity =
                viewModel.userCoinsQuantity.value?.toInt() ?: return@setOnClickListener

            if (userCoinsQuantity >= viewModel.hint5050Price) {
                viewModel.useHint5050()

                if (isHint5050PlayerPrepared) {
                    hint5050Player.start()
                    isHint5050PlayerPlaying = true
                }
            } else {
                WatchRewardedAdConfirmationDialog.newInstance(
                    missingCoinsQuantity = viewModel.hint5050Price - userCoinsQuantity
                ).show(supportFragmentManager, null)
            }
        }

        binding.hintCorrectAnswer.setOnClickListener {
            val userCoinsQuantity =
                viewModel.userCoinsQuantity.value?.toInt() ?: return@setOnClickListener

            if (userCoinsQuantity >= viewModel.hintCorrectAnswerPrice) {
                viewModel.useHintCorrectAnswer()
            } else {
                WatchRewardedAdConfirmationDialog.newInstance(
                    missingCoinsQuantity = viewModel.hintCorrectAnswerPrice - userCoinsQuantity
                ).show(supportFragmentManager, null)
            }
        }
    }

    private fun setHint5050UsedObserver() {
        if (viewModel.hint5050UsedWithOptionsToShow.hasActiveObservers()) return

        viewModel.hint5050UsedWithOptionsToShow.observe(this) { hint5050UsedWithOptionsToShow ->
            if (hint5050UsedWithOptionsToShow.first) {
                binding.hint5050.disableWithTransparency()
                binding.hintCorrectAnswer.disableWithTransparency()

                hint5050UsedWithOptionsToShow.second.forEach { optionStringResId ->
                    val option = getString(optionStringResId)

                    when {
                        option == binding.firstOption.text && binding.firstOption.isEnabled -> {
                            binding.firstOption.disableWithTransparency()
                        }

                        option == binding.secondOption.text && binding.secondOption.isEnabled -> {
                            binding.secondOption.disableWithTransparency()
                        }

                        option == binding.thirdOption.text && binding.thirdOption.isEnabled -> {
                            binding.thirdOption.disableWithTransparency()
                        }

                        option == binding.fourthOption.text && binding.fourthOption.isEnabled -> {
                            binding.fourthOption.disableWithTransparency()
                        }
                    }
                }
            }
        }
    }

    private fun setHintCorrectAnswerUsedObserver() {
        if (viewModel.hintCorrectAnswerUsed.hasActiveObservers()) return

        viewModel.hintCorrectAnswerUsed.observe(this) { hintCorrectAnswerUsed ->
            if (hintCorrectAnswerUsed) {
                viewModel.onCorrectAnswer()

                binding.hint5050.disableWithTransparency()
                binding.hintCorrectAnswer.disableWithTransparency()

                disableButtons()
                stopQuestionPlayers()

                onCorrectAnswer(
                    btn = when (getString(viewModel.correctAnswerStringResId)) {
                        binding.firstOption.text -> binding.firstOption
                        binding.secondOption.text -> binding.secondOption
                        binding.thirdOption.text -> binding.thirdOption
                        binding.fourthOption.text -> binding.fourthOption
                        else -> return@observe
                    }
                )
            }
        }
    }

    private fun hidePreviousQuestion() {
        binding.textQuestion.visibility = View.INVISIBLE
        binding.textQuestion.text = ""
        binding.imageQuestion.visibility = View.INVISIBLE
        binding.audioQuestion.visibility = View.INVISIBLE
        binding.videoQuestionLayout.visibility = View.INVISIBLE
    }

    private fun animateQuizProgress() {
        val currentProgress = (viewModel.questionPosition) * 100
        val newProgress = currentProgress + 100
        binding.quizProgress.animateProgress(currentProgress, newProgress)
    }

    private fun setDefaultButtonColors() {
        binding.firstOption.backgroundTintList = ColorStateList.valueOf(
            ContextCompat.getColor(this, R.color.btn_background)
        )
        binding.secondOption.backgroundTintList = ColorStateList.valueOf(
            ContextCompat.getColor(this, R.color.btn_background)
        )
        binding.thirdOption.backgroundTintList = ColorStateList.valueOf(
            ContextCompat.getColor(this, R.color.btn_background)
        )
        binding.fourthOption.backgroundTintList = ColorStateList.valueOf(
            ContextCompat.getColor(this, R.color.btn_background)
        )
    }

    private fun setTextQuestionData(question: TextQuestion) {
        binding.textQuestion.visibility = View.VISIBLE
        binding.textQuestion.text = getString(question.questionStringResId)

        enableButtons()
        setHint5050UsedObserver()
        setHintCorrectAnswerUsedObserver()
    }

    private fun setImageQuestionData(question: ImageQuestion) {
        Glide
            .with(this)
            .load(
                question.imageDrawableResId
            )
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .transform(RoundedCorners(resources.getDimensionPixelSize(R.dimen.image_question_rounded_corner_size)))
            .into(binding.imageQuestion)

        binding.imageQuestion.visibility = View.VISIBLE

        enableButtons()
        setHint5050UsedObserver()
        setHintCorrectAnswerUsedObserver()
    }

    private fun setVideoQuestionData(question: VideoQuestion) {
        binding.videoQuestion.setOnPreparedListener {
            it.start()
            isVideoQuestionPlayerPlaying = true
        }

        binding.videoQuestion.setOnInfoListener { _, info, _ ->
            if (info == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
                binding.videoQuestionLayout.visibility = View.VISIBLE

                enableButtons()
                setHint5050UsedObserver()
                setHintCorrectAnswerUsedObserver()

                return@setOnInfoListener true
            }

            return@setOnInfoListener false
        }

        binding.videoQuestion.setOnCompletionListener {
            isVideoQuestionPlayerPlaying = false
            positionOfVideoPlayer = binding.videoQuestion.duration
        }

        binding.videoQuestion.setVideoURI(
            Uri.parse("android.resource://$packageName/raw/${question.videoRawResId}")
        )
    }

    private fun setAudioQuestionData(question: AudioQuestion) {
        if (audioQuestionPlayer == null) {
            audioQuestionPlayer = MediaPlayer()

            audioQuestionPlayer?.setOnPreparedListener {
                binding.audioQuestion.visibility = View.VISIBLE

                it.start()
                isAudioQuestionPlayerPlaying = true

                enableButtons()
                setHint5050UsedObserver()
                setHintCorrectAnswerUsedObserver()
            }

            audioQuestionPlayer?.setOnCompletionListener {
                isAudioQuestionPlayerPlaying = false
            }
        }

        audioQuestionPlayer?.setDataSource(
            this,
            Uri.parse("android.resource://$packageName/raw/${question.audioRawResId}")
        )
        audioQuestionPlayer?.prepareAsync()
    }

    private fun enableButtons() {
        binding.firstOption.enable()
        binding.secondOption.enable()
        binding.thirdOption.enable()
        binding.fourthOption.enable()

        binding.hint5050.enable()
        binding.hintCorrectAnswer.enable()
    }

    private fun disableButtons() {
        binding.firstOption.isEnabled = false
        binding.secondOption.isEnabled = false
        binding.thirdOption.isEnabled = false
        binding.fourthOption.isEnabled = false

        binding.hint5050.disableWithTransparency()
        binding.hintCorrectAnswer.disableWithTransparency()
    }

    private fun stopQuestionPlayers() {
        when {
            binding.videoQuestionLayout.visibility == View.VISIBLE -> {
                binding.videoQuestion.pause()
                isVideoQuestionPlayerPlaying = false
                positionOfVideoPlayer = binding.videoQuestion.currentPosition
            }

            binding.audioQuestion.visibility == View.VISIBLE -> {
                audioQuestionPlayer?.pause()
                isAudioQuestionPlayerPlaying = false
            }
        }
    }

    private fun onCorrectAnswer(btn: MaterialButton) {
        if (isCorrectAnswerPlayerPrepared) {
            correctAnswerPlayer.start()
            isCorrectAnswerPlayerPlaying = true
        }

        btn.backgroundTintList = ColorStateList.valueOf(
            ContextCompat.getColor(this, R.color.correct_answer_color)
        )

        viewModel.setQuestionForWhichAnswerWasShown()

        correctAnswerTimer = object : CountDownTimer(2000, 2000) {
            override fun onTick(millisUntilFinished: Long) {}
            override fun onFinish() {
                resetQuestionPlayers()
                viewModel.setQuestionOnNextPosition()
                viewModel.clearQuestionForWhichAnswerWasShown()
            }
        }.start()
    }

    private fun onWrongAnswer(btn: MaterialButton) {
        if (isWrongAnswerPlayerPrepared) {
            wrongAnswerPlayer.start()
            isWrongAnswerPlayerPlaying = true
        }

        btn.backgroundTintList = ColorStateList.valueOf(
            ContextCompat.getColor(this, R.color.incorrect_answer_color)
        )

        viewModel.setQuestionForWhichAnswerWasShown()

        var isCorrectAnswerShown = false

        wrongAnswerTimer = object : CountDownTimer(2000, 250) {
            override fun onTick(millisUntilFinished: Long) {
                if (!isCorrectAnswerShown && millisUntilFinished <= 1750) {
                    showCorrectAnswer(getString(viewModel.correctAnswerStringResId))
                    isCorrectAnswerShown = true
                }
            }

            override fun onFinish() {
                resetQuestionPlayers()
                viewModel.setQuestionOnNextPosition()
                viewModel.clearQuestionForWhichAnswerWasShown()
            }
        }.start()
    }

    private fun showCorrectAnswer(correctAnswer: String) {
        when (correctAnswer) {
            binding.firstOption.text ->
                binding.firstOption.backgroundTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(this, R.color.correct_answer_color)
                )

            binding.secondOption.text ->
                binding.secondOption.backgroundTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(this, R.color.correct_answer_color)
                )

            binding.thirdOption.text ->
                binding.thirdOption.backgroundTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(this, R.color.correct_answer_color)
                )

            binding.fourthOption.text ->
                binding.fourthOption.backgroundTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(this, R.color.correct_answer_color)
                )
        }
    }

    private fun resetQuestionPlayers() {
        when {
            binding.videoQuestionLayout.visibility == View.VISIBLE -> {
                binding.videoQuestion.stopPlayback()
                positionOfVideoPlayer = 0
                binding.videoQuestion.setOnPreparedListener(null)
                binding.videoQuestion.setOnCompletionListener(null)
            }

            binding.audioQuestion.visibility == View.VISIBLE -> {
                audioQuestionPlayer?.stop()
                audioQuestionPlayer?.reset()
            }
        }
    }

    private fun startResultActivity() {
        val intent = Intent(this, QuizResultActivity::class.java)
        intent.putExtra(QuizResultActivity.LEVEL_ARGUMENT_NAME, viewModel.levelId.value)
        intent.putExtra(QuizResultActivity.EARNED_COINS_ARGUMENT_NAME, viewModel.earnedCoins)
        intent.putExtra(
            QuizResultActivity.CORRECTLY_ANSWERED_QUESTIONS_COUNT_ARGUMENT_NAME,
            viewModel.correctlyAnsweredQuestionsCount
        )
        intent.putExtra(
            QuizResultActivity.QUESTIONS_COUNT_ARGUMENT_NAME,
            viewModel.questionsCount
        )
        startActivity(intent)
        finish()
    }

    private fun destroyBannerAdView() {
        refreshAdTimer?.cancel()
        refreshAdTimer = null

        bannerAdView?.destroy()
        bannerAdView = null
    }

    override fun onDestroy() {
        super.onDestroy()

        destroyBannerAdView()

        if (isCorrectAnswerPlayerPrepared) {
            correctAnswerPlayer.release()
        }

        if (isWrongAnswerPlayerPrepared) {
            wrongAnswerPlayer.release()
        }

        audioQuestionPlayer?.release()
        audioQuestionPlayer = null

        if (isHint5050PlayerPrepared) {
            hint5050Player.release()
        }


        correctAnswerTimer?.cancel()
        correctAnswerTimer = null

        wrongAnswerTimer?.cancel()
        wrongAnswerTimer = null
    }
}