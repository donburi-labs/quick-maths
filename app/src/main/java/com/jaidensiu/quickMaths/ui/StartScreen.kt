package com.jaidensiu.quickMaths.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun StartScreen(
    onStartGame: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: StartViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(modifier = modifier.fillMaxSize()) { innerPadding ->
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
        }
    }
}
