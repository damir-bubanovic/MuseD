package com.example.mused.utils

fun formatTime(ms: Int): String {
    val totalSeconds = ms / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%d:%02d".format(minutes, seconds)
}

fun formatFolderName(folderUri: String): String {
    val decoded = java.net.URLDecoder.decode(folderUri, "UTF-8")
    val path = decoded.substringAfterLast(":")
    val parts = path.split("/").filter { it.isNotBlank() }

    return parts.joinToString(" / ")
}