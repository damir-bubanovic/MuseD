package com.example.mused.features.player

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

class PlaybackRuntimeState(
    savedSongUri: String?,
    savedPosition: Int
) {
    var playbackPosition by mutableIntStateOf(0)
    var playbackDuration by mutableIntStateOf(0)
    var pendingSeekPosition by mutableStateOf<Int?>(null)
    var savedSongUri by mutableStateOf(savedSongUri)
    var savedPosition by mutableIntStateOf(savedPosition)
    var hasAutoResumed by mutableStateOf(false)

    fun clearPlaybackProgress() {
        playbackPosition = 0
        playbackDuration = 0
        pendingSeekPosition = null
        savedSongUri = null
        savedPosition = 0
    }

    fun resetAutoResume() {
        hasAutoResumed = false
    }
}

@Composable
fun rememberPlaybackRuntimeState(
    savedSongUri: String?,
    savedPosition: Int
): PlaybackRuntimeState {
    return remember {
        PlaybackRuntimeState(
            savedSongUri = savedSongUri,
            savedPosition = savedPosition
        )
    }
}
