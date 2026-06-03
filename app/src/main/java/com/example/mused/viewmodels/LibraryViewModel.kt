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

    private var sourceSongs: List<SongData> =
        musicRepository.loadCachedSongs()

    var songs: List<SongData> =
        sortSongs(sourceSongs, DEFAULT_SORT_MODE)
        private set

    var mediaItems: List<MediaItem> =
        buildMediaItems(appContext, songs)
        private set

    var searchQuery: String by mutableStateOf("")
        private set

    var visibleSongs: List<SongData> by mutableStateOf(emptyList())
        private set

    private var cachedSourceSongs: List<SongData> = emptyList()
    private var cachedSearchQuery: String = ""
    private var cachedVisibleSongs: List<SongData> = emptyList()

    init {
        refreshVisibleSongs()
    }

    fun loadSongsFromSelectedFolders(
        sortMode: String = DEFAULT_SORT_MODE
    ): List<SongData> {
        sourceSongs =
            musicRepository.loadSongsFromFolders(
                selectedFolderUris
            )

        musicRepository.saveSongCache(sourceSongs)

        return refreshPlaybackQueue(sortMode)
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
        sourceSongs = emptyList()
        songs = emptyList()
        mediaItems = emptyList()

        musicRepository.clearSongCache()
        musicRepository.clearSelectedFolderUris()

        invalidateVisibleSongsCache()
        visibleSongs = emptyList()

        return songs
    }

    fun updateSearchQuery(
        newSearchQuery: String
    ): String {
        searchQuery = newSearchQuery
        refreshVisibleSongs()

        return searchQuery
    }

    fun refreshPlaybackQueue(sortMode: String): List<SongData> {
        songs = sortSongs(sourceSongs, sortMode)

        mediaItems =
            buildMediaItems(
                appContext,
                songs
            )

        invalidateVisibleSongsCache()
        refreshVisibleSongs()

        return songs
    }

    fun refreshVisibleSongs(): List<SongData> {
        if (
            cachedSourceSongs === songs &&
            cachedSearchQuery == searchQuery
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

        cachedSourceSongs = songs
        cachedSearchQuery = searchQuery
        cachedVisibleSongs = filteredSongs

        visibleSongs = filteredSongs

        return visibleSongs
    }

    private fun sortSongs(
        songsToSort: List<SongData>,
        sortMode: String
    ): List<SongData> {
        return when (sortMode) {
            SORT_NAME_DESC -> songsToSort.sortedWith { first, second ->
                compareNaturalTitles(second.title, first.title)
            }

            SORT_NEWEST_FIRST -> songsToSort.sortedByDescending { song ->
                song.lastModified
            }

            SORT_OLDEST_FIRST -> songsToSort.sortedBy { song ->
                song.lastModified
            }

            else -> songsToSort.sortedWith { first, second ->
                compareNaturalTitles(first.title, second.title)
            }
        }
    }

    private fun compareNaturalTitles(
        first: String,
        second: String
    ): Int {
        val firstParts = naturalSortParts(first)
        val secondParts = naturalSortParts(second)
        val maxSize = maxOf(firstParts.size, secondParts.size)

        for (index in 0 until maxSize) {
            val firstPart = firstParts.getOrNull(index) ?: return -1
            val secondPart = secondParts.getOrNull(index) ?: return 1

            val firstNumber = firstPart.toLongOrNull()
            val secondNumber = secondPart.toLongOrNull()

            val result =
                if (firstNumber != null && secondNumber != null) {
                    firstNumber.compareTo(secondNumber)
                } else {
                    firstPart.compareTo(secondPart, ignoreCase = true)
                }

            if (result != 0) {
                return result
            }
        }

        return first.compareTo(second, ignoreCase = true)
    }

    private fun naturalSortParts(title: String): List<String> {
        return Regex("\\d+|\\D+")
            .findAll(title)
            .map { match -> match.value.trim() }
            .filter { part -> part.isNotEmpty() }
            .toList()
    }

    private fun invalidateVisibleSongsCache() {
        cachedSourceSongs = emptyList()
        cachedSearchQuery = ""
        cachedVisibleSongs = emptyList()
    }

    private companion object {
        private const val SORT_NAME_DESC = "Name Z-A"
        private const val SORT_NEWEST_FIRST = "Newest First"
        private const val SORT_OLDEST_FIRST = "Oldest First"

        private const val DEFAULT_SORT_MODE = "Name A-Z"
    }
}