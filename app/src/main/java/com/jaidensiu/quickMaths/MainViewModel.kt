package com.jaidensiu.quickMaths

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jaidensiu.quickMaths.data.ThemeRepository
import com.jaidensiu.quickMaths.domain.ThemePreference
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    themeRepository: ThemeRepository,
) : ViewModel() {
    val theme: StateFlow<ThemePreference> = themeRepository.theme.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
        initialValue = ThemePreference.LIGHT,
    )
}
