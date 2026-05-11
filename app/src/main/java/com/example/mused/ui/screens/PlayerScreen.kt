package com.example.mused.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.mused.ui.components.AlbumArt
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
    onBack: () -> Unit,
    onSeekChange: (Int) -> Unit,
    onSeekFinished: () -> Unit,
    onPrevious: () -> Unit,
    onPlayPause: () -> Unit,
    onNext: () -> Unit,
    onToggleShuffle: () -> Unit,
    onChangeRepeatMode: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = onBack) {
            Text("Back")
        }

        Spacer(Modifier.height(24.dp))

        songName?.let { currentSongName ->
            AlbumArt(songUri = songUri)

            Spacer(Modifier.height(20.dp))

            Text(
                text = currentSongName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(12.dp))

            Slider(
                value = playbackPosition.toFloat(),
                onValueChange = { newValue ->
                    onSeekChange(newValue.toInt())
                },
                onValueChangeFinished = onSeekFinished,
                valueRange = 0f..playbackDuration.coerceAtLeast(1).toFloat(),
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                text = "${formatTime(playbackPosition)} / ${formatTime(playbackDuration)}",
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(Modifier.height(20.dp))

            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onPrevious) {
                    Icon(
                        Icons.Filled.SkipPrevious,
                        contentDescription = "Previous",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                IconButton(onClick = onPlayPause) {
                    Icon(
                        if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                        contentDescription = "Play/Pause",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(40.dp)
                    )
                }

                IconButton(onClick = onNext) {
                    Icon(
                        Icons.Filled.SkipNext,
                        contentDescription = "Next",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = onToggleShuffle) {
                    Text(
                        text = if (isShuffleEnabled) "Shuffle ON" else "Shuffle OFF",
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(Modifier.width(8.dp))

                TextButton(onClick = onChangeRepeatMode) {
                    Text(
                        text = when (selectedRepeatMode) {
                            1 -> "Repeat ONE"
                            2 -> "Repeat ALL"
                            else -> "Repeat OFF"
                        },
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}