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
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.yalantis.ucrop.UCrop
import pl.pawelosinski.skatefreak.R
import pl.pawelosinski.skatefreak.service.databaseService
import pl.pawelosinski.skatefreak.ui.common.myToast
import java.io.File

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
    var imageUri by remember { mutableStateOf<Uri?>(Uri.parse(loggedUser.value.photoUrl)) }
    val context = LocalContext.current

    // Launcher do wyboru obrazu
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // Pobieramy URI wybranego obrazu
            val sourceUri = result.data?.data
            sourceUri?.let {
                val destinationUri = Uri.fromFile(File(context.cacheDir, "cropped"))
                UCrop.of(it, destinationUri)
                    .withAspectRatio(1f, 1f)
                    .start(context as Activity)
                imageUri = destinationUri
                databaseService.uploadUserAvatar(userID = loggedUser.value.firebaseId, file = destinationUri, onComplete = {
                    Log.d("EditProfile", "Avatar uploaded")
                    myToast(context, "Zdjęcie profilowe zostało zmienione")
                    imageUri = Uri.parse(loggedUser.value.photoUrl)
                }, onFail = {
                    Log.d("EditProfile", "Avatar upload failed")
                    myToast(context, "Zdjęcie profilowe nie zostało zmienione.\n Spróbuj ponownie później.")
                })
            }
        }
    }


    Box(
        contentAlignment = Alignment.BottomEnd,
        modifier = Modifier.size(150.dp)
    ) {
        val painter = if (imageUri != null) {
            rememberAsyncImagePainter(imageUri)
        } else {
            rememberAsyncImagePainter(R.drawable.baseline_skateboarding_24) // placeholder to domyślny obraz
        }

        Image(
            painter = painter,
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
                    val pickImageIntent = Intent(Intent.ACTION_PICK)
                    pickImageIntent.type = "image/*"
                    imagePickerLauncher.launch(pickImageIntent)
                }
        )
    }
}


