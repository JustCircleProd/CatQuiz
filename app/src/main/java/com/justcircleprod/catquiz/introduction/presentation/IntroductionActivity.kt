package com.justcircleprod.catquiz.introduction.presentation

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.justcircleprod.catquiz.R
import com.justcircleprod.catquiz.databinding.ActivityIntroductionBinding
import com.justcircleprod.catquiz.introduction.presentation.introductionCardAdapter.HorizontalMarginItemDecoration
import com.justcircleprod.catquiz.introduction.presentation.introductionCardAdapter.IntroductionCardAdapter
import com.justcircleprod.catquiz.introduction.presentation.introductionCardAdapter.IntroductionCardItem
import com.justcircleprod.catquiz.main.presentation.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.abs

@AndroidEntryPoint
class IntroductionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityIntroductionBinding
    private val viewModel: IntroductionViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIntroductionBinding.inflate(layoutInflater)

        setupViewPager()
        setContentView(binding.root)
    }

    private fun setupViewPager() {
        val introductionCardItems = listOf(
            IntroductionCardItem(
                R.string.introduction_card_1_title,
                R.string.introduction_card_1_text,
                R.raw.introduction_card_item_animation_1
            ),
            IntroductionCardItem(
                R.string.introduction_card_2_title,
                R.string.introduction_card_2_text,
                R.raw.introduction_card_item_animation_2
            ),
            IntroductionCardItem(
                R.string.introduction_card_3_title,
                R.string.introduction_card_3_text,
                R.raw.introduction_card_item_animation_3_and_quiz_result_best_congratulation_animation_2
            ),
            IntroductionCardItem(
                R.string.introduction_card_4_title,
                R.string.introduction_card_4_text,
                R.raw.introduction_card_item_animation_4_and_quiz_result_good_congratulation_animation_3
            )
        )

        val adapter = IntroductionCardAdapter(
            introductionCardItems = introductionCardItems,
            onNextBtnClicked = { binding.viewPager.currentItem++ },
            onPlayBtnClicked = {
                viewModel.setIntroductionShown()
                startMainActivity()
            }
        )

        binding.viewPager.offscreenPageLimit = 1

        val nextItemVisiblePx =
            resources.getDimension(R.dimen.introduction_viewpager_next_item_visible)
        val currentItemHorizontalMarginPx =
            resources.getDimension(R.dimen.introduction_viewpager_current_item_horizontal_margin)
        val pageTranslationX = nextItemVisiblePx + currentItemHorizontalMarginPx

        val pageTransformer = ViewPager2.PageTransformer { page: View, position: Float ->
            page.translationX = -pageTranslationX * position
            page.scaleY = 1 - (0.25f * abs(position))
        }

        binding.viewPager.setPageTransformer(pageTransformer)

        val itemDecoration = HorizontalMarginItemDecoration(
            this,
            R.dimen.introduction_viewpager_current_item_horizontal_margin
        )
        binding.viewPager.addItemDecoration(itemDecoration)
        binding.viewPager.adapter = adapter

        // Force remeasure the first item
        binding.viewPager.post {
            recalculateViewPagerHeight(binding.viewPager)
        }

        binding.dotsIndicator.attachTo(binding.viewPager)

        // Recalculate height on page change
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                recalculateViewPagerHeight(binding.viewPager)
            }
        })
    }

    private fun recalculateViewPagerHeight(viewPager: ViewPager2) {
        val currentView =
            (viewPager.getChildAt(0) as RecyclerView).layoutManager?.findViewByPosition(viewPager.currentItem)
        currentView?.let { page ->
            val wMeasureSpec =
                View.MeasureSpec.makeMeasureSpec(page.width, View.MeasureSpec.EXACTLY)
            val hMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            page.measure(wMeasureSpec, hMeasureSpec)
            viewPager.layoutParams =
                (viewPager.layoutParams).also { lp -> lp.height = page.measuredHeight }

            viewPager.requestLayout()
            viewPager.invalidate()
        }
    }

    private fun startMainActivity() {
        if (viewModel.shouldStartMainActivity.hasActiveObservers()) return

        viewModel.shouldStartMainActivity.observe(this) {
            if (it) {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
                viewModel.shouldStartMainActivity.removeObservers(this)
            }
        }
    }
}