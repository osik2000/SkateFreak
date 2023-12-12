package pl.pawelosinski.skatefreak.ui.common

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import pl.pawelosinski.skatefreak.local.firebaseAuthService
import pl.pawelosinski.skatefreak.local.isDarkMode
import pl.pawelosinski.skatefreak.service.LoginScreen
import pl.pawelosinski.skatefreak.ui.home.HomeScreen
import pl.pawelosinski.skatefreak.ui.profile.ProfileScreen
import pl.pawelosinski.skatefreak.ui.settings.SettingsScreen
import pl.pawelosinski.skatefreak.ui.theme.SkateFreakTheme
import pl.pawelosinski.skatefreak.ui.tricks.info.TricksScreen

@Composable
fun BottomNavigationBar() {
//initializing the default selected item
    var navigationSelectedItem by remember {
        mutableIntStateOf(0)
    }

    val navController = rememberNavController()

    // Observe the back stack to update the selected item when the top screen changes
    LaunchedEffect(navController) {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            navigationSelectedItem = when (destination.route) {
                Screens.Home.route -> 0
                Screens.Tricks.route -> 1
                Screens.Profile.route -> 2
                Screens.Settings.route -> 3
                else -> navigationSelectedItem
            }
        }
    }

//scaffold to hold our bottom navigation Bar
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar {
                //getting the list of bottom navigation items for our data class
                BottomNavigationItem().bottomNavigationItems().forEachIndexed {index,navigationItem ->

                    //iterating all items with their respective indexes
                    NavigationBarItem(
                        selected = index == navigationSelectedItem,
                        label = {
                            Text(navigationItem.title)
                        },
                        icon = {
                            if (navigationItem.icon != Icons.Default.Warning)
                                Icon(
                                    imageVector = navigationItem.icon,
                                    contentDescription = navigationItem.title)
                            else
                                Icon(
                                    imageVector = ImageVector.vectorResource(navigationItem.resource),
                                    contentDescription = navigationItem.title)
                        },
                        // used to handle click events of navigation items
                        onClick = {
                            navigationSelectedItem = index
                            navController.navigate(navigationItem.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { paddingValues ->
        //We need to setup our NavHost in here
        NavHost(
            navController = navController,
            startDestination = Screens.Home.route,
            modifier = Modifier.padding(paddingValues = paddingValues)) {
            composable(Screens.Home.route) {
                HomeScreen(navController = navController)
            }
            composable(Screens.Tricks.route) {
                TricksScreen(navController = navController)
            }
            composable(Screens.Profile.route) {
                ProfileScreen(navController = navController)
            }
            composable(Screens.Settings.route) {
                SettingsScreen(navController = navController)
            }
            composable(Screens.Login.route) {
                LoginScreen(firebaseAuthService = firebaseAuthService)
            }
        }
    }
}