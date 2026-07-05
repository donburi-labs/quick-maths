package io.github.donburilabs.quickMaths.ui

import androidx.compose.ui.graphics.Path
import io.github.donburilabs.quickMaths.domain.HandwritingPoint

data class HandwritingStroke(
    val path: Path,
    val points: List<HandwritingPoint>,
)
