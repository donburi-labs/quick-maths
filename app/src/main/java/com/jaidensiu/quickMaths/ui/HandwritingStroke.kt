package com.jaidensiu.quickMaths.ui

import androidx.compose.ui.graphics.Path
import com.jaidensiu.quickMaths.domain.HandwritingPoint

data class HandwritingStroke(
    val path: Path,
    val points: List<HandwritingPoint>,
)
