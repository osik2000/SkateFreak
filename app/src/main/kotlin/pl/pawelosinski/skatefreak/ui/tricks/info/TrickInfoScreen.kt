package pl.pawelosinski.skatefreak.ui.tricks.info

import androidx.annotation.OptIn
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.media3.common.util.UnstableApi
import pl.pawelosinski.skatefreak.model.TrickInfo
import pl.pawelosinski.skatefreak.ui.tricks.info.TrickGif

@OptIn(UnstableApi::class) @Composable
fun TrickInfoComposable(trickInfo: TrickInfo) {
Column {
        Text(text = "Name: ${trickInfo.name}")
        Text(text = "Description: ${trickInfo.description}")
        Text(text = "Difficulty: ${trickInfo.difficulty}")
        Text(text = "Category: ${trickInfo.category}")
        TrickGif(url = trickInfo.photoUrl)
    }
}


