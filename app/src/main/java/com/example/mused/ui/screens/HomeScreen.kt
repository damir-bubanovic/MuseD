package com.example.mused.ui.screens

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import androidx.annotation.OptIn
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.edit
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaController
import com.example.mused.features.folders.rememberFolderPickerLauncher
import com.example.mused.features.player.MediaControllerManager
import com.example.mused.features.player.PlaybackController
import com.example.mused.viewmodels.HomeViewModel
import com.example.mused.viewmodels.LibraryViewModel
import com.example.mused.viewmodels.PlaybackStateViewModel
import com.example.mused.viewmodels.SettingsViewModel
import kotlinx.coroutines.delay

@Suppress("AssignedValueIsNeverRead")
@OptIn(UnstableApi::class, ExperimentalAnimationApi::class)
@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val homeViewModel: HomeViewModel = viewModel()
    val libraryViewModel: LibraryViewModel = viewModel()
    val settingsViewModel: SettingsViewModel = viewModel()
    val playbackStateViewModel: PlaybackStateViewModel = viewModel()

    val playbackUiState = homeViewModel.playbackUiState

    var showPlayerScreen by remember { mutableStateOf(false) }
    var showSettingsScreen by remember { mutableStateOf(false) }

    var selectedFolderUris by remember {
        mutableStateOf(libraryViewModel.selectedFolderUris)
    }

    var songs by remember {
        mutableStateOf(libraryViewModel.songs)
    }

    var mediaController by remember { mutableStateOf<MediaController?>(null) }

    val mediaControllerManager = remember(context) {
        MediaControllerManager(context)
    }

    LaunchedEffect(Unit) {
        playbackStateViewModel.initialize(
            savedSongUri = homeViewModel.savedSongUri,
            savedPosition = homeViewModel.savedPosition
        )
    }

    var isShuffleEnabled by remember {
        mutableStateOf(homeViewModel.shuffleEnabled)
    }

    var selectedRepeatMode by remember {
        mutableIntStateOf(homeViewModel.repeatMode)
    }

    var dynamicThemeEnabled by remember {
        mutableStateOf(settingsViewModel.dynamicThemeEnabled)
    }

    var equalizerEnabled by remember {
        mutableStateOf(settingsViewModel.equalizerEnabled)
    }

    var searchQuery by remember {
        mutableStateOf(libraryViewModel.searchQuery)
    }

    var sleepTimerRemainingSeconds by remember { mutableStateOf<Int?>(null) }

    var sortMode by remember {
        mutableStateOf(settingsViewModel.sortMode)
    }

    fun currentSongFallbackDuration(): Int {
        val currentIndex =
            homeViewModel.playbackUiState.currentSongIndex ?: return 0

        return songs.getOrNull(currentIndex)
            ?.durationMs
            ?.toInt()
            ?.takeIf { duration -> duration > 0 }
            ?: 0
    }

    fun currentPlaybackDuration(): Int {
        val controllerDuration =
            mediaController?.duration
                ?.takeIf { duration -> duration > 0 }
                ?.toInt()
                ?: 0

        return if (controllerDuration > 0) {
            controllerDuration
        } else {
            currentSongFallbackDuration()
        }
    }

    LaunchedEffect(Unit) {
        if (selectedFolderUris.isNotEmpty()) {
            songs = libraryViewModel.loadSongsFromSelectedFolders(sortMode)
        }
    }

    LaunchedEffect(sortMode, searchQuery, songs) {
        libraryViewModel.refreshVisibleSongs(sortMode)
    }

    DisposableEffect(mediaControllerManager) {
        mediaControllerManager.connect { controller ->
            mediaController = controller
            mediaControllerManager.applyPlaybackModes(
                shuffleEnabled = isShuffleEnabled,
                repeatMode = selectedRepeatMode
            )
        }

        onDispose {
            mediaControllerManager.release()
            mediaController = null
        }
    }

    DisposableEffect(mediaController, songs) {
        val controller = mediaController

        if (controller == null) {
            onDispose { }
        } else {
            val listener = object : Player.Listener {
                override fun onMediaItemTransition(
                    mediaItem: MediaItem?,
                    reason: Int
                ) {
                    val index = controller.currentMediaItemIndex
                    val currentSong = songs.getOrNull(index)

                    if (currentSong != null) {
                        playbackStateViewModel.savedSongUri = currentSong.uri
                        playbackStateViewModel.playbackPosition = 0
                        playbackStateViewModel.playbackDuration =
                            currentSong.durationMs
                                .toInt()
                                .takeIf { duration -> duration > 0 }
                                ?: currentPlaybackDuration()

                        playbackStateViewModel.pendingSeekPosition = null

                        homeViewModel.setCurrentSong(
                            songName = currentSong.title,
                            songUri = currentSong.uri,
                            songIndex = index
                        )

                        homeViewModel.setPlaybackPosition(
                            position = 0,
                            duration = playbackStateViewModel.playbackDuration
                        )
                    }
                }

                override fun onIsPlayingChanged(isPlayingNow: Boolean) {
                    homeViewModel.setIsPlaying(isPlayingNow)
                }
            }

            controller.addListener(listener)

            onDispose {
                controller.removeListener(listener)
            }
        }
    }

    fun savePlaybackState() {
        val currentIndex =
            homeViewModel.playbackUiState.currentSongIndex ?: return

        val currentFileUri =
            songs.getOrNull(currentIndex)?.uri ?: return

        val currentPosition =
            mediaController?.currentPosition?.toInt() ?: playbackStateViewModel.playbackPosition

        playbackStateViewModel.savedSongUri = currentFileUri
        playbackStateViewModel.savedPosition = currentPosition

        homeViewModel.savePlaybackState(
            songUri = currentFileUri,
            position = currentPosition,
            shuffleEnabled = isShuffleEnabled,
            repeatMode = selectedRepeatMode
        )

        context.getSharedPreferences(
            "mused_prefs",
            Context.MODE_PRIVATE
        ).edit {
            putInt("current_song_index", currentIndex)
        }
    }

    DisposableEffect(mediaController, playbackUiState.isPlaying) {
        val controller = mediaController

        if (controller != null && playbackUiState.isPlaying) {
            playbackStateViewModel.startProgressTracking(
                controller = controller,
                durationProvider = {
                    currentPlaybackDuration()
                },
                onProgressChanged = { position, duration ->
                    homeViewModel.setPlaybackPosition(
                        position = position,
                        duration = duration
                    )
                },
                savePlaybackState = {
                    savePlaybackState()
                }
            )
        }

        onDispose {
            playbackStateViewModel.stopProgressTracking()
        }
    }

    DisposableEffect(mediaController) {
        val noisyReceiver = object : BroadcastReceiver() {
            override fun onReceive(
                context: Context?,
                intent: Intent?
            ) {
                if (intent?.action == AudioManager.ACTION_AUDIO_BECOMING_NOISY) {
                    mediaController?.pause()
                    savePlaybackState()
                }
            }
        }

        context.registerReceiver(
            noisyReceiver,
            IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY)
        )

        onDispose {
            context.unregisterReceiver(noisyReceiver)
        }
    }

    fun clearPlaybackState() {
        mediaController?.pause()

        playbackStateViewModel.clearPlaybackProgress()

        showPlayerScreen = false

        homeViewModel.clearPlaybackState()
    }

    fun clearFolders() {
        mediaController?.pause()

        songs = libraryViewModel.clearFolders()
        selectedFolderUris = libraryViewModel.selectedFolderUris

        playbackStateViewModel.clearPlaybackProgress()
        playbackStateViewModel.resetAutoResume()

        showPlayerScreen = false
        sleepTimerRemainingSeconds = null

        homeViewModel.clearPlaybackState()
    }

    val playbackController = remember(
        mediaController,
        songs,
        libraryViewModel.mediaItems,
        isShuffleEnabled,
        selectedRepeatMode,
        playbackUiState.currentSongIndex,
        playbackStateViewModel.playbackPosition,
        playbackStateViewModel.playbackDuration,
        playbackStateViewModel.savedSongUri
    ) {
        PlaybackController(
            mediaControllerProvider = {
                mediaController
            },
            songsProvider = {
                songs
            },
            mediaItemsProvider = {
                libraryViewModel.mediaItems
            },
            shuffleEnabledProvider = {
                isShuffleEnabled
            },
            repeatModeProvider = {
                selectedRepeatMode
            },
            currentSongIndexProvider = {
                homeViewModel.playbackUiState.currentSongIndex
            },
            onSongStarted = { song, index, position, duration ->
                playbackStateViewModel.savedSongUri = song.uri
                playbackStateViewModel.playbackPosition = position
                playbackStateViewModel.playbackDuration = duration

                homeViewModel.setCurrentSong(
                    songName = song.title,
                    songUri = song.uri,
                    songIndex = index
                )

                homeViewModel.setPlaybackPosition(
                    position = position,
                    duration = duration
                )
            },
            onPlaybackPositionChanged = { position, duration ->
                playbackStateViewModel.playbackPosition = position
                playbackStateViewModel.playbackDuration = duration

                homeViewModel.setPlaybackPosition(
                    position = position,
                    duration = duration
                )
            },
            onPendingSeekChanged = { pendingPosition ->
                playbackStateViewModel.pendingSeekPosition = pendingPosition
            },
            onShuffleChanged = { enabled ->
                isShuffleEnabled = enabled
            },
            onRepeatModeChanged = { repeatMode ->
                selectedRepeatMode = repeatMode
            },
            savePlaybackState = {
                savePlaybackState()
            }
        )
    }

    LaunchedEffect(sleepTimerRemainingSeconds) {
        val remainingSeconds =
            sleepTimerRemainingSeconds ?: return@LaunchedEffect

        if (remainingSeconds <= 0) {
            playbackController.pauseAndSave()
            sleepTimerRemainingSeconds = null
            return@LaunchedEffect
        }

        delay(1000L)

        sleepTimerRemainingSeconds =
            remainingSeconds - 1
    }

    LaunchedEffect(songs, playbackStateViewModel.savedSongUri, mediaController) {
        if (playbackStateViewModel.hasAutoResumed) return@LaunchedEffect
        if (songs.isEmpty()) return@LaunchedEffect
        if (playbackStateViewModel.savedSongUri == null) return@LaunchedEffect
        if (playbackStateViewModel.savedPosition <= 0) return@LaunchedEffect
        if (mediaController == null) return@LaunchedEffect

        val savedIndex =
            songs.indexOfFirst { song ->
                song.uri == playbackStateViewModel.savedSongUri
            }

        if (savedIndex != -1) {
            val positionToResume = playbackStateViewModel.savedPosition

            playbackStateViewModel.hasAutoResumed = true

            val song = songs[savedIndex]

            playbackStateViewModel.playbackDuration =
                song.durationMs
                    .toInt()
                    .takeIf { duration -> duration > 0 }
                    ?: playbackStateViewModel.playbackDuration

            homeViewModel.setCurrentSong(
                songName = song.title,
                songUri = song.uri,
                songIndex = savedIndex
            )

            mediaController?.apply {
                setMediaItems(
                    libraryViewModel.mediaItems,
                    savedIndex,
                    positionToResume.toLong()
                )

                shuffleModeEnabled = isShuffleEnabled
                repeatMode = PlaybackController.toMedia3RepeatMode(selectedRepeatMode)

                prepare()
            }

            playbackStateViewModel.playbackPosition = positionToResume

            homeViewModel.setPlaybackPosition(
                position = positionToResume,
                duration = playbackStateViewModel.playbackDuration
            )

            showPlayerScreen = false
        }
    }

    val openFolderPicker =
        rememberFolderPickerLauncher { folderUri ->
            if (folderUri == null) {
                return@rememberFolderPickerLauncher
            }

            songs = libraryViewModel.addFolder(folderUri, sortMode)
            selectedFolderUris = libraryViewModel.selectedFolderUris
            playbackStateViewModel.hasAutoResumed = false
        }

    AnimatedContent(
        targetState = when {
            showSettingsScreen -> "settings"
            showPlayerScreen -> "player"
            else -> "library"
        },
        label = "ScreenTransitionAnimation"
    ) { screen ->

        when (screen) {
            "settings" -> {
                SettingsScreen(
                    modifier = modifier,
                    currentSortMode = sortMode,
                    dynamicThemeEnabled = dynamicThemeEnabled,
                    onDynamicThemeChange = { enabled ->
                        dynamicThemeEnabled =
                            settingsViewModel.updateDynamicTheme(enabled)
                    },
                    equalizerEnabled = equalizerEnabled,
                    onEqualizerEnabledChange = { enabled ->
                        equalizerEnabled =
                            settingsViewModel.updateEqualizerEnabled(enabled)
                    },
                    selectedEqualizerPreset =
                        settingsViewModel.selectedEqualizerPresetLabel(),
                    onEqualizerPresetSelected = { preset ->
                        settingsViewModel.updateEqualizerPreset(preset)
                    },
                    onBack = {
                        showSettingsScreen = false
                    },
                    onClearFolders = {
                        clearFolders()
                    },
                    onClearPlaybackState = {
                        clearPlaybackState()
                    }
                )
            }

            "player" -> {
                PlayerScreen(
                    modifier = modifier,
                    songName = playbackUiState.currentSongName,
                    songUri = playbackUiState.currentSongUri,
                    isPlaying = playbackUiState.isPlaying,
                    playbackPosition =
                        playbackStateViewModel.pendingSeekPosition ?: playbackStateViewModel.playbackPosition,
                    playbackDuration = playbackStateViewModel.playbackDuration,
                    isShuffleEnabled = isShuffleEnabled,
                    selectedRepeatMode = selectedRepeatMode,
                    queueSongs = songs.map { song ->
                        song.title
                    },
                    currentSongIndex = playbackUiState.currentSongIndex,
                    sleepTimerRemainingSeconds =
                        sleepTimerRemainingSeconds,
                    onBack = {
                        showPlayerScreen = false
                    },
                    onSeekChange = { newPosition ->
                        playbackStateViewModel.playbackPosition =
                            playbackController.seekTo(
                                newPosition = newPosition,
                                playbackDuration = playbackStateViewModel.playbackDuration
                            )
                    },
                    onSeekFinished = {
                        playbackStateViewModel.playbackPosition =
                            playbackController.finishSeek(
                                pendingSeekPosition = playbackStateViewModel.pendingSeekPosition,
                                playbackPosition = playbackStateViewModel.playbackPosition,
                                playbackDuration = playbackStateViewModel.playbackDuration
                            )
                    },
                    onPrevious = {
                        playbackController.playPrevious()
                    },
                    onPlayPause = {
                        playbackController.togglePlayPause()
                    },
                    onNext = {
                        playbackController.playNext()
                    },
                    onToggleShuffle = {
                        playbackController.toggleShuffle()
                    },
                    onChangeRepeatMode = {
                        playbackController.changeRepeatMode()
                    },
                    onQueueSongClick = { index ->
                        playbackController.playSong(index)
                    },
                    onSleepTimerSelected = { minutes ->
                        sleepTimerRemainingSeconds =
                            minutes?.times(60)
                    }
                )
            }

            else -> {
                LibraryScreen(
                    modifier = modifier,
                    selectedFolderUris = selectedFolderUris,
                    songs = songs,
                    sortedSongs = libraryViewModel.visibleSongs,
                    currentSongIndex = playbackUiState.currentSongIndex,
                    currentSongName = playbackUiState.currentSongName,
                    currentSongUri = playbackUiState.currentSongUri,
                    isPlaying = playbackUiState.isPlaying,
                    playbackPosition = playbackUiState.playbackPosition,
                    playbackDuration = playbackUiState.playbackDuration,
                    searchQuery = searchQuery,
                    sortMode = sortMode,
                    onSearchChange = { newSearchQuery ->
                        searchQuery =
                            libraryViewModel.updateSearchQuery(newSearchQuery, sortMode)
                    },
                    onSortModeChange = { newSortMode ->
                        sortMode =
                            settingsViewModel.updateSortMode(newSortMode)

                        libraryViewModel.refreshVisibleSongs(sortMode)
                    },
                    onPickFolder = {
                        openFolderPicker()
                    },
                    onRemoveFolder = { folderUriToRemove ->
                        songs = libraryViewModel.removeFolder(folderUriToRemove, sortMode)
                        selectedFolderUris = libraryViewModel.selectedFolderUris
                        playbackStateViewModel.hasAutoResumed = false
                    },
                    onPlaySong = { index ->
                        playbackController.playSong(index)
                        showPlayerScreen = true
                    },
                    onOpenPlayer = {
                        showPlayerScreen = true
                    },
                    onPlayPause = {
                        playbackController.togglePlayPause()
                    },
                    onOpenSettings = {
                        showSettingsScreen = true
                    }
                )
            }
        }
    }
}