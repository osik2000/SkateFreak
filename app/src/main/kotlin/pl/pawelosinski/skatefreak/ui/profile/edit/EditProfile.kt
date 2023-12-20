package pl.pawelosinski.skatefreak.ui.profile.edit

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import pl.pawelosinski.skatefreak.local.loggedUser

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfile(navController: NavController) {
    val phoneNumber = remember { mutableStateOf(loggedUser.value.phoneNumber) }
    val verificationCode = remember { mutableStateOf("") }
    val name = remember { mutableStateOf(loggedUser.value.name) }
    val city = remember { mutableStateOf(loggedUser.value.city) }
    val email = remember { mutableStateOf(loggedUser.value.email) }
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Edytuj Profil") }, navigationIcon = {
                IconButton(onClick = { navController.navigateUp() }) {
                    Icon(Icons.Filled.ArrowBack, "Back")
                }
            })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ProfileAvatarEditor()
            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = phoneNumber.value,
                onValueChange = { phoneNumber.value = it },
                label = { Text("Numer Telefonu") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
            )

            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = verificationCode.value,
                onValueChange = { verificationCode.value = it },
                label = { Text("Kod Weryfikacyjny SMS") }
            )

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = { /* TODO: Handle phone number update with verification */ },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Zapisz Zmiany", fontSize = 18.sp)
            }
        }
    }
}


