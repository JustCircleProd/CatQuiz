package com.justcircleprod.catquiz.settings.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.justcircleprod.catquiz.core.domain.repositories.PassedQuestionRepository
import com.justcircleprod.catquiz.core.domain.repositories.SettingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingRepository: SettingRepository,
    private val passedQuestionRepository: PassedQuestionRepository
) : ViewModel() {

    val withoutQuizHints = settingRepository.getWithoutQuizHintsState().asLiveData()

    fun updateWithoutQuizHintsState(state: String) {
        viewModelScope.launch {
            settingRepository.editWithoutQuizHintsState(state)
        }
    }

    suspend fun getPassedQuestionCount() = passedQuestionRepository.getPassedQuestionsCount()
}