package com.example.mused.features.folders

import android.content.Context
import androidx.core.net.toUri
import androidx.documentfile.provider.DocumentFile
import com.example.mused.features.library.readSongMetadata
import com.example.mused.models.SongData

fun loadMusicFilesFromFolder(
    context: Context,
    folderUriString: String
): List<String> {
    val documentFile = DocumentFile.fromTreeUri(context, folderUriString.toUri())
        ?: return emptyList()

    return collectMusicFiles(documentFile)
}

fun loadSongDataFromFolder(
    context: Context,
    folderUriString: String
): List<SongData> {
    val documentFile = DocumentFile.fromTreeUri(context, folderUriString.toUri())
        ?: return emptyList()

    return collectSongData(
        context = context,
        folder = documentFile
    )
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

private fun collectSongData(
    context: Context,
    folder: DocumentFile
): List<SongData> {
    return folder.listFiles().flatMap { file ->
        when {
            file.isDirectory -> collectSongData(
                context = context,
                folder = file
            )

            file.isFile && isSupportedAudioFile(file.name?.lowercase() ?: "") -> {
                val uriString = file.uri.toString()
                val fallbackTitle = file.name ?: "Unknown song"

                val metadata = readSongMetadata(
                    context = context,
                    uriString = uriString,
                    fallbackTitle = fallbackTitle
                )

                listOf(
                    SongData(
                        uri = uriString,
                        title = metadata.title,
                        lastModified = file.lastModified(),
                        durationMs = metadata.durationMs,
                        artist = metadata.artist,
                        album = metadata.album
                    )
                )
            }

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