package pl.pawelosinski.skatefreak.ui.tricks.record

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import pl.pawelosinski.skatefreak.local.currentRecordLikes
import pl.pawelosinski.skatefreak.local.loggedUser
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

    var isFavorite by remember {
        mutableStateOf(loggedUser.value.favoriteTrickRecords.contains(trickRecord.id))
    }
    Log.d("FooterUserAction", "trickid: ${trickRecord.id} isFavorite: $isFavorite")

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        UserAction(
            name = "FavoriteTrickRecord",
            icon = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
            colored = isFavorite,
            color = MaterialTheme.colorScheme.primary,
            onClick = {
                databaseService.addTrickRecordToFavorites( // TODO zrobić po stronie apki wysylanie zaktualizowanego usera do bazy danych
                    trickRecord = trickRecord,
                    onSuccess = {
                        myToast(context = context, message = it)
                        isFavorite = !isFavorite
                        Log.d("FooterUserAction", "usernames who liked ${trickRecord.usersWhoSetAsFavorite}")
                        currentRecordLikes.value = trickRecord.usersWhoSetAsFavorite.size.toString()
                    }

                )

                Log.d("FooterUserAction", "trickid: ${trickRecord.id} isFavorite: $isFavorite")
                Log.d("FooterUserAction", "TrickRecord.usernamesWhoLiked (${trickRecord.usersWhoSetAsFavorite.size}): ${trickRecord.usersWhoSetAsFavorite}")
            }
        )
        Spacer(modifier = Modifier.height(10.dp))
        UserAction(
            icon = Icons.Default.KeyboardArrowUp,
        )
        Spacer(modifier = Modifier.height(10.dp))
        UserAction(
            icon = Icons.Default.KeyboardArrowDown,
        )
    }
}