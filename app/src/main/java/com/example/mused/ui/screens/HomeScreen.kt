package com.example.mused.ui.screens

import android.content.BroadcastReceiver
import android.content.ComponentName
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
import androidx.media3.session.SessionToken
import com.example.mused.features.folders.rememberFolderPickerLauncher
import com.example.mused.features.player.MusicService
import com.example.mused.features.player.PlaybackController
import com.example.mused.viewmodels.PlaybackStateViewModel
import com.example.mused.viewmodels.HomeViewModel
import com.google.common.util.concurrent.MoreExecutors
import kotlinx.coroutines.delay

@Suppress("AssignedValueIsNeverRead")
@OptIn(UnstableApi::class, ExperimentalAnimationApi::class)
@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val homeViewModel: HomeViewModel = viewModel()
    val playbackUiState = homeViewModel.playbackUiState

    var showPlayerScreen by remember { mutableStateOf(false) }
    var showSettingsScreen by remember { mutableStateOf(false) }

    var selectedFolderUris by remember {
        mutableStateOf(homeViewModel.selectedFolderUris)
    }

    var songs by remember {
        mutableStateOf(homeViewModel.songs)
    }

    var mediaController by remember { mutableStateOf<MediaController?>(null) }

    val playbackStateViewModel: PlaybackStateViewModel = viewModel()

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
        mutableStateOf(homeViewModel.dynamicThemeEnabled)
    }

    var equalizerEnabled by remember {
        mutableStateOf(homeViewModel.equalizerEnabled)
    }

    var searchQuery by remember {
        mutableStateOf(homeViewModel.searchQuery)
    }

    var sleepTimerRemainingSeconds by remember { mutableStateOf<Int?>(null) }

    var sortMode by remember {
        mutableStateOf(homeViewModel.sortMode)
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
            songs = homeViewModel.loadSongsFromSelectedFolders()
        }
    }

    LaunchedEffect(Unit) {
        val sessionToken = SessionToken(
            context,
            ComponentName(context, MusicService::class.java)
        )

        val controllerFuture =
            MediaController.Builder(context, sessionToken).buildAsync()

        controllerFuture.addListener(
            {
                mediaController = controllerFuture.get()

                mediaController?.shuffleModeEnabled = isShuffleEnabled
                mediaController?.repeatMode =
                    PlaybackController.toMedia3RepeatMode(selectedRepeatMode)
            },
            MoreExecutors.directExecutor()
        )
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

    LaunchedEffect(mediaController, playbackUiState.isPlaying) {
        while (playbackUiState.isPlaying) {
            val controller = mediaController ?: break

            playbackStateViewModel.playbackPosition =
                controller.currentPosition
                    .toInt()
                    .coerceAtLeast(0)

            playbackStateViewModel.playbackDuration =
                currentPlaybackDuration()

            homeViewModel.setPlaybackPosition(
                position = playbackStateViewModel.playbackPosition,
                duration = playbackStateViewModel.playbackDuration
            )

            savePlaybackState()

            delay(1000)
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

        playbackStateViewModel.savedSongUri = null
        playbackStateViewModel.savedPosition = 0
        playbackStateViewModel.playbackPosition = 0
        playbackStateViewModel.playbackDuration = 0
        playbackStateViewModel.pendingSeekPosition = null

        showPlayerScreen = false

        homeViewModel.setCurrentSong(
            songName = null,
            songUri = null,
            songIndex = null
        )

        homeViewModel.setIsPlaying(false)

        homeViewModel.setPlaybackPosition(
            position = 0,
            duration = 0
        )

        context.getSharedPreferences(
            "mused_prefs",
            Context.MODE_PRIVATE
        ).edit {
            remove("current_song_uri")
            remove("current_song_index")
            remove("current_position_ms")
        }
    }

    fun clearFolders() {
        mediaController?.pause()

        songs = homeViewModel.clearFolders()
        selectedFolderUris = homeViewModel.selectedFolderUris

        playbackStateViewModel.savedSongUri = null
        playbackStateViewModel.savedPosition = 0
        playbackStateViewModel.playbackPosition = 0
        playbackStateViewModel.playbackDuration = 0
        playbackStateViewModel.pendingSeekPosition = null

        playbackStateViewModel.hasAutoResumed = false
        showPlayerScreen = false
        sleepTimerRemainingSeconds = null

        homeViewModel.setCurrentSong(
            songName = null,
            songUri = null,
            songIndex = null
        )

        homeViewModel.setIsPlaying(false)

        homeViewModel.setPlaybackPosition(
            position = 0,
            duration = 0
        )

        context.getSharedPreferences(
            "mused_prefs",
            Context.MODE_PRIVATE
        ).edit {
            remove("current_song_uri")
            remove("current_song_index")
            remove("current_position_ms")
        }
    }

    val playbackController = remember(
        mediaController,
        songs,
        homeViewModel.mediaItems,
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
                homeViewModel.mediaItems
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

            playbackController.playSong(savedIndex)

            mediaController?.seekTo(positionToResume.toLong())

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

            songs = homeViewModel.addFolder(folderUri)
            selectedFolderUris = homeViewModel.selectedFolderUris
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
                            homeViewModel.updateDynamicTheme(enabled)
                    },
                    equalizerEnabled = equalizerEnabled,
                    onEqualizerEnabledChange = { enabled ->
                        equalizerEnabled =
                            homeViewModel.updateEqualizerEnabled(enabled)
                    },
                    selectedEqualizerPreset =
                        homeViewModel.selectedEqualizerPresetLabel(),
                    onEqualizerPresetSelected = { preset ->
                        homeViewModel.updateEqualizerPreset(preset)
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
                    sortedSongs = homeViewModel.sortedSongs(),
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
                            homeViewModel.updateSearchQuery(newSearchQuery)
                    },
                    onSortModeChange = { newSortMode ->
                        sortMode =
                            homeViewModel.updateSortMode(newSortMode)
                    },
                    onPickFolder = {
                        openFolderPicker()
                    },
                    onRemoveFolder = { folderUriToRemove ->
                        songs = homeViewModel.removeFolder(folderUriToRemove)
                        selectedFolderUris = homeViewModel.selectedFolderUris
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