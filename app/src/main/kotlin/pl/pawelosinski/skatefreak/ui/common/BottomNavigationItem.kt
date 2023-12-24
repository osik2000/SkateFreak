package pl.pawelosinski.skatefreak.ui.common

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.ui.graphics.vector.ImageVector


data class BottomNavigationItem(
    val route: String = "",
    val title: String = "",
    val icon: ImageVector = Icons.Default.Warning
) {

    fun bottomNavigationItems() : List<BottomNavigationItem> {
        return listOf(
            BottomNavigationItem(
                route = Screens.Home.route,
                title = "Klipy",
                icon = Icons.Default.PlayArrow
            ),
            BottomNavigationItem(
                route = Screens.Tricks.route,
                title = "Triki",
                icon = Icons.Default.Info
            ),
            BottomNavigationItem(
                route = Screens.AddRecord.route,
                icon = Icons.Default.Add
            ),
            BottomNavigationItem(
                route = Screens.MyProfile.route,
                title = "Profil",
                icon = Icons.Default.Person
            ),
            BottomNavigationItem(
                route = Screens.Settings.route,
                title = "Opcje",
                icon = Icons.Default.Settings
            ),
        )

    }
}

