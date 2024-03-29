package pl.pawelosinski.skatefreak.ui.settings

import android.app.Activity
import android.content.Intent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import pl.pawelosinski.skatefreak.local.ThemePreferences
import pl.pawelosinski.skatefreak.local.firebaseAuthService
import pl.pawelosinski.skatefreak.local.isDarkMode
import pl.pawelosinski.skatefreak.local.loggedUser
import pl.pawelosinski.skatefreak.service.UserService
import pl.pawelosinski.skatefreak.ui.auth.LoginActivity
import pl.pawelosinski.skatefreak.ui.auth.SignOutButton
import pl.pawelosinski.skatefreak.ui.profile.EditProfileButton
import pl.pawelosinski.skatefreak.ui.theme.SkateFreakTheme


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController) {
    val context = LocalContext.current

    // Public Profile state
    var profilePublic by remember { mutableStateOf(loggedUser.value.isPublic) }

    // Theme
    val themePreferences = ThemePreferences(context)
    val currentTheme = if (isDarkMode) "Dark" else "Light"
    var selectedTheme by remember { mutableStateOf(currentTheme) }
    val themes = listOf("Light", "Dark")

    // Budowa ekranu ustawień
    SkateFreakTheme (darkTheme = selectedTheme == "Dark") {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("Opcje") }
                )
            }
        ) { innerPadding ->
            Column(modifier = Modifier.padding(innerPadding)) {
                Text("Profil", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(16.dp))
                // switch
                RowSettingsItem(
                    title = "Widoczność profilu",
                    description = "Pozwól innym użytkownikom na\nprzeglądanie Twojego profilu"
                ) {
                    Switch(checked = profilePublic, onCheckedChange = {
                        profilePublic = it
                        UserService.setUserPublicProfile(it)
                    })
                }
                // profile edit button
                EditProfileButton(navController = navController)

                // radio buttons
                Text("Wybierz motyw", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(16.dp))
                themes.forEach { theme ->
                    RowSettingsItem(
                        title = theme,
                        description = "Ustaw motyw aplikacji na  ${if(theme == "Dark") "ciemny" else "jasny"}"
                    ) {
                        RadioButton(
                            selected = theme == selectedTheme,
                            onClick = {
                                selectedTheme = theme
                                isDarkMode = theme == "Dark"
                                themePreferences.saveThemeSelection(theme)
                                val activity = context as Activity
                                activity.recreate()
                            }
                        )
                    }
                }
                SignOutButton(signOut = {
                    firebaseAuthService.signOut(onComplete = {
                        val intent = Intent(context, LoginActivity::class.java)
                        val activity = context as Activity
                        context.startActivity(intent)
                        activity.finish()
                    })
                })
            }
        }
    }

}

@Composable
fun RowSettingsItem(title: String, description: String, content: @Composable () -> Unit) {
    Row(
        modifier = Modifier.padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Text(description, style = MaterialTheme.typography.bodyMedium)
        }
        content()
    }
}


