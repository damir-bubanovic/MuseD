package com.example.mused.features.library

import com.example.mused.models.SongData

interface MusicRepository {
    fun loadSelectedFolderUris(): List<String>

    fun saveSelectedFolderUris(folderUris: List<String>)

    fun clearSelectedFolderUris()

    fun loadCachedSongs(): List<SongData>

    fun loadSongsFromFolders(folderUris: List<String>): List<SongData>

    fun saveSongCache(songs: List<SongData>)

    fun clearSongCache()
}
