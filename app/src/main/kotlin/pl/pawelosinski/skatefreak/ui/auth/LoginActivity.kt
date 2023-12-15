package pl.pawelosinski.skatefreak.ui.auth

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import pl.pawelosinski.skatefreak.local.firebaseAuthService
import pl.pawelosinski.skatefreak.local.isDarkMode
import pl.pawelosinski.skatefreak.service.FirebaseAuthService
import pl.pawelosinski.skatefreak.service.LoginScreen
import pl.pawelosinski.skatefreak.ui.theme.SkateFreakTheme

class LoginActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebaseAuthService = FirebaseAuthService(this)

        setContent {
//            LocalContext.current
            SkateFreakTheme(darkTheme = isDarkMode) {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,

                    ) {
                    LoginScreen(firebaseAuthService)
                }
            }
        }
    }
}








