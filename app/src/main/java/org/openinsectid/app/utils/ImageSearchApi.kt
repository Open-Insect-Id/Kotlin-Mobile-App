package org.openinsectid.app.utils

import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import org.openinsectid.app.data.FetchedImage
import org.openinsectid.app.data.ImageSearchConfig
import org.openinsectid.app.data.ImageSearchError
import java.io.IOException
import androidx.core.net.toUri

private val httpClient by lazy { OkHttpClient() }

fun searchImages(query: String): List<FetchedImage> {
//     Try Pixabay first, then Unsplash
    val pixabay = try {
        searchPixabayImages(query)
    } catch (_: Throwable) {
        emptyList()
    }

    if (pixabay.isNotEmpty()) return pixabay


    val unsplash = try {
        searchUnsplashImages(query)
    } catch (_: Throwable) {
        emptyList()
    }

    return unsplash
}

private fun buildUrl(base: String, params: Map<String, String>): String {
    val builder = base.toUri().buildUpon()
    params.forEach { (k, v) -> builder.appendQueryParameter(k, v) }
    return builder.build().toString()
}

@Throws(ImageSearchError::class)
private fun searchPixabayImages(query: String): List<FetchedImage> {
    if (ImageSearchConfig.PIXABAY_API_KEY.isBlank()) return emptyList()

    val url = buildUrl(
        "https://pixabay.com/api/",
        mapOf(
            "key" to ImageSearchConfig.PIXABAY_API_KEY,
            "q" to query,
            "image_type" to "photo",
            "safesearch" to "true",
            "per_page" to "20"
        )
    )

    val request = Request.Builder().url(url).get().build()

    val response = try {
        httpClient.newCall(request).execute()
    } catch (e: IOException) {
        throw ImageSearchError.Network(e)
    }

    response.use { resp ->
        if (!resp.isSuccessful) {
            throw ImageSearchError.Api(
                IOException("Pixabay request failed: ${resp.code} ${resp.message}")
            )
        }


        val bodyStr = resp.body.string()
        val json = JSONObject(bodyStr)
        val hits = json.optJSONArray("hits") ?: return emptyList()

        val result = mutableListOf<FetchedImage>()
        for (i in 0 until hits.length()) {
            val o = hits.getJSONObject(i)
            val thumb = o.optString("webformatURL", "")
            val full = o.optString("largeImageURL", thumb)
            val tags = o.optString("tags", "")
            if (thumb.isNotBlank()) {
                result.add(
                    FetchedImage(
                        thumbnail = thumb,
                        full = full,
                        title = tags.ifBlank { "Pixabay image" },
                        source = "Pixabay"
                    )
                )
            }
        }
        return result
    }
}

@Throws(ImageSearchError::class)
private fun searchUnsplashImages(query: String): List<FetchedImage> {
    if (ImageSearchConfig.UNSPLASH_ACCESS_KEY.isBlank()) return emptyList()

    val url = buildUrl(
        "https://api.unsplash.com/search/photos",
        mapOf(
            "query" to query,
            "per_page" to "20"
        )
    )

    val request = Request.Builder()
        .url(url)
        .get()
        .addHeader("Accept-Version", "v1")
        .addHeader("Authorization", "Client-ID ${ImageSearchConfig.UNSPLASH_ACCESS_KEY}")
        .build()

    val response = try {
        httpClient.newCall(request).execute()
    } catch (e: IOException) {
        throw ImageSearchError.Network(e)
    }

    response.use { resp ->
        if (!resp.isSuccessful) {
            throw ImageSearchError.Api(
                IOException("Unsplash request failed: ${resp.code} ${resp.message}")
            )
        }


        val bodyStr = resp.body.string()
        val json = JSONObject(bodyStr)
        val results = json.optJSONArray("results") ?: return emptyList()

        val list = mutableListOf<FetchedImage>()
        for (i in 0 until results.length()) {
            val o = results.getJSONObject(i)
            val urls = o.optJSONObject("urls") ?: continue
            val thumb = cleanUnsplashUrl(urls.optString("small", ""))
            val full = cleanUnsplashUrl(urls.optString("regular", thumb))
            val desc = o.optString("description")
            val alt = o.optString("alt_description")
            val title = when {
                desc.isNotBlank() -> desc
                alt.isNotBlank() -> alt
                else -> "Unsplash image"
            }
            if (thumb.isNotBlank()) {
                list.add(
                    FetchedImage(
                        thumbnail = thumb,
                        full = full,
                        title = title,
                        source = "Unsplash"
                    )
                )
            }
        }
        return list
    }
}


private fun cleanUnsplashUrl(url: String): String {
    return try {
        val uri = url.toUri()
        val builder = uri.buildUpon()
            .clearQuery()
            .fragment(null)
        builder.build().toString()
    } catch (_: Exception) {
        url
    }
}
