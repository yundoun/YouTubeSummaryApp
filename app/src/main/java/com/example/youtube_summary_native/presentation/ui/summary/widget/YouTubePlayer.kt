package com.example.youtube_summary_native.presentation.ui.summary.widget

import android.webkit.PermissionRequest
import android.webkit.WebChromeClient
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerCallback
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView

@Composable
fun YouTubePlayer(
    videoId: String,
    modifier: Modifier = Modifier,
    onReady: (YouTubePlayer) -> Unit = {}
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // VideoId를 key로 사용하여 컴포넌트 재생성
    key(videoId) {
        // playerView를 remember를 통해 생성
        val playerView = remember {
            YouTubePlayerView(context).apply {
                enableAutomaticInitialization = false
            }
        }

        // 초기화 및 lifecycle 관리
        DisposableEffect(videoId) {
            val options = IFramePlayerOptions.Builder()
                .controls(1)
                .autoplay(0)
                .ccLoadPolicy(0)
                .rel(0)
                .ivLoadPolicy(3)
                .build()

            playerView.initialize(object : AbstractYouTubePlayerListener() {
                override fun onReady(youTubePlayer: YouTubePlayer) {
                    youTubePlayer.cueVideo(videoId, 0f)
                    onReady(youTubePlayer)
                }
            }, options)

            lifecycleOwner.lifecycle.addObserver(playerView)

            onDispose {
                lifecycleOwner.lifecycle.removeObserver(playerView)
                playerView.release()
            }
        }

        // Android View 렌더링
        AndroidView(
            factory = { playerView },
            modifier = modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f)
        )
    }
}