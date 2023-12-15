package pl.pawelosinski.skatefreak.local

import androidx.compose.runtime.mutableStateOf
import pl.pawelosinski.skatefreak.model.TrickInfo
import pl.pawelosinski.skatefreak.model.TrickRecord
import pl.pawelosinski.skatefreak.model.User
import pl.pawelosinski.skatefreak.service.FirebaseAuthService

var loggedUser = mutableStateOf(User())
var isDarkMode = true
lateinit var firebaseAuthService: FirebaseAuthService
var currentRecordLikes = mutableStateOf("...")
var currentRecordCreator = mutableStateOf(User())
//val tricksCategories = listOf("Zwykłe", "Flip", "Grind", "Grab", "Slide", "Old School", "Other")
//val tricksDifficulties = listOf("Easy", "Medium", "Hard", "Pro")
val allTrickInfo: MutableList<TrickInfo> = mutableListOf(
    TrickInfo(
        "1",
        "Ollie",
        "Ollie is a basic trick in skateboarding. It is a combination of movements performed in the air in order to jump over an obstacle.",
        "Easy",
        "Zwykłe",
        "https://i.makeagif.com/media/9-12-2016/1TJjLm.gif"
    ),
    TrickInfo(
        "2",
        "Kickflip",
        "Kickflip is a basic trick in skateboarding. It is a combination of movements performed in the air in order to jump over an obstacle.",
        "Medium",
        "Flip",
        "https://64.media.tumblr.com/acf215fd73a6b2c7ffc3cb1ec0d2d456/tumblr_n3x0bvU51w1rq31wzo1_400.gif"
    ),
    TrickInfo(
        "3",
        "Heelflip",
        "Heelflip is a basic trick in skateboarding. It is a combination of movements performed in the air in order to jump over an obstacle.",
        "Medium",
        "Flip",
        "https://media.tenor.com/G0VmFADL4H4AAAAd/kickflip-skateboard-flip.gif"
    ),
    TrickInfo(
        "4",
        "Pop Shove-it",
        "Pop Shove-it is a basic trick in skateboarding. It is a combination of movements performed in the air in order to jump over an obstacle.",
        "Easy",
        "Flip",
        "http://freestylekb.com/wiki/images/8/80/Pop_Shove-It.gif"
    ),
    TrickInfo(
        "5",
        "Drop In",
        "Drop In is a basic trick in skateboarding. It is a combination of movements performed in the air in order to jump over an obstacle.",
        "Easy",
        "Other",
        "https://media1.tenor.com/m/bXbQHKg2tQYAAAAd/skatedrop-skate.gif"
    )
)
var allTrickRecords = mutableListOf<TrickRecord>()
//val allTrickRecords = listOf(
//    TrickRecord(
//        id = "1",
//        userID = "KwUsU9tNz8PN6EdPPuthj5ItrJy2",
//        trickID = "5",
//        date = "2021-01-01",
//        userDescription = "My first Drop In!",
//        videoUrl = "https://firebasestorage.googleapis.com/v0/b/skatefreak-3b665.appspot.com/o/trickRecord%2Fvideo%2F3%20git.mov?alt=media&token=0c9c8abc-adcd-45e7-84af-6061375d1a26",
//    ),
//    TrickRecord(
//        id = "2",
//        userID = "KwUsU9tNz8PN6EdPPuthj5ItrJy2",
//        trickID = "1",
//        date = "2021-01-01",
//        userDescription = "My first Ollie!",
//        videoUrl = "https://firebasestorage.googleapis.com/v0/b/skatefreak-3b665.appspot.com/o/trickRecord%2Fvideo%2F3%20git.mov?alt=media&token=0c9c8abc-adcd-45e7-84af-6061375d1a26"
//    ),
//    TrickRecord(
//        id = "3",
//        userID = "KwUsU9tNz8PN6EdPPuthj5ItrJy2",
//        trickID = "2",
//        date = "2021-01-01",
//        userDescription = "My first Kickflip!",
//        videoUrl = "https://firebasestorage.googleapis.com/v0/b/skatefreak-3b665.appspot.com/o/trickRecord%2Fvideo%2F3%20git.mov?alt=media&token=0c9c8abc-adcd-45e7-84af-6061375d1a26"
//    ),
//    TrickRecord(
//        id = "4",
//        userID = "KwUsU9tNz8PN6EdPPuthj5ItrJy2",
//        trickID = "3",
//        date = "2021-01-01",
//        userDescription = "My first Heelflip!",
//        videoUrl = "https://firebasestorage.googleapis.com/v0/b/skatefreak-3b665.appspot.com/o/trickRecord%2Fvideo%2F3%20git.mov?alt=media&token=0c9c8abc-adcd-45e7-84af-6061375d1a26"
//    ),
//    TrickRecord(
//        id = "5",
//        userID = "KwUsU9tNz8PN6EdPPuthj5ItrJy2",
//        trickID = "4",
//        date = "2021-01-01",
//        userDescription = "My first Pop Shove-it!",
//        videoUrl = "https://firebasestorage.googleapis.com/v0/b/skatefreak-3b665.appspot.com/o/trickRecord%2Fvideo%2F3%20git.mov?alt=media&token=0c9c8abc-adcd-45e7-84af-6061375d1a26"
//    )
//)