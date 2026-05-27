# MUSED — Features Overview (Updated)

## Goal

MUSED is a modern offline Android music player focused on local music playback, smooth background listening, persistent playback state, clean UI, and advanced offline audio architecture.

Built using:

* Kotlin
* Jetpack Compose
* Media3 / ExoPlayer
* MediaSessionService
* Storage Access Framework (SAF)

---

# Current Development Phase

MUSED is now in the **refactor, optimization, and polish phase**.

The current goal is **not to add new user-facing features**.

Current focus:

* Keep existing features stable
* Improve architecture
* Improve performance
* Reduce duplicated logic
* Prepare the app for future scalability
* Polish existing UI/UX

---

# Chapter 1: Local Music Folder ✅

## Implemented

* User selects music folders via system picker (SAF)
* App scans and loads supported audio files
* Recursive subfolder scanning
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

---

# Chapter 2: Song Library ✅

## Implemented

* Displays songs from selected folders
* Scrollable song list
* Current playing song highlighting
* Click any song to start playback
* Auto-refresh after folder changes
* Search filtering by song name, artist, and album
* Styled card-based song rows
* Mini player inside library screen
* Cached library loading for faster startup
* Cached filtered/sorted visible song list for better large-library performance

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

## Implemented

* Play / Pause
* Next / Previous
* Playlist queue using current library
* Auto-play next track
* Seek bar support
* Smooth seeking
* Background playback
* MediaSession integration
* Playback notification controls
* Lock screen controls

## UI Features

* Material icon controls
* Time display
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
* Current song index
* Sort mode
* Dynamic theme preference
* Equalizer state
* Equalizer preset

## Behavior

* Playback state saved automatically
* Position restored after app restart
* Resume after seeking
* Safe handling for missing files
* Safe handling for invalid saved URI

---

# Chapter 5: Auto Resume ✅

## Implemented

* App automatically restores:
  * Last song
  * Playback position
  * Queue context from current library
  * Shuffle/repeat state

## Flow

App launch → files load → saved song is validated → playback state restored

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

## Current Status

Good enough for current refactor phase. Deeper earbud-specific behavior should wait until after architecture/polish work.

---

# Chapter 9: Album Art & Metadata ✅

## Implemented

* Embedded album art extraction
* Album art displayed:
  * Player screen
  * Mini player
  * Notifications
  * Lock screen
* Album art memory cache
* Persistent album art disk cache
* Metadata extraction
* Metadata cache
* Song title support
* Artist metadata support
* Album metadata support
* Duration metadata support

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
* Offline-only local data handling

---

# Chapter 12: UI / UX Redesign ✅ / 🟡

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

## Still to polish

* OLED dark mode polish
* Smoother animations
* Improved player layout
* Enhanced transitions

---

# Chapter 13: Mini Player ✅

## Implemented

Mini player shown inside library screen:

* Album art
* Song title
* Play / Pause button
* Progress indicator
* Tap to reopen player screen

---

# Chapter 14: Search System ✅

## Implemented

* Real-time filtering by song name
* Artist filtering
* Album filtering
* Case-insensitive matching
* Cached visible list updates

---

# Chapter 15: Queue System ✅

## Implemented

* Up Next queue view
* Queue shown inside player screen
* Tap queue item to instantly play song
* Current song highlighting inside queue

## Current Status

Queue behavior is functional. Advanced queue editing is not part of the current refactor phase.

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
* Service-level audio effect management
* Preferences routed through `AppPreferences`

## Not current focus

* Full equalizer sliders
* Band controls
* Audio visualizer
* Bass strength controls

These are future features and should wait until refactor/polish phase is complete.

---

# Chapter 18: Performance Optimization 🟡

## Implemented

* Song cache system
* Faster library startup
* Cached song restoration
* Metadata caching
* Recursive scanning
* Album art memory cache
* Persistent album art disk cache
* Cached filtered/sorted song list
* Playback progress tracking moved out of `HomeScreen`

## Still to improve

* Large library stress testing
* More efficient metadata loading for huge libraries
* Incremental library refresh
* Possible lazy loading improvements

---

# Chapter 19: Architecture & Stack ✅

## Current Architecture

* Jetpack Compose UI
* Media3 playback engine
* MediaSessionService background service
* SAF file access
* Repository layer
* Centralized preferences wrapper
* Dedicated playback controller
* Dedicated MediaController manager
* Dedicated library ViewModel
* Dedicated settings ViewModel
* Dedicated playback runtime/progress ViewModel
* Dedicated audio effects manager

## Completed Refactors

* Playback metadata extraction moved out of UI
* Media item building separated into helper layer
* Folder loading separated into reusable readers
* Song caching system added
* Audio effects separated into dedicated manager
* Playback actions moved out of `HomeScreen`
* Playback persistence moved out of `HomeScreen`
* MediaController connection moved into `MediaControllerManager`
* Runtime playback state moved into `PlaybackStateViewModel`
* Library state moved into `LibraryViewModel`
* Settings state moved into `SettingsViewModel`
* Preferences centralized in `AppPreferences`
* Repository pattern introduced with `MusicRepository`
* Filtered/sorted song list cached in `LibraryViewModel`
* Album art disk cache added

---

# Version 1 Scope — Achieved ✅

## Original Goals

1. Select folder ✔
2. Display songs ✔
3. Play music ✔
4. Pause ✔
5. Auto next ✔
6. Save/restore playback ✔

## Expanded Achieved Features

7. Recursive folder scanning ✔
8. Background playback ✔
9. Notification controls ✔
10. Lock screen controls ✔
11. Auto resume ✔
12. Seek bar ✔
13. Playlist queue ✔
14. Album art ✔
15. Shuffle / Repeat ✔
16. Search ✔
17. Styled UI ✔
18. Mini player ✔
19. Sorting system ✔
20. Queue system ✔
21. Multiple folder support ✔
22. Settings screen ✔
23. Dynamic themes ✔
24. Equalizer presets ✔
25. Song caching ✔
26. Metadata caching ✔
27. Album art disk cache ✔
28. Multi-ViewModel architecture ✔
29. Repository layer ✔
30. Centralized preferences wrapper ✔

---

# Current Remaining Work

Since we are **not adding new features right now**, the remaining work is mostly cleanup, optimization, and polish.

## 1. UI Polish 🟡

* OLED dark mode polish
* Smoother animations
* Improved player layout
* Better visual hierarchy
* More polished mini player/player transitions

## 2. Large Library Optimization 🟡

* Stress test with hundreds/thousands of songs
* Optimize metadata loading further if needed
* Consider incremental refresh
* Consider lazy loading only if performance requires it

## 3. Architecture Cleanup 🟡

* Reduce remaining duplicated playback state where possible
* Continue shrinking `HomeScreen`
* Consider moving remaining side-effect logic into helper/controller layers
* Consider DataStore migration later, after SharedPreferences wrapper remains stable

## 4. Testing / Stability 🟡

* Test folder removal while playing
* Test missing/deleted files
* Test large folders
* Test app restart/auto-resume repeatedly
* Test Bluetooth/headphone disconnect behavior
* Test notification and lock screen controls after long background playback

---

# Recommended Next Steps

## No new features

Current recommended order:

1. UI polish pass for existing PlayerScreen
2. OLED dark mode polish
3. Large library stress testing
4. Fix only performance problems found during testing
5. Final architecture cleanup
6. README/features documentation update

---

# Summary

MUSED is now a portfolio-level offline Android music player featuring:

* Multi-folder local playback
* Recursive folder scanning
* Background playback
* Notification & lock screen controls
* Persistent playback restoration
* Album art support
* Album art disk caching
* Metadata caching
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
* Repository architecture
* Multi-ViewModel state separation
* Centralized preferences wrapper

MUSED has moved beyond prototype architecture and is now in the final refactor, optimization, and UI polish phase.
