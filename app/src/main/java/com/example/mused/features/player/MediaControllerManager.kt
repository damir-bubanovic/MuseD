package com.example.mused.features.player

import android.content.ComponentName
import android.content.Context
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors

@UnstableApi
class MediaControllerManager(
    context: Context
) {
    private val appContext: Context = context.applicationContext
    private var controllerFuture: ListenableFuture<MediaController>? = null
    private var mediaController: MediaController? = null

    fun connect(
        onConnected: (MediaController) -> Unit
    ) {
        if (controllerFuture != null) return

        val sessionToken = SessionToken(
            appContext,
            ComponentName(appContext, MusicService::class.java)
        )

        val future =
            MediaController.Builder(appContext, sessionToken).buildAsync()

        controllerFuture = future

        future.addListener(
            {
                val controller = future.get()
                mediaController = controller
                onConnected(controller)
            },
            MoreExecutors.directExecutor()
        )
    }

    fun applyPlaybackModes(
        shuffleEnabled: Boolean,
        repeatMode: Int
    ) {
        mediaController?.apply {
            shuffleModeEnabled = shuffleEnabled
            this.repeatMode = PlaybackController.toMedia3RepeatMode(repeatMode)
        }
    }

    fun release() {
        controllerFuture?.let { future ->
            MediaController.releaseFuture(future)
        }

        controllerFuture = null
        mediaController = null
    }
}
