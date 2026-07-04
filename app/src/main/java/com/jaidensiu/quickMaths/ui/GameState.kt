package com.jaidensiu.quickMaths.ui

import com.jaidensiu.quickMaths.domain.MathQuestion

const val TOTAL_QUESTIONS = 20

data class GameState(
    val recognizedText: String = "",
    val question: MathQuestion? = null,
    val questionNumber: Int = 1,
    val totalQuestions: Int = TOTAL_QUESTIONS,
    val isFinished: Boolean = false,
    val elapsedTimeMs: Long = 0L,
    val canvasClearKey: Int = 0,
)
