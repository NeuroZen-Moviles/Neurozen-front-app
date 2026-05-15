package com.example.neurozen_front
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.neurozen_front.neurozen.home.presentation.navigation.NeurozenApp
import com.example.neurozen_front.ui.theme.NeurozenTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NeurozenTheme {
                NeurozenApp()
            }
        }
    }
}
