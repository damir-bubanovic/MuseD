package com.example.mused.viewmodels

import android.app.Application
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.edit
import androidx.lifecycle.AndroidViewModel
import androidx.media3.common.MediaItem
import com.example.mused.features.folders.loadSongDataFromFolders
import com.example.mused.features.library.clearSongCache
import com.example.mused.features.library.loadSongCache
import com.example.mused.features.library.saveSongCache
import com.example.mused.features.player.EqualizerPreset
import com.example.mused.features.player.buildMediaItems
import com.example.mused.models.PlaybackUiState
import com.example.mused.models.SongData

class HomeViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val appContext: Context =
        getApplication<Application>().applicationContext

    private val prefs =
        appContext.getSharedPreferences(
            PREFS_NAME,
            Context.MODE_PRIVATE
        )

    var selectedFolderUris: List<String> =
        prefs.getStringSet(
            SELECTED_FOLDER_URIS_KEY,
            emptySet()
        )?.toList() ?: emptyList()
        private set

    var songs: List<SongData> =
        loadSongCache(appContext)
        private set

    var mediaItems: List<MediaItem> = emptyList()
        private set

    var searchQuery: String = ""
        private set

    var sortMode: String =
        prefs.getString(
            SORT_MODE_KEY,
            DEFAULT_SORT_MODE
        ) ?: DEFAULT_SORT_MODE
        private set

    var dynamicThemeEnabled: Boolean =
        prefs.getBoolean(DYNAMIC_THEME_KEY, false)
        private set

    var equalizerEnabled: Boolean =
        prefs.getBoolean(EQUALIZER_ENABLED_KEY, true)
        private set

    var selectedEqualizerPreset: String =
        prefs.getString(
            EQUALIZER_PRESET_KEY,
            EqualizerPreset.FLAT.name
        ) ?: EqualizerPreset.FLAT.name
        private set

    var savedSongUri: String? =
        prefs.getString(CURRENT_SONG_URI_KEY, null)
        private set

    var savedPosition: Int =
        prefs.getInt(CURRENT_POSITION_KEY, 0)
        private set

    var savedSongIndex: Int? =
        prefs.getInt(CURRENT_SONG_INDEX_KEY, -1)
            .takeIf { index -> index >= 0 }
        private set

    var shuffleEnabled: Boolean =
        prefs.getBoolean(SHUFFLE_ENABLED_KEY, false)
        private set

    var repeatMode: Int =
        prefs.getInt(REPEAT_MODE_KEY, 0)
        private set

    var playbackUiState by mutableStateOf(
        PlaybackUiState(
            shuffleEnabled = shuffleEnabled,
            repeatMode = repeatMode
        )
    )
        private set

    fun loadSongsFromSelectedFolders(): List<SongData> {
        val loadedSongs =
            loadSongDataFromFolders(
                context = appContext,
                folderUriStrings = selectedFolderUris
            )

        songs = loadedSongs

        mediaItems = buildMediaItems(
            appContext,
            loadedSongs
        )

        saveSongCache(
            appContext,
            loadedSongs
        )

        return loadedSongs
    }

    fun addFolder(folderUri: String): List<SongData> {
        selectedFolderUris =
            (selectedFolderUris + folderUri).distinct()

        saveSelectedFolders()

        return loadSongsFromSelectedFolders()
    }

    fun removeFolder(folderUri: String): List<SongData> {
        selectedFolderUris =
            selectedFolderUris.filter {
                it != folderUri
            }

        saveSelectedFolders()

        return loadSongsFromSelectedFolders()
    }

    fun clearFolders(): List<SongData> {
        selectedFolderUris = emptyList()
        songs = emptyList()
        mediaItems = emptyList()

        clearSongCache(appContext)

        prefs.edit {
            remove(SELECTED_FOLDER_URIS_KEY)
        }

        return songs
    }

    fun updateSearchQuery(newSearchQuery: String): String {
        searchQuery = newSearchQuery
        return searchQuery
    }

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

    fun savePlaybackState(
        songUri: String?,
        position: Int,
        shuffleEnabled: Boolean,
        repeatMode: Int,
        songIndex: Int? = playbackUiState.currentSongIndex
    ) {
        savedSongUri = songUri
        savedSongIndex = songIndex
        savedPosition = position
        this.shuffleEnabled = shuffleEnabled
        this.repeatMode = repeatMode

        playbackUiState =
            playbackUiState.copy(
                playbackPosition = position,
                shuffleEnabled = shuffleEnabled,
                repeatMode = repeatMode
            )

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
        savedSongUri = null
        savedSongIndex = null
        savedPosition = 0

        playbackUiState =
            playbackUiState.copy(
                currentSongName = null,
                currentSongUri = null,
                currentSongIndex = null,
                isPlaying = false,
                playbackPosition = 0,
                playbackDuration = 0
            )

        prefs.edit {
            remove(CURRENT_SONG_URI_KEY)
            remove(CURRENT_SONG_INDEX_KEY)
            remove(CURRENT_POSITION_KEY)
        }
    }

    fun setCurrentSong(
        songName: String?,
        songUri: String?,
        songIndex: Int?
    ) {
        playbackUiState =
            playbackUiState.copy(
                currentSongName = songName,
                currentSongUri = songUri,
                currentSongIndex = songIndex
            )
    }

    fun setIsPlaying(isPlaying: Boolean) {
        playbackUiState =
            playbackUiState.copy(
                isPlaying = isPlaying
            )
    }

    fun setPlaybackPosition(
        position: Int,
        duration: Int
    ) {
        playbackUiState =
            playbackUiState.copy(
                playbackPosition = position,
                playbackDuration = duration
            )
    }

    fun filteredSongs(): List<SongData> {
        return songs.filter { song ->
            song.title.contains(searchQuery, ignoreCase = true) ||
                    song.artist.contains(searchQuery, ignoreCase = true) ||
                    song.album.orEmpty().contains(searchQuery, ignoreCase = true)
        }
    }

    fun sortedSongs(): List<SongData> {
        val filteredSongs = filteredSongs()

        return when (sortMode) {
            "Name Z-A" -> filteredSongs.sortedByDescending { it.title }
            "Newest First" -> filteredSongs.sortedByDescending { it.lastModified }
            "Oldest First" -> filteredSongs.sortedBy { it.lastModified }
            else -> filteredSongs.sortedBy { it.title }
        }
    }

    private fun saveSelectedFolders() {
        prefs.edit {
            putStringSet(
                SELECTED_FOLDER_URIS_KEY,
                selectedFolderUris.toSet()
            )
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