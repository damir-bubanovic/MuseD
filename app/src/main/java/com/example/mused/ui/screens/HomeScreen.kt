package com.example.mused.ui.screens

import android.content.ComponentName
import android.content.Context
import androidx.annotation.OptIn
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.edit
import androidx.core.net.toUri
import androidx.documentfile.provider.DocumentFile
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.example.mused.features.folders.loadMusicFilesFromFolders
import com.example.mused.features.folders.rememberFolderPickerLauncher
import com.example.mused.features.player.MusicService
import com.example.mused.features.player.buildMediaItems
import com.google.common.util.concurrent.MoreExecutors
import kotlinx.coroutines.delay


@Suppress("AssignedValueIsNeverRead")
@OptIn(UnstableApi::class)
@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var showPlayerScreen by remember { mutableStateOf(false) }

    var selectedFolderUris by remember {
        mutableStateOf(
            context
                .getSharedPreferences("mused_prefs", Context.MODE_PRIVATE)
                .getStringSet("selected_folder_uris", emptySet())
                ?.toList()
                ?: emptyList()
        )
    }

    var files by remember { mutableStateOf<List<String>>(emptyList()) }
    var mediaController by remember { mutableStateOf<MediaController?>(null) }

    var currentSongName by remember { mutableStateOf<String?>(null) }
    var currentSongIndex by remember { mutableStateOf<Int?>(null) }
    var currentSongUri by remember { mutableStateOf<String?>(null) }

    var isPlaying by remember { mutableStateOf(false) }

    var playbackPosition by remember { mutableIntStateOf(0) }
    var playbackDuration by remember { mutableIntStateOf(0) }

    var isShuffleEnabled by remember { mutableStateOf(false) }
    var selectedRepeatMode by remember { mutableIntStateOf(0) }

    var searchQuery by remember { mutableStateOf("") }
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

        val controllerFuture = MediaController.Builder(context, sessionToken).buildAsync()

        controllerFuture.addListener(
            { mediaController = controllerFuture.get() },
            MoreExecutors.directExecutor()
        )
    }

    LaunchedEffect(mediaController, isPlaying) {
        while (isPlaying) {
            playbackPosition = mediaController?.currentPosition?.toInt() ?: 0
            playbackDuration = mediaController?.duration?.takeIf { it > 0 }?.toInt() ?: 0
            delay(1000)
        }
    }

    DisposableEffect(mediaController, files) {
        val controller = mediaController

        if (controller == null) {
            onDispose { }
        } else {
            val listener = object : Player.Listener {
                override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                    val index = controller.currentMediaItemIndex
                    val currentFile = files.getOrNull(index)

                    if (currentFile != null) {
                        val uri = currentFile.toUri()
                        val name =
                            DocumentFile.fromSingleUri(context, uri)?.name ?: "Unknown song"

                        currentSongIndex = index
                        currentSongName = name
                        currentSongUri = currentFile
                        savedSongUri = currentFile
                        playbackPosition = 0
                    }
                }

                override fun onIsPlayingChanged(isPlayingNow: Boolean) {
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
        files = loadMusicFilesFromFolders(context, selectedFolderUris)
    }

    fun savePlaybackState() {
        val currentIndex = currentSongIndex ?: return
        val currentFileUri = files.getOrNull(currentIndex) ?: return
        val currentPosition = mediaController?.currentPosition?.toInt() ?: 0

        savedSongUri = currentFileUri
        savedPosition = currentPosition

        context.getSharedPreferences("mused_prefs", Context.MODE_PRIVATE).edit {
            putString("current_song_uri", currentFileUri)
            putInt("current_song_index", currentIndex)
            putInt("current_position_ms", currentPosition)
        }
    }

    fun playSong(index: Int) {
        if (index !in files.indices) return

        val uri = files[index].toUri()
        val name = DocumentFile.fromSingleUri(context, uri)?.name ?: "Unknown song"

        currentSongName = name
        currentSongIndex = index
        currentSongUri = files[index]
        savedSongUri = files[index]

        mediaController?.apply {
            val mediaItems = buildMediaItems(context, files)

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

    LaunchedEffect(files, savedSongUri, mediaController) {
        if (hasAutoResumed) return@LaunchedEffect
        if (files.isEmpty()) return@LaunchedEffect
        if (savedSongUri == null) return@LaunchedEffect
        if (savedPosition <= 0) return@LaunchedEffect
        if (mediaController == null) return@LaunchedEffect

        val savedIndex = files.indexOf(savedSongUri)

        if (savedIndex != -1) {
            val positionToResume = savedPosition

            hasAutoResumed = true
            playSong(savedIndex)
            mediaController?.seekTo(positionToResume.toLong())
            showPlayerScreen = false
        }
    }

    val openFolderPicker = rememberFolderPickerLauncher { folderUri ->
        if (folderUri == null) return@rememberFolderPickerLauncher

        selectedFolderUris = (selectedFolderUris + folderUri).distinct()
        hasAutoResumed = false

        context.getSharedPreferences("mused_prefs", Context.MODE_PRIVATE).edit {
            putStringSet("selected_folder_uris", selectedFolderUris.toSet())
        }

        files = loadMusicFilesFromFolders(context, selectedFolderUris)
    }

    if (!showPlayerScreen) {
        LibraryScreen(
            modifier = modifier,
            selectedFolderUris = selectedFolderUris,
            files = files,
            currentSongIndex = currentSongIndex,
            currentSongName = currentSongName,
            currentSongUri = currentSongUri,
            isPlaying = isPlaying,
            searchQuery = searchQuery,
            sortMode = sortMode,
            onSearchChange = { newSearchQuery ->
                searchQuery = newSearchQuery
            },
            onSortModeChange = { newSortMode ->
                sortMode = newSortMode

                context.getSharedPreferences("mused_prefs", Context.MODE_PRIVATE).edit {
                    putString("sort_mode", newSortMode)
                }
            },
            onPickFolder = { openFolderPicker() },
            onRemoveFolder = { folderUriToRemove ->
                selectedFolderUris = selectedFolderUris.filter { it != folderUriToRemove }
                hasAutoResumed = false

                context.getSharedPreferences("mused_prefs", Context.MODE_PRIVATE).edit {
                    putStringSet("selected_folder_uris", selectedFolderUris.toSet())
                }

                files = loadMusicFilesFromFolders(context, selectedFolderUris)
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
            }
        )
    } else {
        PlayerScreen(
            modifier = modifier,
            songName = currentSongName,
            songUri = currentSongUri,
            isPlaying = isPlaying,
            playbackPosition = playbackPosition,
            playbackDuration = playbackDuration,
            isShuffleEnabled = isShuffleEnabled,
            selectedRepeatMode = selectedRepeatMode,
            queueSongs = files.map { fileUri ->
                DocumentFile.fromSingleUri(context, fileUri.toUri())?.name ?: "Unknown song"
            },
            currentSongIndex = currentSongIndex,
            onBack = {
                showPlayerScreen = false
            },
            onSeekChange = { newPosition ->
                playbackPosition = newPosition
            },
            onSeekFinished = {
                mediaController?.seekTo(playbackPosition.toLong())
                savePlaybackState()
            },
            onPrevious = {
                val index = currentSongIndex ?: return@PlayerScreen
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
                val index = currentSongIndex ?: return@PlayerScreen
                if (index < files.size - 1) {
                    playSong(index + 1)
                }
            },
            onToggleShuffle = {
                isShuffleEnabled = !isShuffleEnabled
                mediaController?.shuffleModeEnabled = isShuffleEnabled
            },
            onChangeRepeatMode = {
                selectedRepeatMode = (selectedRepeatMode + 1) % 3

                mediaController?.repeatMode = when (selectedRepeatMode) {
                    1 -> Player.REPEAT_MODE_ONE
                    2 -> Player.REPEAT_MODE_ALL
                    else -> Player.REPEAT_MODE_OFF
                }
            },
            onQueueSongClick = { index ->
                playSong(index)
            }
        )
    }
}