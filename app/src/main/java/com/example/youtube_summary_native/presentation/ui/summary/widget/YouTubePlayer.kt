package com.example.youtube_summary_native.presentation.ui.summary.widget

import android.webkit.PermissionRequest
import android.webkit.WebChromeClient
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
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

    val playerView = remember {
        YouTubePlayerView(context).apply {
            enableAutomaticInitialization = false

            val options = IFramePlayerOptions.Builder()
                .controls(1)          // 컨트롤 표시
                .autoplay(0)          // 자동 재생 비활성화
                .ccLoadPolicy(0)      // 자막 비활성화
                .rel(0)               // 관련 동영상은 같은 채널의 영상만 표시
                .ivLoadPolicy(3)      // 동영상 미리보기 이미지 정책
                .build()

            initialize(
                object : AbstractYouTubePlayerListener() {
                    override fun onReady(youTubePlayer: YouTubePlayer) {
                        youTubePlayer.cueVideo(videoId, 0f)  // loadVideo 대신 cueVideo 사용
                        onReady(youTubePlayer)
                    }
                },
                options
            )
        }
    }

    DisposableEffect(videoId) {
        lifecycleOwner.lifecycle.addObserver(playerView)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(playerView)
            playerView.release()
        }
    }

    AndroidView(
        factory = { playerView },
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(16f / 9f)
    )
}