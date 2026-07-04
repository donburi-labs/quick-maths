package com.jaidensiu.quickMaths.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun GameScreen(
    onBackToStart: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: GameViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val strokes = remember { mutableStateListOf<HandwritingStroke>() }

    LaunchedEffect(key1 = state.canvasClearKey) {
        strokes.clear()
    }

    Scaffold(modifier = modifier.fillMaxSize()) { innerPadding ->
        if (state.isFinished) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues = innerPadding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = "All ${state.totalQuestions} solved!",
                    style = MaterialTheme.typography.headlineMedium,
                )
                Button(
                    onClick = onBackToStart,
                    modifier = Modifier.padding(top = 24.dp),
                ) {
                    Text(text = "Back to start")
                }
            }
        } else {
            Column(
                modifier = Modifier.padding(paddingValues = innerPadding),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = "${state.questionNumber} / ${state.totalQuestions}",
                    style = MaterialTheme.typography.labelLarge,
                )
                Text(
                    text = state.question?.text.orEmpty(),
                    style = MaterialTheme.typography.displayMedium,
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = if (state.isRecognizerReady) state.recognizedText else "Loading model…",
                        modifier = Modifier.padding(start = 16.dp),
                    )
                    TextButton(
                        onClick = {
                            strokes.clear()
                            viewModel.onClear()
                        }
                    ) {
                        Text(text = "Clear")
                    }
                }
                HandwritingCanvas(
                    strokes = strokes,
                    onStrokeFinished = {
                        strokes.add(it)
                        viewModel.onStrokeFinished(stroke = it)
                    },
                    modifier = Modifier
                        .weight(weight = 1f)
                        .onSizeChanged { viewModel.onCanvasSizeChanged(size = it) },
                )
            }
        }
    }
}
