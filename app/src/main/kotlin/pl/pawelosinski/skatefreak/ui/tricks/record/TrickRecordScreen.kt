package pl.pawelosinski.skatefreak.ui.tricks.record

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.ScrollScope
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import pl.pawelosinski.skatefreak.local.allTrickRecords
import pl.pawelosinski.skatefreak.ui.common.VideoPlayer
import kotlin.math.abs

@Composable
fun TrickRecordsScreen() {
    val trickRecords = allTrickRecords
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    LazyColumn(
        state = listState,
        flingBehavior = snapFlingBehavior(listState, coroutineScope),
        modifier = Modifier.fillMaxSize()
    ) {
        items(trickRecords) { trickRecord ->
            Box(
                modifier = Modifier
                    .fillParentMaxSize()
            ) {
                VideoPlayer(videoUrl = trickRecord.videoUrl)
                Column(Modifier.align(Alignment.BottomStart)) {
                    TrickRecordsFooter(trickRecord)
                    Divider()
                }
            }
        }
    }
}

fun snapFlingBehavior(listState: LazyListState, coroutineScope: CoroutineScope) = object : FlingBehavior {
    override suspend fun ScrollScope.performFling(initialVelocity: Float): Float {
        val layoutInfo = listState.layoutInfo
        val visibleItemsInfo = layoutInfo.visibleItemsInfo

        val visibleItemClosestToCenter = visibleItemsInfo.minByOrNull {
            abs(it.offset + it.size / 2 - layoutInfo.viewportEndOffset / 2)
        } ?: return initialVelocity

        val targetIndex = visibleItemClosestToCenter.index
        coroutineScope.launch {
            listState.animateScrollToItem(index = targetIndex)
        }

        return initialVelocity
    }
}
