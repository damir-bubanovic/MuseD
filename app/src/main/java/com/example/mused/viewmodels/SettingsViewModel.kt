package com.example.mused.viewmodels

import android.app.Application
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.edit
import androidx.lifecycle.AndroidViewModel
import com.example.mused.features.player.EqualizerPreset

class SettingsViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val prefs =
        getApplication<Application>()
            .applicationContext
            .getSharedPreferences(
                PREFS_NAME,
                Context.MODE_PRIVATE
            )

    var sortMode: String by mutableStateOf(
        prefs.getString(
            SORT_MODE_KEY,
            DEFAULT_SORT_MODE
        ) ?: DEFAULT_SORT_MODE
    )
        private set

    var dynamicThemeEnabled: Boolean by mutableStateOf(
        prefs.getBoolean(DYNAMIC_THEME_KEY, false)
    )
        private set

    var equalizerEnabled: Boolean by mutableStateOf(
        prefs.getBoolean(EQUALIZER_ENABLED_KEY, true)
    )
        private set

    var selectedEqualizerPreset: String by mutableStateOf(
        prefs.getString(
            EQUALIZER_PRESET_KEY,
            EqualizerPreset.FLAT.name
        ) ?: EqualizerPreset.FLAT.name
    )
        private set

    fun updateSortMode(newSortMode: String): String {
        sortMode = newSortMode

        prefs.edit {
            putString(SORT_MODE_KEY, newSortMode)
        }

        return sortMode
    }

    fun updateDynamicTheme(enabled: Boolean): Boolean {
        dynamicThemeEnabled = enabled

        prefs.edit {
            putBoolean(DYNAMIC_THEME_KEY, enabled)
        }

        return dynamicThemeEnabled
    }

    fun updateEqualizerEnabled(enabled: Boolean): Boolean {
        equalizerEnabled = enabled

        prefs.edit {
            putBoolean(EQUALIZER_ENABLED_KEY, enabled)
        }

        return equalizerEnabled
    }

    fun updateEqualizerPreset(presetLabel: String): String {
        selectedEqualizerPreset =
            when (presetLabel) {
                "Bass Boost" -> EqualizerPreset.BASS_BOOST.name
                "Vocal" -> EqualizerPreset.VOCAL.name
                "Rock" -> EqualizerPreset.ROCK.name
                "Classical" -> EqualizerPreset.CLASSICAL.name
                else -> EqualizerPreset.FLAT.name
            }

        prefs.edit {
            putString(EQUALIZER_PRESET_KEY, selectedEqualizerPreset)
        }

        return selectedEqualizerPreset
    }

    fun selectedEqualizerPresetLabel(): String {
        return when (selectedEqualizerPreset) {
            EqualizerPreset.BASS_BOOST.name -> "Bass Boost"
            EqualizerPreset.VOCAL.name -> "Vocal"
            EqualizerPreset.ROCK.name -> "Rock"
            EqualizerPreset.CLASSICAL.name -> "Classical"
            else -> "Flat"
        }
    }

    companion object {
        private const val PREFS_NAME = "mused_prefs"
        private const val SORT_MODE_KEY = "sort_mode"
        private const val DEFAULT_SORT_MODE = "Name A-Z"
        private const val DYNAMIC_THEME_KEY = "dynamic_theme"
        private const val EQUALIZER_ENABLED_KEY = "equalizer_enabled"
        private const val EQUALIZER_PRESET_KEY = "equalizer_preset"
    }
}
