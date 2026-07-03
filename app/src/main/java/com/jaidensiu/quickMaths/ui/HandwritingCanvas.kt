package com.jaidensiu.quickMaths.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
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

@Composable
fun HandwritingCanvas(
    modifier: Modifier = Modifier,
    strokeWidth: Dp = 4.dp,
    strokeColor: Color = Color.Black,
    onStrokeFinished: (Path) -> Unit,
) {
    val strokes = remember { mutableStateListOf<Path>() }
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
                    var last = down.position
                    invalidateTick++

                    fun extendTo(position: Offset) {
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
                    }

                    while (true) {
                        val event = awaitPointerEvent()
                        val change = event.changes.first()
                        change.historical.forEach { extendTo(position = it.position) }
                        extendTo(position = change.position)
                        change.consume()
                        invalidateTick++
                        if (event.changes.none { it.pressed }) {
                            break
                        }
                    }

                    val finished = Path().apply { addPath(path = currentPath) }
                    strokes.add(finished)
                    currentPath.reset()
                    invalidateTick++
                    onStrokeFinished(finished)
                }
            }
    ) {
        invalidateTick
        val style = Stroke(
            width = strokeWidth.toPx(),
            cap = StrokeCap.Round,
            join = StrokeJoin.Round,
        )
        strokes.forEach { drawPath(path = it, color = strokeColor, style = style) }
        drawPath(path = currentPath, color = strokeColor, style = style)
    }
}
