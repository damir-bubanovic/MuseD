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

    fun playSong(index: Int) {
        if (index !in files.indices) return

        val uri = files[index].toUri()
        val name = DocumentFile.fromSingleUri(context, uri)?.name ?: "Unknown song"

        mediaPlayer?.release()

        currentSongName = name
        currentSongIndex = index

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

        if (currentSongName != null) {
            Button(
                onClick = {
                    mediaPlayer?.let { player ->
                        if (player.isPlaying) {
                            player.pause()
                            isPlaying = false
                        } else {
                            player.start()
                            isPlaying = true
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