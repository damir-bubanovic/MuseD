package com.example.mused.features.library

import android.content.Context
import com.example.mused.models.SongData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


private const val PREFS_NAME = "mused_prefs"
private const val SONG_CACHE_KEY = "song_cache"

fun saveSongCache(
    context: Context,
    songs: List<SongData>
) {

    val prefs =
        context.getSharedPreferences(
            PREFS_NAME,
            Context.MODE_PRIVATE
        )

    val gson = Gson()

    val json =
        gson.toJson(songs)

    prefs.edit()
        .putString(SONG_CACHE_KEY, json)
        .apply()
}

fun loadSongCache(
    context: Context
): List<SongData> {

    val prefs =
        context.getSharedPreferences(
            PREFS_NAME,
            Context.MODE_PRIVATE
        )

    val json =
        prefs.getString(
            SONG_CACHE_KEY,
            null
        ) ?: return emptyList()

    return try {

        val gson = Gson()

        val type =
            object : TypeToken<List<SongData>>() {}.type

        gson.fromJson(json, type)

    } catch (_: Exception) {

        emptyList()
    }
}

fun clearSongCache(
    context: Context
) {

    context.getSharedPreferences(
        PREFS_NAME,
        Context.MODE_PRIVATE
    ).edit()
        .remove(SONG_CACHE_KEY)
        .apply()
}