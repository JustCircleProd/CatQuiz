package com.justcircleprod.catquiz.developersAndLicenses.presentation.developersAndLicensesAdapter

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.justcircleprod.catquiz.databinding.DevelopersAndLicenseDevelopersImageItemBinding
import com.justcircleprod.catquiz.databinding.DevelopersAndLicenseLicenseNameItemBinding
import com.justcircleprod.catquiz.databinding.DevelopersAndLicenseLineItemBinding
import com.justcircleprod.catquiz.databinding.DevelopersAndLicenseProjectInfoItemBinding
import com.justcircleprod.catquiz.databinding.DevelopersAndLicenseSectionTitleItemBinding
import com.justcircleprod.catquiz.databinding.DevelopersAndLicenseSpacerItemBinding
import com.justcircleprod.catquiz.databinding.DevelopersAndLicenseTextItemBinding


class DevelopersAndLicenseItemAdapter(
    private var items: List<DevelopersAndLicensesItem>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_SPACER = 0
        private const val VIEW_TYPE_SECTION_TITLE = 1
        private const val VIEW_TYPE_TEXT = 2
        private const val VIEW_TYPE_LINE = 3
        private const val VIEW_TYPE_DEVELOPERS_IMAGE = 4
        private const val VIEW_TYPE_LICENSE_NAME = 5
        private const val VIEW_TYPE_PROJECT_INFO = 6
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_SPACER -> {
                SpacerViewHolder(
                    DevelopersAndLicenseSpacerItemBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }

            VIEW_TYPE_SECTION_TITLE -> {
                SectionTitleViewHolder(
                    DevelopersAndLicenseSectionTitleItemBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }

            VIEW_TYPE_TEXT -> {
                TextViewHolder(
                    DevelopersAndLicenseTextItemBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }

            VIEW_TYPE_LINE -> {
                LineViewHolder(
                    DevelopersAndLicenseLineItemBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }

            VIEW_TYPE_DEVELOPERS_IMAGE -> {
                DevelopersImageViewHolder(
                    DevelopersAndLicenseDevelopersImageItemBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }

            VIEW_TYPE_LICENSE_NAME -> {
                LicenseNameViewHolder(
                    DevelopersAndLicenseLicenseNameItemBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }

            VIEW_TYPE_PROJECT_INFO -> {
                ProjectInfoViewHolder(
                    DevelopersAndLicenseProjectInfoItemBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }

            else -> {
                SpacerViewHolder(
                    DevelopersAndLicenseSpacerItemBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
        }
    }

    override fun getItemCount() = items.size

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is DevelopersAndLicensesItem.Spacer -> VIEW_TYPE_SPACER
            is DevelopersAndLicensesItem.SectionTitle -> VIEW_TYPE_SECTION_TITLE
            is DevelopersAndLicensesItem.Text -> VIEW_TYPE_TEXT
            is DevelopersAndLicensesItem.Line -> VIEW_TYPE_LINE
            is DevelopersAndLicensesItem.DevelopersImage -> VIEW_TYPE_DEVELOPERS_IMAGE
            is DevelopersAndLicensesItem.LicenseName -> VIEW_TYPE_LICENSE_NAME
            is DevelopersAndLicensesItem.ProjectInfo -> VIEW_TYPE_PROJECT_INFO
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = items[position]) {
            is DevelopersAndLicensesItem.Spacer ->
                (holder as SpacerViewHolder).bind((item))

            is DevelopersAndLicensesItem.SectionTitle ->
                (holder as SectionTitleViewHolder).bind(item)

            is DevelopersAndLicensesItem.Text ->
                (holder as TextViewHolder).bind(item)

            is DevelopersAndLicensesItem.Line -> {}

            is DevelopersAndLicensesItem.DevelopersImage -> {}

            is DevelopersAndLicensesItem.LicenseName ->
                (holder as LicenseNameViewHolder).bind(item)

            is DevelopersAndLicensesItem.ProjectInfo ->
                (holder as ProjectInfoViewHolder).bind(item)
        }
    }

    inner class SpacerViewHolder(private val binding: DevelopersAndLicenseSpacerItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: DevelopersAndLicensesItem.Spacer) {
            val params = binding.root.layoutParams
            params.height =
                binding.root.context.resources.getDimensionPixelSize(item.heightDimenResId)
            binding.root.layoutParams = params
        }
    }

    inner class SectionTitleViewHolder(private val binding: DevelopersAndLicenseSectionTitleItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: DevelopersAndLicensesItem.SectionTitle) {
            binding.root.setText(item.stringResId)
        }
    }

    inner class TextViewHolder(private val binding: DevelopersAndLicenseTextItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: DevelopersAndLicensesItem.Text) {
            binding.root.setText(item.stringResId)
        }
    }

    inner class LineViewHolder(binding: DevelopersAndLicenseLineItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    inner class DevelopersImageViewHolder(binding: DevelopersAndLicenseDevelopersImageItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    inner class LicenseNameViewHolder(private val binding: DevelopersAndLicenseLicenseNameItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: DevelopersAndLicensesItem.LicenseName) {
            binding.root.text = item.licenseName

            binding.root.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(item.licenseLink))
                binding.root.context.startActivity(intent)
            }
        }
    }

    inner class ProjectInfoViewHolder(private val binding: DevelopersAndLicenseProjectInfoItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: DevelopersAndLicensesItem.ProjectInfo) {
            binding.projectNameVersionTextView.text = item.projectNameVersion
            binding.projectInfoTextView.text = item.projectInfo
        }
    }
}