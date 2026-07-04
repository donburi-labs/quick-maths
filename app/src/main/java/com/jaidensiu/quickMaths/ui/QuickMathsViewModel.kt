package com.jaidensiu.quickMaths.ui

import androidx.compose.ui.unit.IntSize
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jaidensiu.quickMaths.data.NumberRecognizer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuickMathsViewModel @Inject constructor(
    private val recognizer: NumberRecognizer,
) : ViewModel() {
    private val _state = MutableStateFlow(value = QuickMathsState())
    val state: StateFlow<QuickMathsState> = _state.asStateFlow()

    private val strokes = mutableListOf<HandwritingStroke>()
    private var writingArea: IntSize? = null
    private var recognitionJob: Job? = null

    init {
        viewModelScope.launch {
            runCatching { recognizer.prepare() }
                .onSuccess { _state.update { it.copy(isRecognizerReady = true) } }
        }
    }

    fun onCanvasSizeChanged(size: IntSize) {
        writingArea = size
    }

    fun onStrokeFinished(stroke: HandwritingStroke) {
        strokes.add(stroke)
        recognitionJob?.cancel()
        recognitionJob = viewModelScope.launch {
            runCatching {
                recognizer.recognize(
                    strokes = strokes.map { it.points },
                    writingAreaWidth = writingArea?.width?.toFloat(),
                    writingAreaHeight = writingArea?.height?.toFloat(),
                )
            }.onSuccess { text -> _state.update { it.copy(recognizedText = text) } }
        }
    }

    fun onClear() {
        recognitionJob?.cancel()
        strokes.clear()
        _state.update { it.copy(recognizedText = "") }
    }

    override fun onCleared() {
        recognizer.close()
    }
}
