package com.jaidensiu.quickMaths.ui

import com.jaidensiu.quickMaths.domain.MathQuestion

data class GameState(
    val recognizedText: String = "",
    val isRecognizerReady: Boolean = false,
    val question: MathQuestion? = null,
    val questionNumber: Int = 1,
    val totalQuestions: Int = 20,
    val isFinished: Boolean = false,
    val canvasClearKey: Int = 0,
)
