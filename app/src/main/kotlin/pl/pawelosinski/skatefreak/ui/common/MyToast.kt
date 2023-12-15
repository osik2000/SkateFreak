package pl.pawelosinski.skatefreak.ui.common

import android.content.Context
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT

fun myToast(context: Context, message: String) {
    Toast.makeText(context, message, LENGTH_SHORT).show()
}