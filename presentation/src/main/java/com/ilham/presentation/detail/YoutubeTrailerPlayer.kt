package com.ilham.presentation.detail

import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView

@Composable
fun YouTubeTrailerPlayer(videoId: String, onError: () -> Unit, modifier: Modifier = Modifier) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val currentOnError by rememberUpdatedState(onError)

    AndroidView(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(16f / 9f),
        factory = { context ->
            YouTubePlayerView(context).apply {
                lifecycleOwner.lifecycle.addObserver(this)
                addYouTubePlayerListener(
                    object : AbstractYouTubePlayerListener() {
                        override fun onReady(youTubePlayer: YouTubePlayer) {
                            youTubePlayer.cueVideo(videoId, 0f)
                        }

                        override fun onError(youTubePlayer: YouTubePlayer, error: PlayerConstants.PlayerError) {
                            currentOnError()
                        }
                    },
                )
            }
        },
        onRelease = { view ->
            lifecycleOwner.lifecycle.removeObserver(view)
            view.release()
        },
    )
}