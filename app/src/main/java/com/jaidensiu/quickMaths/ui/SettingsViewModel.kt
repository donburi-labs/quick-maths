package com.jaidensiu.quickMaths.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jaidensiu.quickMaths.data.ThemeRepository
import com.jaidensiu.quickMaths.domain.ThemePreference
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val themeRepository: ThemeRepository,
) : ViewModel() {
    val theme = themeRepository.theme

    fun onThemeSelected(theme: ThemePreference) {
        viewModelScope.launch {
            themeRepository.setTheme(theme = theme)
        }
    }
}
