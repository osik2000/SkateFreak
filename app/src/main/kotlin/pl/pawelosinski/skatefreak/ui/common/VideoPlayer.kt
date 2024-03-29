package pl.pawelosinski.skatefreak.ui.common

import android.annotation.SuppressLint
import android.util.Log
import androidx.annotation.OptIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import pl.pawelosinski.skatefreak.local.ThumbnailCacheManager.Companion.getVideoThumbnail

@OptIn(UnstableApi::class)
@SuppressLint("OpaqueUnitKey")
@Composable
fun VideoPlayer(videoUrl: String, id: String) {
    var isEverPlayed by remember { mutableStateOf(false) }
    var showLoading by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(videoUrl))
            playWhenReady = false
            repeatMode = Player.REPEAT_MODE_ONE
            addListener(object : Player.Listener {
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    super.onIsPlayingChanged(isPlaying)
                    if (isPlaying) {
                        showLoading = false
                    }
                }
            })
        }
    }

    var isPlaying by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }

    Box(modifier = Modifier
        .fillMaxSize()
        .clickable(
            interactionSource = interactionSource,
            indication = null, // brak wizualnego wskaźnika kliknięcia
            onClick = {
                if (!isEverPlayed) {
                    showLoading = true
                    isEverPlayed = true
                    exoPlayer.prepare()
                }
                isPlaying = !isPlaying
                exoPlayer.playWhenReady = !exoPlayer.playWhenReady
            }
        ),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (showLoading) {
                Log.d("VideoLoadingBar", "showLoading = $showLoading")
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(48.dp)
                        .align(Alignment.Center)
                        .zIndex(Float.MAX_VALUE)
                )
            }
            AndroidView(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null, // brak wizualnego wskaźnika kliknięcia
                        onClick = {
                            if (!isEverPlayed) {
                                showLoading = true
                                isEverPlayed = true
                                exoPlayer.prepare()
                            }
                            isPlaying = !isPlaying
                            exoPlayer.playWhenReady = !exoPlayer.playWhenReady
                        }
                    ),
                factory = { context ->
                    PlayerView(context).apply {
                        player = exoPlayer
                        useController = false
                    }
                },
                update = { playerView ->
                    playerView.player = exoPlayer
                }
            )
            if (!isPlaying) {
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    if (!isEverPlayed) {
                        val thumbnail = getVideoThumbnail(trickId = id)
                        thumbnail?.asImageBitmap()?.let {
                            Image(
                                bitmap = it,
                                contentDescription = "Thumbnail",
                                modifier = Modifier
                                    //                            .size(48.dp)
                                    .fillMaxSize()
                                    .align(Alignment.Center)
                            )
                        }
                    }
                    IconButton(
                        onClick = {
                            isPlaying = true
                            exoPlayer.playWhenReady = true
                        },
                        modifier = Modifier.align(Alignment.Center)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Play",
                            modifier = Modifier.size(48.dp),
                            tint = Color.White
                        )
                    }
                }
            }
        }
        DisposableEffect(Unit) {
            onDispose {
                exoPlayer.release()
            }
        }
    }
}