package com.example.mused.features.folders

import android.content.Context

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