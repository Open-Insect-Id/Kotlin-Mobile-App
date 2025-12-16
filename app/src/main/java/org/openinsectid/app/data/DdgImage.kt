package org.openinsectid.app.data

import android.net.Uri

data class DdgImage(
    val image: String,
    val thumbnail: String,
    val title: String?
)


suspend fun searchDuckDuckGoImages(query: String): List<DdgImage> {
    val url =
        "https://duckduckgo.com/i.js?q=${Uri.encode(query)}&o=json"

    val connection = java.net.URL(url).openConnection() as java.net.HttpURLConnection
    connection.setRequestProperty("User-Agent", "Mozilla/5.0")
    connection.connectTimeout = 10_000
    connection.readTimeout = 10_000

    connection.inputStream.use { stream ->
        val text = stream.bufferedReader().readText()
        val json = org.json.JSONObject(text)
        val results = json.getJSONArray("results")

        return List(results.length()) { i ->
            val o = results.getJSONObject(i)
            DdgImage(
                image = o.getString("image"),
                thumbnail = o.getString("thumbnail"),
                title = o.optString("title")
            )
        }
    }
}
