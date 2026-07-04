package com.jaidensiu.quickMaths.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.jaidensiu.quickMaths.domain.HandwritingPoint

@Composable
fun HandwritingCanvas(
    strokes: List<HandwritingStroke>,
    onStrokeFinished: (HandwritingStroke) -> Unit,
    modifier: Modifier = Modifier,
    onStrokeStarted: () -> Unit = {},
    onStrokeMoved: (speedPxPerMs: Float) -> Unit = {},
    strokeWidth: Dp = 4.dp,
    strokeColor: Color = MaterialTheme.colorScheme.onSurface,
) {
    val currentPath = remember { Path() }
    var invalidateTick by remember { mutableIntStateOf(value = 0) }

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .clipToBounds()
            .pointerInput(key1 = Unit) {
                awaitEachGesture {
                    val down = awaitFirstDown()
                    down.consume()
                    currentPath.reset()
                    currentPath.moveTo(x = down.position.x, y = down.position.y)
                    val points = mutableListOf(
                        HandwritingPoint(
                            x = down.position.x,
                            y = down.position.y,
                            timeMillis = down.uptimeMillis
                        )
                    )
                    var last = down.position
                    var lastMoveUptime = down.uptimeMillis
                    invalidateTick++
                    onStrokeStarted()

                    fun extendTo(position: Offset, timeMillis: Long) {
                        val mid = Offset(
                            x = (last.x + position.x) / 2f,
                            y = (last.y + position.y) / 2f
                        )
                        currentPath.quadraticTo(
                            x1 = last.x,
                            y1 = last.y,
                            x2 = mid.x,
                            y2 = mid.y
                        )
                        last = position
                        points.add(
                            HandwritingPoint(
                                x = position.x,
                                y = position.y,
                                timeMillis = timeMillis
                            )
                        )
                    }

                    while (true) {
                        val event = awaitPointerEvent()
                        val change = event.changes.first()
                        val previousPosition = last
                        val elapsedMs = change.uptimeMillis - lastMoveUptime
                        change.historical.forEach {
                            extendTo(position = it.position, timeMillis = it.uptimeMillis)
                        }
                        extendTo(position = change.position, timeMillis = change.uptimeMillis)
                        change.consume()
                        invalidateTick++
                        if (elapsedMs > 0) {
                            val distance = (change.position - previousPosition).getDistance()
                            onStrokeMoved(distance / elapsedMs)
                            lastMoveUptime = change.uptimeMillis
                        }
                        if (event.changes.none { it.pressed }) {
                            break
                        }
                    }

                    val finished = HandwritingStroke(
                        path = Path().apply { addPath(path = currentPath) },
                        points = points.toList(),
                    )
                    onStrokeFinished(finished)
                    currentPath.reset()
                    invalidateTick++
                }
            }
    ) {
        invalidateTick
        val style = Stroke(
            width = strokeWidth.toPx(),
            cap = StrokeCap.Round,
            join = StrokeJoin.Round,
        )
        strokes.forEach { drawPath(path = it.path, color = strokeColor, style = style) }
        drawPath(path = currentPath, color = strokeColor, style = style)
    }
}
