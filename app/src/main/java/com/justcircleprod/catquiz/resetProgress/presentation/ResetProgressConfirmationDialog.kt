package com.justcircleprod.catquiz.resetProgress.presentation

import android.animation.LayoutTransition
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.justcircleprod.catquiz.R
import com.justcircleprod.catquiz.databinding.DialogResetProgressConfirmationBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ResetProgressConfirmationDialog : DialogFragment() {

    private lateinit var binding: DialogResetProgressConfirmationBinding
    private val viewModel by viewModels<ResetProgressConfirmationViewModel>()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialogBuilder = AlertDialog.Builder(requireContext(), R.style.DialogRoundedCorner)

        binding = DialogResetProgressConfirmationBinding.inflate(layoutInflater)

        enableAnimations()
        setOnBtnClickListeners()

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

        setIsProgressResetObserver()
    }

    private fun enableAnimations() {
        binding.root.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
    }

    private fun setIsProgressResetObserver() {
        viewModel.isProgressReset.observe(viewLifecycleOwner) {
            if (it) {
                binding.title.text = getString(R.string.progress_was_reset_dialog_title)
                binding.hint.text = getString(R.string.progress_was_reset)
                binding.resetProgressConfirmationButtons.visibility = View.GONE
                binding.submitProgressWasResetBtn.visibility = View.VISIBLE
            } else {
                binding.title.text = getString(R.string.reset_progress_dialog_title)
                binding.hint.text = getString(R.string.reset_confirmation_question)
                binding.submitProgressWasResetBtn.visibility = View.GONE
                binding.resetProgressConfirmationButtons.visibility = View.VISIBLE
            }
        }
    }

    private fun setOnBtnClickListeners() {
        binding.titleCancelBtn.setOnClickListener { dialog?.cancel() }
        binding.cancelBtn.setOnClickListener { dialog?.cancel() }

        binding.resetProgressBtn.setOnClickListener {
            viewModel.resetProgress()
        }

        binding.submitProgressWasResetBtn.setOnClickListener {
            dismiss()
        }
    }
}