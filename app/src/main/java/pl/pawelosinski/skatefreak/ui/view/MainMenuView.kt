package pl.pawelosinski.skatefreak.ui.view

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import pl.pawelosinski.skatefreak.ui.theme.SkateFreakTheme


@Composable
fun MainMenu() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "friendslist") {
        composable("login") {
            SkateFreakTheme {
                LoginForm()
            }
        }
        composable("friendslist") { ExampleButton(navController) }
    }
}

@Composable
fun ExampleButton(navController: NavController) {
    val context = LocalContext.current
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "Menu Główne",
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )
        Button(
            onClick = {
                navController.navigate("login")
                Toast.makeText(
                    context,
                    "Otwieranie Ekranu Logowania...",
                    Toast.LENGTH_SHORT
                ).show()
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            Text("Logowanie")
        }
    }
}
