package org.openinsectid.app.utils

import android.net.Uri
import android.util.Log
import org.json.JSONObject
import org.openinsectid.app.data.FetchedImage
import org.openinsectid.app.data.ImageSearchError
import java.net.HttpURLConnection
import java.net.URL


fun searchInaturalistImages(
    query: String
): List<FetchedImage> {

    val requestUrl =
        "https://api.inaturalist.org/v1/observations" +
                "?taxon_name=${Uri.encode(query)}" +
                "&photos=true" +
                "&per_page=12"

    val conn = (URL(requestUrl).openConnection() as HttpURLConnection).apply {
        connectTimeout = 10_000
        readTimeout = 10_000
        requestMethod = "GET"
    }

    val code = conn.responseCode
    if (code != 200) {
        val body = conn.errorStream?.bufferedReader()?.use { it.readText() }
        throw ImageSearchError.Http(code, body)
    }

    val body = conn.inputStream.bufferedReader().use { it.readText() }

    try {
        val json = JSONObject(body)
        Log.d("result", json.toString())
        val results = json.getJSONArray("results")

        val images = mutableListOf<FetchedImage>()

        for (i in 0 until results.length()) {
            val photos = results
                .getJSONObject(i)
                .optJSONArray("photos") ?: continue

            for (j in 0 until photos.length()) {
                val photoObj = photos.optJSONObject(j) ?: continue

                val imageUrl = photoObj.optString("medium_url")
                val thumbUrl = photoObj.optString("small_url")

                if (imageUrl.isBlank() || thumbUrl.isBlank()) continue

                images += FetchedImage(
                    image = imageUrl,
                    thumbnail = thumbUrl,
                    title = query
                )
            }
        }
        Log.d("ImageSearch", "Parsed ${images.size} images from iNaturalist")


        return images

    } catch (t: Throwable) {
        throw ImageSearchError.Parsing(t)
    }
}
