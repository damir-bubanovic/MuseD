package com.example.mused.ui.screens

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.edit

@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current

    var selectedFolderUri by remember {
        mutableStateOf(
            context
                .getSharedPreferences("mused_prefs", Context.MODE_PRIVATE)
                .getString("selected_folder_uri", null)
        )
    }

    val folderPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val folderUri = result.data?.data?.toString()

            selectedFolderUri = folderUri

            context
                .getSharedPreferences("mused_prefs", Context.MODE_PRIVATE)
                .edit {
                    putString("selected_folder_uri", folderUri)
                }
        }
    }

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "MUSED",
            style = MaterialTheme.typography.headlineMedium
        )

        Button(
            onClick = {
                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
                folderPickerLauncher.launch(intent)
            }
        ) {
            Text("Select Music Folder")
        }

        selectedFolderUri?.let { uri ->
            Text(text = "Selected folder:")
            Text(text = uri)
        }
    }
}