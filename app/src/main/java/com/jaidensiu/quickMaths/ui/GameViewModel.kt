package com.jaidensiu.quickMaths.ui

import android.os.SystemClock
import androidx.compose.ui.unit.IntSize
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jaidensiu.quickMaths.data.BestTimeRepository
import com.jaidensiu.quickMaths.data.NumberRecognizer
import com.jaidensiu.quickMaths.data.SoundManager
import com.jaidensiu.quickMaths.domain.MathQuestion
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GameViewModel @Inject constructor(
    private val recognizer: NumberRecognizer,
    private val bestTimeRepository: BestTimeRepository,
    private val soundManager: SoundManager,
) : ViewModel() {
    private val _state = MutableStateFlow(value = GameState(question = MathQuestion.random()))
    val state: StateFlow<GameState> = _state.asStateFlow()

    private val startTimeMs = SystemClock.elapsedRealtime()
    private val strokes = mutableListOf<HandwritingStroke>()
    private var writingArea: IntSize? = null
    private var recognitionJob: Job? = null
    private var pencilIdleJob: Job? = null

    init {
        // No-op after StartScreen has prepared the model; covers process-death restore.
        viewModelScope.launch {
            runCatching { recognizer.prepare() }
        }
    }

    fun onCanvasSizeChanged(size: IntSize) {
        writingArea = size
    }

    fun onStrokeStarted() {
        recognitionJob?.cancel()
        soundManager.startPencil()
        scheduleIdleMute()
    }

    fun onStrokeMoved(speedPxPerMs: Float) {
        if (speedPxPerMs < PENCIL_MIN_SPEED_PX_PER_MS) {
            return
        }
        soundManager.updatePencilSpeed(speedPxPerMs = speedPxPerMs)
        scheduleIdleMute()
    }

    private fun scheduleIdleMute() {
        pencilIdleJob?.cancel()
        pencilIdleJob = viewModelScope.launch {
            delay(timeMillis = PENCIL_IDLE_TIMEOUT_MS)
            soundManager.mutePencil()
        }
    }

    fun onStrokeFinished(stroke: HandwritingStroke) {
        stopPencil()
        strokes.add(stroke)
        recognitionJob?.cancel()
        recognitionJob = viewModelScope.launch {
            runCatching {
                recognizer.recognize(
                    strokes = strokes.map { it.points },
                    writingAreaWidth = writingArea?.width?.toFloat(),
                    writingAreaHeight = writingArea?.height?.toFloat(),
                )
            }.onSuccess { text ->
                _state.update { it.copy(recognizedText = text.ifBlank { "?" }) }
                checkAnswer(text = text)
            }
        }
    }

    fun onClear() {
        recognitionJob?.cancel()
        strokes.clear()
        _state.update { it.copy(recognizedText = "") }
    }

    private fun checkAnswer(text: String) {
        val question = _state.value.question ?: return
        if (text.trim() != question.answer.toString()) {
            return
        }
        strokes.clear()
        _state.update { current ->
            if (current.questionNumber >= current.totalQuestions) {
                current.copy(
                    question = null,
                    isFinished = true,
                    elapsedTimeMs = SystemClock.elapsedRealtime() - startTimeMs,
                    recognizedText = "",
                    canvasClearKey = current.canvasClearKey + 1,
                )
            } else {
                current.copy(
                    question = MathQuestion.random(),
                    questionNumber = current.questionNumber + 1,
                    recognizedText = "",
                    canvasClearKey = current.canvasClearKey + 1,
                )
            }
        }
        val finished = _state.value
        if (finished.isFinished) {
            soundManager.playFinish()
            viewModelScope.launch {
                runCatching { bestTimeRepository.submitTime(timeMs = finished.elapsedTimeMs) }
            }
        } else {
            soundManager.playCorrect()
        }
    }

    private fun stopPencil() {
        pencilIdleJob?.cancel()
        soundManager.stopPencil()
    }

    override fun onCleared() {
        stopPencil()
    }

    private companion object {
        const val PENCIL_MIN_SPEED_PX_PER_MS = 0.01f
        const val PENCIL_IDLE_TIMEOUT_MS = 50L
    }
}
