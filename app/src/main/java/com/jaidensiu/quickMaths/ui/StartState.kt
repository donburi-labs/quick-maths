package com.jaidensiu.quickMaths.ui

enum class ModelStatus {
    LOADING,
    READY,
    ERROR,
}

data class StartState(
    val modelStatus: ModelStatus = ModelStatus.LOADING,
)
