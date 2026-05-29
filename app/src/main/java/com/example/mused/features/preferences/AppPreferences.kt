package com.example.mused.features.preferences

import android.content.Context
import androidx.core.content.edit
import com.example.mused.features.player.EqualizerPreset

class AppPreferences(
    context: Context
) {
    private val prefs =
        context.applicationContext.getSharedPreferences(
            PREFS_NAME,
            Context.MODE_PRIVATE
        )

    fun loadSelectedFolderUris(): List<String> {
        return prefs.getStringSet(
            SELECTED_FOLDER_URIS_KEY,
            emptySet()
        )?.toList() ?: emptyList()
    }

    fun saveSelectedFolderUris(folderUris: List<String>) {
        prefs.edit {
            putStringSet(
                SELECTED_FOLDER_URIS_KEY,
                folderUris.toSet()
            )
        }
    }

    fun clearSelectedFolderUris() {
        prefs.edit {
            remove(SELECTED_FOLDER_URIS_KEY)
        }
    }

    fun loadSortMode(): String {
        return prefs.getString(
            SORT_MODE_KEY,
            DEFAULT_SORT_MODE
        ) ?: DEFAULT_SORT_MODE
    }

    fun saveSortMode(sortMode: String) {
        prefs.edit {
            putString(SORT_MODE_KEY, sortMode)
        }
    }

    fun loadDynamicThemeEnabled(): Boolean {
        return prefs.getBoolean(DYNAMIC_THEME_KEY, false)
    }

    fun saveDynamicThemeEnabled(enabled: Boolean) {
        prefs.edit {
            putBoolean(DYNAMIC_THEME_KEY, enabled)
        }
    }

    fun loadEqualizerEnabled(): Boolean {
        return prefs.getBoolean(EQUALIZER_ENABLED_KEY, true)
    }

    fun saveEqualizerEnabled(enabled: Boolean) {
        prefs.edit {
            putBoolean(EQUALIZER_ENABLED_KEY, enabled)
        }
    }

    fun loadEqualizerPreset(): String {
        return prefs.getString(
            EQUALIZER_PRESET_KEY,
            EqualizerPreset.FLAT.name
        ) ?: EqualizerPreset.FLAT.name
    }

    fun saveEqualizerPreset(preset: String) {
        prefs.edit {
            putString(EQUALIZER_PRESET_KEY, preset)
        }
    }

    fun loadSavedSongUri(): String? {
        return prefs.getString(CURRENT_SONG_URI_KEY, null)
    }

    fun loadSavedPosition(): Int {
        return prefs.getInt(CURRENT_POSITION_KEY, 0)
    }

    fun loadSavedSongIndex(): Int? {
        return prefs.getInt(CURRENT_SONG_INDEX_KEY, -1)
            .takeIf { index -> index >= 0 }
    }

    fun loadShuffleEnabled(): Boolean {
        return prefs.getBoolean(SHUFFLE_ENABLED_KEY, false)
    }

    fun loadRepeatMode(): Int {
        return prefs.getInt(REPEAT_MODE_KEY, 0)
    }

    fun savePlaybackState(
        songUri: String?,
        songIndex: Int?,
        position: Int,
        shuffleEnabled: Boolean,
        repeatMode: Int
    ) {
        prefs.edit {
            if (songUri == null) {
                remove(CURRENT_SONG_URI_KEY)
            } else {
                putString(CURRENT_SONG_URI_KEY, songUri)
            }

            if (songIndex == null) {
                remove(CURRENT_SONG_INDEX_KEY)
            } else {
                putInt(CURRENT_SONG_INDEX_KEY, songIndex)
            }

            putInt(CURRENT_POSITION_KEY, position)
            putBoolean(SHUFFLE_ENABLED_KEY, shuffleEnabled)
            putInt(REPEAT_MODE_KEY, repeatMode)
        }
    }

    fun clearPlaybackState() {
        prefs.edit {
            remove(CURRENT_SONG_URI_KEY)
            remove(CURRENT_SONG_INDEX_KEY)
            remove(CURRENT_POSITION_KEY)
        }
    }

    companion object {
        private const val PREFS_NAME = "mused_prefs"

        private const val SELECTED_FOLDER_URIS_KEY = "selected_folder_uris"

        private const val SORT_MODE_KEY = "sort_mode"
        private const val DEFAULT_SORT_MODE = "Name A-Z"

        private const val DYNAMIC_THEME_KEY = "dynamic_theme"
        private const val EQUALIZER_ENABLED_KEY = "equalizer_enabled"
        private const val EQUALIZER_PRESET_KEY = "equalizer_preset"

        private const val CURRENT_SONG_URI_KEY = "current_song_uri"
        private const val CURRENT_SONG_INDEX_KEY = "current_song_index"
        private const val CURRENT_POSITION_KEY = "current_position_ms"

        private const val SHUFFLE_ENABLED_KEY = "shuffle_enabled"
        private const val REPEAT_MODE_KEY = "repeat_mode"
    }
}
