package com.example.mused.features.player

import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import com.example.mused.models.SongData

class PlaybackController(
    private val mediaControllerProvider: () -> MediaController?,
    private val songsProvider: () -> List<SongData>,
    private val mediaItemsProvider: () -> List<MediaItem>,
    private val shuffleEnabledProvider: () -> Boolean,
    private val repeatModeProvider: () -> Int,
    private val onSongStarted: (
        song: SongData,
        index: Int,
        playbackPosition: Int,
        playbackDuration: Int
    ) -> Unit,
    private val onPlaybackPositionChanged: (
        position: Int,
        duration: Int
    ) -> Unit,
    private val onPendingSeekChanged: (Int?) -> Unit,
    private val onShuffleChanged: (Boolean) -> Unit,
    private val onRepeatModeChanged: (Int) -> Unit,
    private val savePlaybackState: () -> Unit
) {

    fun playSong(index: Int) {
        val songs = songsProvider()

        if (index !in songs.indices) return

        val song = songs[index]
        val playbackDuration =
            song.durationMs
                .toInt()
                .takeIf { duration -> duration > 0 }
                ?: 0

        onPendingSeekChanged(null)

        onSongStarted(
            song,
            index,
            0,
            playbackDuration
        )

        mediaControllerProvider()?.apply {
            setMediaItems(
                mediaItemsProvider(),
                index,
                0L
            )

            shuffleModeEnabled = shuffleEnabledProvider()
            repeatMode = toMedia3RepeatMode(repeatModeProvider())

            prepare()
            play()
        }
    }

    fun playPrevious() {
        val controller = mediaControllerProvider() ?: return

        if (controller.hasPreviousMediaItem()) {
            controller.seekToPreviousMediaItem()
            controller.play()
        } else {
            controller.seekTo(0)
        }
    }

    fun playNext() {
        val controller = mediaControllerProvider() ?: return

        if (controller.hasNextMediaItem()) {
            controller.seekToNextMediaItem()
            controller.play()
        }
    }

    fun togglePlayPause() {
        val controller = mediaControllerProvider() ?: return

        if (controller.isPlaying) {
            controller.pause()
            savePlaybackState()
        } else {
            controller.play()
        }
    }

    fun pauseAndSave() {
        mediaControllerProvider()?.pause()
        savePlaybackState()
    }

    fun seekTo(
        newPosition: Int,
        playbackDuration: Int
    ): Int {
        val safePosition =
            newPosition.coerceIn(
                0,
                playbackDuration.coerceAtLeast(1)
            )

        onPendingSeekChanged(safePosition)
        mediaControllerProvider()?.seekTo(safePosition.toLong())

        onPlaybackPositionChanged(
            safePosition,
            playbackDuration
        )

        return safePosition
    }

    fun finishSeek(
        pendingSeekPosition: Int?,
        playbackPosition: Int,
        playbackDuration: Int
    ): Int {
        val seekPosition =
            (pendingSeekPosition ?: playbackPosition)
                .coerceIn(
                    0,
                    playbackDuration.coerceAtLeast(1)
                )

        mediaControllerProvider()?.seekTo(seekPosition.toLong())
        onPendingSeekChanged(null)

        onPlaybackPositionChanged(
            seekPosition,
            playbackDuration
        )

        savePlaybackState()

        return seekPosition
    }

    fun toggleShuffle() {
        val newShuffleEnabled = !shuffleEnabledProvider()

        mediaControllerProvider()?.shuffleModeEnabled = newShuffleEnabled
        onShuffleChanged(newShuffleEnabled)

        savePlaybackState()
    }

    fun changeRepeatMode() {
        val newRepeatMode =
            (repeatModeProvider() + 1) % 3

        mediaControllerProvider()?.repeatMode =
            toMedia3RepeatMode(newRepeatMode)

        onRepeatModeChanged(newRepeatMode)

        savePlaybackState()
    }

    companion object {
        fun toMedia3RepeatMode(repeatMode: Int): Int {
            return when (repeatMode) {
                1 -> Player.REPEAT_MODE_ONE
                2 -> Player.REPEAT_MODE_ALL
                else -> Player.REPEAT_MODE_OFF
            }
        }
    }
}
