package com.justcircleprod.catquiz.doubleCoins.presentation

import android.animation.LayoutTransition
import android.app.Dialog
import android.content.DialogInterface
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.justcircleprod.catquiz.R
import com.justcircleprod.catquiz.core.presentation.RewardedAdState
import com.justcircleprod.catquiz.databinding.DialogDoubleCoinsConfirmationBinding
import com.yandex.mobile.ads.common.AdError
import com.yandex.mobile.ads.common.AdRequestConfiguration
import com.yandex.mobile.ads.common.AdRequestError
import com.yandex.mobile.ads.common.ImpressionData
import com.yandex.mobile.ads.rewarded.Reward
import com.yandex.mobile.ads.rewarded.RewardedAd
import com.yandex.mobile.ads.rewarded.RewardedAdEventListener
import com.yandex.mobile.ads.rewarded.RewardedAdLoadListener
import com.yandex.mobile.ads.rewarded.RewardedAdLoader
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DoubleCoinsConfirmationDialog : DialogFragment() {

    companion object {
        const val EARNED_COINS_NAME_ARGUMENT = "EARNED_COINS"

        fun newInstance(earnedCoins: Int): DoubleCoinsConfirmationDialog {
            return DoubleCoinsConfirmationDialog().apply {
                arguments = Bundle().apply {
                    putInt(EARNED_COINS_NAME_ARGUMENT, earnedCoins)
                }
            }
        }
    }

    private lateinit var binding: DialogDoubleCoinsConfirmationBinding
    private val viewModel by viewModels<DoubleCoinsConfirmationViewModel>()

    private lateinit var rewardReceivedPlayer: MediaPlayer
    private var isRewardReceivedPlayerPrepared = false
    private var isRewardReceivedPlayerPlaying = false

    private var rewardedAd: RewardedAd? = null
    private var rewardedAdLoader: RewardedAdLoader? = null
    private var rewardedAdLoadTimer: CountDownTimer? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialogBuilder = AlertDialog.Builder(requireContext(), R.style.DialogRoundedCorner)

        binding = DialogDoubleCoinsConfirmationBinding.inflate(layoutInflater)

        enableAnimations()
        initRewardReceivedPlayer()
        setOnButtonsClickListeners()

        dialogBuilder.setView(binding.root).setCancelable(true)
        return dialogBuilder.create()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // if the dialog was recreated when loading the ad, you must reload the ad
        if (viewModel.rewardedAdState.value == RewardedAdState.Loading) {
            viewModel.rewardedAdState.value = RewardedAdState.UserNotAgreedYet
        }

        setRewardedAdStateObserver()
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)

        if (viewModel.rewardedAdState.value == RewardedAdState.RewardReceived) {
            (activity as DoubleCoinsConfirmationDialogCallback).onCoinsDoublingConfirmed()
        }
    }

    override fun onResume() {
        super.onResume()

        if (isRewardReceivedPlayerPlaying) {
            rewardReceivedPlayer.start()
        }
    }

    override fun onPause() {
        super.onPause()

        if (isRewardReceivedPlayerPlaying) {
            rewardReceivedPlayer.pause()
        }
    }

    private fun enableAnimations() {
        binding.root.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
    }

    private fun initRewardReceivedPlayer() {
        rewardReceivedPlayer = MediaPlayer()

        rewardReceivedPlayer.setOnPreparedListener {
            isRewardReceivedPlayerPrepared = true
            it.setOnPreparedListener(null)
        }

        rewardReceivedPlayer.setOnCompletionListener {
            isRewardReceivedPlayerPlaying = false
            isRewardReceivedPlayerPrepared = false

            it.setOnCompletionListener(null)
            it.release()
        }

        rewardReceivedPlayer.setDataSource(
            requireContext(),
            Uri.parse("android.resource://${requireActivity().packageName}/raw/${R.raw.ad_reward_received_sound}")
        )
        rewardReceivedPlayer.prepareAsync()
    }

    private fun setOnButtonsClickListeners() {
        binding.titleCancelBtn.setOnClickListener { dialog?.cancel() }
        binding.cancelBtn.setOnClickListener { dialog?.cancel() }

        binding.watchAdBtn.setOnClickListener {
            viewModel.rewardedAdState.value = RewardedAdState.Loading
            loadRewardedAd()
        }

        binding.submitRewardResultBtn.setOnClickListener {
            if (viewModel.rewardedAdState.value == RewardedAdState.RewardReceived) {
                (activity as DoubleCoinsConfirmationDialogCallback).onCoinsDoublingConfirmed()
            }
            dismiss()
        }
    }

    private fun loadRewardedAd() {
        rewardedAdLoader = RewardedAdLoader(requireContext()).apply {
            setAdLoadListener(object : RewardedAdLoadListener {
                override fun onAdLoaded(rewarded: RewardedAd) {
                    rewardedAdLoadTimer?.cancel()
                    rewardedAd = rewarded
                    showAd()
                }

                override fun onAdFailedToLoad(error: AdRequestError) {
                    rewardedAdLoadTimer?.cancel()
                    viewModel.rewardedAdState.value = RewardedAdState.FailedToLoad
                    destroyRewardedAd()
                }
            })
        }

        val adUnitId =
            String(Base64.decode("Ui1NLTExNDcwMjc0LTM=", Base64.DEFAULT), Charsets.UTF_8)
        val adRequestConfiguration = AdRequestConfiguration.Builder(adUnitId).build()

        rewardedAdLoader?.loadAd(adRequestConfiguration)

        // to limit the time to load an ad
        rewardedAdLoadTimer = object : CountDownTimer(10000, 10000) {
            override fun onTick(mills: Long) {}

            override fun onFinish() {
                rewardedAdLoader?.cancelLoading()
                viewModel.rewardedAdState.value = RewardedAdState.FailedToLoad
                destroyRewardedAd()
            }
        }.start()
    }

    private fun showAd() {
        rewardedAd?.apply {
            setAdEventListener(object : RewardedAdEventListener {
                override fun onAdShown() {}

                override fun onAdFailedToShow(adError: AdError) {
                    viewModel.rewardedAdState.value = if (viewModel.rewardReceived) {
                        RewardedAdState.RewardReceived
                    } else {
                        RewardedAdState.FailedToLoad
                    }

                    destroyRewardedAd()
                }

                override fun onAdDismissed() {
                    destroyRewardedAd()

                    if (viewModel.rewardReceived) {
                        viewModel.rewardedAdState.value = RewardedAdState.RewardReceived
                    } else {
                        viewModel.rewardedAdState.value = RewardedAdState.UserNotAgreedYet
                    }
                }

                override fun onAdClicked() {}

                override fun onAdImpression(impressionData: ImpressionData?) {}

                override fun onRewarded(reward: Reward) {
                    viewModel.doubleEarnedCoins()
                    viewModel.rewardReceived = true
                }
            })

            show(requireActivity())
        }
    }

    private fun setRewardedAdStateObserver() {
        viewModel.rewardedAdState.observe(viewLifecycleOwner) {
            when (it) {
                RewardedAdState.UserNotAgreedYet -> {
                    binding.title.text = getString(R.string.double_coins)

                    binding.loadingLayout.visibility = View.GONE
                    binding.rewardResultLayout.visibility = View.GONE
                    binding.rewardResultQuantityLayout.visibility = View.GONE

                    binding.earnedCoinsToDouble.text =
                        getString(
                            R.string.double_coins_quantity_with_placeholder,
                            viewModel.earnedCoins
                        )

                    binding.questionLayout.visibility = View.VISIBLE
                }

                RewardedAdState.Loading -> {
                    binding.title.text = getString(R.string.loading_ad_title)

                    binding.questionLayout.visibility = View.GONE
                    binding.rewardResultLayout.visibility = View.GONE
                    binding.rewardResultQuantityLayout.visibility = View.GONE

                    binding.loadingLayout.visibility = View.VISIBLE
                }

                RewardedAdState.FailedToLoad -> {
                    binding.title.text =
                        getString(R.string.failed_to_load_rewarded_ad_title)

                    binding.questionLayout.visibility = View.GONE
                    binding.loadingLayout.visibility = View.GONE
                    binding.rewardResultQuantityLayout.visibility = View.GONE

                    binding.rewardResultTv.text =
                        getString(R.string.failed_to_load_rewarded_ad)
                    binding.submitRewardResultBtn.setText(R.string.confirm)

                    binding.rewardResultLayout.visibility = View.VISIBLE
                }

                RewardedAdState.RewardReceived -> {
                    binding.title.text = getString(R.string.coins_doubled_title)

                    binding.rewardResultTv.text = getString(R.string.coins_doubled)
                    binding.rewardResultQuantity.text = (viewModel.earnedCoins * 2).toString()

                    binding.submitRewardResultBtn.setText(R.string.great)

                    binding.questionLayout.visibility = View.GONE
                    binding.loadingLayout.visibility = View.GONE

                    binding.rewardResultLayout.visibility = View.VISIBLE
                    binding.rewardResultQuantityLayout.visibility = View.VISIBLE

                    if (isRewardReceivedPlayerPrepared) {
                        rewardReceivedPlayer.start()
                        isRewardReceivedPlayerPlaying = true
                    }
                }
            }
        }
    }

    private fun destroyRewardedAd() {
        rewardedAdLoadTimer?.cancel()
        rewardedAdLoadTimer = null

        rewardedAdLoader?.setAdLoadListener(null)
        rewardedAdLoader = null

        rewardedAd?.setAdEventListener(null)
        rewardedAd = null
    }

    override fun onDestroy() {
        super.onDestroy()

        destroyRewardedAd()
    }
}