package pl.pawelosinski.skatefreak.ui.tricks.record.add

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import pl.pawelosinski.skatefreak.local.isDarkMode
import pl.pawelosinski.skatefreak.model.TrickInfo
import pl.pawelosinski.skatefreak.model.TrickRecord
import pl.pawelosinski.skatefreak.ui.common.MyDivider
import pl.pawelosinski.skatefreak.ui.common.Screens
import pl.pawelosinski.skatefreak.ui.common.myToast
import pl.pawelosinski.skatefreak.ui.profile.ScreenTitle
import pl.pawelosinski.skatefreak.ui.theme.SkateFreakTheme
import java.util.Date

@Composable
fun AddRecordScreen(navController: NavController) {
    val context = LocalContext.current
    var record = TrickRecord()
    val chosenTrickInfo by remember { mutableStateOf(TrickInfo.chosenOne)}
    val localFileUri by remember { mutableStateOf(TrickRecord.localFileUri)}
    var title by remember { mutableStateOf("Mój klip")}
    var description by remember { mutableStateOf("Zobaczcie moje nagranie!")}

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

                RecordColumn {
                    UploadFileComposable()
                }


                RecordColumn {
                    OutlinedTextField(
                        value = title,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        onValueChange = {
                            if (it.length > 30) {
                                myToast(context, "Tytuł nie może przekraczać 30 znaków")
                            }
                            else {
                                title = it
                            }
                        },
                        label = { Text("Tytuł") }
                    )
                }


                // Pole do wprowadzania opisu
                RecordColumn {
                    OutlinedTextField(
                        value = record.userDescription,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        onValueChange = {
                            if (it.length > 30) {
                                myToast(context, "Opis nie może przekraczać 30 znaków")
                            }
                            else {
                                description = it
                            }
                        },
                        label = { Text("Opis") }
                    )
                }

                RecordColumn {
                    RecordRowName(text = if (chosenTrickInfo.value.name.isEmpty()) "Nie wybrano triku" else "Wybrany Trik: ${chosenTrickInfo.value.name}")
                    ChooseTrickInfoButton(navController = navController)
                }


                RecordColumn {
                    RecordRowName(text = if (localFileUri.value.isEmpty()) "Nie wybrano pliku" else "Wybrano plik")
                    VideoPickerButton()
                }



                AddRecordButton(onClick = {
                    record = TrickRecord(
                        title = title,
                        userDescription = description,
                        trickID = chosenTrickInfo.value.id,
                        videoUrl = localFileUri.value, // TODO CHANGE TO URL
                        date = Date().toString()
                    )
                })

            }
        }
    }
}


@Composable
fun RecordColumn(content: @Composable ColumnScope.() -> Unit) {
    Column (
        modifier = Modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        content()
    }
    Spacer(modifier = Modifier.padding(10.dp))
    MyDivider()
    Spacer(modifier = Modifier.padding(10.dp))

}

@Composable
fun RecordRowName(text: String) {
    Text(
        text = text,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        textAlign = TextAlign.Center
    )
    Spacer(modifier = Modifier.padding(5.dp))
}

@Composable
fun ChooseTrickInfoButton(navController: NavController) {
    Button(onClick = { navController.navigate(Screens.ChooseTrickInfo.route) }) {
        Text("Wybierz Trik")
    }

}

@Composable
fun UploadFileComposable() {
    // todo
}

@Composable
fun AddRecordButton(onClick : () -> Unit) {
    Button(onClick = { onClick() }) {
        Text("Zapisz")
    }
}