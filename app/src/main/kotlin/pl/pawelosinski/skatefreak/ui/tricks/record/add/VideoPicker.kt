package pl.pawelosinski.skatefreak.ui.tricks.record.add

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.gowtham.library.utils.CompressOption
import com.gowtham.library.utils.LogMessage
import com.gowtham.library.utils.TrimType
import com.gowtham.library.utils.TrimVideo
import pl.pawelosinski.skatefreak.model.TrickRecord
import pl.pawelosinski.skatefreak.ui.common.myToast

@Composable
fun VideoPickerButton() {
    val context = LocalContext.current

    val startForResult = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK &&
            result.data != null
        ) {
            val uri = Uri.parse(TrimVideo.getTrimmedVideoPath(result.data))
            val trimmedVideoPath = TrimVideo.getTrimmedVideoPath(result.data)

            Log.d("VideoPicker", "Trimmed path: $trimmedVideoPath")
            Log.d("VideoPicker", "Trimmed path:: $uri")
            myToast(context, "Trimmed path: $trimmedVideoPath")
            TrickRecord.localFileUri.value = trimmedVideoPath
            TrickRecord.trimmedVideoPath.value = trimmedVideoPath
            TrickRecord.whileAdding.value = true
        } else {
            LogMessage.v("videoTrimResultLauncher data is null")
        }
    }


    val pickMediaActivityResultLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        Log.d("VideoPicker", "Uri: $uri")
        if (uri == null) {
            myToast(context, "Nie wybrano pliku")
        }
        else {
            val fileName = getFileNameFromUri(context, uri)
            myToast(context, "Wybrano plik: $fileName")
            Log.d("VideoPicker", "file chosen: $fileName")
            TrickRecord.localFileUri.value = uri.toString()

            TrimVideo.activity(uri.toString())
                .setCompressOption(CompressOption())
                .setTrimType(TrimType.MIN_MAX_DURATION)
                .setMinToMax(2, 10)  //seconds`
                .setHideSeekBar(true)
                .start(context as Activity, startForResult)
        }
    }

    Button(onClick = {
        pickMediaActivityResultLauncher.launch("video/*")
    }) {
        Text(text = "Wybierz nagranie")
    }

}
fun getFileNameFromUri(context: Context, uri: Uri): String {
    var result = "Unknown"
    try {
        context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val displayNameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (displayNameIndex >= 0) {
                    val displayName = cursor.getString(displayNameIndex)
                    result = displayName ?: "Unknown"
                } else {
                    Log.e("getFileNameFromUri", "Column DISPLAY_NAME not found in the cursor")
                }
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return result
}