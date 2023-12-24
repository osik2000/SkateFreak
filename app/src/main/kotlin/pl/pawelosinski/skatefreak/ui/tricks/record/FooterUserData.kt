package pl.pawelosinski.skatefreak.ui.tricks.record

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ThumbsUpDown
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation
import pl.pawelosinski.skatefreak.R
import pl.pawelosinski.skatefreak.local.allTrickRecordsCreators
import pl.pawelosinski.skatefreak.model.TrickRecord
import pl.pawelosinski.skatefreak.model.User
import pl.pawelosinski.skatefreak.repository.UserRepository
import pl.pawelosinski.skatefreak.service.databaseService

/**
 * Footer user data
 *
 * @param trickRecord
 * @param modifier
 */
@Composable
fun FooterUserData(trickRecord: TrickRecord, modifier: Modifier, navController: NavController) {
    val padding = 10.dp
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center
    ) {
        var currentCreator by remember { mutableStateOf(User()) }
        UserRepository.getCreatorById(
            id = trickRecord.userID,
            creatorsList = allTrickRecordsCreators,
            databaseService = databaseService,
            onSuccess = {
                currentCreator = it
            },
            onFail = {
                Log.d("TrickRecordsScreen", "getCreatorById: onFail")
            }
        )

        Row ( // Title
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ){
            Text(
                text = trickRecord.title,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.labelLarge
            )
        }
        Row ( // Description
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ){
            Text(
                text = trickRecord.userDescription.ifEmpty { "Brak opisu" },
                color = Color.White,
                style = MaterialTheme.typography.labelMedium
            )
        }
        Spacer(modifier = Modifier.height(padding))
        Row( // creator and counters
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            Row (
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .clickable {
                        navController.navigate("profile/${currentCreator.firebaseId}")
                    }
            ){
                Box(
                    modifier = Modifier
                        .height(28.dp)
                        .width(28.dp),
                    contentAlignment = Alignment.Center
                ) {
                    val painter = rememberAsyncImagePainter(
                        ImageRequest.Builder(LocalContext.current)
                            .data(data = currentCreator.photoUrl.let { it.ifEmpty { R.drawable.rounded_person_24 } })
                            .apply(block = fun ImageRequest.Builder.() {
                                transformations(
                                    CircleCropTransformation()
                                )
                            }).build()
                    )
                    Image(
                        painter = painter,
                        contentDescription = null,
                    )
                }
                Spacer(modifier = Modifier.width(padding))

                Text(
                    text = "@${currentCreator.nickname}",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.labelMedium
                )
            }

            Spacer(modifier = Modifier.width(padding))

            Row (
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                Icon(
                    modifier = Modifier.size(20.dp),
                    imageVector = Icons.Outlined.Favorite,
                    tint = if (trickRecord.favoriteCounter.intValue > 0) Color.Red else Color.White,
                    contentDescription = "Favorites counter"
                )
                Text(
                    text = " ${trickRecord.favoriteCounter.intValue}",
                    color = Color.White,
                    style = MaterialTheme.typography.labelMedium
                )
            }


            Spacer(modifier = Modifier.width(padding))

            Row (
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                Icon(
                    modifier = Modifier.size(20.dp),
                    imageVector = Icons.Default.ThumbsUpDown,
                    tint = if (trickRecord.likeCounter.intValue - trickRecord.dislikeCounter.intValue > 0)
                        Color.Green else
                            if (trickRecord.likeCounter.intValue - trickRecord.dislikeCounter.intValue == 0)
                                Color.White
                            else
                                Color.Red,
                    contentDescription = "Likes and DislikesCounter"
                )
                Text(
                    text = " ${trickRecord.likeCounter.intValue - trickRecord.dislikeCounter.intValue}",
                    color = if (trickRecord.likeCounter.intValue - trickRecord.dislikeCounter.intValue > 0)
                        Color.Green else
                        if (trickRecord.likeCounter.intValue - trickRecord.dislikeCounter.intValue == 0)
                            Color.White
                        else
                            Color.Red,
                    style = MaterialTheme.typography.labelMedium
                )
            }

//            Icon(
//                modifier = Modifier.size(15.dp),
//                imageVector = Icons.Outlined.Add,
//                contentDescription = ""
//            )
        }
        Spacer(modifier = Modifier.height(padding))

        // autor
//        Row(
//            modifier = Modifier
//                .clip(RoundedCornerShape(10.dp))
//                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
//                .padding(10.dp),
//            verticalAlignment = Alignment.CenterVertically,
//            horizontalArrangement = Arrangement.Center,
//        ) {
//            Icon(
//                modifier = Modifier.size(15.dp),
//                imageVector = Icons.Outlined.Star,
//                contentDescription = "Bookmark"
//            )
//            Spacer(modifier = Modifier.width(10.dp))
//            Text(
//                text = "@${trickRecord.userNickname}'s trick record",
//                color = Color.White,
//                style = MaterialTheme.typography.bodySmall
//            )
//        }
    }
}