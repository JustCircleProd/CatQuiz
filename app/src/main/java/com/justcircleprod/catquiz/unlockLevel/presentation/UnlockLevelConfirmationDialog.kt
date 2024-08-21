package com.justcircleprod.catquiz.unlockLevel.presentation

import android.animation.LayoutTransition
import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import com.justcircleprod.catquiz.R
import com.justcircleprod.catquiz.databinding.DialogUnlockLevelConfirmationBinding

class UnlockLevelConfirmationDialog : DialogFragment() {

    companion object {
        const val SHOULD_UNLOCK_A_LEVEL_RESULT_KEY = "SHOULD_UNLOCK_A_LEVE"
    }

    private lateinit var binding: DialogUnlockLevelConfirmationBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialogBuilder = AlertDialog.Builder(requireContext(), R.style.DialogRoundedCorner)

        binding = DialogUnlockLevelConfirmationBinding.inflate(layoutInflater)

        enableAnimations()
        binding.cancelBtn.setOnClickListener { dialog?.cancel() }
        binding.cancelBtn2.setOnClickListener { dialog?.cancel() }
        setOnSubmitClickListeners()

        dialogBuilder.setView(binding.root).setCancelable(true)
        return dialogBuilder.create()
    }

    private fun enableAnimations() {
        binding.root.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
    }

    private fun setOnSubmitClickListeners() {
        binding.submitBtn.setOnClickListener {
            setFragmentResult(
                SHOULD_UNLOCK_A_LEVEL_RESULT_KEY, bundleOf(
                    SHOULD_UNLOCK_A_LEVEL_RESULT_KEY to true
                )
            )

            dismiss()
        }
    }
}