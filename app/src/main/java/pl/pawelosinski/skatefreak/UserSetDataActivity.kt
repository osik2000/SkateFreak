package pl.pawelosinski.skatefreak

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import pl.pawelosinski.skatefreak.auth.loggedUser
import pl.pawelosinski.skatefreak.service.DataService
import pl.pawelosinski.skatefreak.ui.common.MyDivider
import pl.pawelosinski.skatefreak.ui.theme.SkateFreakTheme

@OptIn(ExperimentalMaterial3Api::class)
class UserSetDataActivity : ComponentActivity() {
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
                        Text(
                            text = "Uzupełnij swoje dane",
                            modifier = Modifier.padding(16.dp)
                        )
                        ChangeNickname()
                        MyDivider()
                        ChangeEmail()
                        MyDivider()
                        ChangeName()
                        MyDivider()
                        ChangeNumber()
                        MyDivider()
                        ChangeCity()
                        MyDivider()
                        SaveAllButton()
                    }

                }
            }
        }
    }
    @Composable
    fun EditButton(onClick: () -> Unit) {
        Button(
            onClick = onClick,
            modifier = Modifier
        ) {
            Text("Edytuj")
        }
    }

    @Composable
    fun SaveButton(onClick: () -> Unit) {
        Button(
            onClick = onClick,
            modifier = Modifier
        ) {
            Text("Zapisz")
        }
    }

    @Composable
    fun SaveAllButton() { // TODO add validation
        val context = LocalContext.current
        Button(
            onClick = {
                if(!loggedUser.checkRequiredData()) {
                    Log.d("UserSetDataActivity", "User data is incomplete:\n$loggedUser")
                    Toast.makeText(context, "Uzupełnij wszystkie dane", Toast.LENGTH_SHORT).show()
                    return@Button
                }
                else {
                    Log.d("UserSetDataActivity", "User data is complete:\n$loggedUser")
                    dataService.changeUserData()
                    Toast.makeText(context, "Dane użytkownika zaaktualizowane", Toast.LENGTH_SHORT).show()
                    //Navigate to MainMenuActivity
                    val intent = Intent(context, MainMenuActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            },
            modifier = Modifier.padding(16.dp)
        ) {
            Text("Zapisz wszystkie dane")
        }
    }

        @Composable
        fun ChangeNumber() { // TODO add validation and check if number is unique
            var phoneNumber by remember { mutableStateOf(loggedUser.phoneNumber) }
            var isInEditMode by remember { mutableStateOf(loggedUser.phoneNumber.isEmpty()) }
            val pattern = remember { Regex("^\\+48\\d\\d\\d\\d\\d\\d\\d\\d\\d$") }
            // log loggedUser data
            if (loggedUser.phoneNumber.isEmpty()) {
                Log.d("ChangeNumber", "loggedUser is empty")
                loggedUser.phoneNumber = "+48"
            }
            Log.d("ChangeNumber", "loggedUser: $loggedUser")
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {

                OutlinedTextField(
                    enabled = isInEditMode,
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp),
                    value = phoneNumber,
                    onValueChange = { phoneNumber = it },
                    label = { Text("Numer Telefonu") }
                )
                if (isInEditMode) {
                    SaveButton {
                        // check if number is correct
                        if (!pattern.matches(phoneNumber)) {
                            return@SaveButton
                        }
                        isInEditMode = false
                        loggedUser.phoneNumber = phoneNumber
                    }
                } else {
                    EditButton(onClick = { isInEditMode = true })
                }
            }
        }

        @Composable
        fun ChangeEmail() { // TODO is email required? if yes -> add validation and check if email is unique
            var email by remember { mutableStateOf(loggedUser.email) }
            var isInEditMode by remember { mutableStateOf(loggedUser.email.isEmpty()) }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                OutlinedTextField(
                    enabled = isInEditMode,
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp),
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") }
                )
                if (isInEditMode) {
                    SaveButton {
                        // check if email is correct
                        if (email.isEmpty()) {
                            return@SaveButton
                        }
                        isInEditMode = false
                        loggedUser.email = email
                    }
                } else {
                    EditButton(onClick = { isInEditMode = true })
                }
            }
        }

        @Composable
        fun ChangeName() {
            var name by remember { mutableStateOf(loggedUser.name) }
            var isInEditMode by remember { mutableStateOf(loggedUser.name.isEmpty()) }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
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
                    SaveButton {
                        // check if name is correct
                        if (name.isEmpty()) {
                            return@SaveButton
                        }
                        isInEditMode = false
                        loggedUser.name = name
                    }
                } else {
                    EditButton(onClick = { isInEditMode = true })
                }
            }
        }

        @Composable
        fun ChangeNickname() {
            var nickname by remember { mutableStateOf(loggedUser.nickname) }
            var isInEditMode by remember { mutableStateOf(loggedUser.nickname.isEmpty()) }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
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
                    SaveButton {
                        // check if nickname is correct
                        if (nickname.isEmpty()) {
                            return@SaveButton
                        }
                        isInEditMode = false
                        loggedUser.nickname = nickname
                    }
                }
            }
        }

        @OptIn(ExperimentalMaterial3Api::class)
        @Composable
        fun ChangeCity() {
            var city by remember { mutableStateOf(loggedUser.city) }
            var isInEditMode by remember { mutableStateOf(loggedUser.city.isEmpty()) }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
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
                    SaveButton {
                        // check if city is correct
                        if (city.isEmpty()) {
                            return@SaveButton
                        }
                        isInEditMode = false
                        loggedUser.city = city
                    }
                } else {
                    EditButton(onClick = { isInEditMode = true })
                }
            }
        }
    }