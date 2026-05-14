package com.example.mused.features.folders

import android.content.Context
import com.example.mused.models.SongData

fun loadMusicFilesFromFolders(
    context: Context,
    folderUriStrings: List<String>
): List<String> {
    return folderUriStrings
        .flatMap { folderUri ->
            loadMusicFilesFromFolder(context, folderUri)
        }
        .distinct()
}

fun loadSongDataFromFolders(
    context: Context,
    folderUriStrings: List<String>
): List<SongData> {
    return folderUriStrings
        .flatMap { folderUri ->
            loadSongDataFromFolder(context, folderUri)
        }
        .distinctBy { song ->
            song.uri
        }
}