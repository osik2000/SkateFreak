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
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import pl.pawelosinski.skatefreak.ui.theme.SkateFreakTheme

class LoggedUserMenuActivity : ComponentActivity() {

    private lateinit var auth: FirebaseAuth
    private var user: FirebaseUser? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Firebase Auth
        auth = Firebase.auth

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
            user = auth.currentUser
            Text(
                text = "Menu Główne",
                modifier = Modifier.padding(bottom = 16.dp)
            )
            Text(text = "Witaj ${user?.displayName ?: user?.phoneNumber}!", modifier = Modifier.padding(16.dp))
            Log.d("LoggedUserMenuActivity", "User: ${user?.displayName}")
        }
    }



}
