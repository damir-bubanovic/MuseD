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

* Displays songs from selected folders
* Scrollable song list
* Current playing song highlighting
* Click any song to start playback
* Auto-refresh after folder changes
* Search filtering by song name
* Styled card-based song rows
* Mini player at bottom of library screen
* Multiple folder support
* Folder removal support

## Sorting System ✅

### Implemented

* Name A-Z
* Name Z-A
* Newest First
* Oldest First

### Persistence

* Sort mode saved across app restarts

---

# Chapter 3: Playback System ✅

## Implemented (Media3-based)

* Play / Pause
* Next / Previous
* Playlist queue (entire library)
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

* Selected folder URIs
* Current song URI
* Playback position
* Current playlist state
* Sort mode

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

* Multiple folder support
* Combined library loading
* Folder removal
* Duplicate folder prevention
* Playback updates to current library state
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
* Access limited to selected folders only

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

# Chapter 15: Queue System ✅

## Implemented

* Up Next queue view
* Queue shown inside player screen
* Tap queue item to instantly play song
* Current song highlighting inside queue

---

# Chapter 16: Settings Screen ✅

## Implemented

### Library Settings

* Clear all folders
* Clear playback state

### Information

* Current sort mode display
* About screen
* Version display

---

# Chapter 17: Architecture & Stack ✅

## Current Architecture

* Jetpack Compose UI
* Media3 playback engine
* MediaSessionService background service
* SharedPreferences persistence
* SAF file access

## Current Refactors

* Playback metadata extraction moved out of UI
* Media item building separated into helper layer
* Folder loading separated into reusable readers

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
18. Sorting system ✔
19. Queue system ✔
20. Multiple folder support ✔
21. Settings screen ✔

---

# 🚀 Version 2 Roadmap

## High Priority

### 1. Sleep Timer

* Auto-stop playback
* Timer presets

---

## UI / UX Improvements

### 2. Animations

* Screen transitions
* Mini player animations
* Playback animations

---

### 3. Better Dark Mode

* OLED-friendly colors
* Enhanced contrast

---

### 4. Dynamic Themes

* Material You support
* Theme customization

---

## Advanced Features

### 5. Equalizer / Visualizer

* Audio visualizer
* Equalizer support

---

### 6. Recursive Folder Scanning

* Detect music inside subfolders

---

### 7. Performance Optimization

* Faster loading for large libraries
* Metadata caching

---

# Chapter 8: Earbuds & External Controls 🟡

## Working

* Basic external playback compatibility
* MediaSession integration
* Bluetooth media button compatibility
* External play/pause command support
* Lock screen media command support

## Planned

### Earbud Integration

* Reliable earbud play/pause handling
* Hardware next/previous support
* Single-tap media controls
* Double-tap next track support
* Triple-tap previous track support

### Smart Pause Features

* Auto-pause on wired headphone disconnect
* Auto-pause on Bluetooth disconnect
* Audio route change detection
* Resume support after reconnect

## Planned Android Integration

### Audio Becoming Noisy Receiver

Will use:

* `AudioManager.ACTION_AUDIO_BECOMING_NOISY`

Purpose:

* Detect wired headphone unplug
* Detect Bluetooth audio disconnect
* Automatically pause playback before audio switches to speaker

## Notes

Modern earbuds may also send pause commands directly through MediaSession controls when removed from the ear. MUSED's Media3 + MediaSessionService architecture is designed to support these standard Android media control behaviors.

---

# Summary

MUSED is now a portfolio-level offline Android music player featuring:

* Multi-folder local playback
* Background playback
* Notification & lock screen controls
* Persistent playback restoration
* Album art support
* Search functionality
* Sorting system
* Queue system
* Shuffle & repeat
* Mini player
* Settings management
* Modern Compose UI
* Media3 architecture

The app has evolved beyond a simple prototype and is now entering advanced Version 2 development.