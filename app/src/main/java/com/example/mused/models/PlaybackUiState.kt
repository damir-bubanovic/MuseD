package com.example.mused.models

data class PlaybackUiState(
    val currentSongName: String? = null,
    val currentSongUri: String? = null,
    val currentSongIndex: Int? = null,

    val isPlaying: Boolean = false,

    val playbackPosition: Int = 0,
    val playbackDuration: Int = 0,

    val shuffleEnabled: Boolean = false,
    val repeatMode: Int = 0,

    val sleepTimerRemainingSeconds: Int? = null,

    val showPlayerScreen: Boolean = false,
    val showSettingsScreen: Boolean = false
)