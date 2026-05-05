package com.example.mused.ui.screens

import android.content.ComponentName
import android.content.Context
import androidx.annotation.OptIn
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.edit
import androidx.core.net.toUri
import androidx.documentfile.provider.DocumentFile
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.example.mused.features.folders.loadMusicFilesFromFolder
import com.example.mused.features.folders.rememberFolderPickerLauncher
import com.example.mused.features.player.MusicService
import com.google.common.util.concurrent.MoreExecutors
import kotlinx.coroutines.delay



fun formatTime(ms: Int): String {
    val totalSeconds = ms / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%d:%02d".format(minutes, seconds)
}

@OptIn(UnstableApi::class)
@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current

    var selectedFolderUri by remember {
        mutableStateOf(
            context
                .getSharedPreferences("mused_prefs", Context.MODE_PRIVATE)
                .getString("selected_folder_uri", null)
        )
    }

    var files by remember { mutableStateOf<List<String>>(emptyList()) }
    var mediaController by remember { mutableStateOf<MediaController?>(null) }
    var currentSongName by remember { mutableStateOf<String?>(null) }
    var currentSongIndex by remember { mutableStateOf<Int?>(null) }
    var isPlaying by remember { mutableStateOf(false) }
    var hasAutoResumed by remember { mutableStateOf(false) }
    var playbackPosition by remember { mutableIntStateOf(0) }
    var playbackDuration by remember { mutableIntStateOf(0) }

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

    // Connect to Media3 service
    LaunchedEffect(Unit) {
        val sessionToken = SessionToken(
            context,
            ComponentName(context, MusicService::class.java)
        )

        val controllerFuture = MediaController.Builder(context, sessionToken).buildAsync()

        controllerFuture.addListener(
            {
                mediaController = controllerFuture.get()
            },
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

    // Load files
    LaunchedEffect(selectedFolderUri) {
        selectedFolderUri?.let { uriString ->
            files = loadMusicFilesFromFolder(context, uriString)
        }
    }

    fun playSong(index: Int) {
        if (index !in files.indices) return

        val uri = files[index].toUri()
        val name = DocumentFile.fromSingleUri(context, uri)?.name ?: "Unknown song"

        currentSongName = name
        currentSongIndex = index
        savedSongUri = files[index]
        savedPosition = 0

        mediaController?.apply {
            val mediaItems = files.map { fileUri ->
                val itemUri = fileUri.toUri()
                val itemName =
                    DocumentFile.fromSingleUri(context, itemUri)?.name ?: "Unknown song"

                MediaItem.Builder()
                    .setUri(itemUri)
                    .setMediaMetadata(
                        androidx.media3.common.MediaMetadata.Builder()
                            .setTitle(itemName)
                            .setArtist("MUSED")
                            .build()
                    )
                    .build()
            }

            setMediaItems(mediaItems, index, 0L)
            prepare()
            play()
        }

        isPlaying = true
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

    // AUTO RESUME (correct position in file)
    LaunchedEffect(files, savedSongUri, savedPosition, mediaController) {
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
            savedPosition = positionToResume
        }
    }

    val openFolderPicker = rememberFolderPickerLauncher { folderUri ->
        selectedFolderUri = folderUri

        context.getSharedPreferences("mused_prefs", Context.MODE_PRIVATE).edit {
            putString("selected_folder_uri", selectedFolderUri)
        }

        folderUri?.let {
            files = loadMusicFilesFromFolder(context, it)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        Text("MUSED", style = MaterialTheme.typography.headlineMedium)

        Spacer(Modifier.height(16.dp))

        Button(onClick = openFolderPicker) {
            Text("Select Music Folder")
        }

        Spacer(Modifier.height(16.dp))

        selectedFolderUri?.let {
            Text("Selected folder:")
            Text(it)
            Spacer(Modifier.height(16.dp))
        }

        currentSongName?.let {
            Text("Now playing: $it")

            Slider(
                value = playbackPosition.toFloat(),
                onValueChange = { newValue ->
                    playbackPosition = newValue.toInt()
                },
                onValueChangeFinished = {
                    mediaController?.seekTo(playbackPosition.toLong())
                    savePlaybackState()
                },
                valueRange = 0f..playbackDuration.coerceAtLeast(1).toFloat()
            )

            Text("Time: ${formatTime(playbackPosition)} / ${formatTime(playbackDuration)}")
            Spacer(Modifier.height(8.dp))
        }

        savedSongUri?.let {
            val savedName =
                DocumentFile.fromSingleUri(context, it.toUri())?.name ?: "Saved song"

            Text("Saved song: $savedName")
            Text("Saved position: ${savedPosition / 1000}s")
            Spacer(Modifier.height(8.dp))
        }

        if (currentSongName != null) {
            Row {
                IconButton(onClick = {
                    val i = currentSongIndex ?: return@IconButton
                    if (i > 0) playSong(i - 1)
                }) {
                    Icon(Icons.Filled.SkipPrevious, "Previous")
                }

                IconButton(onClick = {
                    mediaController?.let {
                        if (it.isPlaying) {
                            it.pause()
                            isPlaying = false
                            savePlaybackState()
                        } else {
                            it.play()
                            isPlaying = true
                            savePlaybackState()
                        }
                    }
                }) {
                    Icon(
                        if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                        "Play/Pause"
                    )
                }

                IconButton(onClick = {
                    val i = currentSongIndex ?: return@IconButton
                    if (i < files.size - 1) playSong(i + 1)
                }) {
                    Icon(Icons.Filled.SkipNext, "Next")
                }
            }

            Spacer(Modifier.height(16.dp))
        }

        LazyColumn(Modifier.fillMaxSize()) {
            itemsIndexed(files) { index, fileUri ->
                val uri = fileUri.toUri()
                val name =
                    DocumentFile.fromSingleUri(context, uri)?.name ?: "Unknown song"

                Text(
                    text = if (index == currentSongIndex) "▶ $name" else name,
                    fontWeight = if (index == currentSongIndex)
                        FontWeight.Bold else FontWeight.Normal,
                    modifier = Modifier.clickable {
                        playSong(index)
                    }
                )
            }
        }
    }
}