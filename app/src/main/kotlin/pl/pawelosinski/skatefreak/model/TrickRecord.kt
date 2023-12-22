package pl.pawelosinski.skatefreak.model

import androidx.compose.runtime.mutableStateOf

data class TrickRecord(
    val id: String = "",
    val userID: String = "",
    val trickID: String = "",
    val date: String = "",
    val title: String = "",
    val userDescription: String = "",
    val videoUrl: String = "",
    val usersWhoSetAsFavorite: MutableList<String> = mutableListOf(),
) {
    companion object {
        var localFileUri = mutableStateOf("")
    }
}

