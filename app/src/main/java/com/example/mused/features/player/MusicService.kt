package com.example.mused.features.player

import android.content.SharedPreferences
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService

@UnstableApi
class MusicService :
    MediaSessionService(),
    SharedPreferences.OnSharedPreferenceChangeListener {

    private lateinit var player: ExoPlayer
    private lateinit var mediaSession: MediaSession
    private lateinit var prefs: SharedPreferences

    private val audioEffectsManager = AudioEffectsManager()

    override fun onCreate() {
        super.onCreate()

        prefs = getSharedPreferences(
            "mused_prefs",
            MODE_PRIVATE
        )

        prefs.registerOnSharedPreferenceChangeListener(this)

        val equalizerEnabled =
            prefs.getBoolean("equalizer_enabled", true)

        audioEffectsManager.setEnabled(equalizerEnabled)

        val presetName =
            prefs.getString(
                "equalizer_preset",
                EqualizerPreset.FLAT.name
            ) ?: EqualizerPreset.FLAT.name

        audioEffectsManager.setPreset(
            EqualizerPreset.valueOf(presetName)
        )

        val audioAttributes = AudioAttributes.Builder()
            .setUsage(C.USAGE_MEDIA)
            .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
            .build()

        player = ExoPlayer.Builder(this).build().apply {
            setAudioAttributes(audioAttributes, true)
            playWhenReady = false
            repeatMode = Player.REPEAT_MODE_OFF
            shuffleModeEnabled = false

            addListener(
                object : Player.Listener {
                    override fun onAudioSessionIdChanged(
                        audioSessionId: Int
                    ) {
                        audioEffectsManager.attachToAudioSession(
                            audioSessionId
                        )
                    }
                }
            )
        }

        mediaSession = MediaSession.Builder(this, player)
            .build()

        setMediaNotificationProvider(
            androidx.media3.session.DefaultMediaNotificationProvider(this)
        )
    }

    override fun onGetSession(
        controllerInfo: MediaSession.ControllerInfo
    ): MediaSession {
        return mediaSession
    }

    override fun onTaskRemoved(rootIntent: android.content.Intent?) {
        if (!player.playWhenReady || player.mediaItemCount == 0) {
            stopSelf()
        }

        super.onTaskRemoved(rootIntent)
    }

    override fun onSharedPreferenceChanged(
        sharedPreferences: SharedPreferences?,
        key: String?
    ) {
        when (key) {
            "equalizer_enabled" -> {
                val enabled =
                    prefs.getBoolean(
                        "equalizer_enabled",
                        true
                    )

                audioEffectsManager.setEnabled(enabled)
            }

            "equalizer_preset" -> {
                val presetName =
                    prefs.getString(
                        "equalizer_preset",
                        EqualizerPreset.FLAT.name
                    ) ?: EqualizerPreset.FLAT.name

                audioEffectsManager.setPreset(
                    EqualizerPreset.valueOf(presetName)
                )
            }
        }
    }

    override fun onDestroy() {
        prefs.unregisterOnSharedPreferenceChangeListener(this)

        audioEffectsManager.release()
        mediaSession.release()
        player.release()

        super.onDestroy()
    }
}