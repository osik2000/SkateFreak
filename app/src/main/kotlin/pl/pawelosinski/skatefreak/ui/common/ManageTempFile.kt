package pl.pawelosinski.skatefreak.ui.common

import android.util.Log
import java.io.File

fun deleteTempFile(filePath: String) {
    val tempFile = File(filePath)
    if (tempFile.exists()) {
        if (tempFile.delete()) {
            Log.d("TempManager", "Temporary file deleted successfully: $filePath")
        } else {
            Log.e("TempManager", "Failed to delete temporary file: $filePath")
        }
    }
}