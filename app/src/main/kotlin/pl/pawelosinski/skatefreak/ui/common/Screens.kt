package pl.pawelosinski.skatefreak.ui.common

sealed class Screens(val route : String) {
    data object Home : Screens("home")
    data object Tricks : Screens("tricks")
    data object TrickInfo : Screens("tricks/{trickId}") {
        fun createRoute(trickId: String) = "tricks/$trickId"
    }
    data object AddRecord : Screens("addRecord")
    data object Profile : Screens("profile")
    data object EditProfile : Screens("profile/edit")
    data object Settings : Screens("settings")
    data object Login : Screens("login")

}