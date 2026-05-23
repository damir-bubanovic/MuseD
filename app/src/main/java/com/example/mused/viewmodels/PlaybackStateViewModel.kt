package com.example.mused.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class PlaybackStateViewModel : ViewModel() {

    var playbackPosition by mutableIntStateOf(0)

    var playbackDuration by mutableIntStateOf(0)

    var pendingSeekPosition by mutableStateOf<Int?>(null)

    var savedSongUri by mutableStateOf<String?>(null)

    var savedPosition by mutableIntStateOf(0)

    var hasAutoResumed by mutableStateOf(false)

    private var hasInitialized = false

    fun initialize(
        savedSongUri: String?,
        savedPosition: Int
    ) {
        if (hasInitialized) return

        this.savedSongUri = savedSongUri
        this.savedPosition = savedPosition
        hasInitialized = true
    }

    fun updatePlaybackProgress(
        position: Int,
        duration: Int
    ) {
        playbackPosition = position
        playbackDuration = duration
    }

    fun updatePlaybackPosition(position: Int) {
        playbackPosition = position
    }

    fun updatePlaybackDuration(duration: Int) {
        playbackDuration = duration
    }

    fun updatePendingSeekPosition(position: Int?) {
        pendingSeekPosition = position
    }

    fun updateSavedPlayback(
        songUri: String?,
        position: Int
    ) {
        savedSongUri = songUri
        savedPosition = position
    }

    fun markAutoResumed() {
        hasAutoResumed = true
    }

    fun resetAutoResume() {
        hasAutoResumed = false
    }

    fun clearPlaybackProgress() {
        playbackPosition = 0
        playbackDuration = 0
        pendingSeekPosition = null
        savedSongUri = null
        savedPosition = 0
    }
}
