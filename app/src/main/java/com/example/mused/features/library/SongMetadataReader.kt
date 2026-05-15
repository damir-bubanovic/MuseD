package com.example.mused.features.library

import android.content.Context
import android.media.MediaMetadataRetriever
import androidx.core.net.toUri

data class SongMetadata(
    val title: String,
    val artist: String,
    val album: String?,
    val durationMs: Long
)

fun readSongMetadata(
    context: Context,
    uriString: String,
    fallbackTitle: String
): SongMetadata {
    val retriever = MediaMetadataRetriever()

    return try {
        retriever.setDataSource(context, uriString.toUri())

        val title =
            retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)
                ?.takeIf { it.isNotBlank() }
                ?: fallbackTitle

        val artist =
            retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST)
                ?.takeIf { it.isNotBlank() }
                ?: "MUSED"

        val album =
            retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM)
                ?.takeIf { it.isNotBlank() }

        val durationMs =
            retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                ?.toLongOrNull()
                ?: 0L

        SongMetadata(
            title = title,
            artist = artist,
            album = album,
            durationMs = durationMs
        )
    } catch (_: Exception) {
        SongMetadata(
            title = fallbackTitle,
            artist = "MUSED",
            album = null,
            durationMs = 0L
        )
    } finally {
        retriever.release()
    }
}