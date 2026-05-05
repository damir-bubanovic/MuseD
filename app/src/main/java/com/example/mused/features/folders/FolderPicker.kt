package com.example.mused.features.folders

import android.app.Activity
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable

@Composable
fun rememberFolderPickerLauncher(
    onFolderSelected: (String?) -> Unit
): () -> Unit {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            onFolderSelected(result.data?.data?.toString())
        }
    }

    return {
        launcher.launch(Intent(Intent.ACTION_OPEN_DOCUMENT_TREE))
    }
}