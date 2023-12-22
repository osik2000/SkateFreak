package pl.pawelosinski.skatefreak.ui.profile.edit

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.yalantis.ucrop.UCrop
import pl.pawelosinski.skatefreak.R
import pl.pawelosinski.skatefreak.local.loggedUser
import pl.pawelosinski.skatefreak.service.databaseService
import pl.pawelosinski.skatefreak.ui.common.avatarModifier
import pl.pawelosinski.skatefreak.ui.common.myToast
import java.io.File


@Composable
fun ProfileAvatarEditor() {
    var imageUri by remember { mutableStateOf<Uri?>(Uri.parse(loggedUser.value.photoUrl)) }
    val context = LocalContext.current


    // Launcher do przyciętego obrazu

    val imageCropperLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val croppedUri = UCrop.getOutput(result.data!!)
            croppedUri?.let {
                imageUri = it
                databaseService.uploadUserAvatar(userID = loggedUser.value.firebaseId, file = imageUri!!, onComplete = {
                    Log.d("EditProfile", "Avatar uploaded")
                    myToast(context, "Zdjęcie profilowe zostało zmienione")
                    imageUri = Uri.parse(loggedUser.value.photoUrl)
                }, onFail = {
                    imageUri = Uri.parse(loggedUser.value.photoUrl)
                    Log.d("EditProfile", "Avatar upload failed")
                    myToast(context, "Zdjęcie profilowe nie zostało zmienione.\n Spróbuj ponownie później.")
                })
            }
        }
    }

    // Launcher do wyboru obrazu
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // Aktywujemy UCrop do przycięcia wybranego obrazu
            val sourceUri = result.data?.data
            sourceUri?.let {
                val destinationUri = Uri.fromFile(File(context.cacheDir, "cropped"))
                // Po przycięciu obrazu, zapisujemy go w cache a następnie wysyłamy do Firebase Storage
                val uCropIntent = UCrop.of(sourceUri, destinationUri)
                    .withAspectRatio(1f, 1f)
                    .withMaxResultSize(500, 500)
                    .getIntent(context)
                imageCropperLauncher.launch(uCropIntent)
            }
        }
    }

    // Wyświetlanie avatara i ikony do zmiany

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(150.dp)
            .clickable {
                val pickImageIntent = Intent(Intent.ACTION_PICK)
                pickImageIntent.type = "image/*"
                imagePickerLauncher.launch(pickImageIntent)
            }
    ) {
        val painter = if (imageUri != null) {
            rememberAsyncImagePainter(imageUri)
        } else {
            rememberAsyncImagePainter(R.drawable.baseline_skateboarding_24) // placeholder to domyślny obraz
        }

        Image(
            painter = painter,
            contentDescription = "Profile Picture",
            modifier = avatarModifier(borderColor = MaterialTheme.colorScheme.primary),
            alignment = Alignment.Center,
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
        )
    }
}
