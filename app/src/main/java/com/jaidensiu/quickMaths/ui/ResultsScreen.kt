package com.jaidensiu.quickMaths.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ResultsScreen(
    elapsedTimeMs: Long,
    totalQuestions: Int,
    onBackToStart: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(modifier = modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues = innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = "All $totalQuestions solved!",
                style = MaterialTheme.typography.headlineMedium,
            )
            Text(
                text = Util.formatElapsedTime(elapsedTimeMs = elapsedTimeMs),
                style = MaterialTheme.typography.displayLarge,
                modifier = Modifier.padding(top = 16.dp),
            )
            Button(
                onClick = onBackToStart,
                modifier = Modifier.padding(top = 32.dp),
            ) {
                Text(text = "Back to start")
            }
        }
    }
}
