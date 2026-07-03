package com.jaidensiu.quickMaths

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import com.jaidensiu.quickMaths.ui.HandwritingCanvas
import com.jaidensiu.quickMaths.ui.theme.QuickMathsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            QuickMathsTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(modifier = Modifier.padding(paddingValues = innerPadding)) {
                        Text(text = "Quick Maths")
                        HandwritingCanvas(
                            modifier = Modifier.weight(weight = 1f),
                            onStrokeFinished = {},
                        )
                    }
                }
            }
        }
    }
}
