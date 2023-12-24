package pl.pawelosinski.skatefreak.ui.common

@Suppress("unused")
sealed class Screens(val route : String) {
    data object Home : Screens("home")
    data object Tricks : Screens("tricks")
    data object TrickInfo : Screens("tricks/{trickId}") {
        fun createRoute(trickId: String) = "tricks/$trickId"
    }
    data object ChooseTrickInfo : Screens("chooseTrickInfo")
    data object AddRecord : Screens("addRecord")
    data object Profile : Screens("profile/{userId}") {
        fun createRoute(userId: String) = "profile/$userId"
    }
    data object MyProfile : Screens("profile/my")
    data object EditProfile : Screens("profile/edit")
    data object EditPhone : Screens("profile/edit/phone")
    data object Settings : Screens("settings")
    data object Login : Screens("login")

}