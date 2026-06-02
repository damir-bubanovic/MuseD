package com.example.mused.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.mused.ui.components.AlbumArt
import com.example.mused.ui.theme.MusedCardSurface
import com.example.mused.ui.theme.MusedRed
import com.example.mused.ui.theme.MusedSurfaceVariant
import com.example.mused.ui.theme.MusedTextPrimary
import com.example.mused.ui.theme.MusedTextSecondary
import com.example.mused.ui.theme.rememberResponsiveSizes
import com.example.mused.utils.formatTime

@Composable
fun PlayerScreen(
    modifier: Modifier = Modifier,
    songName: String?,
    songUri: String?,
    isPlaying: Boolean,
    playbackPosition: Int,
    playbackDuration: Int,
    isShuffleEnabled: Boolean,
    selectedRepeatMode: Int,
    queueSongs: List<String>,
    currentSongIndex: Int?,
    sleepTimerRemainingSeconds: Int?,
    onBack: () -> Unit,
    onSeekChange: (Int) -> Unit,
    onSeekFinished: () -> Unit,
    onPrevious: () -> Unit,
    onPlayPause: () -> Unit,
    onNext: () -> Unit,
    onToggleShuffle: () -> Unit,
    onChangeRepeatMode: () -> Unit,
    onQueueSongClick: (Int) -> Unit,
    onSleepTimerSelected: (Int?) -> Unit
) {
    val responsive = rememberResponsiveSizes()

    var showQueue by remember { mutableStateOf(true) }
    var showSleepTimerOptions by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .navigationBarsPadding()
            .padding(
                horizontal = responsive.screenPadding,
                vertical = responsive.sectionSpacing
            )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedButton(
                onClick = onBack,
                modifier = Modifier.height(responsive.buttonHeight),
                shape = RoundedCornerShape(50),
                border = BorderStroke(1.dp, MusedTextPrimary),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MusedTextPrimary
                )
            ) {
                Text(
                    text = "Back",
                    fontSize = responsive.bodyTextSize
                )
            }

            Text(
                text = "Now Playing",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = responsive.bodyTextSize
                ),
                fontWeight = FontWeight.Bold,
                color = MusedTextPrimary
            )
        }

        Spacer(Modifier.height(responsive.sectionSpacing))

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AlbumArt(
                songUri = songUri,
                modifier = Modifier
                    .size(responsive.albumArtSize)
                    .clip(RoundedCornerShape(responsive.cardCornerRadius))
            )

            Spacer(Modifier.height(responsive.sectionSpacing))

            Text(
                text = songName ?: "No Song Playing",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontSize = responsive.playerSongTitleSize
                ),
                fontWeight = FontWeight.Bold,
                color = MusedTextPrimary,
                maxLines = 1
            )

            Spacer(
                Modifier.height(
                    responsive.sectionSpacing
                )
            )

            Slider(
                value = playbackPosition.toFloat(),
                onValueChange = { newPosition ->
                    onSeekChange(newPosition.toInt())
                },
                onValueChangeFinished = onSeekFinished,
                valueRange = 0f..playbackDuration.coerceAtLeast(1).toFloat(),
                modifier = Modifier.fillMaxWidth(),
                colors = androidx.compose.material3.SliderDefaults.colors(
                    thumbColor = MusedRed,
                    activeTrackColor = MusedRed,
                    inactiveTrackColor = MusedSurfaceVariant
                )
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = formatTime(playbackPosition),
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = responsive.smallTextSize
                    ),
                    color = MusedTextSecondary
                )

                Text(
                    text = formatTime(playbackDuration),
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = responsive.smallTextSize
                    ),
                    color = MusedTextSecondary
                )
            }

            Spacer(Modifier.height(responsive.smallSpacing))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                IconButton(onClick = onPrevious) {
                    Icon(
                        imageVector = Icons.Default.SkipPrevious,
                        contentDescription = "Previous",
                        modifier = Modifier.size(responsive.playerButtonSize / 2),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                Box(
                    modifier = Modifier
                        .size(responsive.playerButtonSize)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                        .clickable { onPlayPause() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector =
                            if (isPlaying) {
                                Icons.Default.Pause
                            } else {
                                Icons.Default.PlayArrow
                            },
                        contentDescription = "Play/Pause",
                        tint = Color.White,
                        modifier = Modifier.size(responsive.playerButtonSize / 2)
                    )
                }

                IconButton(onClick = onNext) {
                    Icon(
                        imageVector = Icons.Default.SkipNext,
                        contentDescription = "Next",
                        modifier = Modifier.size(responsive.playerButtonSize / 2),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(Modifier.height(responsive.sectionSpacing))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(responsive.smallSpacing)
            ) {
                OutlinedButton(
                    modifier = Modifier
                        .weight(1f)
                        .height(responsive.buttonHeight),
                    onClick = onToggleShuffle,
                    border = BorderStroke(1.dp, MusedTextPrimary),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MusedTextPrimary
                    )
                ) {
                    Text(
                        text =
                            if (isShuffleEnabled) {
                                "Shuffle ON"
                            } else {
                                "Shuffle OFF"
                            },
                        fontSize = responsive.smallTextSize
                    )
                }

                OutlinedButton(
                    modifier = Modifier
                        .weight(1f)
                        .height(responsive.buttonHeight),
                    onClick = onChangeRepeatMode,
                    border = BorderStroke(1.dp, MusedTextPrimary),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MusedTextPrimary
                    )
                ) {
                    Text(
                        text =
                            when (selectedRepeatMode) {
                                1 -> "Repeat ONE"
                                2 -> "Repeat ALL"
                                else -> "Repeat OFF"
                            },
                        fontSize = responsive.smallTextSize
                    )
                }
            }
        }

        Spacer(Modifier.height(responsive.sectionSpacing))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(responsive.smallSpacing)
        ) {
            Button(
                modifier = Modifier
                    .weight(1f)
                    .height(responsive.buttonHeight),
                onClick = { showQueue = !showQueue },
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(
                    contentColor = Color.White
                )
            ) {
                Text(
                    text =
                        if (showQueue) {
                            "Hide Queue"
                        } else {
                            "Show Queue"
                        },
                    fontSize = responsive.smallTextSize
                )
            }

            OutlinedButton(
                modifier = Modifier
                    .weight(1f)
                    .height(responsive.buttonHeight),
                onClick = { showSleepTimerOptions = !showSleepTimerOptions },
                shape = RoundedCornerShape(50),
                border = BorderStroke(1.dp, MusedTextPrimary),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MusedTextPrimary
                )
            ) {
                Text(
                    text =
                        sleepTimerRemainingSeconds?.let {
                            "Timer ${it}s"
                        } ?: "Sleep Timer",
                    fontSize = responsive.smallTextSize
                )
            }
        }

        AnimatedVisibility(
            visible = showSleepTimerOptions,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = responsive.smallSpacing),
                horizontalArrangement = Arrangement.spacedBy(responsive.smallSpacing)
            ) {
                OutlinedButton(
                    modifier = Modifier
                        .weight(1f)
                        .height(responsive.buttonHeight),
                    onClick = {
                        onSleepTimerSelected(5)
                        showSleepTimerOptions = false
                    },
                    border = BorderStroke(1.dp, MusedTextPrimary),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MusedTextPrimary
                    )
                ) {
                    Text(
                        text = "5 min",
                        fontSize = responsive.smallTextSize
                    )
                }

                OutlinedButton(
                    modifier = Modifier
                        .weight(1f)
                        .height(responsive.buttonHeight),
                    onClick = {
                        onSleepTimerSelected(15)
                        showSleepTimerOptions = false
                    },
                    border = BorderStroke(1.dp, MusedTextPrimary),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MusedTextPrimary
                    )
                ) {
                    Text(
                        text = "15 min",
                        fontSize = responsive.smallTextSize
                    )
                }

                OutlinedButton(
                    modifier = Modifier
                        .weight(1f)
                        .height(responsive.buttonHeight),
                    onClick = {
                        onSleepTimerSelected(null)
                        showSleepTimerOptions = false
                    },
                    border = BorderStroke(1.dp, MusedTextPrimary),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MusedTextPrimary
                    )
                ) {
                    Text(
                        text = "Off",
                        fontSize = responsive.smallTextSize
                    )
                }
            }
        }

        Spacer(Modifier.height(responsive.smallSpacing))

        AnimatedVisibility(
            visible = showQueue,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.weight(1f)
        ) {
            Card(
                modifier = Modifier.fillMaxSize(),
                shape = RoundedCornerShape(responsive.cardCornerRadius),
                colors = CardDefaults.cardColors(
                    containerColor = MusedCardSurface
                )
            ) {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(
                        text = "Up Next",
                        modifier = Modifier.padding(responsive.cardPadding),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontSize = responsive.bodyTextSize
                        ),
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    LazyColumn(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        itemsIndexed(queueSongs) { index, title ->
                            val isCurrentSong = index == currentSongIndex

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        onQueueSongClick(index)
                                    }
                                    .padding(
                                        horizontal = responsive.cardPadding,
                                        vertical = responsive.cardVerticalPadding / 2
                                    )
                            ) {
                                Text(
                                    text = title,
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        fontSize = responsive.songTitleTextSize
                                    ),
                                    fontWeight =
                                        if (isCurrentSong) {
                                            FontWeight.Bold
                                        } else {
                                            FontWeight.Normal
                                        },
                                    color =
                                        if (isCurrentSong) {
                                            MusedRed
                                        } else {
                                            MusedTextPrimary
                                        },
                                    maxLines = 1
                                )

                                Spacer(
                                    Modifier.height(
                                        responsive.smallSpacing / 2
                                    )
                                )

                                HorizontalDivider(
                                    color = Color(0xFF252525)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}