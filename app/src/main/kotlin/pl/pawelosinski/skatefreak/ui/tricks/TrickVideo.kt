//package pl.pawelosinski.testdm
//
//import android.annotation.SuppressLint
//import android.os.Bundle
//import android.util.Log
//import android.view.View
//import android.webkit.WebView
//import androidx.activity.compose.setContent
//import androidx.appcompat.app.AppCompatActivity
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.wrapContentSize
//import androidx.compose.material3.CircularProgressIndicator
//import androidx.compose.material3.Surface
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.viewinterop.AndroidView
//import androidx.core.view.size
//import androidx.fragment.app.DialogFragment
//import com.dailymotion.player.android.sdk.Dailymotion
//import com.dailymotion.player.android.sdk.LogLevel
//import com.dailymotion.player.android.sdk.PlayerView
//import com.dailymotion.player.android.sdk.listeners.PlayerListener
//import com.dailymotion.player.android.sdk.webview.error.PlayerError
//import pl.pawelosinski.skatefreak.ui.theme.SkateFreakTheme
//
//class MainActivity : AppCompatActivity() {
//
//    private var dmPlayerState = mutableStateOf<PlayerView?>(null)
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
////        setContentView(R.layout.videolayout)
////
////        val logTag = "dmsample-${Dailymotion.version()}"
////        val playerId = "xmbx2"
////        val videoId = "x8qf4eg"
////        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
////
////        val containerView = findViewById<FrameLayout>(R.id.playerContainerView)
////        findViewById<TextView>(R.id.playerIdTextView)?.text = playerId
////        findViewById<TextView>(R.id.videoIdTextView)?.text = videoId
////        findViewById<TextView>(R.id.sdkVersionTextView)?.text = Dailymotion.version()
////        findViewById<TextView>(R.id.adsVersionTextView)?.text = DailymotionAds.version()
////
////        Dailymotion.setLogLevel(LogLevel.All)
////        DailymotionAds.setLogLevel(com.dailymotion.player.android.sdk.ads.LogLevel.All)
////
////        if (dmPlayer == null) {
////            Log.d(
////                logTag,
////                "Creating dailymotion player with playerId=$playerId, videoId=$videoId"
////            )
////            Dailymotion.createPlayer(
////                context = this,
////                playerId = playerId,
////                videoId = videoId,
////                playerSetupListener = object : Dailymotion.PlayerSetupListener {
////                    override fun onPlayerSetupSuccess(player: PlayerView) {
////                        Log.d(logTag, "Successfully created dailymotion player")
////
////                        progressBar.visibility = View.GONE
////                        val lp = FrameLayout.LayoutParams(
////                            FrameLayout.LayoutParams.MATCH_PARENT,
////                            FrameLayout.LayoutParams.MATCH_PARENT
////                        )
////                        containerView.addView(player, lp)
////                        Log.d(logTag, "Added dailymotion player to view hierarchy")
////
////                        dmPlayer = player
////                    }
////
////                    override fun onPlayerSetupFailed(error: PlayerError) {
////                        Log.e(logTag, "Error while creating dailymotion player: ${error.message}")
////                    }
////                },
////                playerListener = object : PlayerListener {
////                    override fun onFullscreenRequested(playerDialogFragment: DialogFragment) {
////                        super.onFullscreenRequested(playerDialogFragment)
////                        playerDialogFragment.show(this@MainActivity.supportFragmentManager, "dmPlayerFullscreenFragment")
////                    }
////                })
////        }
//        setContent{
//            Surface (Modifier.fillMaxSize()) {
//                PlayerScreen()
//            }
//        }
//    }
//
//    @Composable
//    fun PlayerScreen() {
//        val playerId = "xmbx2"
//        val videoId = "x8qf4eg"
//
//        DailymotionPlayer(playerId, videoId)
//    }
//
//
//
//    override fun onDestroy() {
//        super.onDestroy()
//        // Clear the WebView's cache
//        WebView(this).clearCache(true)
//    }
//}
//
//
//@SuppressLint("SetJavaScriptEnabled")
//@Composable
//fun DailymotionPlayer(playerId: String, videoId: String) {
//    val context = LocalContext.current
//    val progressBar = remember { mutableStateOf(true) }
//    val dmPlayerState = remember { mutableStateOf<PlayerView?>(null) }
//
//    AndroidView(
//        factory = { context ->
//            PlayerView(context).apply {
//                Dailymotion.setLogLevel(LogLevel.All)
//
//                Dailymotion.createPlayer(
//                    context = context,
//                    playerId = playerId,
//                    videoId = videoId,
//                    playerSetupListener = object : Dailymotion.PlayerSetupListener {
//                        override fun onPlayerSetupSuccess(player: PlayerView) {
//                            Log.d("DailyMotion", "Successfully created dailymotion player")
//                            progressBar.value = false
//                            dmPlayerState.value = player
//                        }
//
//                        override fun onPlayerSetupFailed(error: PlayerError) {
//                            Log.e("DailyMotion", "Error while creating dailymotion player: ${error.message}")
//                        }
//                    },
//                        playerListener = object : PlayerListener {
//                            override fun onFullscreenRequested(playerDialogFragment: DialogFragment) {
//                                super.onFullscreenRequested(playerDialogFragment)
//                                playerDialogFragment.show(
//                                    (context as AppCompatActivity).supportFragmentManager,
//                                    "dmPlayerFullscreenFragment"
//                                )
//                            }
//                        })
//            }
//        },
//        update = {
//            Log.d("DailyMotion", "Updating dailymotion player")
//            it.let {
//                    dmPlayerState.value = it
//                    Log.d("DailyMotion", "Updated dailymotion player")
//            }
//        },
//        modifier = Modifier.wrapContentSize()
//    )
//
//    if (progressBar.value) {
//        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
//            CircularProgressIndicator()
//        }
//    }
//}
//
//
//@Preview
//@Composable
//fun VideoPreview() {
//    SkateFreakTheme {
//        Surface {
//            DailymotionPlayer(playerId = "xmbx2" , videoId = "x8qf4eg")
//        }
//    }
//
//}