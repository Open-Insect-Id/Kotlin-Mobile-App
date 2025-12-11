package org.openinsectid.app.data

import android.content.Context
import android.net.Uri
import androidx.core.net.toUri
import java.io.File
import java.io.InputStream

object ImageStore {
    private const val IMAGES_DIR = "images"
    private const val TEMP_DIR = "temp"

    data class StoredImage(val fileName: String, val file: Uri)

    fun ensureImagesDir(context: Context) {
        File(context.filesDir, IMAGES_DIR).apply { if (!exists()) mkdirs() }
        File(context.filesDir, TEMP_DIR).apply { if (!exists()) mkdirs() }
    }

    fun getImagesDir(context: Context): File = File(context.filesDir, IMAGES_DIR)
    fun getTempDir(context: Context): File = File(context.filesDir, TEMP_DIR)

    fun createTempImageFile(context: Context): File {
        ensureImagesDir(context)
        val f = File(getTempDir(context), "temp_${System.currentTimeMillis()}.jpg")
        f.outputStream().use { /* create empty file */ }
        return f
    }

    fun moveTempToImages(context: Context, tempUri: Uri): StoredImage? {
        val tempPath = tempUri.path ?: return null
        val tempFile = File(tempPath)
        if (!tempFile.exists()) return null
        val dest = File(getImagesDir(context), "img_${System.currentTimeMillis()}.jpg")
        tempFile.copyTo(dest, overwrite = true)
        tempFile.delete()
        return StoredImage(dest.name, dest.toUri())
    }

    fun storeUriToAppImages(context: Context, sourceUri: Uri): StoredImage? {
        val input: InputStream? = context.contentResolver.openInputStream(sourceUri)
        input ?: return null
        val dest = File(getImagesDir(context), "img_${System.currentTimeMillis()}.jpg")
        dest.outputStream().use { out -> input.copyTo(out) }
        input.close()
        return StoredImage(dest.name, dest.toUri())
    }

    fun listHistory(context: Context): List<StoredImage> {
        val dir = getImagesDir(context)
        if (!dir.exists()) return emptyList()
        return dir.listFiles()?.sortedByDescending { it.lastModified() }?.map { StoredImage(it.name, it.toUri()) } ?: emptyList()
    }

    fun getLatest(context: Context): StoredImage? {
        return listHistory(context).firstOrNull()
    }

    fun delete(context: Context, fileName: String) {
        val f = File(getImagesDir(context), fileName)
        if (f.exists()) f.delete()
    }

    fun deleteTempIfExists(tempUri: Uri) {
        val p = tempUri.path ?: return
        File(p).takeIf { it.exists() }?.delete()
    }
}
