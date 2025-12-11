package org.openinsectid.app.ui.components

import android.content.Context
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.camera.core.ImageCapture
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview as CameraPreview
import androidx.lifecycle.LifecycleOwner
import androidx.core.content.ContextCompat

object CameraPreview {
    /**
     * Creates/sets up a PreviewView and binds CameraX use cases.
     * onReady returns the PreviewView and ImageCapture instance.
     */
    fun setupPreviewView(
        context: Context,
        lifecycleOwner: LifecycleOwner,
        onReady: (PreviewView, ImageCapture) -> Unit
    ): PreviewView {
        val previewView = PreviewView(context)
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            // Preview use case
            val previewUseCase = CameraPreview.Builder().build().also {
                it.surfaceProvider = previewView.surfaceProvider
            }

            // Image capture use case
            val imageCapture = ImageCapture.Builder().build()

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    CameraSelector.DEFAULT_BACK_CAMERA,
                    previewUseCase,
                    imageCapture
                )
                onReady(previewView, imageCapture)
            } catch (_: Exception) {
                // ignore errors for now (log in production)
            }
        }, ContextCompat.getMainExecutor(context))
        return previewView
    }
}
