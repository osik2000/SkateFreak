package pl.pawelosinski.skatefreak.ui.tricks.record

import android.util.Log
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.ScrollScope
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import pl.pawelosinski.skatefreak.local.allTrickRecords
import pl.pawelosinski.skatefreak.ui.common.VideoPlayer
import kotlin.math.abs

@Composable
fun TrickRecordsScreen(navController: NavController) {
    val trickRecords = allTrickRecords
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
//    var title by remember { if (trickRecords.size > 0) mutableStateOf(trickRecords[0].title) else mutableStateOf("...") }

    LazyColumn(
        state = listState,
        flingBehavior = snapFlingBehavior(listState, coroutineScope) { index ->
            // Akcja do wykonania po przewinięciu na element o indeksie 'index';
//            title = trickRecords[index].title
//            currentRecordLikes.value = allTrickRecords[index].usersWhoSetAsFavorite.size.toString()
//            LocalDataInit.loadCurrentRecordData(index, trickRecords)
            Log.d("TrickRecordsScreen", "snapFlingBehavior: index = '$index'")
        },
        modifier = Modifier.fillMaxSize(),
    ) {
        items(trickRecords) { trickRecord ->
            Box(
                modifier = Modifier
                    .fillParentMaxSize()
            ) {
                VideoPlayer(videoUrl = trickRecord.videoUrl)
                Column(Modifier.align(Alignment.BottomStart)) {
                    TrickRecordsFooter(
                        navController = navController,
                        trickRecord = trickRecord
                    )
                    Divider()
                }
            }
        }
    }
}

fun snapFlingBehavior(listState: LazyListState, coroutineScope: CoroutineScope,
                      onItemFocused: (Int) -> Unit) = object : FlingBehavior {
    override suspend fun ScrollScope.performFling(initialVelocity: Float): Float {
        val layoutInfo = listState.layoutInfo
        val visibleItemsInfo = layoutInfo.visibleItemsInfo

        val visibleItemClosestToCenter = visibleItemsInfo.minByOrNull {
            abs(it.offset + it.size / 2 - layoutInfo.viewportEndOffset / 2)
        } ?: return initialVelocity

        val targetIndex = visibleItemClosestToCenter.index
        coroutineScope.launch {
            listState.animateScrollToItem(index = targetIndex)
            onItemFocused(targetIndex)
        }

        return initialVelocity
    }
}
