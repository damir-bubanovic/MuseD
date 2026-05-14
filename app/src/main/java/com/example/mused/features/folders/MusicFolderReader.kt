package com.example.mused.features.folders

import android.content.Context
import androidx.core.net.toUri
import androidx.documentfile.provider.DocumentFile

fun loadMusicFilesFromFolder(
    context: Context,
    folderUriString: String
): List<String> {
    val documentFile = DocumentFile.fromTreeUri(context, folderUriString.toUri())
        ?: return emptyList()

    return collectMusicFiles(documentFile)
}

private fun collectMusicFiles(folder: DocumentFile): List<String> {
    return folder.listFiles().flatMap { file ->
        when {
            file.isDirectory -> collectMusicFiles(file)

            file.isFile && isSupportedAudioFile(file.name?.lowercase() ?: "") ->
                listOf(file.uri.toString())

            else -> emptyList()
        }
    }
}

private fun isSupportedAudioFile(fileName: String): Boolean {
    return fileName.endsWith(".mp3") ||
            fileName.endsWith(".wav") ||
            fileName.endsWith(".m4a") ||
            fileName.endsWith(".flac") ||
            fileName.endsWith(".ogg")
}