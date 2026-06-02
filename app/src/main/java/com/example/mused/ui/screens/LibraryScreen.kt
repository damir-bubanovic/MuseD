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
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.example.mused.R
import com.example.mused.models.SongData
import com.example.mused.ui.components.AlbumArt
import com.example.mused.ui.theme.MusedCardSurface
import com.example.mused.ui.theme.MusedDarkSurface
import com.example.mused.ui.theme.MusedRed
import com.example.mused.ui.theme.MusedSurfaceVariant
import com.example.mused.ui.theme.MusedTextPrimary
import com.example.mused.ui.theme.MusedTextSecondary
import com.example.mused.ui.theme.rememberResponsiveSizes
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
    val responsive = rememberResponsiveSizes()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(
                horizontal = responsive.screenPadding,
                vertical = responsive.sectionSpacing
            )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.mused_header_logo),
                contentDescription = "MuseD Logo",
                modifier = Modifier.size(responsive.logoSize),
                contentScale = ContentScale.Fit
            )

            Spacer(Modifier.width(responsive.sectionSpacing))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "MuseD",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontSize = responsive.titleTextSize
                    ),
                    color = MusedRed,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "${songs.size} songs",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = responsive.smallTextSize
                    ),
                    color = MusedTextSecondary
                )
            }

            OutlinedButton(
                onClick = onOpenSettings,
                modifier = Modifier.height(responsive.buttonHeight),
                shape = RoundedCornerShape(50),
                border = BorderStroke(1.dp, MusedTextPrimary),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MusedTextPrimary
                )
            ) {
                Text(
                    text = "Settings",
                    fontSize = responsive.bodyTextSize
                )
            }
        }

        Spacer(Modifier.height(responsive.sectionSpacing))

        Button(
            onClick = onPickFolder,
            modifier = Modifier
                .fillMaxWidth()
                .height(responsive.buttonHeight),
            shape = RoundedCornerShape(responsive.cardCornerRadius)
        ) {
            Text(
                text = "Add Music Folder",
                fontSize = responsive.bodyTextSize
            )
        }

        if (selectedFolderUris.isNotEmpty()) {
            Spacer(Modifier.height(responsive.sectionSpacing))

            Text(
                text = "Folders",
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = responsive.smallTextSize
                ),
                color = MusedTextSecondary,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(responsive.smallSpacing))

            selectedFolderUris.forEach { folderUri ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = responsive.smallSpacing / 2),
                    shape = RoundedCornerShape(responsive.cardCornerRadius),
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
                            .padding(
                                start = responsive.cardPadding,
                                end = responsive.smallSpacing,
                                top = responsive.cardVerticalPadding,
                                bottom = responsive.cardVerticalPadding
                            ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = formatFolderName(folderUri),
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = responsive.smallTextSize
                            ),
                            color = MusedTextSecondary,
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

        Spacer(Modifier.height(responsive.sectionSpacing))

        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchChange,
            label = {
                Text(
                    text = "Search songs",
                    fontSize = responsive.bodyTextSize
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(responsive.searchBarHeight),
            singleLine = true,
            shape = RoundedCornerShape(responsive.cardCornerRadius),
            textStyle = MaterialTheme.typography.bodyLarge.copy(
                fontSize = responsive.bodyTextSize
            ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = MusedTextPrimary,
                unfocusedTextColor = MusedTextPrimary,
                focusedBorderColor = MusedTextPrimary,
                unfocusedBorderColor = MusedTextPrimary,
                focusedLabelColor = MusedTextPrimary,
                unfocusedLabelColor = MusedTextSecondary,
                cursorColor = MusedRed
            )
        )

        Spacer(Modifier.height(responsive.sectionSpacing))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(responsive.smallSpacing),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.weight(1f)) {
                SortChip(
                    label = "A-Z",
                    selected = sortMode == "Name A-Z",
                    chipHeight = responsive.chipHeight,
                    fontSize = responsive.smallTextSize,
                    onClick = { onSortModeChange("Name A-Z") }
                )
            }

            Box(modifier = Modifier.weight(1f)) {
                SortChip(
                    label = "Z-A",
                    selected = sortMode == "Name Z-A",
                    chipHeight = responsive.chipHeight,
                    fontSize = responsive.smallTextSize,
                    onClick = { onSortModeChange("Name Z-A") }
                )
            }

            Box(modifier = Modifier.weight(1f)) {
                SortChip(
                    label = "New",
                    selected = sortMode == "Newest First",
                    chipHeight = responsive.chipHeight,
                    fontSize = responsive.smallTextSize,
                    onClick = { onSortModeChange("Newest First") }
                )
            }

            Box(modifier = Modifier.weight(1f)) {
                SortChip(
                    label = "Old",
                    selected = sortMode == "Oldest First",
                    chipHeight = responsive.chipHeight,
                    fontSize = responsive.smallTextSize,
                    onClick = { onSortModeChange("Oldest First") }
                )
            }
        }

        Spacer(Modifier.height(responsive.sectionSpacing))

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(responsive.smallSpacing)
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
                    shape = RoundedCornerShape(responsive.cardCornerRadius),
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
                            .padding(
                                horizontal = responsive.cardPadding,
                                vertical = responsive.cardVerticalPadding
                            ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isCurrentSong) "▶" else "",
                            modifier = Modifier
                                .width(responsive.sectionSpacing)
                                .graphicsLayer {
                                    scaleX =
                                        if (isCurrentSong && isPlaying) pulseScale else 1f
                                    scaleY =
                                        if (isCurrentSong && isPlaying) pulseScale else 1f
                                },
                            color = MusedRed,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(Modifier.width(responsive.smallSpacing))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = song.title,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontSize = responsive.songTitleTextSize
                                ),
                                fontWeight = if (isCurrentSong) {
                                    FontWeight.Bold
                                } else {
                                    FontWeight.Normal
                                },
                                color = MusedTextPrimary,
                                maxLines = 1
                            )

                            Spacer(Modifier.height(responsive.smallSpacing / 2))

                            Text(
                                text = song.artist,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = responsive.songArtistTextSize
                                ),
                                color = MusedTextSecondary,
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
                    .height(responsive.miniPlayerHeight)
                    .clickable { onOpenPlayer() },
                shape = RoundedCornerShape(responsive.cardCornerRadius),
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
                            .weight(1f)
                            .padding(
                                horizontal = responsive.cardPadding,
                                vertical = responsive.smallSpacing
                            ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AlbumArt(
                            songUri = currentSongUri,
                            modifier = Modifier.size(responsive.miniPlayerAlbumSize)
                        )

                        Spacer(Modifier.width(responsive.sectionSpacing))

                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = currentSongName ?: "",
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontSize = responsive.songTitleTextSize
                                ),
                                fontWeight = FontWeight.Bold,
                                color = MusedTextPrimary,
                                maxLines = 1
                            )

                            Spacer(Modifier.height(responsive.smallSpacing / 2))

                            Text(
                                text =
                                    if (isPlaying) {
                                        "Playing"
                                    } else {
                                        "Paused"
                                    },
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = responsive.songArtistTextSize
                                ),
                                color = MusedTextSecondary
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
    chipHeight: Dp,
    fontSize: TextUnit,
    onClick: () -> Unit
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(chipHeight),
        label = {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = label,
                    fontSize = fontSize,
                    fontWeight = if (selected) {
                        FontWeight.Bold
                    } else {
                        FontWeight.Normal
                    }
                )
            }
        },
        shape = RoundedCornerShape(50),
        colors = FilterChipDefaults.filterChipColors(
            containerColor = MusedDarkSurface,
            selectedContainerColor = MusedSurfaceVariant,
            labelColor = MusedTextSecondary,
            selectedLabelColor = MusedRed
        )
    )
}