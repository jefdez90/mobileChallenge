package com.discogs.mobilechallenge

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.discogs.mobilechallenge.presentation.navigation.AppNavigation
import com.discogs.mobilechallenge.ui.theme.MobileChallengeTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MobileChallengeTheme {
                AppNavigation()
            }
        }
    }
}
