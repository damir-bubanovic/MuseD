package com.example.mused.features.player

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import com.example.mused.models.SongData
import java.io.ByteArrayOutputStream

fun buildMediaItems(
    context: Context,
    songs: List<SongData>
): List<MediaItem> {
    return songs.map { song ->
        val artworkBytes = loadArtworkBytes(context, song.uri)

        val metadata = MediaMetadata.Builder()
            .setTitle(song.title)
            .setArtist(song.artist)
            .setAlbumTitle(song.album)
            .apply {
                artworkBytes?.let {
                    setArtworkData(
                        it,
                        MediaMetadata.PICTURE_TYPE_FRONT_COVER
                    )
                }
            }
            .build()

        MediaItem.Builder()
            .setUri(song.uri.toUri())
            .setMediaMetadata(metadata)
            .build()
    }
}

private fun loadArtworkBytes(
    context: Context,
    fileUri: String
): ByteArray? {
    val retriever = MediaMetadataRetriever()

    return try {
        retriever.setDataSource(context, fileUri.toUri())

        val embeddedPicture = retriever.embeddedPicture ?: return null
        val bitmap = BitmapFactory.decodeByteArray(
            embeddedPicture,
            0,
            embeddedPicture.size
        ) ?: return null

        bitmapToJpegByteArray(bitmap)
    } catch (_: Exception) {
        null
    } finally {
        retriever.release()
    }
}

private fun bitmapToJpegByteArray(bitmap: Bitmap): ByteArray {
    val stream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream)
    return stream.toByteArray()
}