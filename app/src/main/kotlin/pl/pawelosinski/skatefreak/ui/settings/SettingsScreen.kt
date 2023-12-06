package pl.pawelosinski.skatefreak.ui.settings

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
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import pl.pawelosinski.skatefreak.ui.auth.SignOutButton
import pl.pawelosinski.skatefreak.local.ThemePreferences
import pl.pawelosinski.skatefreak.local.isDarkMode
import pl.pawelosinski.skatefreak.ui.theme.SkateFreakTheme


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController) {

    // Notifications
    var notificationsEnabled by remember { mutableStateOf(true) }

    // Theme
    val themePreferences = ThemePreferences(LocalContext.current)
    val currentTheme = if (isDarkMode) "Dark" else "Light"
    var selectedTheme by remember { mutableStateOf(currentTheme) }
    val themes = listOf("Light", "Dark")

    // Budowa ekranu ustawień
    SkateFreakTheme (darkTheme = selectedTheme == "Dark") {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("Settings") }
                )
            }
        ) { innerPadding ->
            Column(modifier = Modifier.padding(innerPadding)) {
                // Przełącznik
                RowSettingsItem(
                    title = "Enable Notifications",
                    description = "Turn on or off notifications"
                ) {
                    Switch(checked = notificationsEnabled, onCheckedChange = { notificationsEnabled = it })
                }

                // Przyciski radio
                Text("Select Theme", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(16.dp))
                themes.forEach { theme ->
                    RowSettingsItem(
                        title = theme,
                        description = "Set application theme to $theme"
                    ) {
                        RadioButton(
                            selected = theme == selectedTheme,
                            onClick = {
                                selectedTheme = theme
                                isDarkMode = theme == "Dark"
                                themePreferences.saveThemeSelection(theme)
                            }
                        )
                    }
                }
                SignOutButton(signOut = {
                    Firebase.auth.signOut()
                }) // TODO repair sign out
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

