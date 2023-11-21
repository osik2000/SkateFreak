package pl.pawelosinski.skatefreak

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import pl.pawelosinski.skatefreak.model.User
import pl.pawelosinski.skatefreak.ui.theme.SkateFreakTheme

class LoggedUserMenuActivity : ComponentActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var user: User
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Firebase Auth
        auth = Firebase.auth
        user = User.getUserFromFirebaseUser(auth.currentUser)

        if (!user.checkRequiredData()) {
            Log.d("LoggedUserMenuActivity", "User data is incomplete:\n$user")
        }

        setContent {
            SkateFreakTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    LoggedUserMenuScreen()
                }
            }
        }
    }

    @Composable
    fun LoggedUserMenuScreen() {
        Column(
            modifier = Modifier.fillMaxSize(),
        ) {
            Text(
                text = "Menu Główne",
                modifier = Modifier.padding(16.dp)
            )
            if (user.name.isNotEmpty()) {
                Text(
                    text = "Witaj ${user.name}!",
                    modifier = Modifier.padding(16.dp)
                )
            } else {
                Text(
                    text = "Witaj ${user.phoneNumber}!",
                    modifier = Modifier.padding(16.dp)
                )
            }
            Log.d("LoggedUserMenuActivity", "User: ${user.name}")
        }
    }



}
