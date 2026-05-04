# MUSED — Features Overview

## Goal

MUSED is an offline Android music player that plays local files from a selected folder and allows seamless continuation from where playback stopped.

---

## Chapter 1: Local Music Folder

* User selects a folder containing music files
* App scans and loads supported audio files
* Works fully offline
* User can change folder anytime

Supported formats (initial):

* .mp3
* .wav
* .m4a
* .flac
* .ogg

---

## Chapter 2: Song List

* Display list of songs from selected folder
* Show current playing song
* Default order: file name

Future improvements:

* Sorting options
* Shuffle
* Search

---

## Chapter 3: Playback Controls

* Play
* Pause
* Stop
* Next song
* Previous song
* Auto-play next track

UI elements:

* Play/Pause button
* Progress bar
* Time indicator

---

## Chapter 4: Remember Playback State

App stores:

* Selected folder
* Current song
* Playback position

Example:
User stops at:
Song 5 → 01:42

Next launch:
Resume Song 5 at 01:42

---

## Chapter 5: Resume on App Start

* App loads last session
* User can continue playback immediately

Flow:
Open app → Tap "Continue" → Resume playback

---

## Chapter 6: Folder Changes

* Selecting new folder resets playback
* If songs are deleted:

    * If current song exists → continue
    * If missing → start from first song
* If all songs replaced → start fresh

---

## Chapter 7: Earbuds Support

* Uses system audio output (Bluetooth handled by Android)
* Future:

    * Play/Pause from earbuds
    * Auto-pause on disconnect

---

## Chapter 8: Offline Only

* No internet required
* No streaming
* No accounts
* All files local

---

## Chapter 9: Permissions

* User selects folder manually
* App gets access only to that folder
* No full storage access required

---

## Chapter 10: Version 1 Scope

Initial version includes:

1. Select folder
2. Display songs
3. Play music
4. Pause
5. Auto next
6. Save and restore playback state

---

## Summary

MUSED is a simple, offline-first music player focused on continuous listening from a selected folder.
