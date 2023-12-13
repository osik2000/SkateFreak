package pl.pawelosinski.skatefreak

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import pl.pawelosinski.skatefreak.local.LocalDataInit
import pl.pawelosinski.skatefreak.local.isDarkMode
import pl.pawelosinski.skatefreak.local.loggedUser
import pl.pawelosinski.skatefreak.service.DatabaseService
import pl.pawelosinski.skatefreak.service.databaseService
import pl.pawelosinski.skatefreak.ui.auth.LoginActivity
import pl.pawelosinski.skatefreak.ui.common.myCommonModifier
import pl.pawelosinski.skatefreak.ui.menu.MainMenuActivity
import pl.pawelosinski.skatefreak.ui.theme.SkateFreakTheme


class MainActivity : ComponentActivity() {

    private var isUserLoggedIn = false
    private lateinit var localDataInit: LocalDataInit

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        databaseService = DatabaseService()
        isUserLoggedIn = checkIfUserIsLoggedIn()
        localDataInit = LocalDataInit(this)
        localDataInit.loadData()

        setContent {
            SkateFreakTheme (darkTheme = isDarkMode) {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    LoadingScreen()
                }
            }
        }
    }

    private fun checkIfUserIsLoggedIn() : Boolean {
        var isUserLoggedIn = true
        val currentUser = Firebase.auth.currentUser ?: return false
        databaseService.setLoggedUserById(currentUser.uid, onSuccess = {
            isUserLoggedIn = true
            if (loggedUser.value.checkRequiredData()) {
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
        //databaseService.setDefaultTrickInfo()
//        databaseService.getUrlOfStorageFile("trickRecord/video/3 git.mov")
//        databaseService.setDefaultTrickRecord()

        return isUserLoggedIn
    }

    @Composable
    fun LoadingScreen() {
        val loadingMessage by remember {
            mutableStateOf("Ładowanie...\n" +
                    "(Aplikacja wymaga połączenia z internetem)")
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

