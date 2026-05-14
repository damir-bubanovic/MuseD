package com.example.mused.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.documentfile.provider.DocumentFile
import com.example.mused.R
import com.example.mused.ui.components.AlbumArt
import com.example.mused.utils.formatFolderName

@Composable
fun LibraryScreen(
    modifier: Modifier = Modifier,
    selectedFolderUris: List<String>,
    files: List<String>,
    currentSongIndex: Int?,
    currentSongName: String?,
    currentSongUri: String?,
    isPlaying: Boolean,
    playbackPosition: Int,
    playbackDuration: Int,
    searchQuery: String,
    sortMode: String,
    onSearchChange: (String) -> Unit,
    onSortModeChange: (String) -> Unit,
    onPickFolder: () -> Unit,
    onRemoveFolder: (String) -> Unit,
    onPlaySong: (Int) -> Unit,
    onOpenPlayer: () -> Unit,
    onPlayPause: () -> Unit,
    onOpenSettings: () -> Unit,
) {
    val context = LocalContext.current

    val filteredFiles = files.filter { fileUri ->
        val name = DocumentFile.fromSingleUri(context, fileUri.toUri())?.name ?: "Unknown"
        name.contains(searchQuery, ignoreCase = true)
    }

    val sortedFiles = when (sortMode) {
        "Name Z-A" -> filteredFiles.sortedByDescending { fileUri ->
            DocumentFile.fromSingleUri(context, fileUri.toUri())?.name ?: "Unknown"
        }

        "Newest First" -> filteredFiles.sortedByDescending { fileUri ->
            DocumentFile.fromSingleUri(context, fileUri.toUri())?.lastModified() ?: 0L
        }

        "Oldest First" -> filteredFiles.sortedBy { fileUri ->
            DocumentFile.fromSingleUri(context, fileUri.toUri())?.lastModified() ?: 0L
        }

        else -> filteredFiles.sortedBy { fileUri ->
            DocumentFile.fromSingleUri(context, fileUri.toUri())?.name ?: "Unknown"
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = R.drawable.mused_header_logo),
                contentDescription = "MuseD Logo",
                modifier = Modifier.size(42.dp),
                contentScale = ContentScale.Fit
            )

            Spacer(Modifier.width(12.dp))

            Text(
                text = "MuseD",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                onClick = onPickFolder,
                modifier = Modifier.weight(1f)
            ) {
                Text("Add Music Folder")
            }

            OutlinedButton(onClick = onOpenSettings) {
                Text("Settings")
            }
        }

        Spacer(Modifier.height(12.dp))

        if (selectedFolderUris.isNotEmpty()) {
            Text(
                text = "Folders:",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(6.dp))

            selectedFolderUris.forEach { folderUri ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 3.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 10.dp, end = 4.dp, top = 6.dp, bottom = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = formatFolderName(folderUri),
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1
                        )

                        IconButton(onClick = { onRemoveFolder(folderUri) }) {
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = "Remove folder",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(12.dp))
        }

        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchChange,
            label = { Text("Search songs") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Sort:",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.width(8.dp))

            SortButton(
                label = "Name A-Z",
                selected = sortMode == "Name A-Z",
                onClick = { onSortModeChange("Name A-Z") }
            )

            Spacer(Modifier.width(6.dp))

            SortButton(
                label = "Name Z-A",
                selected = sortMode == "Name Z-A",
                onClick = { onSortModeChange("Name Z-A") }
            )
        }

        Spacer(Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SortButton(
                label = "Newest First",
                selected = sortMode == "Newest First",
                onClick = { onSortModeChange("Newest First") }
            )

            Spacer(Modifier.width(6.dp))

            SortButton(
                label = "Oldest First",
                selected = sortMode == "Oldest First",
                onClick = { onSortModeChange("Oldest First") }
            )
        }

        Spacer(Modifier.height(12.dp))

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            itemsIndexed(sortedFiles) { _, fileUri ->
                val index = files.indexOf(fileUri)
                val name =
                    DocumentFile.fromSingleUri(context, fileUri.toUri())?.name ?: "Unknown"
                val isCurrentSong = index == currentSongIndex

                val animatedCardColor by animateColorAsState(
                    targetValue = if (isCurrentSong)
                        MaterialTheme.colorScheme.primaryContainer
                    else
                        MaterialTheme.colorScheme.surfaceVariant,
                    label = "SongRowColorAnimation"
                )

                val animatedElevation by animateDpAsState(
                    targetValue = if (isCurrentSong) 4.dp else 1.dp,
                    label = "SongRowElevationAnimation"
                )

                val pulseTransition = rememberInfiniteTransition(label = "NowPlayingPulseTransition")

                val pulseScale by pulseTransition.animateFloat(
                    initialValue = 1f,
                    targetValue = if (isCurrentSong && isPlaying) 1.25f else 1f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(durationMillis = 700),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "NowPlayingPulseScale"
                )

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onPlaySong(index) },
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = animatedCardColor
                    ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = animatedElevation
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isCurrentSong) "▶" else "",
                            modifier = Modifier
                                .width(24.dp)
                                .graphicsLayer {
                                    scaleX = if (isCurrentSong && isPlaying) pulseScale else 1f
                                    scaleY = if (isCurrentSong && isPlaying) pulseScale else 1f
                                },
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(Modifier.width(6.dp))

                        Text(
                            text = name,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = if (isCurrentSong)
                                FontWeight.Bold
                            else
                                FontWeight.Normal,
                            color = if (isCurrentSong)
                                MaterialTheme.colorScheme.onPrimaryContainer
                            else
                                MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1
                        )
                    }
                }
            }
        }

        AnimatedVisibility(
            visible = currentSongName != null,
            enter = slideInVertically { it } + fadeIn(),
            exit = slideOutVertically { it } + fadeOut()
        ) {
            Column {
                Spacer(Modifier.height(10.dp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onOpenPlayer() },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                ) {
                    Column {
                        LinearProgressIndicator(
                            progress = {
                                if (playbackDuration > 0) {
                                    playbackPosition.toFloat() / playbackDuration.toFloat()
                                } else {
                                    0f
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            color = MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.primaryContainer
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AlbumArt(
                                songUri = currentSongUri,
                                modifier = Modifier.size(48.dp)
                            )

                            Spacer(Modifier.width(12.dp))

                            Text(
                                text = currentSongName ?: "",
                                modifier = Modifier.weight(1f),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                maxLines = 1
                            )

                            IconButton(onClick = onPlayPause) {
                                Icon(
                                    if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                                    contentDescription = "Play/Pause",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SortButton(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    TextButton(
        onClick = onClick,
        colors = ButtonDefaults.textButtonColors(
            contentColor = if (selected)
                MaterialTheme.colorScheme.primary
            else
                MaterialTheme.colorScheme.onSurfaceVariant
        )
    ) {
        Text(
            text = label,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
        )
    }
}