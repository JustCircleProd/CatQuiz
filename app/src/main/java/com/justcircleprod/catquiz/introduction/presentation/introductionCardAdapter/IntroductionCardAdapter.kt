package com.justcircleprod.catquiz.introduction.presentation.introductionCardAdapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.justcircleprod.catquiz.databinding.IntroductionCardItemBinding

class IntroductionCardAdapter(
    private val introductionCardItems: List<IntroductionCardItem>,
    private val onNextBtnClicked: () -> Unit,
    private val onPlayBtnClicked: () -> Unit
) :
    RecyclerView.Adapter<IntroductionCardAdapter.PagerViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagerViewHolder {
        val binding =
            IntroductionCardItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PagerViewHolder(binding)
    }

    override fun getItemCount(): Int = introductionCardItems.size

    override fun onBindViewHolder(holder: PagerViewHolder, position: Int) {
        holder.bind(position)
    }

    inner class PagerViewHolder(private val binding: IntroductionCardItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            val introductionCardItem = introductionCardItems[position]

            val context = binding.root.context

            binding.animation.setAnimation(introductionCardItem.animationRawResId)

            binding.title.text = context.getString(introductionCardItem.titleStringResId)

            if (absoluteAdapterPosition == 2) {
                binding.titleIconCoins.visibility = View.VISIBLE
            } else {
                binding.titleIconCoins.visibility = View.GONE
            }

            binding.text.text = context.getString(introductionCardItem.textStringResId)

            if (absoluteAdapterPosition == introductionCardItems.size - 1) {
                binding.nextBtn.visibility = View.GONE
                binding.playBtn.visibility = View.VISIBLE
            } else {
                binding.nextBtn.visibility = View.VISIBLE
                binding.playBtn.visibility = View.GONE
            }

            binding.nextBtn.setOnClickListener {
                onNextBtnClicked()
            }

            binding.playBtn.setOnClickListener {
                onPlayBtnClicked()
            }
        }
    }
}