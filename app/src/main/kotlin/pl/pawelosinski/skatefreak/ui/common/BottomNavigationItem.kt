package pl.pawelosinski.skatefreak.ui.common

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import pl.pawelosinski.skatefreak.R


data class BottomNavigationItem(
    val route: String = "",
    val title: String = "",
    val icon: ImageVector = Icons.Default.Warning,
    val resource: Int = R.drawable.baseline_skateboarding_20
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
                resource = R.drawable.baseline_skateboarding_20
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

