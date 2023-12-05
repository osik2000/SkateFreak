package pl.pawelosinski.skatefreak.ui.common

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector


data class BottomNavigationItem(
    val route: String = "",
    val title: String = "",
    val icon: ImageVector = Icons.Default.Home,
) {

    fun bottomNavigationItems() : List<BottomNavigationItem> {
        return listOf(
            BottomNavigationItem(
                route = "home",
                title = "Menu",
                icon = Icons.Default.Home
            ),
            BottomNavigationItem(
                route = "tricks",
                title = "Tricki",
                icon = Icons.Default.Info
            ),
            BottomNavigationItem(
                route = "profile",
                title = "Profil",
                icon = Icons.Default.Person
            ),
            BottomNavigationItem(
                route = "settings",
                title = "Ustawienia",
                icon = Icons.Default.Settings
            ),
        )

    }
}

