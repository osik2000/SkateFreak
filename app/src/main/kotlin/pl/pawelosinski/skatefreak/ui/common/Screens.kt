package pl.pawelosinski.skatefreak.ui.common

sealed class Screens(val route : String) {
    data object Home : Screens("home")
    data object Tricks : Screens("tricks")
    data object AddRecord : Screens("addRecord")
    data object Profile : Screens("profile")
    data object Settings : Screens("settings")
    data object Login : Screens("login")
    data object TrickInfo : Screens("trickInfo/{trickId}") {
        fun createRoute(trickId: String) = "trickInfo/$trickId"
    }

}