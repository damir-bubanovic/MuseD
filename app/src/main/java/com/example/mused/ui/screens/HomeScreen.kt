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

    var isPlaying by remember { mutableStateOf(false) }

    var playbackPosition by remember { mutableIntStateOf(0) }
    var playbackDuration by remember { mutableIntStateOf(0) }

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

    var savedSongUri by remember {
        mutableStateOf(homeViewModel.savedSongUri)
    }

    var savedPosition by remember {
        mutableIntStateOf(homeViewModel.savedPosition)
    }

    var hasAutoResumed by remember { mutableStateOf(false) }

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

                mediaController?.repeatMode = when (selectedRepeatMode) {
                    1 -> Player.REPEAT_MODE_ONE
                    2 -> Player.REPEAT_MODE_ALL
                    else -> Player.REPEAT_MODE_OFF
                }
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
                        savedSongUri = currentSong.uri
                        playbackPosition = 0

                        homeViewModel.setCurrentSong(
                            songName = currentSong.title,
                            songUri = currentSong.uri,
                            songIndex = index
                        )
                    }
                }

                override fun onIsPlayingChanged(isPlayingNow: Boolean) {
                    isPlaying = isPlayingNow
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
            mediaController?.currentPosition?.toInt() ?: 0

        savedSongUri = currentFileUri
        savedPosition = currentPosition

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

    LaunchedEffect(mediaController, isPlaying) {
        while (isPlaying) {
            playbackPosition =
                mediaController?.currentPosition?.toInt() ?: 0

            playbackDuration =
                mediaController?.duration
                    ?.takeIf { it > 0 }
                    ?.toInt() ?: 0

            homeViewModel.setPlaybackPosition(
                position = playbackPosition,
                duration = playbackDuration
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

    LaunchedEffect(sleepTimerRemainingSeconds) {
        val remainingSeconds =
            sleepTimerRemainingSeconds ?: return@LaunchedEffect

        if (remainingSeconds <= 0) {
            mediaController?.pause()
            savePlaybackState()
            sleepTimerRemainingSeconds = null
            return@LaunchedEffect
        }

        delay(1000L)

        sleepTimerRemainingSeconds =
            remainingSeconds - 1
    }

    fun clearPlaybackState() {
        mediaController?.pause()

        savedSongUri = null
        savedPosition = 0

        playbackPosition = 0
        playbackDuration = 0

        isPlaying = false
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

        savedSongUri = null
        savedPosition = 0

        playbackPosition = 0
        playbackDuration = 0

        isPlaying = false
        hasAutoResumed = false
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

    fun playSong(index: Int) {
        if (index !in songs.indices) return

        val song = songs[index]

        savedSongUri = song.uri

        homeViewModel.setCurrentSong(
            songName = song.title,
            songUri = song.uri,
            songIndex = index
        )

        mediaController?.apply {
            setMediaItems(
                homeViewModel.mediaItems,
                index,
                0L
            )

            shuffleModeEnabled = isShuffleEnabled

            repeatMode = when (selectedRepeatMode) {
                1 -> Player.REPEAT_MODE_ONE
                2 -> Player.REPEAT_MODE_ALL
                else -> Player.REPEAT_MODE_OFF
            }

            prepare()
            play()
        }
    }

    LaunchedEffect(songs, savedSongUri, mediaController) {
        if (hasAutoResumed) return@LaunchedEffect
        if (songs.isEmpty()) return@LaunchedEffect
        if (savedSongUri == null) return@LaunchedEffect
        if (savedPosition <= 0) return@LaunchedEffect
        if (mediaController == null) return@LaunchedEffect

        val savedIndex =
            songs.indexOfFirst { song ->
                song.uri == savedSongUri
            }

        if (savedIndex != -1) {
            val positionToResume = savedPosition

            hasAutoResumed = true

            val song = songs[savedIndex]

            homeViewModel.setCurrentSong(
                songName = song.title,
                songUri = song.uri,
                songIndex = savedIndex
            )

            playSong(savedIndex)

            mediaController?.seekTo(positionToResume.toLong())
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
            hasAutoResumed = false
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
                    playbackPosition = playbackUiState.playbackPosition,
                    playbackDuration = playbackUiState.playbackDuration,
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
                        playbackPosition = newPosition

                        homeViewModel.setPlaybackPosition(
                            position = newPosition,
                            duration = playbackDuration
                        )
                    },
                    onSeekFinished = {
                        mediaController?.seekTo(
                            playbackPosition.toLong()
                        )

                        savePlaybackState()
                    },
                    onPrevious = {
                        val index =
                            playbackUiState.currentSongIndex
                                ?: return@PlayerScreen

                        if (index > 0) {
                            playSong(index - 1)
                        }
                    },
                    onPlayPause = {
                        mediaController?.let { controller ->
                            if (controller.isPlaying) {
                                controller.pause()
                                savePlaybackState()
                            } else {
                                controller.play()
                            }
                        }
                    },
                    onNext = {
                        val index =
                            playbackUiState.currentSongIndex
                                ?: return@PlayerScreen

                        if (index < songs.size - 1) {
                            playSong(index + 1)
                        }
                    },
                    onToggleShuffle = {
                        isShuffleEnabled = !isShuffleEnabled
                        mediaController?.shuffleModeEnabled = isShuffleEnabled

                        homeViewModel.savePlaybackState(
                            songUri = savedSongUri,
                            position = playbackPosition,
                            shuffleEnabled = isShuffleEnabled,
                            repeatMode = selectedRepeatMode
                        )
                    },
                    onChangeRepeatMode = {
                        selectedRepeatMode =
                            (selectedRepeatMode + 1) % 3

                        mediaController?.repeatMode =
                            when (selectedRepeatMode) {
                                1 -> Player.REPEAT_MODE_ONE
                                2 -> Player.REPEAT_MODE_ALL
                                else -> Player.REPEAT_MODE_OFF
                            }

                        homeViewModel.savePlaybackState(
                            songUri = savedSongUri,
                            position = playbackPosition,
                            shuffleEnabled = isShuffleEnabled,
                            repeatMode = selectedRepeatMode
                        )
                    },
                    onQueueSongClick = { index ->
                        playSong(index)
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
                        hasAutoResumed = false
                    },
                    onPlaySong = { index ->
                        playSong(index)
                        showPlayerScreen = true
                    },
                    onOpenPlayer = {
                        showPlayerScreen = true
                    },
                    onPlayPause = {
                        mediaController?.let { controller ->
                            if (controller.isPlaying) {
                                controller.pause()
                                savePlaybackState()
                            } else {
                                controller.play()
                            }
                        }
                    },
                    onOpenSettings = {
                        showSettingsScreen = true
                    }
                )
            }
        }
    }
}