# MUSED — Features Overview (Updated)

## Goal

MUSED is a modern offline Android music player focused on local music playback, clean UI, persistent playback state, and smooth background listening experience.

Built using:

* Kotlin
* Jetpack Compose
* Media3 / ExoPlayer
* MediaSessionService
* Storage Access Framework (SAF)

---

# Chapter 1: Local Music Folder ✅

## Implemented

* User selects music folder via system picker (SAF)
* App scans and loads supported audio files
* Fully offline playback
* Folder persists across app restarts
* User can change folder anytime

## Supported Formats

* .mp3
* .wav
* .m4a
* .flac
* .ogg

---

# Chapter 2: Song Library ✅

## Implemented

* Displays songs from selected folder
* Scrollable song list
* Current playing song highlighting
* Click any song to start playback
* Auto-refresh after folder changes
* Search filtering by song name
* Styled card-based song rows
* Mini player at bottom of library screen

## Current Sorting

* System / file order

## Planned

* Sorting by:
  * Name
  * Date
  * Duration

---

# Chapter 3: Playback System ✅

## Implemented (Media3-based)

* Play / Pause
* Next / Previous
* Playlist queue (entire folder)
* Auto-play next track
* Seek bar support
* Smooth seeking
* Background playback
* MediaSession integration

## UI Features

* Material icon controls
* Time display (mm:ss / mm:ss)
* Shuffle mode
* Repeat modes:
  * Off
  * Repeat One
  * Repeat All

---

# Chapter 4: Playback Persistence ✅

## Stored

* Selected folder URI
* Current song URI
* Playback position
* Current playlist state

## Behavior

* Playback state saved automatically
* Position restored after app restart
* Resume after seeking
* Safe handling for missing files

---

# Chapter 5: Auto Resume ✅

## Implemented

* App automatically restores:
  * last song
  * playback position
  * playlist context

## Flow

App launch → files load → restore playback state

## Safety

* Missing song → safely ignored
* Invalid URI → skipped without crash

---

# Chapter 6: Folder Management ✅

## Implemented

* Switching folders reloads library
* Playback updates to new folder context
* Auto-resume validates song existence
* Safe handling for deleted/replaced files

---

# Chapter 7: Background Playback & System Controls ✅

## Implemented

* Notification media controls
* Lock screen controls
* Background playback while app closed
* Media button support:
  * Play / Pause
  * Next
  * Previous

## Powered By

* Media3
* MediaSessionService
* ExoPlayer

---

# Chapter 8: Earbuds & External Controls 🟡

## Working

* Basic external playback compatibility
* MediaSession integration

## Planned

* Earbud play/pause support
* Hardware next/previous support
* Auto-pause on disconnect

---

# Chapter 9: Album Art & Metadata ✅

## Implemented

* Embedded album art extraction
* Album art displayed:
  * in player screen
  * mini player
  * notifications
  * lock screen

## Metadata

* Song title support
* Artist metadata support ("MUSED")

---

# Chapter 10: Offline-Only Philosophy ✅

* No internet usage
* No streaming
* No accounts
* No cloud dependency
* All playback is local

---

# Chapter 11: Permissions & Privacy ✅

## Implemented

* SAF-based folder access
* No broad storage permission required
* Access limited to selected folder only

---

# Chapter 12: UI / UX Redesign ✅

## Implemented

* Custom MuseD branding
* Custom launcher icon
* Red theme styling
* Styled library cards
* Improved typography
* Modern spacing/layout
* Styled player screen
* Styled mini player
* Current-song highlighting
* Responsive Compose UI

---

# Chapter 13: Mini Player ✅

## Implemented

Mini player shown inside library screen:

* Album art
* Song title
* Play / Pause button
* Tap to reopen player screen

---

# Chapter 14: Search System ✅

## Implemented

* Real-time filtering by song name
* Case-insensitive matching

---

# Chapter 15: Architecture & Stack ✅

## Current Architecture

* Jetpack Compose UI
* Media3 playback engine
* MediaSessionService background service
* SharedPreferences persistence
* SAF file access

## Current Refactors

* Playback metadata extraction moved out of UI
* Media item building separated into helper layer

## Planned Refactors

* ViewModel architecture
* Dedicated playback manager
* Better state separation

---

# Version 1 Scope — ACHIEVED ✅

## Original Goals

1. Select folder ✔
2. Display songs ✔
3. Play music ✔
4. Pause ✔
5. Auto next ✔
6. Save/restore playback ✔

## Expanded Features

7. Background playback ✔
8. Notification controls ✔
9. Lock screen controls ✔
10. Auto resume ✔
11. Seek bar ✔
12. Playlist queue ✔
13. Album art ✔
14. Shuffle / Repeat ✔
15. Search ✔
16. Styled UI ✔
17. Mini player ✔

---

# 🚀 Version 2 Roadmap

## High Priority

### 1. Sorting System

* Sort by:
  * Name
  * Date
  * Duration

---

### 2. Favorites System

* Mark favorite songs
* Favorites playlist
* Persistent favorites storage

---

### 3. Playlist Support

* Create playlists
* Save playlists
* Edit playlists

---

### 4. Queue System

* Up Next queue
* Reorder queue
* Remove songs from queue

---

## UI / UX Improvements

### 5. Animations

* Screen transitions
* Mini player animations
* Playback animations

---

### 6. Settings Screen

* Theme settings
* Playback behavior
* Library options

---

### 7. Better Dark Mode

* OLED-friendly colors
* Enhanced contrast

---

## Advanced Features

### 8. Multiple Folder Support

* Combine multiple music folders
* Persistent multi-library support

---

### 9. Visualizer / Equalizer

* Audio visualizer
* Equalizer support

---

### 10. Architecture Refactor

* ViewModel migration
* State management cleanup
* Better service/UI separation

---

# Summary

MUSED is now a portfolio-level offline Android music player featuring:

* Local folder-based playback
* Background playback
* Notification & lock screen controls
* Persistent playback restoration
* Album art support
* Search functionality
* Shuffle & repeat
* Mini player
* Modern Compose UI
* Media3 architecture

The app has evolved beyond a simple prototype and is now entering Version 2 feature development.