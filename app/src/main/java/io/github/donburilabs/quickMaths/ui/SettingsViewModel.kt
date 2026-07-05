package io.github.donburilabs.quickMaths.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.donburilabs.quickMaths.data.ThemeRepository
import io.github.donburilabs.quickMaths.domain.ThemePreference
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
