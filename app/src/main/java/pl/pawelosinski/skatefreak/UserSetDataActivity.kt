package pl.pawelosinski.skatefreak

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.unit.dp
import pl.pawelosinski.skatefreak.auth.loggedUser
import pl.pawelosinski.skatefreak.service.DataService
import pl.pawelosinski.skatefreak.ui.common.myCommonModifier
import pl.pawelosinski.skatefreak.ui.theme.SkateFreakTheme

class UserSetDataActivity : ComponentActivity() {
    private val dataService = DataService()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SkateFreakTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column {
                        Text(
                            text = "Uzupełnij swoje dane",
                            modifier = Modifier.padding(16.dp)
                        )
                        ChangeName { dataService.changeUserData() }
                        ChangeNumber { dataService.changeUserData() }
                    }

                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangeNumber(onSave: () -> Unit) {
    var phoneNumber by remember { mutableStateOf(loggedUser.phoneNumber) }
    var isInEditMode by remember { mutableStateOf(loggedUser.phoneNumber.isEmpty()) }
    val pattern = remember { Regex("^\\+48\\d\\d\\d\\d\\d\\d\\d\\d\\d$") }
    // log loggedUser data
    Log.d("ChangeNumber", "loggedUser: $loggedUser")
    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "Numer Telefonu",
            modifier = myCommonModifier,
            style = MaterialTheme.typography.labelSmall
        )
        Row (
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

                OutlinedTextField(
                    enabled = isInEditMode,
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp),
                    value = phoneNumber,
                    onValueChange = { phoneNumber = it },
                    label = { Text("np.(+48555666555)") }
                )
            if (isInEditMode) {
                Button(
                    onClick = {
                        // check if phone number is correct
                        if (!pattern.matches(phoneNumber)) {
                            return@Button
                        }
                        isInEditMode = false
                        loggedUser.phoneNumber = phoneNumber
                        onSave()
                    },
                    modifier = Modifier
                ) {
                    Text("Zapisz")
                }
            } else {
                Button(
                    onClick = { isInEditMode = true },
                    modifier = Modifier
                ) {
                    Text("Edytuj")
                }
            }
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangeName(onSave: () -> Unit) {
    var name by remember { mutableStateOf(loggedUser.name) }
    var isInEditMode by remember { mutableStateOf(loggedUser.name.isEmpty()) }
    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "Nazwa Profilu",
            modifier = myCommonModifier,
            style = MaterialTheme.typography.labelSmall
        )
        Row (
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                enabled = isInEditMode,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp),
                value = name,
                onValueChange = { name = it },
                label = { Text("Imię i nazwisko") }
            )
            if (isInEditMode) {
                Button(
                    onClick = {
                        // check if name is correct
                        if (name.isEmpty()) {
                            return@Button
                        }
                        isInEditMode = false
                        loggedUser.name = name
                        onSave()
                    },
                    modifier = Modifier
                ) {
                    Text("Zapisz")
                }
            } else {
                Button(
                    onClick = { isInEditMode = true },
                    modifier = Modifier
                ) {
                    Text("Edytuj")
                }
            }
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangeNickname(onSave: () -> Unit) {
    var nickname by remember { mutableStateOf(loggedUser.nickname) }
    var isInEditMode by remember { mutableStateOf(loggedUser.nickname.isEmpty()) }
    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "Pseudonim",
            modifier = myCommonModifier,
            style = MaterialTheme.typography.labelSmall
        )
        Row (
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                enabled = isInEditMode,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp),
                value = nickname,
                onValueChange = { nickname = it },
                label = { Text("nick") }
            )
            if (isInEditMode) {
                Button(
                    onClick = {
                        // check if nickname is correct
                        if (nickname.isEmpty()) {
                            return@Button
                        }
                        isInEditMode = false
                        loggedUser.nickname = nickname
                        onSave()
                    },
                    modifier = Modifier
                ) {
                    Text("Zapisz")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangeCity(onSave: () -> Unit) {
    var city by remember { mutableStateOf(loggedUser.city) }
    var isInEditMode by remember { mutableStateOf(loggedUser.city.isEmpty()) }
    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "Miasto",
            modifier = myCommonModifier,
            style = MaterialTheme.typography.labelSmall
        )
        Row (
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                enabled = isInEditMode,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp),
                value = city,
                onValueChange = { city = it },
                label = { Text("Miasto") }
            )
            if (isInEditMode) {
                Button(
                    onClick = {
                        // check if city is correct
                        if (city.isEmpty()) {
                            return@Button
                        }
                        isInEditMode = false
                        loggedUser.city = city
                        onSave()
                    },
                    modifier = Modifier
                ) {
                    Text("Zapisz")
                }
            }
        }
    }
}