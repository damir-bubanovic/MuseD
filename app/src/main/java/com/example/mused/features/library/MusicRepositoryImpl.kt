package com.example.mused.features.library

import android.content.Context
import androidx.core.content.edit
import com.example.mused.features.folders.loadSongDataFromFolders
import com.example.mused.models.SongData

class MusicRepositoryImpl(
    private val context: Context
) : MusicRepository {

    private val prefs =
        context.getSharedPreferences(
            PREFS_NAME,
            Context.MODE_PRIVATE
        )

    override fun loadSelectedFolderUris(): List<String> {
        return prefs.getStringSet(
            SELECTED_FOLDER_URIS_KEY,
            emptySet()
        )?.toList() ?: emptyList()
    }

    override fun saveSelectedFolderUris(folderUris: List<String>) {
        prefs.edit {
            putStringSet(
                SELECTED_FOLDER_URIS_KEY,
                folderUris.toSet()
            )
        }
    }

    override fun clearSelectedFolderUris() {
        prefs.edit {
            remove(SELECTED_FOLDER_URIS_KEY)
        }
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

    private companion object {
        private const val PREFS_NAME = "mused_prefs"
        private const val SELECTED_FOLDER_URIS_KEY = "selected_folder_uris"
    }
}
