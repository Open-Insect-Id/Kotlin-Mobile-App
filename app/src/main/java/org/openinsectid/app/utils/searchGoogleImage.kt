package org.openinsectid.app.utils


import android.net.Uri
import org.json.JSONObject
import org.openinsectid.app.data.FetchedImage
import java.net.HttpURLConnection
import java.net.URL

fun searchGoogleImages(query: String, apiKey: String, cx: String): List<FetchedImage> {
    val urlStr = "https://www.googleapis.com/customsearch/v1" +
            "?q=${Uri.encode(query)}" +
            "&cx=$cx&searchType=image&num=10&key=$apiKey"

    val conn = (URL(urlStr).openConnection() as HttpURLConnection).apply {
        connectTimeout = 10_000
        readTimeout = 10_000
        requestMethod = "GET"
    }

    val code = conn.responseCode
    if (code != 200) {
        val body = conn.errorStream?.bufferedReader()?.use { it.readText() }
        throw Exception("HTTP $code – $body")
    }

    val body = conn.inputStream.bufferedReader().use { it.readText() }
    val json = JSONObject(body)
    val items = json.optJSONArray("items") ?: return emptyList()

    val images = mutableListOf<FetchedImage>()
    for (i in 0 until items.length()) {
        val item = items.getJSONObject(i)
        val imageLink = item.optString("link")
        val thumbnail = item.optJSONObject("image")?.optString("thumbnailLink") ?: imageLink
        val title = item.optString("title")
        if (imageLink.isNotBlank()) {
            images += FetchedImage(
                image = imageLink,
                thumbnail = thumbnail,
                title = title
            )
        }
    }
    return images
}
