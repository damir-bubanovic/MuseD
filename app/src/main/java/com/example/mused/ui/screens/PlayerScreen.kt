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

        Spacer(Modifier.height(16.dp))

        songName?.let { currentSongName ->
            AlbumArt(songUri = songUri)

            Spacer(Modifier.height(16.dp))

            Text(currentSongName)

            Slider(
                value = playbackPosition.toFloat(),
                onValueChange = { newValue ->
                    onSeekChange(newValue.toInt())
                },
                onValueChangeFinished = onSeekFinished,
                valueRange = 0f..playbackDuration.coerceAtLeast(1).toFloat(),
                modifier = Modifier.fillMaxWidth()
            )

            Text("${formatTime(playbackPosition)} / ${formatTime(playbackDuration)}")

            Spacer(Modifier.height(16.dp))

            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onPrevious) {
                    Icon(Icons.Filled.SkipPrevious, "Previous")
                }

                IconButton(onClick = onPlayPause) {
                    Icon(
                        if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                        "Play/Pause"
                    )
                }

                IconButton(onClick = onNext) {
                    Icon(Icons.Filled.SkipNext, "Next")
                }
            }

            Spacer(Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = onToggleShuffle) {
                    Text(if (isShuffleEnabled) "Shuffle ON" else "Shuffle OFF")
                }

                Spacer(Modifier.width(8.dp))

                TextButton(onClick = onChangeRepeatMode) {
                    Text(
                        when (selectedRepeatMode) {
                            1 -> "Repeat ONE"
                            2 -> "Repeat ALL"
                            else -> "Repeat OFF"
                        }
                    )
                }
            }
        }
    }
}