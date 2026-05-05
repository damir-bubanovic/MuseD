package com.example.mused.ui.screens

import android.content.ComponentName
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import androidx.annotation.OptIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.edit
import androidx.core.net.toUri
import androidx.documentfile.provider.DocumentFile
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.example.mused.features.folders.loadMusicFilesFromFolder
import com.example.mused.features.folders.rememberFolderPickerLauncher
import com.example.mused.features.player.MusicService
import com.google.common.util.concurrent.MoreExecutors
import kotlinx.coroutines.delay
import androidx.media3.common.Player


fun formatTime(ms: Int): String {
    val totalSeconds = ms / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%d:%02d".format(minutes, seconds)
}

fun loadAlbumArtImage(
    context: Context,
    songUriString: String
): ImageBitmap? {
    val retriever = MediaMetadataRetriever()

    return try {
        retriever.setDataSource(context, songUriString.toUri())

        val artworkBytes = retriever.embeddedPicture ?: return null
        val bitmap = BitmapFactory.decodeByteArray(
            artworkBytes,
            0,
            artworkBytes.size
        )

        bitmap?.asImageBitmap()
    } catch (_: Exception) {
        null
    } finally {
        retriever.release()
    }
}

@Composable
fun AlbumArt(
    songUri: String?,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val albumArt by produceState<ImageBitmap?>(initialValue = null, songUri) {
        value = songUri?.let {
            loadAlbumArtImage(context, it)
        }
    }

    Box(
        modifier = modifier
            .size(180.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Color.LightGray),
        contentAlignment = Alignment.Center
    ) {
        if (albumArt != null) {
            Image(
                bitmap = albumArt!!,
                contentDescription = "Album art",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            Text(
                text = "♪",
                style = MaterialTheme.typography.displayLarge
            )
        }
    }
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
    var currentSongUri by remember { mutableStateOf<String?>(null) }
    var isPlaying by remember { mutableStateOf(false) }
    var hasAutoResumed by remember { mutableStateOf(false) }
    var playbackPosition by remember { mutableIntStateOf(0) }
    var playbackDuration by remember { mutableIntStateOf(0) }
    var isShuffleEnabled by remember { mutableStateOf(false) }
    var selectedRepeatMode by remember { mutableIntStateOf(0) }

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
                override fun onMediaItemTransition(
                    mediaItem: MediaItem?,
                    reason: Int
                ) {
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
                        savedPosition = 0
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
        currentSongUri = files[index]

        mediaController?.apply {
            val mediaItems = files.map { fileUri ->
                val itemUri = fileUri.toUri()
                val itemName =
                    DocumentFile.fromSingleUri(context, itemUri)?.name ?: "Unknown song"

                val retriever = MediaMetadataRetriever()

                val artworkBytes = try {
                    retriever.setDataSource(context, itemUri)
                    retriever.embeddedPicture
                } catch (_: Exception) {
                    null
                } finally {
                    retriever.release()
                }

                val metadata = MediaMetadata.Builder()
                    .setTitle(itemName)
                    .setArtist("MUSED")
                    .apply {
                        artworkBytes?.let {
                            val bitmap = BitmapFactory.decodeByteArray(it, 0, it.size)

                            val stream = java.io.ByteArrayOutputStream()
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream)

                            setArtworkData(
                                stream.toByteArray(),
                                MediaMetadata.PICTURE_TYPE_FRONT_COVER
                            )
                        }
                    }
                    .build()

                MediaItem.Builder()
                    .setUri(itemUri)
                    .setMediaMetadata(metadata)
                    .build()
            }

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
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AlbumArt(
                    songUri = currentSongUri
                )

                Spacer(Modifier.height(12.dp))

                Text(
                    text = it,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(8.dp))

                Slider(
                    value = playbackPosition.toFloat(),
                    onValueChange = { newValue ->
                        playbackPosition = newValue.toInt()
                    },
                    onValueChangeFinished = {
                        mediaController?.seekTo(playbackPosition.toLong())
                        savePlaybackState()
                    },
                    valueRange = 0f..playbackDuration.coerceAtLeast(1).toFloat(),
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = "${formatTime(playbackPosition)} / ${formatTime(playbackDuration)}",
                    style = MaterialTheme.typography.bodySmall
                )

                Spacer(Modifier.height(8.dp))
            }
        }

        savedSongUri?.let {
            val savedName =
                DocumentFile.fromSingleUri(context, it.toUri())?.name ?: "Saved song"

            Text("Saved song: $savedName")
            Text("Saved position: ${savedPosition / 1000}s")
            Spacer(Modifier.height(8.dp))
        }

        if (currentSongName != null) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
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

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = {
                    isShuffleEnabled = !isShuffleEnabled
                    mediaController?.shuffleModeEnabled = isShuffleEnabled
                }) {
                    Text(if (isShuffleEnabled) "Shuffle ON" else "Shuffle OFF")
                }

                Spacer(Modifier.width(8.dp))

                TextButton(onClick = {
                    selectedRepeatMode = (selectedRepeatMode + 1) % 3

                    mediaController?.repeatMode = when (selectedRepeatMode) {
                        1 -> Player.REPEAT_MODE_ONE
                        2 -> Player.REPEAT_MODE_ALL
                        else -> Player.REPEAT_MODE_OFF
                    }
                }) {
                    Text(
                        when (selectedRepeatMode) {
                            1 -> "Repeat ONE"
                            2 -> "Repeat ALL"
                            else -> "Repeat OFF"
                        }
                    )
                }
            }

            Spacer(Modifier.height(16.dp))
        }

        LazyColumn(Modifier.fillMaxSize()) {
            itemsIndexed(files) { index, fileUri ->
                val uri = fileUri.toUri()
                val name =
                    DocumentFile.fromSingleUri(context, uri)?.name ?: "Unknown song"

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable { playSong(index) },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (index == currentSongIndex)
                            MaterialTheme.colorScheme.primaryContainer
                        else
                            MaterialTheme.colorScheme.surface
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .padding(12.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (index == currentSongIndex) "▶" else "",
                            modifier = Modifier.width(24.dp),
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(Modifier.width(8.dp))

                        Text(
                            text = name,
                            fontWeight = if (index == currentSongIndex)
                                FontWeight.Bold else FontWeight.Normal,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}