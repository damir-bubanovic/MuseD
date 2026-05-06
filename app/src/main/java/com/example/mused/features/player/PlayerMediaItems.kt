package com.example.mused.features.player

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import androidx.core.net.toUri
import androidx.documentfile.provider.DocumentFile
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import java.io.ByteArrayOutputStream

fun buildMediaItems(
    context: Context,
    files: List<String>
): List<MediaItem> {
    return files.map { fileUri ->
        val itemUri = fileUri.toUri()
        val itemName =
            DocumentFile.fromSingleUri(context, itemUri)?.name ?: "Unknown song"

        val artworkBytes = loadArtworkBytes(context, fileUri)

        val metadata = MediaMetadata.Builder()
            .setTitle(itemName)
            .setArtist("MUSED")
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
            .setUri(itemUri)
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