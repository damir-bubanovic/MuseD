# MUSED — Features Overview (Updated)

## Goal

MUSED is an offline Android music player that plays local files from a selected folder, supports background playback, and automatically resumes from the last position.

---

## Chapter 1: Local Music Folder ✅

**Implemented:**

* User selects a folder via system picker (Storage Access Framework)
* App scans and loads supported audio files
* Works fully offline
* Folder is persisted across app restarts
* User can change folder anytime

**Supported formats:**

* .mp3
* .wav
* .m4a
* .flac
* .ogg

---

## Chapter 2: Song List ✅

**Implemented:**

* Displays songs from selected folder
* Shows currently playing song (highlight + indicator)
* Click to play any song
* Auto-updates when folder changes
* Default sorting: file name (system order)

**Planned:**

* Sorting (name, date, duration)
* Shuffle mode
* Search

---

## Chapter 3: Playback Controls ✅

**Implemented (Media3-based):**

* Play / Pause
* Next / Previous
* Auto-play next track
* Playlist queue (entire folder)
* Background playback via `MediaSessionService`

**UI:**

* Icon-based controls (Material icons)
* Seek bar (draggable)
* Time display (mm:ss / mm:ss)

---

## Chapter 4: Playback State Persistence ✅

**Stored:**

* Selected folder URI
* Current song URI
* Playback position

**Behavior:**

* State saved on pause/play interactions
* Position updated after seeking
* Safe handling if file is missing

---

## Chapter 5: Auto Resume on App Start ✅

**Implemented:**

* App automatically resumes last song and position
* No manual “Resume” button required

**Flow:**

Open app → files load → auto resume (if valid)

**Safety:**

* If song exists → resume
* If missing → ignore safely (no crash)

---

## Chapter 6: Folder Changes ✅

**Implemented behavior:**

* Selecting a new folder reloads song list
* Playback resets to new context
* Auto-resume only runs if saved song exists in new list

**Handling edge cases:**

* Deleted song → ignored
* Replaced files → treated as new playlist
* Invalid saved URI → safely skipped

---

## Chapter 7: System & Lock Screen Controls ✅

**Implemented:**

* Notification media player
* Lock screen controls
* Background playback (app can be closed)
* Media buttons supported:

  * Play / Pause
  * Next
  * Previous

**Powered by:**

Media3 + MediaSessionService + ExoPlayer

---

## Chapter 8: Earbuds / External Controls (Partial) 🟡

**Working:**

* Basic playback via system audio routing
* MediaSession enables external control compatibility

**Planned:**

* Play/Pause via earbuds
* Next/Previous via hardware buttons
* Auto-pause on disconnect

---

## Chapter 9: Offline Only ✅

* No internet usage
* No streaming
* No accounts
* All files are local

---

## Chapter 10: Permissions ✅

* Uses system folder picker (SAF)
* Access limited to selected folder only
* No broad storage permission required

---

## Chapter 11: UI / Player Experience ✅

**Implemented:**

* Clean layout (Compose)
* Highlight current song
* Icon-based controls
* Seek bar
* Formatted time display
* Scrollable song list

---

## Chapter 12: Version 1 Scope (ACHIEVED + EXPANDED)

### Originally planned:

1. Select folder ✔
2. Display songs ✔
3. Play music ✔
4. Pause ✔
5. Auto next ✔
6. Save/restore playback ✔

### Now additionally implemented:

7. Background playback ✔
8. Notification controls ✔
9. Lock screen controls ✔
10. Auto resume ✔
11. Seek bar ✔
12. Playlist queue ✔

---

# 🚀 Next Features (Not Yet Implemented)

## High Impact (Recommended Next)

### 1. Album Art

* Show in app
* Show in notification
* Show on lock screen

---

### 2. Shuffle / Repeat

* Shuffle playlist
* Repeat one / all

---

### 3. Better UI

* Card-style song rows
* Improved spacing & typography
* Larger touch targets

---

### 4. Seek Bar Improvements

* Show buffered progress
* Smooth dragging

---

## Medium Priority

### 5. Search

* Filter songs by name

### 6. Sorting

* Name / date / duration

### 7. Folder memory improvements

* Multiple folders support

---

## Advanced / Architecture

### 8. Refactor player logic out of UI

* Move playback logic into dedicated layer (ViewModel / Manager)

---

## Summary

MUSED is now a fully functional offline music player with:

* folder-based playback
* persistent state
* background + lock screen control
* modern Media3 architecture

This is portfolio-level Android work.
