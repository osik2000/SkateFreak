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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import pl.pawelosinski.skatefreak.ui.common.myCommonModifier
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
                    BeforeLoginScreen()
                }
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
                    startActivity(intent)
                    finish()
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
}
