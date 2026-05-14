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
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.example.mused.features.folders.loadSongDataFromFolders
import com.example.mused.features.folders.rememberFolderPickerLauncher
import com.example.mused.features.player.MusicService
import com.example.mused.features.player.buildMediaItems
import com.example.mused.models.SongData
import com.google.common.util.concurrent.MoreExecutors
import kotlinx.coroutines.delay

@Suppress("AssignedValueIsNeverRead")
@OptIn(UnstableApi::class, ExperimentalAnimationApi::class)
@Composable
fun HomeScreen(modifier: Modifier = Modifier) {

    val context = LocalContext.current

    var showPlayerScreen by remember { mutableStateOf(false) }
    var showSettingsScreen by remember { mutableStateOf(false) }

    var selectedFolderUris by remember {
        mutableStateOf(
            context
                .getSharedPreferences("mused_prefs", Context.MODE_PRIVATE)
                .getStringSet("selected_folder_uris", emptySet())
                ?.toList()
                ?: emptyList()
        )
    }

    var songs by remember { mutableStateOf<List<SongData>>(emptyList()) }

    var mediaController by remember {
        mutableStateOf<MediaController?>(null)
    }

    var currentSongName by remember { mutableStateOf<String?>(null) }
    var currentSongIndex by remember { mutableStateOf<Int?>(null) }
    var currentSongUri by remember { mutableStateOf<String?>(null) }

    var isPlaying by remember { mutableStateOf(false) }

    var playbackPosition by remember { mutableIntStateOf(0) }
    var playbackDuration by remember { mutableIntStateOf(0) }

    var isShuffleEnabled by remember { mutableStateOf(false) }
    var selectedRepeatMode by remember { mutableIntStateOf(0) }

    var searchQuery by remember { mutableStateOf("") }

    var sleepTimerRemainingSeconds by remember {
        mutableStateOf<Int?>(null)
    }

    var sortMode by remember {
        mutableStateOf(
            context
                .getSharedPreferences("mused_prefs", Context.MODE_PRIVATE)
                .getString("sort_mode", "Name A-Z") ?: "Name A-Z"
        )
    }

    var savedSongUri by remember {
        mutableStateOf(
            context
                .getSharedPreferences("mused_prefs", Context.MODE_PRIVATE)
                .getString("current_song_uri", null)
        )
    }

    var savedPosition by remember {
        mutableIntStateOf(
            context
                .getSharedPreferences("mused_prefs", Context.MODE_PRIVATE)
                .getInt("current_position_ms", 0)
        )
    }

    var hasAutoResumed by remember { mutableStateOf(false) }

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
            },
            MoreExecutors.directExecutor()
        )
    }

    LaunchedEffect(mediaController, isPlaying) {

        while (isPlaying) {

            playbackPosition =
                mediaController?.currentPosition?.toInt() ?: 0

            playbackDuration =
                mediaController?.duration
                    ?.takeIf { it > 0 }
                    ?.toInt() ?: 0

            delay(1000)
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

                        currentSongIndex = index
                        currentSongName = currentSong.title
                        currentSongUri = currentSong.uri
                        savedSongUri = currentSong.uri

                        playbackPosition = 0
                    }
                }

                override fun onIsPlayingChanged(
                    isPlayingNow: Boolean
                ) {
                    isPlaying = isPlayingNow
                }
            }

            controller.addListener(listener)

            onDispose {
                controller.removeListener(listener)
            }
        }
    }

    LaunchedEffect(selectedFolderUris) {

        songs = loadSongDataFromFolders(
            context,
            selectedFolderUris
        )
    }

    fun savePlaybackState() {

        val currentIndex = currentSongIndex ?: return

        val currentFileUri =
            songs.getOrNull(currentIndex)?.uri ?: return

        val currentPosition =
            mediaController?.currentPosition?.toInt() ?: 0

        savedSongUri = currentFileUri
        savedPosition = currentPosition

        context.getSharedPreferences(
            "mused_prefs",
            Context.MODE_PRIVATE
        ).edit {

            putString("current_song_uri", currentFileUri)

            putInt("current_song_index", currentIndex)

            putInt("current_position_ms", currentPosition)
        }
    }

    DisposableEffect(mediaController) {

        val noisyReceiver = object : BroadcastReceiver() {

            override fun onReceive(
                context: Context?,
                intent: Intent?
            ) {

                if (intent?.action ==
                    AudioManager.ACTION_AUDIO_BECOMING_NOISY
                ) {

                    mediaController?.pause()

                    savePlaybackState()
                }
            }
        }

        val intentFilter =
            IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY)

        context.registerReceiver(
            noisyReceiver,
            intentFilter
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

        currentSongName = null
        currentSongIndex = null
        currentSongUri = null

        savedSongUri = null
        savedPosition = 0

        playbackPosition = 0
        playbackDuration = 0

        isPlaying = false

        showPlayerScreen = false

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

        selectedFolderUris = emptyList()

        songs = emptyList()

        currentSongName = null
        currentSongIndex = null
        currentSongUri = null

        savedSongUri = null
        savedPosition = 0

        playbackPosition = 0
        playbackDuration = 0

        isPlaying = false

        hasAutoResumed = false

        showPlayerScreen = false

        sleepTimerRemainingSeconds = null

        context.getSharedPreferences(
            "mused_prefs",
            Context.MODE_PRIVATE
        ).edit {

            remove("selected_folder_uris")
            remove("current_song_uri")
            remove("current_song_index")
            remove("current_position_ms")
        }
    }

    fun playSong(index: Int) {

        if (index !in songs.indices) return

        val song = songs[index]

        currentSongName = song.title
        currentSongIndex = index
        currentSongUri = song.uri
        savedSongUri = song.uri

        mediaController?.apply {

            val mediaItems =
                buildMediaItems(context, songs)

            setMediaItems(mediaItems, index, 0L)

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

            selectedFolderUris =
                (selectedFolderUris + folderUri).distinct()

            hasAutoResumed = false

            context.getSharedPreferences(
                "mused_prefs",
                Context.MODE_PRIVATE
            ).edit {

                putStringSet(
                    "selected_folder_uris",
                    selectedFolderUris.toSet()
                )
            }

            songs = loadSongDataFromFolders(
                context,
                selectedFolderUris
            )
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

                    songName = currentSongName,

                    songUri = currentSongUri,

                    isPlaying = isPlaying,

                    playbackPosition = playbackPosition,

                    playbackDuration = playbackDuration,

                    isShuffleEnabled = isShuffleEnabled,

                    selectedRepeatMode = selectedRepeatMode,

                    queueSongs = songs.map { song ->
                        song.title
                    },

                    currentSongIndex = currentSongIndex,

                    sleepTimerRemainingSeconds =
                        sleepTimerRemainingSeconds,

                    onBack = {
                        showPlayerScreen = false
                    },

                    onSeekChange = { newPosition ->
                        playbackPosition = newPosition
                    },

                    onSeekFinished = {

                        mediaController?.seekTo(
                            playbackPosition.toLong()
                        )

                        savePlaybackState()
                    },

                    onPrevious = {

                        val index =
                            currentSongIndex ?: return@PlayerScreen

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
                            currentSongIndex ?: return@PlayerScreen

                        if (index < songs.size - 1) {
                            playSong(index + 1)
                        }
                    },

                    onToggleShuffle = {

                        isShuffleEnabled = !isShuffleEnabled

                        mediaController?.shuffleModeEnabled =
                            isShuffleEnabled
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

                    currentSongIndex = currentSongIndex,

                    currentSongName = currentSongName,

                    currentSongUri = currentSongUri,

                    isPlaying = isPlaying,

                    playbackPosition = playbackPosition,

                    playbackDuration = playbackDuration,

                    searchQuery = searchQuery,

                    sortMode = sortMode,

                    onSearchChange = { newSearchQuery ->
                        searchQuery = newSearchQuery
                    },

                    onSortModeChange = { newSortMode ->

                        sortMode = newSortMode

                        context.getSharedPreferences(
                            "mused_prefs",
                            Context.MODE_PRIVATE
                        ).edit {

                            putString(
                                "sort_mode",
                                newSortMode
                            )
                        }
                    },

                    onPickFolder = {
                        openFolderPicker()
                    },

                    onRemoveFolder = { folderUriToRemove ->

                        selectedFolderUris =
                            selectedFolderUris.filter {
                                it != folderUriToRemove
                            }

                        hasAutoResumed = false

                        context.getSharedPreferences(
                            "mused_prefs",
                            Context.MODE_PRIVATE
                        ).edit {

                            putStringSet(
                                "selected_folder_uris",
                                selectedFolderUris.toSet()
                            )
                        }

                        songs = loadSongDataFromFolders(
                            context,
                            selectedFolderUris
                        )
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