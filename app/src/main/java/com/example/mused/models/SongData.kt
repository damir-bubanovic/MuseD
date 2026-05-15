package com.example.mused.models

data class SongData(
    val uri: String,
    val title: String,
    val lastModified: Long,
    val durationMs: Long = 0L,
    val artist: String = "MUSED",
    val album: String? = null,
    val albumArtCached: Boolean = false
)