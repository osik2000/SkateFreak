package pl.pawelosinski.skatefreak.local

import pl.pawelosinski.skatefreak.model.TrickInfo
import pl.pawelosinski.skatefreak.model.User

var loggedUser: User = User()
var isDarkMode = true
val tricksCategories = listOf("Zwykłe", "Flip", "Grind", "Grab", "Slide", "Old School", "Other")
val tricksDifficulties = listOf("Easy", "Medium", "Hard", "Pro")
val allTricks: List<TrickInfo> = listOf(
    TrickInfo(
        "1",
        "Ollie",
        "Ollie is a basic trick in skateboarding. It is a combination of movements performed in the air in order to jump over an obstacle.",
        "Easy",
        "Zwykłe",
        "https://www.surfertoday.com/images/stories/ollie-higher.jpg"
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
    )
)