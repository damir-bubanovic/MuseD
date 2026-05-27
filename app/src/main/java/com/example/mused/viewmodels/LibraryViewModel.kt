package com.example.mused.viewmodels

import android.app.Application
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.media3.common.MediaItem
import com.example.mused.features.library.MusicRepository
import com.example.mused.features.library.MusicRepositoryImpl
import com.example.mused.features.player.buildMediaItems
import com.example.mused.features.preferences.AppPreferences
import com.example.mused.models.SongData

class LibraryViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val appContext: Context =
        getApplication<Application>().applicationContext

    private val appPreferences =
        AppPreferences(appContext)

    private val musicRepository: MusicRepository =
        MusicRepositoryImpl(
            context = appContext,
            appPreferences = appPreferences
        )

    var selectedFolderUris: List<String> =
        musicRepository.loadSelectedFolderUris()
        private set

    var songs: List<SongData> =
        musicRepository.loadCachedSongs()
        private set

    var mediaItems: List<MediaItem> = emptyList()
        private set

    var searchQuery: String by mutableStateOf("")
        private set

    var visibleSongs: List<SongData> by mutableStateOf(emptyList())
        private set

    private var cachedSourceSongs: List<SongData> = emptyList()
    private var cachedSearchQuery: String = ""
    private var cachedSortMode: String = ""
    private var cachedVisibleSongs: List<SongData> = emptyList()

    init {
        mediaItems =
            buildMediaItems(
                appContext,
                songs
            )

        refreshVisibleSongs(DEFAULT_SORT_MODE)
    }

    fun loadSongsFromSelectedFolders(
        sortMode: String = DEFAULT_SORT_MODE
    ): List<SongData> {
        val loadedSongs =
            musicRepository.loadSongsFromFolders(
                selectedFolderUris
            )

        songs = loadedSongs

        mediaItems =
            buildMediaItems(
                appContext,
                loadedSongs
            )

        musicRepository.saveSongCache(loadedSongs)

        refreshVisibleSongs(sortMode)

        return loadedSongs
    }

    fun addFolder(
        folderUri: String,
        sortMode: String = DEFAULT_SORT_MODE
    ): List<SongData> {
        selectedFolderUris =
            (selectedFolderUris + folderUri).distinct()

        musicRepository.saveSelectedFolderUris(selectedFolderUris)

        return loadSongsFromSelectedFolders(sortMode)
    }

    fun removeFolder(
        folderUri: String,
        sortMode: String = DEFAULT_SORT_MODE
    ): List<SongData> {
        selectedFolderUris =
            selectedFolderUris.filter {
                it != folderUri
            }

        musicRepository.saveSelectedFolderUris(selectedFolderUris)

        return loadSongsFromSelectedFolders(sortMode)
    }

    fun clearFolders(): List<SongData> {
        selectedFolderUris = emptyList()
        songs = emptyList()
        mediaItems = emptyList()

        musicRepository.clearSongCache()
        musicRepository.clearSelectedFolderUris()

        invalidateVisibleSongsCache()
        visibleSongs = emptyList()

        return songs
    }

    fun updateSearchQuery(
        newSearchQuery: String,
        sortMode: String
    ): String {
        searchQuery = newSearchQuery
        refreshVisibleSongs(sortMode)

        return searchQuery
    }

    fun refreshVisibleSongs(sortMode: String): List<SongData> {
        if (
            cachedSourceSongs === songs &&
            cachedSearchQuery == searchQuery &&
            cachedSortMode == sortMode
        ) {
            visibleSongs = cachedVisibleSongs
            return visibleSongs
        }

        val filteredSongs =
            if (searchQuery.isBlank()) {
                songs
            } else {
                songs.filter { song ->
                    song.title.contains(searchQuery, ignoreCase = true) ||
                            song.artist.contains(searchQuery, ignoreCase = true) ||
                            song.album.orEmpty().contains(searchQuery, ignoreCase = true)
                }
            }

        val sortedSongs =
            when (sortMode) {
                "Name Z-A" -> filteredSongs.sortedByDescending { song ->
                    song.title.lowercase()
                }

                "Newest First" -> filteredSongs.sortedByDescending { song ->
                    song.lastModified
                }

                "Oldest First" -> filteredSongs.sortedBy { song ->
                    song.lastModified
                }

                else -> filteredSongs.sortedBy { song ->
                    song.title.lowercase()
                }
            }

        cachedSourceSongs = songs
        cachedSearchQuery = searchQuery
        cachedSortMode = sortMode
        cachedVisibleSongs = sortedSongs

        visibleSongs = sortedSongs

        return visibleSongs
    }

    fun sortedSongs(sortMode: String): List<SongData> {
        return refreshVisibleSongs(sortMode)
    }

    private fun invalidateVisibleSongsCache() {
        cachedSourceSongs = emptyList()
        cachedSearchQuery = ""
        cachedSortMode = ""
        cachedVisibleSongs = emptyList()
    }

    private companion object {
        private const val DEFAULT_SORT_MODE = "Name A-Z"
    }
}
