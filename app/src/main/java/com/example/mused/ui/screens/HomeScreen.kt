package com.example.mused.ui.screens

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.edit
import androidx.core.net.toUri
import androidx.documentfile.provider.DocumentFile
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.LaunchedEffect


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
    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }
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

    LaunchedEffect(selectedFolderUri) {
        selectedFolderUri?.let { uriString ->
            val documentFile = DocumentFile.fromTreeUri(context, uriString.toUri())

            files = documentFile
                ?.listFiles()
                ?.filter { it.isFile }
                ?.filter { file ->
                    val name = file.name?.lowercase() ?: ""
                    name.endsWith(".mp3") ||
                            name.endsWith(".wav") ||
                            name.endsWith(".m4a") ||
                            name.endsWith(".flac") ||
                            name.endsWith(".ogg")
                }
                ?.map { file -> file.uri.toString() }
                ?: emptyList()
        }
    }

    fun playSong(index: Int) {
        if (index !in files.indices) return

        val uri = files[index].toUri()
        val name = DocumentFile.fromSingleUri(context, uri)?.name ?: "Unknown song"

        mediaPlayer?.release()

        currentSongName = name
        currentSongIndex = index
        savedSongUri = files[index]
        savedPosition = 0

        mediaPlayer = MediaPlayer().apply {
            setDataSource(context, uri)
            prepare()
            start()
            isPlaying = true

            setOnCompletionListener {
                val nextIndex = index + 1

                if (nextIndex < files.size) {
                    playSong(nextIndex)
                } else {
                    isPlaying = false
                }
            }
        }
    }

    fun savePlaybackState() {
        val currentIndex = currentSongIndex ?: return
        val currentFileUri = files.getOrNull(currentIndex) ?: return
        val currentPosition = mediaPlayer?.currentPosition ?: 0
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

    val folderPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val folderUri = result.data?.data
            selectedFolderUri = folderUri?.toString()

            context
                .getSharedPreferences("mused_prefs", Context.MODE_PRIVATE)
                .edit {
                    putString("selected_folder_uri", selectedFolderUri)
                }

            folderUri?.let { uri ->
                val documentFile = DocumentFile.fromTreeUri(context, uri)

                files = documentFile
                    ?.listFiles()
                    ?.filter { it.isFile }
                    ?.filter { file ->
                        val name = file.name?.lowercase() ?: ""
                        name.endsWith(".mp3") ||
                                name.endsWith(".wav") ||
                                name.endsWith(".m4a") ||
                                name.endsWith(".flac") ||
                                name.endsWith(".ogg")
                    }
                    ?.map { file -> file.uri.toString() }
                    ?: emptyList()
            }
        }
    }

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "MUSED",
            style = MaterialTheme.typography.headlineMedium
        )

        Button(
            onClick = {
                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
                folderPickerLauncher.launch(intent)
            }
        ) {
            Text("Select Music Folder")
        }

        selectedFolderUri?.let { uri ->
            Text(text = "Selected folder:")
            Text(text = uri)
        }

        currentSongName?.let { songName ->
            Text(text = "Now playing: $songName")
        }

        savedSongUri?.let { uriString ->
            val savedName = DocumentFile.fromSingleUri(context, uriString.toUri())?.name ?: "Saved song"

            Text(text = "Saved song: $savedName")
            Text(text = "Saved position: ${savedPosition / 1000}s")
        }

        if (savedSongUri != null && savedPosition > 0) {
            Button(
                onClick = {
                    val savedIndex = files.indexOf(savedSongUri)

                    if (savedIndex != -1) {
                        val positionToResume = savedPosition

                        playSong(savedIndex)
                        mediaPlayer?.seekTo(positionToResume)
                        savedPosition = positionToResume
                    }
                }
            ) {
                Text("Resume")
            }
        }

        if (currentSongName != null) {
            Button(
                onClick = {
                    mediaPlayer?.let { player ->
                        if (player.isPlaying) {
                            player.pause()
                            isPlaying = false
                            savePlaybackState()
                        } else {
                            player.start()
                            isPlaying = true
                            savePlaybackState()
                        }
                    }
                }
            ) {
                Text(if (isPlaying) "Pause" else "Play")
            }
        }

        files.forEachIndexed { index, fileUri ->
            val uri = fileUri.toUri()
            val name = DocumentFile.fromSingleUri(context, uri)?.name ?: "Unknown song"

            Text(
                text = name,
                modifier = Modifier.clickable {
                    playSong(index)
                }
            )
        }
    }
}