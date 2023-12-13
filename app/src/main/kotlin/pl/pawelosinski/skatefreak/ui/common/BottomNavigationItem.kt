package pl.pawelosinski.skatefreak.ui.common

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.ui.graphics.vector.ImageVector
import pl.pawelosinski.skatefreak.R


data class BottomNavigationItem(
    val route: String = "",
    val title: String = "",
    val icon: ImageVector = Icons.Default.Warning,
    val resource: Int = R.drawable.baseline_skateboarding_24
) {

    fun bottomNavigationItems() : List<BottomNavigationItem> {
        return listOf(
            BottomNavigationItem(
                route = "home",
                title = "Klipy",
                icon = Icons.Default.PlayArrow
            ),
            BottomNavigationItem(
                route = "tricks",
                title = "Triki",
                icon = Icons.Default.Info
            ),
            BottomNavigationItem(
                route = "addRecord",
                icon = Icons.Default.Add
            ),
            BottomNavigationItem(
                route = "profile",
                title = "Profil",
                icon = Icons.Default.Person
            ),
            BottomNavigationItem(
                route = "settings",
                title = "Opcje",
                icon = Icons.Default.Settings
            ),
        )

    }
}

