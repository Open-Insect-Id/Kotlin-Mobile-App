package org.openinsectid.app

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.BlendMode.Companion.Screen
import androidx.navigation.compose.rememberNavController
import org.openinsectid.app.data.ImageStore
import org.openinsectid.app.ui.screens.MainNavHost
import org.openinsectid.app.ui.screens.MainScreen
import org.openinsectid.app.ui.theme.OpenInsectIdTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ImageStore.ensureImagesDir(applicationContext)

        enableEdgeToEdge()
        setContent {
            OpenInsectIdTheme {
                val navController = rememberNavController()
                MainNavHost(
                    navController = navController,
                    onImagePicked = { uri -> /* no-op global callback; screens handle storage */ }
                )
            }
        }
    }
}
