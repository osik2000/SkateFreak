package pl.pawelosinski.skatefreak.ui.common

import android.annotation.SuppressLint
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

val myButtonModifier8dp = Modifier
        .fillMaxWidth()
        .padding(8.dp)

val myButtonModifier16dp = Modifier
        .fillMaxWidth()
        .padding(16.dp)

@SuppressLint("ModifierFactoryExtensionFunction")
fun avatarModifier(borderColor: Color) = Modifier
    .size(125.dp)
    .border(2.dp, borderColor, CircleShape)
    .padding(3.dp)
    .clip(CircleShape)