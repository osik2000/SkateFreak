package pl.pawelosinski.skatefreak

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import pl.pawelosinski.skatefreak.auth.loggedUser
import pl.pawelosinski.skatefreak.service.DataService
import pl.pawelosinski.skatefreak.ui.common.BottomNavigationBar
import pl.pawelosinski.skatefreak.ui.common.myCommonModifier
import pl.pawelosinski.skatefreak.ui.mainScreen.MainScreen
import pl.pawelosinski.skatefreak.ui.theme.SkateFreakTheme


class MainActivity : ComponentActivity() {

    private var isUserLoggedIn = checkIfUserIsLoggedIn()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            SkateFreakTheme (darkTheme = true) {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    //BeforeLoginScreen()
                    LoadingScreen()
//                    BottomNavigationBar()
                }
            }
        }
    }

    private fun checkIfUserIsLoggedIn() : Boolean {
        var isUserLoggedIn = true
        val currentUser = Firebase.auth.currentUser ?: return false
        DataService().getUserById(currentUser.uid, onSuccess = {
            isUserLoggedIn = true
            if (loggedUser.checkRequiredData()) {
                val intent = Intent(this, MainMenuActivity::class.java)
                startActivity(intent)
                finish()
            }
            else {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        }, onFail = {
            isUserLoggedIn = false
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        })
        return isUserLoggedIn
    }

    @Composable
    fun LoadingScreen() {
        val loadingMessage by remember {
            mutableStateOf("Ładowanie...\n" +
                    "(Pamiętaj że aplikacja wymaga połączenia z internetem)")
        }

        if(!isUserLoggedIn) {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = "Skatefreak",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.headlineLarge,
                modifier = myCommonModifier
            )
            Text(
                text = loadingMessage,
                textAlign = TextAlign.Center,
                modifier = myCommonModifier
            )
        }
    }


}

@Composable
fun BeforeLoginScreen() {
    val context = LocalContext.current
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "Menu Główne",
            textAlign = TextAlign.Center,
            modifier = myCommonModifier
        )
        Button(
            onClick = {
                Toast.makeText(
                    context,
                    "Otwieranie Ekranu Logowania...",
                    Toast.LENGTH_SHORT
                ).show()
                val intent = Intent(context, LoginActivity::class.java)
//                startActivity(intent)
//                finish()
            },
            modifier = myCommonModifier
        ) {
            Text("Zaloguj się aby kontynuować")
        }
    }
}



@Preview(
    showBackground = true,
    showSystemUi = true)
@Composable
fun BeforeLoginScreenPreview() {
    SkateFreakTheme {
        BeforeLoginScreen()
    }
}