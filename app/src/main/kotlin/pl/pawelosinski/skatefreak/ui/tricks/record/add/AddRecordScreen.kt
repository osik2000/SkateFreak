package pl.pawelosinski.skatefreak.ui.tricks.record.add

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import pl.pawelosinski.skatefreak.local.isDarkMode
import pl.pawelosinski.skatefreak.model.TrickRecord
import pl.pawelosinski.skatefreak.ui.profile.ScreenTitle
import pl.pawelosinski.skatefreak.ui.theme.SkateFreakTheme

@Composable
fun AddRecordScreen(navController: NavController) {
    var record = TrickRecord()

    SkateFreakTheme (darkTheme = isDarkMode){
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(15.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                ScreenTitle(text = "Dodaj Klip")
                Spacer(modifier = Modifier.padding(10.dp))

                // Dodaj komponent do wybierania pliku wideo
                UploadFileComposable()

                Spacer(modifier = Modifier.padding(10.dp))

                // Pole do wprowadzania tytułu
                OutlinedTextField(
                    value = record.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    onValueChange = { record = record.copy(title = it) },
                    label = { Text("Tytuł") }
                )

                Spacer(modifier = Modifier.padding(10.dp))

                // Pole do wprowadzania opisu
                OutlinedTextField(
                    value = record.userDescription,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    onValueChange = { record = record.copy(userDescription = it) },
                    label = { Text("Opis") }
                )

                Spacer(modifier = Modifier.padding(10.dp))

                ChooseTrickInfoButton()

                Spacer(modifier = Modifier.padding(10.dp))

                VideoPickerButton()

                Spacer(modifier = Modifier.padding(10.dp))

                AddRecordButton()

            }
        }
    }
}

@Composable
fun ChooseTrickInfoButton() {
    Button(onClick = { /* todo */ }) {
        Text("Wybierz Trik")
    }
}

@Composable
fun UploadFileComposable() {
    // todo
}

@Composable
fun AddRecordButton() {
    Button(onClick = { /*  TODO  */ }) {
        Text("Zapisz")
    }
}