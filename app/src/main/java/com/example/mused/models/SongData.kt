package com.example.mused.models

data class SongData(
    val uri: String,
    val title: String,
    val lastModified: Long,
    val albumArtCached: Boolean = false
)