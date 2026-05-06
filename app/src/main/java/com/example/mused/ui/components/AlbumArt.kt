package com.example.mused.ui.components

import android.content.Context
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri

private fun loadAlbumArtImage(
    context: Context,
    songUriString: String
): ImageBitmap? {
    val retriever = MediaMetadataRetriever()

    return try {
        retriever.setDataSource(context, songUriString.toUri())

        val artworkBytes = retriever.embeddedPicture ?: return null
        val bitmap = BitmapFactory.decodeByteArray(
            artworkBytes,
            0,
            artworkBytes.size
        )

        bitmap?.asImageBitmap()
    } catch (_: Exception) {
        null
    } finally {
        retriever.release()
    }
}

@Composable
fun AlbumArt(
    songUri: String?,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val albumArt by produceState<ImageBitmap?>(initialValue = null, songUri) {
        value = songUri?.let {
            loadAlbumArtImage(context, it)
        }
    }

    Box(
        modifier = modifier
            .size(200.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Color.LightGray),
        contentAlignment = Alignment.Center
    ) {
        if (albumArt != null) {
            Image(
                bitmap = albumArt!!,
                contentDescription = "Album art",
                modifier = Modifier.matchParentSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            Text(
                text = "♪",
                style = MaterialTheme.typography.displayLarge
            )
        }
    }
}