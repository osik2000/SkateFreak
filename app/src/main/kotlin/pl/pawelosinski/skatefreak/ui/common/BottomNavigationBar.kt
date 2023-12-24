package pl.pawelosinski.skatefreak.ui.common

import android.util.Log
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import pl.pawelosinski.skatefreak.local.firebaseAuthService
import pl.pawelosinski.skatefreak.local.loggedUser
import pl.pawelosinski.skatefreak.model.TrickInfo
import pl.pawelosinski.skatefreak.model.TrickRecord
import pl.pawelosinski.skatefreak.model.User
import pl.pawelosinski.skatefreak.repository.UserRepository
import pl.pawelosinski.skatefreak.service.LoginScreen
import pl.pawelosinski.skatefreak.ui.home.HomeScreen
import pl.pawelosinski.skatefreak.ui.profile.ProfileScreen
import pl.pawelosinski.skatefreak.ui.profile.edit.EditProfile
import pl.pawelosinski.skatefreak.ui.profile.edit.PhoneChangeScreen
import pl.pawelosinski.skatefreak.ui.settings.SettingsScreen
import pl.pawelosinski.skatefreak.ui.tricks.info.TrickInfoComposable
import pl.pawelosinski.skatefreak.ui.tricks.info.TricksScreen
import pl.pawelosinski.skatefreak.ui.tricks.record.add.AddRecordScreen
import pl.pawelosinski.skatefreak.ui.tricks.record.add.ChooseTrickInfoScreen

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
                Screens.AddRecord.route -> 2
                Screens.Profile.route -> 3
                Screens.Settings.route -> 4
                else -> navigationSelectedItem
            }
        }
    }

//scaffold to hold our bottom navigation Bar
    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        bottomBar = {
            NavigationBar {
                //getting the list of bottom navigation items for our data class
                BottomNavigationItem().bottomNavigationItems().forEachIndexed {index,navigationItem ->

                    if(navigationItem.route == Screens.AddRecord.route){
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .wrapContentSize(),
                            color = MaterialTheme.colorScheme.background
                        ) {
                            NavigationBarItem(
                                selected = false,
                                label = {
                                    Text(navigationItem.title)
                                },
                                icon = {
                                        Icon(
                                            imageVector = navigationItem.icon,
                                            tint = MaterialTheme.colorScheme.primary,
                                            contentDescription = navigationItem.title,
                                            modifier = Modifier
                                                .size(36.dp)
                                                .align(alignment = androidx.compose.ui.Alignment.CenterVertically)
                                                // thickness of the icon
                                                .border(1.dp, MaterialTheme.colorScheme.primary)
                                        )
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
                                },
                                modifier = Modifier
                                    .wrapContentSize()
                            )
                        }
                    }
                    else {
                        //iterating all items with their respective indexes
                        NavigationBarItem(
                            selected = index == navigationSelectedItem,
                            label = {
                                Text(navigationItem.title)
                            },
                            icon = {
                                    Icon(
                                        imageVector = navigationItem.icon,
                                        contentDescription = navigationItem.title
                                    )
                            },
                            // used to handle click events of navigation items
                            onClick = {
                                val tricksFix = (
                                    navigationItem.route == Screens.Tricks.route &&
                                    navController.currentDestination?.route?.contains(Screens.Tricks.route) == true &&
                                    navController.currentDestination?.route?.equals(Screens.Tricks.route) == false)
                                val profileEditFix = (
                                    navigationItem.route.contains("profile/") &&
                                    navController.currentDestination?.route?.equals(Screens.EditProfile.route) == true)
                                val clipFix = (
                                    navigationItem.route == Screens.Home.route &&
                                    navController.currentDestination?.route?.equals(Screens.Profile.route) == true)

                                Log.d("BottomNavigationBar", "currentDestination: ${navController.currentDestination?.route}")
                                Log.d("BottomNavigationBar", "navigationItem.route = ${navigationItem.route}")
                                Log.d("BottomNavigationBar", "tricksFix = $tricksFix  clipFix = $clipFix")
                                if(tricksFix || clipFix || profileEditFix) {
                                        navController.navigateUp()
                                }
                                else {
                                    navigationSelectedItem = index
                                    navController.navigate(navigationItem.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
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
            composable(
                route = Screens.TrickInfo.route,
                arguments = listOf(navArgument("trickId") { type = NavType.StringType })
            ) { backStackEntry ->
                val trickId = backStackEntry.arguments?.getString("trickId")
                TrickInfoComposable(trickId = trickId ?: "", navController = navController)
            }
            composable(Screens.ChooseTrickInfo.route) {
                ChooseTrickInfoScreen(navController = navController)
            }
            composable(Screens.AddRecord.route) {
                if(!TrickRecord.whileAdding.value){
                    TrickRecord.whileAdding.value = false
                    TrickRecord.chosenTitle.value = ""
                    TrickRecord.chosenDescription.value = ""
                    TrickRecord.localFileUri.value = ""
                    TrickRecord.trimmedVideoPath.value = ""
                    TrickRecord.chosenTrickInfo.value = TrickInfo()
                }
                AddRecordScreen(navController = navController)
            }
            composable(
                route = Screens.Profile.route,
                arguments = listOf(navArgument("userId") { type = NavType.StringType })
            ) { backStackEntry ->
                val userId = backStackEntry.arguments?.getString("userId")
                val isMyProfile = userId == loggedUser.value.firebaseId
                var user = User()
                if (userId != null) {
                    user = if(isMyProfile) loggedUser.value else UserRepository.getUserById(userId)
                }
                ProfileScreen(navController = navController, user = user)
            }
            composable(Screens.MyProfile.route) {
                ProfileScreen(navController = navController, user = loggedUser.value)
            }
            composable(Screens.EditProfile.route) {
                EditProfile(navController = navController)
            }
            composable(Screens.EditPhone.route) {
                PhoneChangeScreen(navController = navController)
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
