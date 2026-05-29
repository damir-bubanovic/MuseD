package com.example.mused.features.folders

import android.content.Context
import com.example.mused.models.SongData

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