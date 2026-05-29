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
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.mused.R
import com.example.mused.models.SongData
import com.example.mused.ui.components.AlbumArt
import com.example.mused.ui.theme.MusedCardSurface
import com.example.mused.ui.theme.MusedDarkSurface
import com.example.mused.ui.theme.MusedRed
import com.example.mused.ui.theme.MusedSurfaceVariant
import com.example.mused.utils.formatFolderName

@Composable
fun LibraryScreen(
    modifier: Modifier = Modifier,
    selectedFolderUris: List<String>,
    songs: List<SongData>,
    sortedSongs: List<SongData>,
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
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.mused_header_logo),
                contentDescription = "MuseD Logo",
                modifier = Modifier.size(40.dp),
                contentScale = ContentScale.Fit
            )

            Spacer(Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "MuseD",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MusedRed,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "${songs.size} songs",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            OutlinedButton(
                onClick = onOpenSettings,
                shape = RoundedCornerShape(50)
            ) {
                Text("Settings")
            }
        }

        Spacer(Modifier.height(14.dp))

        Button(
            onClick = onPickFolder,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp)
        ) {
            Text("Add Music Folder")
        }

        if (selectedFolderUris.isNotEmpty()) {
            Spacer(Modifier.height(12.dp))

            Text(
                text = "Folders",
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
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MusedDarkSurface
                    ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 0.dp
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 12.dp, end = 2.dp, top = 4.dp, bottom = 4.dp),
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
        }

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchChange,
            label = { Text("Search songs") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(18.dp)
        )

        Spacer(Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SortChip(
                label = "Name A-Z",
                selected = sortMode == "Name A-Z",
                onClick = { onSortModeChange("Name A-Z") }
            )

            SortChip(
                label = "Name Z-A",
                selected = sortMode == "Name Z-A",
                onClick = { onSortModeChange("Name Z-A") }
            )
        }

        Spacer(Modifier.height(6.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SortChip(
                label = "Newest",
                selected = sortMode == "Newest First",
                onClick = { onSortModeChange("Newest First") }
            )

            SortChip(
                label = "Oldest",
                selected = sortMode == "Oldest First",
                onClick = { onSortModeChange("Oldest First") }
            )
        }

        Spacer(Modifier.height(12.dp))

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            itemsIndexed(sortedSongs) { _, song ->
                val index =
                    songs.indexOfFirst { librarySong ->
                        librarySong.uri == song.uri
                    }

                val isCurrentSong = index == currentSongIndex

                val animatedCardColor by animateColorAsState(
                    targetValue = if (isCurrentSong) {
                        MusedSurfaceVariant
                    } else {
                        MusedCardSurface
                    },
                    label = "SongRowColorAnimation"
                )

                val animatedElevation by animateDpAsState(
                    targetValue = if (isCurrentSong) 3.dp else 0.dp,
                    label = "SongRowElevationAnimation"
                )

                val pulseTransition =
                    rememberInfiniteTransition(
                        label = "NowPlayingPulseTransition"
                    )

                val pulseScale by pulseTransition.animateFloat(
                    initialValue = 1f,
                    targetValue = if (isCurrentSong && isPlaying) 1.18f else 1f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(durationMillis = 700),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "NowPlayingPulseScale"
                )

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            if (index != -1) {
                                onPlaySong(index)
                            }
                        },
                    shape = RoundedCornerShape(16.dp),
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
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isCurrentSong) "▶" else "",
                            modifier = Modifier
                                .width(22.dp)
                                .graphicsLayer {
                                    scaleX =
                                        if (isCurrentSong && isPlaying) pulseScale else 1f
                                    scaleY =
                                        if (isCurrentSong && isPlaying) pulseScale else 1f
                                },
                            color = MusedRed,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(Modifier.width(6.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = song.title,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = if (isCurrentSong) {
                                    FontWeight.Bold
                                } else {
                                    FontWeight.Normal
                                },
                                color = if (isCurrentSong) {
                                    MaterialTheme.colorScheme.onSurface
                                } else {
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                },
                                maxLines = 1
                            )

                            Spacer(Modifier.height(2.dp))

                            Text(
                                text = song.artist,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }

        AnimatedVisibility(
            visible = currentSongName != null,
            enter = slideInVertically { it } + fadeIn(),
            exit = slideOutVertically { it } + fadeOut()
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOpenPlayer() },
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MusedCardSurface
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 3.dp
                )
            ) {
                Column {
                    LinearProgressIndicator(
                        progress = {
                            if (playbackDuration > 0) {
                                playbackPosition.toFloat() /
                                        playbackDuration.toFloat()
                            } else {
                                0f
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(3.dp),
                        color = MusedRed,
                        trackColor = MusedSurfaceVariant
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AlbumArt(
                            songUri = currentSongUri,
                            modifier = Modifier.size(52.dp)
                        )

                        Spacer(Modifier.width(12.dp))

                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = currentSongName ?: "",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1
                            )

                            Spacer(Modifier.height(2.dp))

                            Text(
                                text =
                                    if (isPlaying) {
                                        "Playing"
                                    } else {
                                        "Paused"
                                    },
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        IconButton(
                            onClick = onPlayPause
                        ) {
                            Icon(
                                imageVector =
                                    if (isPlaying) {
                                        Icons.Filled.Pause
                                    } else {
                                        Icons.Filled.PlayArrow
                                    },
                                contentDescription = "Play/Pause",
                                tint = MusedRed
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SortChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = {
            Text(
                text = label,
                fontWeight = if (selected) {
                    FontWeight.Bold
                } else {
                    FontWeight.Normal
                }
            )
        },
        shape = RoundedCornerShape(50),
        colors = FilterChipDefaults.filterChipColors(
            containerColor = MusedDarkSurface,
            selectedContainerColor = MusedSurfaceVariant,
            labelColor = MaterialTheme.colorScheme.onSurfaceVariant,
            selectedLabelColor = MusedRed
        )
    )
}
