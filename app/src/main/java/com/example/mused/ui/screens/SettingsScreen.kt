package com.example.mused.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import com.example.mused.ui.theme.MusedCardSurface
import com.example.mused.ui.theme.MusedDarkSurface
import com.example.mused.ui.theme.MusedRed
import com.example.mused.ui.theme.MusedSurfaceVariant

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
            .background(Color.Black)
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 18.dp, vertical = 14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedButton(
                onClick = onBack,
                shape = RoundedCornerShape(50)
            ) {
                Text("Back")
            }

            Text(
                text = "Settings",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(Modifier.height(18.dp))

        SettingsSectionCard(
            title = "Library"
        ) {
            SettingsInfoRow(
                title = "Default Sort Mode",
                subtitle = currentSortMode
            )

            Spacer(Modifier.height(14.dp))

            SettingsActionButton(
                text = "Clear All Folders",
                onClick = onClearFolders
            )

            Spacer(Modifier.height(10.dp))

            SettingsActionButton(
                text = "Clear Playback State",
                onClick = onClearPlaybackState
            )
        }

        Spacer(Modifier.height(14.dp))

        SettingsSectionCard(
            title = "Appearance"
        ) {
            SettingsSwitchRow(
                title = "Dynamic Theme",
                subtitle = "Use Android wallpaper colors",
                checked = dynamicThemeEnabled,
                onCheckedChange = onDynamicThemeChange
            )
        }

        Spacer(Modifier.height(14.dp))

        SettingsSectionCard(
            title = "Audio"
        ) {
            SettingsSwitchRow(
                title = "Equalizer",
                subtitle = "Enable audio enhancement",
                checked = equalizerEnabled,
                onCheckedChange = onEqualizerEnabledChange
            )

            Spacer(Modifier.height(16.dp))

            EqualizerPresetDropdown(
                selectedEqualizerPreset = selectedEqualizerPreset,
                onEqualizerPresetSelected = onEqualizerPresetSelected
            )
        }

        Spacer(Modifier.height(14.dp))

        SettingsSectionCard(
            title = "About"
        ) {
            Text(
                text = "MuseD",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = "Offline Android Music Player",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = "Built with Kotlin + Media3 + Jetpack Compose",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(10.dp))

            Text(
                text = "Version 1",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun SettingsSectionCard(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MusedCardSurface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MusedRed
            )

            Spacer(Modifier.height(12.dp))

            content()
        }
    }
}

@Composable
private fun SettingsInfoRow(
    title: String,
    subtitle: String
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(Modifier.height(3.dp))

        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun SettingsSwitchRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(Modifier.height(3.dp))

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

@Composable
private fun EqualizerPresetDropdown(
    selectedEqualizerPreset: String,
    onEqualizerPresetSelected: (String) -> Unit
) {
    val expanded = remember {
        mutableStateOf(false)
    }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Equalizer Preset",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(Modifier.height(8.dp))

        Box {
            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    expanded.value = true
                },
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = MusedDarkSurface
                )
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
    }
}

@Composable
private fun SettingsActionButton(
    text: String,
    onClick: () -> Unit
) {
    TextButton(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        shape = RoundedCornerShape(18.dp),
        colors = ButtonDefaults.textButtonColors(
            containerColor = MusedSurfaceVariant,
            contentColor = MaterialTheme.colorScheme.primary
        )
    ) {
        Text(
            text = text,
            fontWeight = FontWeight.Bold
        )
    }
}
