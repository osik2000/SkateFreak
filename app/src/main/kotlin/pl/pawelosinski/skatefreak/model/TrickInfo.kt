package pl.pawelosinski.skatefreak.model

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

data class TrickInfo (
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val difficulty: String = "",
    val category: String = "",
    val photoUrl: String = "",
)