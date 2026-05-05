package com.example.mused.ui.screens

import android.content.ComponentName
import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.edit
import androidx.core.net.toUri
import androidx.documentfile.provider.DocumentFile
import androidx.media3.common.MediaItem
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.example.mused.features.folders.loadMusicFilesFromFolder
import com.example.mused.features.folders.rememberFolderPickerLauncher
import com.example.mused.features.player.MusicService
import com.google.common.util.concurrent.MoreExecutors

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
            setMediaItem(MediaItem.fromUri(uri))
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

        context
            .getSharedPreferences("mused_prefs", Context.MODE_PRIVATE)
            .edit {
                putString("current_song_uri", currentFileUri)
                putInt("current_song_index", currentIndex)
                putInt("current_position_ms", currentPosition)
            }
    }

    val openFolderPicker = rememberFolderPickerLauncher { folderUri ->
        selectedFolderUri = folderUri

        context
            .getSharedPreferences("mused_prefs", Context.MODE_PRIVATE)
            .edit {
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
        Text(
            text = "MUSED",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = openFolderPicker) {
            Text("Select Music Folder")
        }

        Spacer(modifier = Modifier.height(16.dp))

        selectedFolderUri?.let { uri ->
            Text(text = "Selected folder:")
            Text(text = uri)
            Spacer(modifier = Modifier.height(16.dp))
        }

        currentSongName?.let { songName ->
            Text(text = "Now playing: $songName")
            Spacer(modifier = Modifier.height(8.dp))
        }

        savedSongUri?.let { uriString ->
            val savedName = DocumentFile.fromSingleUri(context, uriString.toUri())?.name ?: "Saved song"

            Text(text = "Saved song: $savedName")
            Text(text = "Saved position: ${savedPosition / 1000}s")

            Spacer(modifier = Modifier.height(8.dp))
        }

        if (savedSongUri != null && savedPosition > 0) {
            Button(
                onClick = {
                    val savedIndex = files.indexOf(savedSongUri)

                    if (savedIndex != -1) {
                        val positionToResume = savedPosition

                        playSong(savedIndex)
                        mediaController?.seekTo(positionToResume.toLong())
                        savedPosition = positionToResume
                    }
                }
            ) {
                Text("Resume")
            }

            Spacer(modifier = Modifier.height(8.dp))
        }

        if (currentSongName != null) {
            Row {
                IconButton(
                    onClick = {
                        val currentIndex = currentSongIndex ?: return@IconButton
                        val previousIndex = currentIndex - 1

                        if (previousIndex >= 0) {
                            playSong(previousIndex)
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Filled.SkipPrevious,
                        contentDescription = "Previous"
                    )
                }

                IconButton(
                    onClick = {
                        mediaController?.let { controller ->
                            if (controller.isPlaying) {
                                controller.pause()
                                isPlaying = false
                                savePlaybackState()
                            } else {
                                controller.play()
                                isPlaying = true
                                savePlaybackState()
                            }
                        }
                    }
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                        contentDescription = if (isPlaying) "Pause" else "Play"
                    )
                }

                IconButton(
                    onClick = {
                        val currentIndex = currentSongIndex ?: return@IconButton
                        val nextIndex = currentIndex + 1

                        if (nextIndex < files.size) {
                            playSong(nextIndex)
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Filled.SkipNext,
                        contentDescription = "Next"
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            itemsIndexed(files) { index, fileUri ->
                val uri = fileUri.toUri()
                val name = DocumentFile.fromSingleUri(context, uri)?.name ?: "Unknown song"

                Text(
                    text = if (index == currentSongIndex) "▶ $name" else name,
                    fontWeight = if (index == currentSongIndex) FontWeight.Bold else FontWeight.Normal,
                    modifier = Modifier.clickable {
                        playSong(index)
                    }
                )
            }
        }
    }
}