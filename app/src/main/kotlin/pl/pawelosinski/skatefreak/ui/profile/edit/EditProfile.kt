package pl.pawelosinski.skatefreak.ui.profile.edit

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import pl.pawelosinski.skatefreak.local.loggedUser

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfile(navController: NavController) {
    var user = loggedUser.value
    val phoneNumber = remember { mutableStateOf(user.phoneNumber) }
    val verificationCode = remember { mutableStateOf("") }
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

@Composable
fun ProfileAvatarEditor() {
    val avatarUrl = loggedUser.value.photoUrl
    val imagePainter = rememberAsyncImagePainter(avatarUrl)

    Box(
        contentAlignment = Alignment.BottomEnd,
        modifier = Modifier.size(150.dp)
    ) {
        Image(
            painter = imagePainter,
            contentDescription = "Profile Picture",
            modifier = Modifier
                .size(150.dp)
                .clip(CircleShape)
        )
        Icon(
            imageVector = Icons.Filled.CameraAlt,
            contentDescription = "Edit",
            tint = Color.White,
            modifier = Modifier
                .size(40.dp)
                .background(
                    color = Color.Black.copy(alpha = 0.5f),
                    shape = CircleShape
                )
                .clickable {
                    // TODO: Implement avatar change logic
                }
        )
    }
}