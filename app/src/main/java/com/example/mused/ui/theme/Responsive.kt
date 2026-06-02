package com.example.mused.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class ScreenSize {
    Small,
    Medium,
    Large
}

data class ResponsiveSizes(
    val screenPadding: Dp,
    val sectionSpacing: Dp,
    val smallSpacing: Dp,

    val cardPadding: Dp,
    val cardCornerRadius: Dp,
    val cardVerticalPadding: Dp,

    val buttonHeight: Dp,
    val chipHeight: Dp,
    val searchBarHeight: Dp,

    val headerButtonWidth: Dp,
    val settingsSwitchScale: Float,
    val progressBarHeight: Dp,

    val logoSize: Dp,
    val albumArtSize: Dp,
    val miniPlayerAlbumSize: Dp,
    val miniPlayerHeight: Dp,
    val playerButtonSize: Dp,

    val titleTextSize: TextUnit,
    val bodyTextSize: TextUnit,
    val smallTextSize: TextUnit,
    val songTitleTextSize: TextUnit,
    val songArtistTextSize: TextUnit,

    val playerSongTitleSize: TextUnit,
)

@Composable
fun rememberScreenSize(): ScreenSize {
    val configuration = LocalConfiguration.current
    val screenWidthDp = configuration.screenWidthDp

    return when {
        screenWidthDp <= 360 -> ScreenSize.Small
        screenWidthDp < 600 -> ScreenSize.Medium
        else -> ScreenSize.Large
    }
}

@Composable
fun rememberResponsiveSizes(): ResponsiveSizes {
    return when (rememberScreenSize()) {

        ScreenSize.Small -> ResponsiveSizes(
            screenPadding = 20.dp,
            sectionSpacing = 8.dp,
            smallSpacing = 4.dp,

            cardPadding = 10.dp,
            cardCornerRadius = 14.dp,
            cardVerticalPadding = 7.dp,

            buttonHeight = 44.dp,
            chipHeight = 36.dp,
            searchBarHeight = 56.dp,

            headerButtonWidth = 110.dp,
            settingsSwitchScale = 0.9f,
            progressBarHeight = 3.dp,

            logoSize = 32.dp,
            albumArtSize = 110.dp,
            miniPlayerAlbumSize = 46.dp,
            miniPlayerHeight = 76.dp,
            playerButtonSize = 64.dp,

            titleTextSize = 21.sp,
            bodyTextSize = 13.sp,
            smallTextSize = 12.sp,
            songTitleTextSize = 15.sp,
            songArtistTextSize = 13.sp,

            playerSongTitleSize = 18.sp,
        )

        ScreenSize.Medium -> ResponsiveSizes(
            screenPadding = 28.dp,
            sectionSpacing = 12.dp,
            smallSpacing = 6.dp,

            cardPadding = 12.dp,
            cardCornerRadius = 16.dp,
            cardVerticalPadding = 9.dp,

            buttonHeight = 48.dp,
            chipHeight = 38.dp,
            searchBarHeight = 58.dp,

            headerButtonWidth = 120.dp,
            settingsSwitchScale = 1.0f,
            progressBarHeight = 4.dp,

            logoSize = 40.dp,
            albumArtSize = 128.dp,
            miniPlayerAlbumSize = 52.dp,
            miniPlayerHeight = 88.dp,
            playerButtonSize = 78.dp,

            titleTextSize = 26.sp,
            bodyTextSize = 15.sp,
            smallTextSize = 13.sp,
            songTitleTextSize = 16.sp,
            songArtistTextSize = 13.sp,

            playerSongTitleSize = 22.sp,
        )

        ScreenSize.Large -> ResponsiveSizes(
            screenPadding = 30.dp,
            sectionSpacing = 14.dp,
            smallSpacing = 8.dp,

            cardPadding = 14.dp,
            cardCornerRadius = 18.dp,
            cardVerticalPadding = 10.dp,

            buttonHeight = 52.dp,
            chipHeight = 40.dp,
            searchBarHeight = 60.dp,

            headerButtonWidth = 130.dp,
            settingsSwitchScale = 1.1f,
            progressBarHeight = 5.dp,

            logoSize = 44.dp,
            albumArtSize = 150.dp,
            miniPlayerAlbumSize = 56.dp,
            miniPlayerHeight = 94.dp,
            playerButtonSize = 84.dp,

            titleTextSize = 28.sp,
            bodyTextSize = 16.sp,
            smallTextSize = 14.sp,
            songTitleTextSize = 17.sp,
            songArtistTextSize = 14.sp,

            playerSongTitleSize = 26.sp,
        )
    }
}