package com.example.mused.viewmodels

import android.app.Application
import android.content.Context
import androidx.core.content.edit
import androidx.lifecycle.AndroidViewModel
import com.example.mused.features.folders.loadSongDataFromFolders
import com.example.mused.features.library.clearSongCache
import com.example.mused.features.library.loadSongCache
import com.example.mused.features.library.saveSongCache
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

    var searchQuery: String = ""
        private set

    var sortMode: String =
        prefs.getString(
            SORT_MODE_KEY,
            DEFAULT_SORT_MODE
        ) ?: DEFAULT_SORT_MODE
        private set

    var savedSongUri: String? =
        prefs.getString(
            CURRENT_SONG_URI_KEY,
            null
        )
        private set

    var savedPosition: Int =
        prefs.getInt(
            CURRENT_POSITION_KEY,
            0
        )
        private set

    var shuffleEnabled: Boolean =
        prefs.getBoolean(
            SHUFFLE_ENABLED_KEY,
            false
        )
        private set

    var repeatMode: Int =
        prefs.getInt(
            REPEAT_MODE_KEY,
            0
        )
        private set

    fun loadSongsFromSelectedFolders(): List<SongData> {
        val loadedSongs =
            loadSongDataFromFolders(
                context = appContext,
                folderUriStrings = selectedFolderUris
            )

        songs = loadedSongs

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

    fun savePlaybackState(
        songUri: String?,
        position: Int,
        shuffleEnabled: Boolean,
        repeatMode: Int
    ) {
        savedSongUri = songUri
        savedPosition = position
        this.shuffleEnabled = shuffleEnabled
        this.repeatMode = repeatMode

        prefs.edit {
            putString(
                CURRENT_SONG_URI_KEY,
                songUri
            )

            putInt(
                CURRENT_POSITION_KEY,
                position
            )

            putBoolean(
                SHUFFLE_ENABLED_KEY,
                shuffleEnabled
            )

            putInt(
                REPEAT_MODE_KEY,
                repeatMode
            )
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

        private const val SELECTED_FOLDER_URIS_KEY =
            "selected_folder_uris"

        private const val SORT_MODE_KEY =
            "sort_mode"

        private const val DEFAULT_SORT_MODE =
            "Name A-Z"

        private const val CURRENT_SONG_URI_KEY =
            "current_song_uri"

        private const val CURRENT_POSITION_KEY =
            "current_position_ms"

        private const val SHUFFLE_ENABLED_KEY =
            "shuffle_enabled"

        private const val REPEAT_MODE_KEY =
            "repeat_mode"
    }
}