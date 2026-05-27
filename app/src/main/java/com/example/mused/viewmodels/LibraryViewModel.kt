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

    init {
        mediaItems =
            buildMediaItems(
                appContext,
                songs
            )
    }

    fun loadSongsFromSelectedFolders(): List<SongData> {
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

        return loadedSongs
    }

    fun addFolder(folderUri: String): List<SongData> {
        selectedFolderUris =
            (selectedFolderUris + folderUri).distinct()

        musicRepository.saveSelectedFolderUris(selectedFolderUris)

        return loadSongsFromSelectedFolders()
    }

    fun removeFolder(folderUri: String): List<SongData> {
        selectedFolderUris =
            selectedFolderUris.filter {
                it != folderUri
            }

        musicRepository.saveSelectedFolderUris(selectedFolderUris)

        return loadSongsFromSelectedFolders()
    }

    fun clearFolders(): List<SongData> {
        selectedFolderUris = emptyList()
        songs = emptyList()
        mediaItems = emptyList()

        musicRepository.clearSongCache()
        musicRepository.clearSelectedFolderUris()

        return songs
    }

    fun updateSearchQuery(newSearchQuery: String): String {
        searchQuery = newSearchQuery
        return searchQuery
    }

    fun filteredSongs(): List<SongData> {
        return songs.filter { song ->
            song.title.contains(searchQuery, ignoreCase = true) ||
                    song.artist.contains(searchQuery, ignoreCase = true) ||
                    song.album.orEmpty().contains(searchQuery, ignoreCase = true)
        }
    }

    fun sortedSongs(
        sortMode: String
    ): List<SongData> {
        val filteredSongs = filteredSongs()

        return when (sortMode) {
            "Name Z-A" -> filteredSongs.sortedByDescending { it.title }
            "Newest First" -> filteredSongs.sortedByDescending { it.lastModified }
            "Oldest First" -> filteredSongs.sortedBy { it.lastModified }
            else -> filteredSongs.sortedBy { it.title }
        }
    }
}
