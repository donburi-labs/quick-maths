package com.jaidensiu.quickMaths.ui

import com.jaidensiu.quickMaths.domain.ThemePreference

enum class ModelStatus {
    LOADING,
    READY,
    ERROR,
}

data class StartState(
    val modelStatus: ModelStatus = ModelStatus.LOADING,
    val themePreference: ThemePreference = ThemePreference.LIGHT,
    val bestTimeMs: Long? = null,
)
