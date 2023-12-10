package pl.pawelosinski.skatefreak.ui.tricks.record

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import pl.pawelosinski.skatefreak.model.TrickRecord
import pl.pawelosinski.skatefreak.service.databaseService
import pl.pawelosinski.skatefreak.ui.common.myToast

/**
 * Footer user action
 *
 * @param modifier
 */
@Composable
fun FooterUserAction(modifier: Modifier, trickRecord: TrickRecord) {
    val context = LocalContext.current

    var isFavorite by remember { mutableStateOf(trickRecord.isFavorite) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        UserAction(
            name = "FavoriteTrickRecord",
            icon = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
            onClick = {
                isFavorite = !isFavorite
                databaseService.addTrickRecordToFavorites( // TODO zrobić po stronie apki wysylanie zaktualizowanego usera do bazy danych
                    userID = trickRecord.userID,
                    trickRecordID = trickRecord.id,
                    onSuccess = { myToast(context = context, message = it) }
                )
            }
        )
        Spacer(modifier = Modifier.height(10.dp))
        UserAction(
            icon = Icons.Default.Send,
        )
        Spacer(modifier = Modifier.height(10.dp))
        UserAction(
            icon = Icons.Default.MoreVert,
        )
    }
}