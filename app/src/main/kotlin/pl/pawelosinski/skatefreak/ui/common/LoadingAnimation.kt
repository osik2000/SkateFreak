package pl.pawelosinski.skatefreak.ui.common

import android.app.Activity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun LoadingAnimation(loadingLabel: String = "Trwa ładowanie danych...") {
    val activity = LocalContext.current as Activity
    var showAlert by remember { mutableStateOf(false) }
    var labelText by remember { mutableStateOf(loadingLabel) }
    val alertMessage = "Wystąpił błąd. \nSprawdź połączenie z internetem."

    LaunchedEffect(showAlert) {
        if (showAlert) {
            myToast(activity, "Wystąpił błąd.\nZamykanie aplikacji...")
            delay(3000)
            activity.finish()
        }
        else {
            delay(3000)

            labelText += "\n(Może to trochę potrwać...)"

            delay(5000)

            showAlert = true
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            RoundedIcon()

            Spacer(modifier = Modifier.height(32.dp))

            if(showAlert) {
                labelText = alertMessage
            }
            else {
                LinearProgressIndicator(
                    modifier = Modifier
                        .width(150.dp)
                        .height(8.dp),
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
            Text(
                labelText,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center)
        }
    }
}