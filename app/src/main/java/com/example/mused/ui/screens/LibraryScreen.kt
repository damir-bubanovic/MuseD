package com.example.mused.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.documentfile.provider.DocumentFile
import com.example.mused.utils.formatFolderName

@Composable
fun LibraryScreen(
    modifier: Modifier = Modifier,
    selectedFolderUri: String?,
    files: List<String>,
    currentSongIndex: Int?,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    onPickFolder: () -> Unit,
    onPlaySong: (Int) -> Unit
) {
    val context = LocalContext.current

    val filteredFiles = files.filter { fileUri ->
        val name = DocumentFile.fromSingleUri(context, fileUri.toUri())?.name ?: "Unknown"
        name.contains(searchQuery, ignoreCase = true)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("MUSED", style = MaterialTheme.typography.headlineMedium)

        Spacer(Modifier.height(16.dp))

        Button(onClick = onPickFolder) {
            Text("Select Music Folder")
        }

        Spacer(Modifier.height(16.dp))

        selectedFolderUri?.let { folderUri ->
            Text("Folder: ${formatFolderName(folderUri)}")
            Spacer(Modifier.height(16.dp))
        }

        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchChange,
            label = { Text("Search songs") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(Modifier.height(12.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            itemsIndexed(filteredFiles) { _, fileUri ->
                val index = files.indexOf(fileUri)
                val name =
                    DocumentFile.fromSingleUri(context, fileUri.toUri())?.name ?: "Unknown"

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable { onPlaySong(index) },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (index == currentSongIndex)
                            MaterialTheme.colorScheme.primaryContainer
                        else
                            MaterialTheme.colorScheme.surface
                    )
                ) {
                    Text(
                        text = if (index == currentSongIndex) "▶ $name" else name,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        }
    }
}