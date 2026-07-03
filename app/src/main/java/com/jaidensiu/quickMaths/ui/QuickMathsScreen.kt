package com.jaidensiu.quickMaths.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Path
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun QuickMathsScreen(
    modifier: Modifier = Modifier,
    viewModel: QuickMathsViewModel = viewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val strokes = remember { mutableStateListOf<Path>() }

    Scaffold(modifier = modifier.fillMaxSize()) { innerPadding ->
        Column(modifier = Modifier.padding(paddingValues = innerPadding)) {
            Text(text = "Quick Maths")
            Text(text = state.recognizedText)
            TextButton(onClick = { strokes.clear() }) {
                Text(text = "Clear")
            }
            HandwritingCanvas(
                strokes = strokes,
                onStrokeFinished = {
                    strokes.add(it)
                    viewModel.onStrokeFinished(stroke = it)
                },
                modifier = Modifier.weight(weight = 1f),
            )
        }
    }
}
