package com.justcircleprod.catquiz.levels.presentation.levelAdapter

import androidx.recyclerview.widget.DiffUtil

class LevelItemDiffCallback(
    private var oldLevelItems: List<LevelItem>,
    private var newLevelItems: List<LevelItem>
) : DiffUtil.Callback() {

    override fun getOldListSize() = oldLevelItems.size

    override fun getNewListSize() = newLevelItems.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
        oldLevelItems[oldItemPosition].levelId == newLevelItems[newItemPosition].levelId

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
        oldLevelItems[oldItemPosition] == newLevelItems[newItemPosition]
}