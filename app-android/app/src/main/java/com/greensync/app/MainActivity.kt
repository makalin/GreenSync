package com.greensync.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.greensync.app.ui.GreenSyncApp
import com.greensync.app.ui.theme.GreenSyncTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GreenSyncTheme {
                GreenSyncApp()
            }
        }
    }
}
