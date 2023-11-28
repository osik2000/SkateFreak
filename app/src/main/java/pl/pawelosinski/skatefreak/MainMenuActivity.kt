package pl.pawelosinski.skatefreak

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import pl.pawelosinski.skatefreak.service.DataService
import pl.pawelosinski.skatefreak.ui.theme.SkateFreakTheme

class MainMenuActivity : ComponentActivity() {
    private val dataService = DataService()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SkateFreakTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    //modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "Menu Główne", modifier = Modifier.padding(16.dp))

                        //logout button
                        Button(
                            modifier = Modifier.padding(16.dp),
                            onClick = {
                                Firebase.auth.signOut()
                                finish()
                            }
                        ) {
                            Text(text = "Wyloguj")
                        }
                    }
                }
            }
        }
    }
}
