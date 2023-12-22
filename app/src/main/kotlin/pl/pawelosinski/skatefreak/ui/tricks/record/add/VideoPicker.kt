package pl.pawelosinski.skatefreak.ui.tricks.record.add

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import pl.pawelosinski.skatefreak.ui.common.myToast

@Composable
fun VideoPickerButton() {
    val context = LocalContext.current
    val pickMediaActivityResultLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        Log.d("VideoPicker", "Uri: $uri")
        if (uri == null) {
            myToast(context, "Nie wybrano pliku")
        }
        else {
            myToast(context, "Wybrano plik: ${uri.lastPathSegment}")
            Log.d("VideoPicker", "file chosen: ${uri.lastPathSegment}")

        }
    }

    Button(onClick = {
        pickMediaActivityResultLauncher.launch("video/*")
    }) {
        Text(text = "Pick media content")
    }

}
