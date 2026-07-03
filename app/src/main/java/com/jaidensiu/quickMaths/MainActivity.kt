package com.jaidensiu.quickMaths

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.jaidensiu.quickMaths.ui.QuickMathsScreen
import com.jaidensiu.quickMaths.ui.theme.QuickMathsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            QuickMathsTheme {
                QuickMathsScreen()
            }
        }
    }
}
