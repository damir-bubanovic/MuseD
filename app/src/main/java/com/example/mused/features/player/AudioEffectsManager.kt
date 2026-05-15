package com.example.mused.features.player

import android.media.audiofx.Equalizer
import kotlin.math.roundToInt

class AudioEffectsManager {

    private var equalizer: Equalizer? = null
    private var enabled: Boolean = true

    private var currentPreset =
        EqualizerPreset.FLAT

    fun setEnabled(isEnabled: Boolean) {
        enabled = isEnabled
        equalizer?.enabled = isEnabled
    }

    fun setPreset(
        preset: EqualizerPreset
    ) {
        currentPreset = preset

        val eq = equalizer ?: return

        val numberOfBands =
            eq.numberOfBands.toInt()

        val minLevel =
            eq.bandLevelRange[0]

        val maxLevel =
            eq.bandLevelRange[1]

        fun level(percent: Float): Short {
            return (
                    minLevel +
                            ((maxLevel - minLevel) * percent)
                    ).roundToInt().toShort()
        }

        for (band in 0 until numberOfBands) {
            val bandLevel = when (preset) {
                EqualizerPreset.FLAT -> {
                    level(0.5f)
                }

                EqualizerPreset.BASS_BOOST -> {
                    when (band) {
                        0, 1 -> level(0.9f)
                        2 -> level(0.6f)
                        else -> level(0.4f)
                    }
                }

                EqualizerPreset.VOCAL -> {
                    when (band) {
                        1, 2, 3 -> level(0.8f)
                        else -> level(0.4f)
                    }
                }

                EqualizerPreset.ROCK -> {
                    when (band) {
                        0, 4 -> level(0.8f)
                        1, 3 -> level(0.6f)
                        else -> level(0.5f)
                    }
                }

                EqualizerPreset.CLASSICAL -> {
                    when (band) {
                        2, 3 -> level(0.7f)
                        else -> level(0.5f)
                    }
                }
            }

            eq.setBandLevel(
                band.toShort(),
                bandLevel
            )
        }
    }

    fun attachToAudioSession(audioSessionId: Int) {
        if (audioSessionId == 0) return

        release()

        equalizer = Equalizer(0, audioSessionId).apply {
            enabled = this@AudioEffectsManager.enabled
        }

        setPreset(currentPreset)
    }

    fun release() {
        equalizer?.release()
        equalizer = null
    }
}