package com.example.ui

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.theme.SmartCampusTheme
import com.example.viewmodel.SmartCampusViewModel

/**
 * Bridge entry point to launch Compose UI seamlessly from Java MainActivity.
 */
object AppEntry {
    @JvmStatic
    fun launch(activity: ComponentActivity) {
        activity.enableEdgeToEdge()
        activity.setContent {
            val mainViewModel: SmartCampusViewModel = viewModel()
            SmartCampusTheme {
                SmartCampusApp(viewModel = mainViewModel)
            }
        }
    }
}
