package com.example.mused.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.example.mused.ui.theme.MusedCardSurface
import com.example.mused.ui.theme.MusedDarkSurface
import com.example.mused.ui.theme.MusedRed
import com.example.mused.ui.theme.MusedSurfaceVariant
import com.example.mused.ui.theme.MusedTextPrimary
import com.example.mused.ui.theme.MusedTextSecondary
import com.example.mused.ui.theme.rememberResponsiveSizes

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
    val responsive = rememberResponsiveSizes()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(
                horizontal = responsive.screenPadding,
                vertical = responsive.sectionSpacing
            )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedButton(
                onClick = onBack,
                modifier = Modifier
                    .width(responsive.headerButtonWidth)
                    .height(responsive.buttonHeight),
                shape = RoundedCornerShape(50),
                border = BorderStroke(1.dp, MusedTextPrimary),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MusedTextPrimary
                )
            ) {
                Text(
                    text = "Back",
                    fontSize = responsive.bodyTextSize
                )
            }

            Text(
                text = "Settings",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontSize = responsive.titleTextSize
                ),
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(Modifier.height(responsive.sectionSpacing))

        SettingsSectionCard(
            title = "Library",
            cardCornerRadius = responsive.cardCornerRadius,
            cardPadding = responsive.cardPadding,
            sectionSpacing = responsive.sectionSpacing,
            titleTextSize = responsive.bodyTextSize
        ) {
            SettingsInfoRow(
                subtitle = currentSortMode,
                bodyTextSize = responsive.bodyTextSize,
                smallTextSize = responsive.smallTextSize,
                smallSpacing = responsive.smallSpacing
            )

            Spacer(Modifier.height(responsive.sectionSpacing))

            SettingsActionButton(
                text = "Clear All Folders",
                buttonHeight = responsive.buttonHeight,
                cardCornerRadius = responsive.cardCornerRadius,
                bodyTextSize = responsive.bodyTextSize,
                onClick = onClearFolders
            )

            Spacer(Modifier.height(responsive.smallSpacing))

            SettingsActionButton(
                text = "Clear Playback State",
                buttonHeight = responsive.buttonHeight,
                cardCornerRadius = responsive.cardCornerRadius,
                bodyTextSize = responsive.bodyTextSize,
                onClick = onClearPlaybackState
            )
        }

        Spacer(Modifier.height(responsive.sectionSpacing))

        SettingsSectionCard(
            title = "Appearance",
            cardCornerRadius = responsive.cardCornerRadius,
            cardPadding = responsive.cardPadding,
            sectionSpacing = responsive.sectionSpacing,
            titleTextSize = responsive.bodyTextSize
        ) {
            SettingsSwitchRow(
                title = "Dynamic Theme",
                subtitle = "Use Android wallpaper colors",
                checked = dynamicThemeEnabled,
                bodyTextSize = responsive.bodyTextSize,
                smallTextSize = responsive.smallTextSize,
                smallSpacing = responsive.smallSpacing,
                onCheckedChange = onDynamicThemeChange
            )
        }

        Spacer(Modifier.height(responsive.sectionSpacing))

        SettingsSectionCard(
            title = "Audio",
            cardCornerRadius = responsive.cardCornerRadius,
            cardPadding = responsive.cardPadding,
            sectionSpacing = responsive.sectionSpacing,
            titleTextSize = responsive.bodyTextSize
        ) {
            SettingsSwitchRow(
                title = "Equalizer",
                subtitle = "Enable audio enhancement",
                checked = equalizerEnabled,
                bodyTextSize = responsive.bodyTextSize,
                smallTextSize = responsive.smallTextSize,
                smallSpacing = responsive.smallSpacing,
                onCheckedChange = onEqualizerEnabledChange
            )

            Spacer(Modifier.height(responsive.sectionSpacing))

            EqualizerPresetDropdown(
                selectedEqualizerPreset = selectedEqualizerPreset,
                buttonHeight = responsive.buttonHeight,
                cardCornerRadius = responsive.cardCornerRadius,
                bodyTextSize = responsive.bodyTextSize,
                smallSpacing = responsive.smallSpacing,
                onEqualizerPresetSelected = onEqualizerPresetSelected
            )
        }

        Spacer(Modifier.height(responsive.sectionSpacing))

        SettingsSectionCard(
            title = "About",
            cardCornerRadius = responsive.cardCornerRadius,
            cardPadding = responsive.cardPadding,
            sectionSpacing = responsive.sectionSpacing,
            titleTextSize = responsive.bodyTextSize
        ) {
            Text(
                text = "MuseD",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = responsive.bodyTextSize
                ),
                fontWeight = FontWeight.Bold,
                color = MusedTextPrimary
            )

            Spacer(Modifier.height(responsive.smallSpacing))

            Text(
                text = "Version 1.0.0",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = responsive.smallTextSize
                ),
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(Modifier.height(responsive.smallSpacing))

            Text(
                text = "Modern Offline Music Player",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = responsive.smallTextSize
                ),
                color = MusedTextSecondary
            )

            Spacer(Modifier.height(responsive.smallSpacing))

            Text(
                text = "Built with Kotlin, Jetpack Compose, Media3 and ExoPlayer",
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = responsive.smallTextSize
                ),
                color = MusedTextSecondary
            )

            Spacer(Modifier.height(responsive.sectionSpacing))

            Text(
                text = "© 2026 Damir Bubanović",
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = responsive.smallTextSize
                ),
                fontWeight = FontWeight.Bold,
                color = MusedTextPrimary
            )
        }
    }
}

@Composable
private fun SettingsSectionCard(
    title: String,
    cardCornerRadius: Dp,
    cardPadding: Dp,
    sectionSpacing: Dp,
    titleTextSize: TextUnit,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(cardCornerRadius),
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
                .padding(cardPadding)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = titleTextSize
                ),
                fontWeight = FontWeight.Bold,
                color = MusedRed
            )

            Spacer(Modifier.height(sectionSpacing))

            content()
        }
    }
}

@Composable
private fun SettingsInfoRow(
    subtitle: String,
    bodyTextSize: TextUnit,
    smallTextSize: TextUnit,
    smallSpacing: Dp
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Default Sort Mode",
            style = MaterialTheme.typography.bodyMedium.copy(
                fontSize = bodyTextSize
            ),
            fontWeight = FontWeight.Bold,
            color = MusedTextPrimary
        )

        Spacer(Modifier.height(smallSpacing))

        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodySmall.copy(
                fontSize = smallTextSize
            ),
            color = MusedTextSecondary
        )
    }
}

@Composable
private fun SettingsSwitchRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    bodyTextSize: TextUnit,
    smallTextSize: TextUnit,
    smallSpacing: Dp,
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
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = bodyTextSize
                ),
                fontWeight = FontWeight.Bold,
                color = MusedTextPrimary
            )

            Spacer(Modifier.height(smallSpacing))

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = smallTextSize
                ),
                color = MusedTextSecondary
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
    buttonHeight: Dp,
    cardCornerRadius: Dp,
    bodyTextSize: TextUnit,
    smallSpacing: Dp,
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
            style = MaterialTheme.typography.bodyMedium.copy(
                fontSize = bodyTextSize
            ),
            fontWeight = FontWeight.Bold,
            color = MusedTextPrimary
        )

        Spacer(Modifier.height(smallSpacing))

        Box {
            OutlinedButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(buttonHeight),
                onClick = {
                    expanded.value = true
                },
                shape = RoundedCornerShape(cardCornerRadius),
                border = BorderStroke(1.dp, MusedTextPrimary),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = MusedDarkSurface,
                    contentColor = MusedTextPrimary
                )
            ) {
                Text(
                    text = selectedEqualizerPreset,
                    fontSize = bodyTextSize
                )
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
                            Text(
                                text = preset,
                                color = MusedTextPrimary
                            )
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
    buttonHeight: Dp,
    cardCornerRadius: Dp,
    bodyTextSize: TextUnit,
    onClick: () -> Unit
) {
    TextButton(
        modifier = Modifier
            .fillMaxWidth()
            .height(buttonHeight),
        onClick = onClick,
        shape = RoundedCornerShape(cardCornerRadius),
        colors = ButtonDefaults.textButtonColors(
            containerColor = MusedSurfaceVariant,
            contentColor = MusedTextPrimary
        )
    ) {
        Text(
            text = text,
            fontSize = bodyTextSize,
            fontWeight = FontWeight.Bold
        )
    }
}