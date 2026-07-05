package io.github.donburilabs.quickMaths.ui

enum class ModelStatus {
    LOADING,
    READY,
    ERROR,
}

data class StartState(
    val modelStatus: ModelStatus = ModelStatus.LOADING,
    val bestTimeMs: Long? = null,
)
