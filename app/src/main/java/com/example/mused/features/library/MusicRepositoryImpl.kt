package com.example.mused.features.library

import android.content.Context
import com.example.mused.features.folders.loadSongDataFromFolders
import com.example.mused.features.preferences.AppPreferences
import com.example.mused.models.SongData

class MusicRepositoryImpl(
    private val context: Context,
    private val appPreferences: AppPreferences = AppPreferences(context)
) : MusicRepository {

    override fun loadSelectedFolderUris(): List<String> {
        return appPreferences.loadSelectedFolderUris()
    }

    override fun saveSelectedFolderUris(folderUris: List<String>) {
        appPreferences.saveSelectedFolderUris(folderUris)
    }

    override fun clearSelectedFolderUris() {
        appPreferences.clearSelectedFolderUris()
    }

    override fun loadCachedSongs(): List<SongData> {
        return loadSongCache(context)
    }

    override fun loadSongsFromFolders(folderUris: List<String>): List<SongData> {
        return loadSongDataFromFolders(
            context = context,
            folderUriStrings = folderUris
        )
    }

    override fun saveSongCache(songs: List<SongData>) {
        saveSongCache(
            context,
            songs
        )
    }

    override fun clearSongCache() {
        clearSongCache(context)
    }
}
