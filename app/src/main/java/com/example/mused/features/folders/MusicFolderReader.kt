package com.example.mused.features.folders

import android.content.Context
import androidx.core.net.toUri
import androidx.documentfile.provider.DocumentFile

fun loadMusicFilesFromFolder(
    context: Context,
    folderUriString: String
): List<String> {
    val documentFile = DocumentFile.fromTreeUri(context, folderUriString.toUri())

    return documentFile
        ?.listFiles()
        ?.filter { it.isFile }
        ?.filter { file ->
            val name = file.name?.lowercase() ?: ""
            name.endsWith(".mp3") ||
                    name.endsWith(".wav") ||
                    name.endsWith(".m4a") ||
                    name.endsWith(".flac") ||
                    name.endsWith(".ogg")
        }
        ?.map { file -> file.uri.toString() }
        ?: emptyList()
}