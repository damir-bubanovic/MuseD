package com.example.mused.features.player

import android.content.Context
import androidx.collection.LruCache
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import com.example.mused.features.library.AlbumArtDiskCache
import com.example.mused.models.SongData

private val artworkMemoryCache =
    LruCache<String, ByteArray>(50)

fun buildMediaItems(
    context: Context,
    songs: List<SongData>
): List<MediaItem> {
    return songs.map { song ->
        val artworkBytes =
            artworkMemoryCache[song.uri]
                ?: AlbumArtDiskCache
                    .loadArtworkBytes(
                        context = context,
                        songUriString = song.uri
                    )
                    ?.also { bytes ->
                        artworkMemoryCache.put(
                            song.uri,
                            bytes
                        )
                    }

        val metadata =
            MediaMetadata.Builder()
                .setTitle(song.title)
                .setArtist(song.artist)
                .setAlbumTitle(song.album)
                .apply {
                    artworkBytes?.let { bytes ->
                        setArtworkData(
                            bytes,
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
