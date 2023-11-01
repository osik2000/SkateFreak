package pl.pawelosinski.skatefreak.ui.view

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import pl.pawelosinski.skatefreak.ui.theme.SkateFreakTheme


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainMenu() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "friendslist") {
        composable("login") {
            SkateFreakTheme {
                LoginForm()
            }
        }
        composable("friendslist") { Butt(navController, "xd", "xdd") }
        /*...*/
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Butt(navController: NavController, email: String, password: String) {
    val context = LocalContext.current
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
    ) {
        Button(
            onClick = {
                navController.navigate("login")
                Toast.makeText(
                    context,
                    "Email: $email, Password: $password",
                    Toast.LENGTH_SHORT
                ).show()
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            Text("Logowanie")
        }
        Spacer(modifier = Modifier.padding(8.dp))
    }

}
