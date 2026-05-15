# MUSED — Features Overview (Updated)

## Goal

MUSED is a modern offline Android music player focused on local music playback, smooth background listening, persistent playback state, clean UI, and advanced offline audio features.

Built using:

* Kotlin
* Jetpack Compose
* Media3 / ExoPlayer
* MediaSessionService
* Storage Access Framework (SAF)

---

# Chapter 1: Local Music Folder ✅

## Implemented

* User selects music folders via system picker (SAF)
* App scans and loads supported audio files
* Fully offline playback
* Folder selection persists across app restarts
* User can add/remove folders anytime
* Multiple folder support
* Duplicate folder prevention

## Supported Formats

* .mp3
* .wav
* .m4a
* .flac
* .ogg

## Planned

* Recursive subfolder scanning

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
* Mini player inside library screen
* Cached library loading for faster startup

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
* Playback notification controls
* Lock screen controls

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
* Shuffle state
* Repeat mode
* Current playlist state
* Sort mode
* Dynamic theme preference
* Equalizer state
* Equalizer preset

## Behavior

* Playback state saved automatically
* Position restored after app restart
* Resume after seeking
* Persistent queue restoration
* Safe handling for missing files

---

# Chapter 5: Auto Resume ✅

## Implemented

* App automatically restores:
  * Last song
  * Playback position
  * Queue context
  * Shuffle/repeat state

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
* Audio focus support
* Proper media audio attributes

## Powered By

* Media3
* MediaSessionService
* ExoPlayer

---

# Chapter 8: Earbuds & External Controls 🟡

## Working

* Basic external playback compatibility
* MediaSession integration
* Bluetooth media button compatibility
* External play/pause command support
* Lock screen media command support
* Audio becoming noisy receiver
* Auto-pause on disconnect events

## Planned

### Earbud Integration

* Reliable earbud play/pause handling
* Hardware next/previous support
* Single-tap media controls
* Double-tap next track support
* Triple-tap previous track support

### Smart Pause Features

* Resume after reconnect
* Advanced audio route detection

## Android Integration

### Audio Becoming Noisy Receiver ✅

Uses:

* `AudioManager.ACTION_AUDIO_BECOMING_NOISY`

Purpose:

* Detect wired headphone unplug
* Detect Bluetooth disconnect
* Automatically pause playback before audio switches to speaker

---

# Chapter 9: Album Art & Metadata ✅

## Implemented

* Embedded album art extraction
* Album art displayed:
  * Player screen
  * Mini player
  * Notifications
  * Lock screen

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
* Dynamic Material You theme support
* Styled library cards
* Improved typography
* Modern spacing/layout
* Styled player screen
* Styled mini player
* Current-song highlighting
* Responsive Compose UI

## Planned

* OLED dark mode polish
* Advanced animations
* Enhanced transitions

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

### Theme Settings

* Dynamic theme toggle

### Audio Settings

* Equalizer enable/disable
* Equalizer preset selector

### Information

* Current sort mode display
* About screen
* Version display

---

# Chapter 17: Equalizer System 🟡

## Implemented

* Android Equalizer integration
* Audio session attachment
* Persistent equalizer state
* Persistent preset saving
* Live preset switching
* Live enable/disable support

## Presets

* Flat
* Bass Boost
* Vocal
* Rock
* Classical

## Architecture

* Dedicated `AudioEffectsManager`
* Live SharedPreferences listener support
* Service-level audio effect management

## Planned

* Full equalizer sliders
* Band controls
* Audio visualizer
* Bass strength controls

---

# Chapter 18: Performance Optimization 🟡

## Implemented

* Song cache system
* Faster library startup
* Cached song restoration

## Planned

* Metadata caching
* Recursive optimized scanning
* Large library optimization
* Lazy loading improvements

---

# Chapter 19: Architecture & Stack ✅

## Current Architecture

* Jetpack Compose UI
* Media3 playback engine
* MediaSessionService background service
* SharedPreferences persistence
* SAF file access
* Dedicated audio effects manager

## Current Refactors

* Playback metadata extraction moved out of UI
* Media item building separated into helper layer
* Folder loading separated into reusable readers
* Song caching system added
* Audio effects separated into dedicated manager

## Planned Refactors

* ViewModel architecture
* Dedicated playback manager
* Better state separation
* Repository pattern
* Improved service/UI synchronization

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
22. Dynamic themes ✔
23. Equalizer presets ✔
24. Song caching ✔

---

# 🚀 Version 2 Roadmap

## High Priority

### 1. Recursive Folder Scanning

* Detect music inside subfolders
* Better real-world library support

---

### 2. Metadata Caching

* Faster startup
* Reduced SAF scanning overhead

---

### 3. Advanced Equalizer

* Manual EQ sliders
* Band controls
* Bass enhancement
* Audio visualizer

---

## UI / UX Improvements

### 4. Animations

* Screen transitions
* Mini player animations
* Playback animations

---

### 5. Better Dark Mode

* OLED-friendly colors
* Enhanced contrast

---

### 6. Dynamic Theme Expansion

* Additional theme customization
* Accent color options

---

## Architecture Improvements

### 7. ViewModel Refactor

* Cleaner state management
* Better Compose architecture
* Improved lifecycle handling

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
* Dynamic theming
* Equalizer presets
* Song caching
* Settings management
* Modern Compose UI
* Media3 architecture
* Audio effects system

MUSED has evolved far beyond a basic prototype and is now entering advanced Version 2 development focused on scalability, audio enhancement, performance optimization, and architecture refinement.