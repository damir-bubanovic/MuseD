package com.example.mused.viewmodels

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import com.example.mused.features.player.EqualizerPreset
import com.example.mused.features.preferences.AppPreferences

class SettingsViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val appPreferences =
        AppPreferences(
            getApplication<Application>().applicationContext
        )

    var sortMode: String by mutableStateOf(
        appPreferences.loadSortMode()
    )
        private set

    var dynamicThemeEnabled: Boolean by mutableStateOf(
        appPreferences.loadDynamicThemeEnabled()
    )
        private set

    var equalizerEnabled: Boolean by mutableStateOf(
        appPreferences.loadEqualizerEnabled()
    )
        private set

    var selectedEqualizerPreset: String by mutableStateOf(
        appPreferences.loadEqualizerPreset()
    )
        private set

    fun updateSortMode(newSortMode: String): String {
        sortMode = newSortMode
        appPreferences.saveSortMode(newSortMode)

        return sortMode
    }

    fun updateDynamicTheme(enabled: Boolean): Boolean {
        dynamicThemeEnabled = enabled
        appPreferences.saveDynamicThemeEnabled(enabled)

        return dynamicThemeEnabled
    }

    fun updateEqualizerEnabled(enabled: Boolean): Boolean {
        equalizerEnabled = enabled
        appPreferences.saveEqualizerEnabled(enabled)

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

        appPreferences.saveEqualizerPreset(selectedEqualizerPreset)

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
}
