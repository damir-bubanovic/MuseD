package com.example.mused.features.player

import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService

@UnstableApi
class MusicService : MediaSessionService() {

    private lateinit var player: ExoPlayer
    private lateinit var mediaSession: MediaSession

    override fun onCreate() {
        super.onCreate()

        val audioAttributes = AudioAttributes.Builder()
            .setUsage(C.USAGE_MEDIA)
            .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
            .build()

        player = ExoPlayer.Builder(this).build().apply {
            setAudioAttributes(audioAttributes, true)
            playWhenReady = false
            repeatMode = Player.REPEAT_MODE_OFF
            shuffleModeEnabled = false
        }

        mediaSession = MediaSession.Builder(this, player)
            .build()

        setMediaNotificationProvider(
            androidx.media3.session.DefaultMediaNotificationProvider(this)
        )
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession {
        return mediaSession
    }

    override fun onTaskRemoved(rootIntent: android.content.Intent?) {
        if (!player.playWhenReady || player.mediaItemCount == 0) {
            stopSelf()
        }

        super.onTaskRemoved(rootIntent)
    }

    override fun onDestroy() {
        mediaSession.release()
        player.release()
        super.onDestroy()
    }
}