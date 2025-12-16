package org.openinsectid.app.data


data class DdgDebugInfo(
    val query: String,
    val url: String,
    val httpCode: Int,
    val rawLength: Int
)
