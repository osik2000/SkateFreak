package pl.pawelosinski.skatefreak.ui.tricks.record

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.ThumbDownAlt
import androidx.compose.material.icons.filled.ThumbDownOffAlt
import androidx.compose.material.icons.filled.ThumbUpAlt
import androidx.compose.material.icons.filled.ThumbUpOffAlt
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
    var isLiked by remember {
        mutableStateOf(loggedUser.value.likedTrickRecords.contains(trickRecord.id))
    }
    var isDisliked by remember {
        mutableStateOf(loggedUser.value.dislikedTrickRecords.contains(trickRecord.id))
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
                    trickRecord = trickRecord.toDTO(),
                    onSuccess = {
                        myToast(context = context, message = it)
                        isFavorite = !isFavorite
                        trickRecord.favoriteCounter.intValue += if (isFavorite) 1 else -1
                        Log.d("FooterUserAction", "usernames who liked ${trickRecord.usersWhoSetAsFavorite}")
                    }

                )
                Log.d("FooterUserAction", "trickid: ${trickRecord.id} isFavorite: $isFavorite")
                Log.d("FooterUserAction", "TrickRecord.usernamesWhoSetAsFavorite (${trickRecord.usersWhoSetAsFavorite.size}): ${trickRecord.usersWhoSetAsFavorite}")
            }
        )

        Spacer(modifier = Modifier.height(10.dp))

        UserAction(
            name = "LikeTrickRecord",
            icon = if (isLiked) Icons.Default.ThumbUpAlt else Icons.Default.ThumbUpOffAlt,
            colored = isLiked,
            color = MaterialTheme.colorScheme.primary,
            onClick = {
                databaseService.setLikeStatusOnTrickRecord(
                    trickRecord = trickRecord.toDTO(),
                    onSuccess = {
                        myToast(context = context, message = it)
                        isLiked = !isLiked
                        trickRecord.likeCounter.intValue += if (isLiked) 1 else -1
                        Log.d("FooterUserAction", "usernames who liked ${trickRecord.usersWhoLiked}")
                        if(isDisliked) {
                            databaseService.setDislikeStatusOnTrickRecord(
                                trickRecord = trickRecord.toDTO(),
                                onSuccess = {
                                    myToast(context = context, message = it)
                                    isDisliked = !isDisliked
                                    trickRecord.dislikeCounter.intValue -= 1
                                    Log.d("FooterUserAction", "usernames who disliked ${trickRecord.usersWhoDisliked}")
                                })
                        }
                    }
                )
                Log.d("FooterUserAction", "trickid: ${trickRecord.id} isLiked: $isLiked")
                Log.d("FooterUserAction", "TrickRecord.usernamesWhoLiked (${trickRecord.usersWhoLiked.size}): ${trickRecord.usersWhoLiked}")
            }
        )

        Spacer(modifier = Modifier.height(10.dp))

        UserAction(
            name = "DislikeTrickRecord",
            icon = if (isDisliked) Icons.Default.ThumbDownAlt else Icons.Default.ThumbDownOffAlt,
            colored = isDisliked,
            color = MaterialTheme.colorScheme.primary,
            onClick = {
                databaseService.setDislikeStatusOnTrickRecord(
                    trickRecord = trickRecord.toDTO(),
                    onSuccess = {
                        myToast(context = context, message = it)
                        isDisliked = !isDisliked
                        trickRecord.dislikeCounter.intValue += if (isDisliked) 1 else -1
                        Log.d("FooterUserAction", "usernames who disliked ${trickRecord.usersWhoDisliked}")
                        if(isLiked) {
                            databaseService.setLikeStatusOnTrickRecord(
                                trickRecord = trickRecord.toDTO(),
                                onSuccess = {
                                    myToast(context = context, message = it)
                                    isLiked = !isLiked
                                    trickRecord.likeCounter.intValue -= 1
                                    Log.d("FooterUserAction", "usernames who liked ${trickRecord.usersWhoLiked}")
                                })
                        }
                    }
                )
                Log.d("FooterUserAction", "trickid: ${trickRecord.id} isDisliked: $isDisliked")
                Log.d("FooterUserAction", "TrickRecord.usernamesWhoLiked (${trickRecord.usersWhoDisliked.size}): ${trickRecord.usersWhoDisliked}")
            }
        )
    }
}