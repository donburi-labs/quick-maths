package com.jaidensiu.quickMaths.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jaidensiu.quickMaths.domain.ThemePreference

@Composable
fun StartScreen(
    onStartGame: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: StartViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val strokes = remember { mutableStateListOf<HandwritingStroke>() }

    Scaffold(modifier = modifier.fillMaxSize()) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            HandwritingCanvas(
                strokes = strokes,
                onStrokeFinished = { strokes.add(it) },
                modifier = Modifier.fillMaxSize(),
            )
            if (strokes.isNotEmpty()) {
                TextButton(
                    onClick = { strokes.clear() },
                    modifier = Modifier
                        .align(alignment = Alignment.TopEnd)
                        .padding(paddingValues = innerPadding)
                        .padding(end = 8.dp),
                ) {
                    Text(text = "Clear")
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues = innerPadding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = "Quick Maths",
                    style = MaterialTheme.typography.displayMedium,
                )
                Text(
                    text = "Solve 20 arithmetic problems as fast as you can",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(top = 8.dp),
                )
                state.bestTimeMs?.let { bestTimeMs ->
                    Text(
                        text = "Best time: ${Util.formatElapsedTime(elapsedTimeMs = bestTimeMs)}",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(top = 16.dp),
                    )
                }
                Button(
                    onClick = onStartGame,
                    enabled = state.modelStatus == ModelStatus.READY,
                    modifier = Modifier.padding(top = 32.dp),
                ) {
                    Text(
                        text = when (state.modelStatus) {
                            ModelStatus.LOADING -> "Loading model..."
                            ModelStatus.READY -> "Start Game"
                            ModelStatus.ERROR -> "Model loading error"
                        }
                    )
                }
                if (state.modelStatus == ModelStatus.ERROR) {
                    Text(
                        text = "Couldn't download the handwriting model",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(top = 16.dp),
                    )
                    TextButton(onClick = viewModel::onRetry) {
                        Text(text = "Retry")
                    }
                }
                SingleChoiceSegmentedButtonRow(modifier = Modifier.padding(top = 32.dp)) {
                    ThemePreference.entries.forEachIndexed { index, preference ->
                        SegmentedButton(
                            selected = state.themePreference == preference,
                            onClick = { viewModel.onThemeSelected(theme = preference) },
                            shape = SegmentedButtonDefaults.itemShape(
                                index = index,
                                count = ThemePreference.entries.size,
                            ),
                        ) {
                            Text(text = preference.label)
                        }
                    }
                }
            }
        }
    }
}

private val ThemePreference.label: String
    get() = when (this) {
        ThemePreference.LIGHT -> "Light"
        ThemePreference.DARK -> "Dark"
        ThemePreference.SYSTEM -> "System"
    }
