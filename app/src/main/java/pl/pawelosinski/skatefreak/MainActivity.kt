package pl.pawelosinski.skatefreak

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import pl.pawelosinski.skatefreak.ui.theme.SkateFreakTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            SkateFreakTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainMenu()
                }
            }
        }
    }
    @Preview(
        showBackground = true,
        showSystemUi = true)
    @Composable
    fun MainActivityPreview() {
        SkateFreakTheme {
            MainMenu()
        }
    }

    @Composable
    fun MainMenu() {
        val current = LocalContext.current
        var selectedItem by remember { mutableStateOf(0) }
        val items = listOf("login", "friendslist", "Playlists")
        val navController = rememberNavController()
        NavHost(navController = navController, startDestination = "friendslist") {
            composable("login") {
                Button(onClick = {
                    val navigate = Intent(current, LoginActivity::class.java)
                    current.startActivity(navigate)
                }) {
                    Text(text = "Logowanie")
                }
            }
            composable("friendslist") { ExampleButton(navController) }


        }

//    BottomNavigation() {
//        items.forEachIndexed { index, item ->
//            BottomNavigationItem(
//                icon = { Icon(Icons.Filled.Favorite, contentDescription = null) },
//                label = { Text(item) },
//                selected = selectedItem == index,
//                onClick = {
//                    selectedItem = index
//                    Toast.makeText(
//                        current,
//                        "Otwieranie Ekranu Logowania...",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }
//            )
//        }
//    }

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

}
