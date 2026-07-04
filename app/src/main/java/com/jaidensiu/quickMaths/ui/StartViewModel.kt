package com.jaidensiu.quickMaths.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jaidensiu.quickMaths.data.NumberRecognizer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StartViewModel @Inject constructor(
    private val recognizer: NumberRecognizer,
) : ViewModel() {
    private val _state = MutableStateFlow(value = StartState())
    val state: StateFlow<StartState> = _state.asStateFlow()

    init {
        prepareModel()
    }

    fun onRetry() {
        if (_state.value.modelStatus == ModelStatus.ERROR) {
            prepareModel()
        }
    }

    private fun prepareModel() {
        _state.update { it.copy(modelStatus = ModelStatus.LOADING) }
        viewModelScope.launch {
            runCatching { recognizer.prepare() }
                .onSuccess { _state.update { it.copy(modelStatus = ModelStatus.READY) } }
                .onFailure { _state.update { it.copy(modelStatus = ModelStatus.ERROR) } }
        }
    }
}
