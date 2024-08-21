package com.justcircleprod.catquiz.levels.presentation.levelAdapter

import android.animation.LayoutTransition
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.justcircleprod.catquiz.R
import com.justcircleprod.catquiz.core.data.models.levels.LevelId
import com.justcircleprod.catquiz.core.presentation.animateProgress
import com.justcircleprod.catquiz.databinding.LevelItemBinding

class LevelItemAdapter(
    private var levelItems: List<LevelItem>,
    private val levelItemAdapterActions: LevelItemAdapterActions
) : RecyclerView.Adapter<LevelItemAdapter.LevelViewHolder>() {

    init {
        setHasStableIds(true)
    }

    override fun getItemId(position: Int) = levelItems[position].levelId.value.toLong()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LevelViewHolder {
        val binding = LevelItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LevelViewHolder(binding)
    }

    override fun getItemCount() = levelItems.size

    override fun onBindViewHolder(holder: LevelViewHolder, position: Int) {
        holder.bind(levelItems[position])
    }

    fun submitList(newLevelItems: List<LevelItem>) {
        val diffCallback = LevelItemDiffCallback(levelItems, newLevelItems)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        levelItems = newLevelItems
        diffResult.dispatchUpdatesTo(this)
    }

    inner class LevelViewHolder(private val binding: LevelItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.rootCardLayout.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
            binding.nameLayout.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
            binding.progressTextLayout.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
        }

        fun bind(levelItem: LevelItem) {
            val context = binding.root.context

            if (levelItem.levelId == LevelId.LEVEL_PLACEHOLDER) {
                binding.iconLock.visibility = View.GONE

                binding.nameTv.text = context.getString(levelItem.nameStringResId)

                binding.progressIndicator.progress = 0
                binding.progressIndicator.max = 0

                binding.progressTv.text = ""
                binding.progressCoinsIcon.visibility = View.GONE

                binding.root.setOnClickListener(null)

                return
            }

            binding.nameTv.text = context.getString(levelItem.nameStringResId)

            when (levelItem.isOpened) {
                true -> {
                    binding.iconLock.visibility = View.GONE

                    binding.progressIndicator.max = levelItem.questionNumber * 10
                    binding.progressIndicator.animateProgress(0, levelItem.progress * 10)

                    binding.progressTv.text =
                        context.getString(
                            R.string.level_progress,
                            levelItem.progress,
                            levelItem.questionNumber
                        )
                    binding.progressCoinsIcon.visibility = View.GONE

                    binding.root.setOnClickListener {
                        levelItemAdapterActions.startQuizActivity(levelId = levelItem.levelId)
                    }
                }

                false -> {
                    binding.iconLock.visibility = View.VISIBLE

                    binding.progressIndicator.progress = 0
                    binding.progressIndicator.max = 0

                    binding.progressTv.text =
                        context.getString(R.string.open_for_n_coins, levelItem.price)
                    binding.progressCoinsIcon.visibility = View.VISIBLE

                    binding.root.setOnClickListener {
                        levelItemAdapterActions.tryToUnlockLevel(
                            levelId = levelItem.levelId,
                            levelPrice = levelItem.price,
                            confettiAnimationView = binding.confettiAnimation
                        )
                    }
                }

                // LevelId.LEVEL_PASSED_QUESTIONS
                null -> {
                    binding.iconLock.visibility = View.GONE

                    binding.progressIndicator.max = levelItem.questionNumber
                    binding.progressIndicator.progress = levelItem.questionNumber

                    binding.progressTv.text = levelItem.questionNumber.toString()
                    binding.progressCoinsIcon.visibility = View.GONE

                    binding.root.setOnClickListener {
                        levelItemAdapterActions.startQuizActivity(levelId = levelItem.levelId)
                    }
                }
            }
        }
    }
}