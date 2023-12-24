package pl.pawelosinski.skatefreak.ui.common

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import androidx.annotation.OptIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
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
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView

@OptIn(UnstableApi::class)
@SuppressLint("OpaqueUnitKey")
@Composable
fun VideoPlayer(videoUrl: String) {
    val context = LocalContext.current
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(videoUrl))
            prepare()
            playWhenReady = false
            repeatMode = Player.REPEAT_MODE_ONE
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
                isPlaying = !isPlaying
                exoPlayer.playWhenReady = !exoPlayer.playWhenReady
            }
        ),
        contentAlignment = Alignment.Center
    ) {
        if (!isPlaying) {
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
        else {
            AndroidView(
                modifier = Modifier
                    .fillMaxWidth(),
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
            DisposableEffect(Unit) {
                onDispose {
                    exoPlayer.release()
                }
            }
        }
    }
}
