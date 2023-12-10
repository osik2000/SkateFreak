package pl.pawelosinski.skatefreak.ui.tricks

import android.net.Uri
import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.media3.common.util.UnstableApi
import pl.pawelosinski.skatefreak.local.allTrickRecords
import pl.pawelosinski.skatefreak.model.TrickRecord
import pl.pawelosinski.skatefreak.service.LocalDataService
import pl.pawelosinski.skatefreak.ui.common.VideoPlayer
import pl.pawelosinski.skatefreak.ui.common.VideoPlayer2

@OptIn(UnstableApi::class) @Composable
fun TrickRecordComposable(trickRecord: TrickRecord) {
    val trickInfo = LocalDataService.getTrickInfo(trickRecord.trickID)


    Column {
        Text(text = "ID: ${trickRecord.id}")
        Text(text = "Name: ${trickInfo.name}")
        Text(text = "Description: ${trickRecord.userDescription}")
        Text(text = "Difficulty: ${trickInfo.difficulty}")
        VideoPlayer(videoUrl = trickRecord.videoUrl)
    }
}



val horizontalPadding = 10.dp
@Composable
fun TrickRecordsScreen() {
    val trickRecords = allTrickRecords
    Box(
        Modifier
            .clip(RoundedCornerShape(bottomEnd = 10.dp, bottomStart = 10.dp))
            .background(color = Color.Black)
    ) {
        LazyColumn {
            items(trickRecords.size) { index ->
                Box(
                    modifier = Modifier
                        .fillParentMaxSize()
                ) {
                    VideoPlayer(videoUrl = trickRecords[index].videoUrl)
//                    VideoPlayer2(uri = Uri.parse(trickRecords[index].videoUrl))
                    Column(Modifier.align(Alignment.BottomStart)) {
                        TrickRecordsFooter(trickRecords[index])
                        Divider()
                    }
                }
            }
        }
    }
}


