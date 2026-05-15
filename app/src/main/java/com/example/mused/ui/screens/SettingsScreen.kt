package com.example.mused.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    currentSortMode: String,
    dynamicThemeEnabled: Boolean,
    onDynamicThemeChange: (Boolean) -> Unit,
    equalizerEnabled: Boolean,
    onEqualizerEnabledChange: (Boolean) -> Unit,
    selectedEqualizerPreset: String,
    onEqualizerPresetSelected: (String) -> Unit,
    onBack: () -> Unit,
    onClearFolders: () -> Unit,
    onClearPlaybackState: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Button(onClick = onBack) {
            Text("Back")
        }

        Spacer(Modifier.height(20.dp))

        Text(
            text = "Settings",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Library",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(12.dp))

                Text(
                    text = "Default Sort Mode",
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(Modifier.height(4.dp))

                Text(
                    text = currentSortMode,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Dynamic Theme",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "Use Android wallpaper colors",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Switch(
                        checked = dynamicThemeEnabled,
                        onCheckedChange = onDynamicThemeChange
                    )
                }

                Spacer(Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Equalizer",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "Enable audio enhancement",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Switch(
                        checked = equalizerEnabled,
                        onCheckedChange = onEqualizerEnabledChange
                    )
                }

                Spacer(Modifier.height(16.dp))

                Text(
                    text = "Equalizer Preset",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(8.dp))

                val expanded = remember {
                    mutableStateOf(false)
                }

                Box {
                    OutlinedButton(
                        onClick = {
                            expanded.value = true
                        }
                    ) {
                        Text(selectedEqualizerPreset)
                    }

                    DropdownMenu(
                        expanded = expanded.value,
                        onDismissRequest = {
                            expanded.value = false
                        }
                    ) {
                        listOf(
                            "Flat",
                            "Bass Boost",
                            "Vocal",
                            "Rock",
                            "Classical"
                        ).forEach { preset ->
                            DropdownMenuItem(
                                text = {
                                    Text(preset)
                                },
                                onClick = {
                                    expanded.value = false
                                    onEqualizerPresetSelected(preset)
                                }
                            )
                        }
                    }
                }

                Spacer(Modifier.height(20.dp))

                Button(
                    onClick = onClearFolders,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Clear All Folders")
                }

                Spacer(Modifier.height(10.dp))

                Button(
                    onClick = onClearPlaybackState,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Clear Playback State")
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "About",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(12.dp))

                Text("MuseD")
                Text("Offline Android Music Player")
                Text("Built with Kotlin + Media3 + Jetpack Compose")

                Spacer(Modifier.height(12.dp))

                Text(
                    text = "Version 1",
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}