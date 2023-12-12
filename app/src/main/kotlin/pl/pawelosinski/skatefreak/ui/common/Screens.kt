package pl.pawelosinski.skatefreak.ui.common

sealed class Screens(val route : String) {
    object Home : Screens("home")
    object Tricks : Screens("tricks")
    object Profile : Screens("profile")
    object Settings : Screens("settings")
    object Login : Screens("login")

}