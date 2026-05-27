package com.example.mused.viewmodels

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import com.example.mused.features.preferences.AppPreferences
import com.example.mused.models.PlaybackUiState

class HomeViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val appPreferences =
        AppPreferences(
            getApplication<Application>().applicationContext
        )

    var savedSongUri: String? =
        appPreferences.loadSavedSongUri()
        private set

    var savedPosition: Int =
        appPreferences.loadSavedPosition()
        private set

    var savedSongIndex: Int? =
        appPreferences.loadSavedSongIndex()
        private set

    var shuffleEnabled: Boolean =
        appPreferences.loadShuffleEnabled()
        private set

    var repeatMode: Int =
        appPreferences.loadRepeatMode()
        private set

    var playbackUiState by mutableStateOf(
        PlaybackUiState(
            shuffleEnabled = shuffleEnabled,
            repeatMode = repeatMode
        )
    )
        private set

    fun savePlaybackState(
        songUri: String?,
        position: Int,
        shuffleEnabled: Boolean,
        repeatMode: Int,
        songIndex: Int? = playbackUiState.currentSongIndex
    ) {
        savedSongUri = songUri
        savedSongIndex = songIndex
        savedPosition = position
        this.shuffleEnabled = shuffleEnabled
        this.repeatMode = repeatMode

        playbackUiState =
            playbackUiState.copy(
                playbackPosition = position,
                shuffleEnabled = shuffleEnabled,
                repeatMode = repeatMode
            )

        appPreferences.savePlaybackState(
            songUri = songUri,
            songIndex = songIndex,
            position = position,
            shuffleEnabled = shuffleEnabled,
            repeatMode = repeatMode
        )
    }

    fun clearPlaybackState() {
        savedSongUri = null
        savedSongIndex = null
        savedPosition = 0

        playbackUiState =
            playbackUiState.copy(
                currentSongName = null,
                currentSongUri = null,
                currentSongIndex = null,
                isPlaying = false,
                playbackPosition = 0,
                playbackDuration = 0
            )

        appPreferences.clearPlaybackState()
    }

    fun setCurrentSong(
        songName: String?,
        songUri: String?,
        songIndex: Int?
    ) {
        playbackUiState =
            playbackUiState.copy(
                currentSongName = songName,
                currentSongUri = songUri,
                currentSongIndex = songIndex
            )
    }

    fun setIsPlaying(isPlaying: Boolean) {
        playbackUiState =
            playbackUiState.copy(
                isPlaying = isPlaying
            )
    }

    fun setPlaybackPosition(
        position: Int,
        duration: Int
    ) {
        playbackUiState =
            playbackUiState.copy(
                playbackPosition = position,
                playbackDuration = duration
            )
    }
}
