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
            isSupportedAudioFile(name)
        }
        ?.map { file -> file.uri.toString() }
        ?: emptyList()
}

private fun isSupportedAudioFile(fileName: String): Boolean {
    return fileName.endsWith(".mp3") ||
            fileName.endsWith(".wav") ||
            fileName.endsWith(".m4a") ||
            fileName.endsWith(".flac") ||
            fileName.endsWith(".ogg")
}