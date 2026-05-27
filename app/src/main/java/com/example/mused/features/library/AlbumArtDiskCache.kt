package com.example.mused.features.library

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import androidx.core.net.toUri
import java.io.ByteArrayOutputStream
import java.io.File
import java.security.MessageDigest

object AlbumArtDiskCache {

    private const val CACHE_FOLDER_NAME = "album_art"
    private const val JPEG_QUALITY = 85

    fun loadArtworkBytes(
        context: Context,
        songUriString: String
    ): ByteArray? {
        val cachedFile =
            artworkFile(
                context = context,
                songUriString = songUriString
            )

        if (cachedFile.exists() && cachedFile.length() > 0L) {
            return runCatching {
                cachedFile.readBytes()
            }.getOrNull()
        }

        val artworkBytes =
            extractArtworkBytes(
                context = context,
                songUriString = songUriString
            ) ?: return null

        runCatching {
            cachedFile.parentFile?.mkdirs()
            cachedFile.writeBytes(artworkBytes)
        }

        return artworkBytes
    }

    fun loadArtworkBitmap(
        context: Context,
        songUriString: String
    ): Bitmap? {
        val artworkBytes =
            loadArtworkBytes(
                context = context,
                songUriString = songUriString
            ) ?: return null

        return BitmapFactory.decodeByteArray(
            artworkBytes,
            0,
            artworkBytes.size
        )
    }

    fun clear(
        context: Context
    ) {
        val cacheDirectory =
            File(
                context.cacheDir,
                CACHE_FOLDER_NAME
            )

        if (cacheDirectory.exists()) {
            cacheDirectory.deleteRecursively()
        }
    }

    private fun extractArtworkBytes(
        context: Context,
        songUriString: String
    ): ByteArray? {
        val retriever =
            MediaMetadataRetriever()

        return try {
            retriever.setDataSource(
                context,
                songUriString.toUri()
            )

            val embeddedPicture =
                retriever.embeddedPicture ?: return null

            val bitmap =
                BitmapFactory.decodeByteArray(
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

    private fun bitmapToJpegByteArray(
        bitmap: Bitmap
    ): ByteArray {
        val stream =
            ByteArrayOutputStream()

        bitmap.compress(
            Bitmap.CompressFormat.JPEG,
            JPEG_QUALITY,
            stream
        )

        return stream.toByteArray()
    }

    private fun artworkFile(
        context: Context,
        songUriString: String
    ): File {
        val cacheDirectory =
            File(
                context.cacheDir,
                CACHE_FOLDER_NAME
            )

        return File(
            cacheDirectory,
            "${sha256(songUriString)}.jpg"
        )
    }

    private fun sha256(
        value: String
    ): String {
        val digest =
            MessageDigest
                .getInstance("SHA-256")
                .digest(value.toByteArray())

        return digest.joinToString("") { byte ->
            "%02x".format(byte)
        }
    }
}
