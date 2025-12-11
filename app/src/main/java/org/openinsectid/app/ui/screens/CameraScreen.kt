package org.openinsectid.app.ui.screens

import android.net.Uri
import android.widget.FrameLayout
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import org.openinsectid.app.data.ImageStore
import org.openinsectid.app.ui.components.CameraPreview

@Suppress("COMPOSE_APPLIER_CALL_MISMATCH")
@Composable
fun CameraScreen(navController: NavController) {
    val ctx = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }
    var lastTakenUri by remember { mutableStateOf<Uri?>(null) }

    val showingPreview = lastTakenUri == null

    Box(modifier = Modifier.fillMaxSize()) {

        // ------------------------------------------------------------
        // CAMERA MODE
        // ------------------------------------------------------------
        if (showingPreview) {
            AndroidView(
                factory = { context ->
                    FrameLayout(context).apply {
                        CameraPreview.setupPreviewView(
                            context = context,
                            lifecycleOwner = lifecycleOwner,
                            onReady = { previewView, ic ->
                                if (previewView.parent == null) addView(previewView)
                                imageCapture = ic
                            }
                        )
                    }
                },
                modifier = Modifier.fillMaxSize()
            )

            // Shutter button
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 40.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                Box(
                    modifier = Modifier
                        .size(88.dp)
                        .background(
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Button(
                        onClick = {
                            imageCapture?.let { ic ->
                                val saved = ImageStore.createTempImageFile(ctx)
                                val outputOptions =
                                    ImageCapture.OutputFileOptions.Builder(saved).build()

                                ic.takePicture(
                                    outputOptions,
                                    ContextCompat.getMainExecutor(ctx),
                                    object : ImageCapture.OnImageSavedCallback {
                                        override fun onError(exc: ImageCaptureException) {}

                                        override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                                            lastTakenUri = saved.absolutePath.toUri()
                                        }
                                    }
                                )
                            }
                        },
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        ),
                        modifier = Modifier.size(64.dp)
                    ) {}
                }
            }
        }

        // ------------------------------------------------------------
        // CONFIRMATION MODE (Full-screen image)
        // ------------------------------------------------------------
        if (!showingPreview && lastTakenUri != null) {

            // Full-screen captured image
            AsyncImage(
                model = lastTakenUri,
                contentDescription = null,
                modifier = Modifier.fillMaxSize()
            )

            // Left: Validate | Right: Delete
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(24.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {

                // Validate
                IconButton(
                    onClick = {
                        ImageStore.moveTempToImages(ctx, lastTakenUri!!)
                        navController.popBackStack()
                    },
                    modifier = Modifier
                        .size(64.dp)
                        .background(
                            MaterialTheme.colorScheme.primaryContainer,
                            shape = CircleShape
                        )
                ) {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = "Validate",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }

                // Delete / retake
                IconButton(
                    onClick = {
                        ImageStore.deleteTempIfExists(lastTakenUri!!)
                        lastTakenUri = null
                    },
                    modifier = Modifier
                        .size(64.dp)
                        .background(
                            MaterialTheme.colorScheme.errorContainer,
                            shape = CircleShape
                        )
                ) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Discard",
                        tint = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
        }
    }
}
