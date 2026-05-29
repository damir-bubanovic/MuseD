package com.example.mused.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.session.MediaController
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PlaybackStateViewModel : ViewModel() {

    var playbackPosition by mutableIntStateOf(0)

    var playbackDuration by mutableIntStateOf(0)

    var pendingSeekPosition by mutableStateOf<Int?>(null)

    var savedSongUri by mutableStateOf<String?>(null)

    var savedPosition by mutableIntStateOf(0)

    var hasAutoResumed by mutableStateOf(false)

    private var hasInitialized = false
    private var progressTrackingJob: Job? = null

    fun initialize(
        savedSongUri: String?,
        savedPosition: Int
    ) {
        if (hasInitialized) return

        this.savedSongUri = savedSongUri
        this.savedPosition = savedPosition
        hasInitialized = true
    }

    fun startProgressTracking(
        controller: MediaController,
        durationProvider: () -> Int,
        onProgressChanged: (position: Int, duration: Int) -> Unit,
        savePlaybackState: () -> Unit
    ) {
        stopProgressTracking()

        progressTrackingJob = viewModelScope.launch {
            while (controller.isPlaying) {
                val position =
                    controller.currentPosition
                        .toInt()
                        .coerceAtLeast(0)

                val duration = durationProvider()

                updatePlaybackProgress(
                    position = position,
                    duration = duration
                )

                onProgressChanged(
                    position,
                    duration
                )

                savePlaybackState()

                delay(1000L)
            }
        }
    }

    fun stopProgressTracking() {
        progressTrackingJob?.cancel()
        progressTrackingJob = null
    }

    fun updatePlaybackProgress(
        position: Int,
        duration: Int
    ) {
        playbackPosition = position
        playbackDuration = duration
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

    override fun onCleared() {
        stopProgressTracking()
        super.onCleared()
    }
}