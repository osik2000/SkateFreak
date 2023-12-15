package pl.pawelosinski.skatefreak.ui.common

sealed class Screens(val route : String) {
    object Home : Screens("home")
    object Tricks : Screens("tricks")
    object AddRecord : Screens("addRecord")
    object Profile : Screens("profile")
    object Settings : Screens("settings")
    object Login : Screens("login")
    object TrickInfo : Screens("trickInfo/{trickId}") {
        fun createRoute(trickId: String) = "trickInfo/$trickId"
    }

}