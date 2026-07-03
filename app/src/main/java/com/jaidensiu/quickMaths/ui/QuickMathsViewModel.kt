package com.jaidensiu.quickMaths.ui

import androidx.compose.ui.graphics.Path
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class QuickMathsViewModel : ViewModel() {
    private val _state = MutableStateFlow(value = QuickMathsState())
    val state: StateFlow<QuickMathsState> = _state.asStateFlow()

    fun onStrokeFinished(stroke: Path) {
        // TODO: feed strokes into recognition and update state
    }
}
