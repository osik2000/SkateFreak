package pl.pawelosinski.skatefreak.model

import androidx.compose.runtime.mutableStateOf

data class TrickRecord(
    var id: String = "",
    val userID: String = "",
    val trickID: String = "",
    val date: String = "",
    val title: String = "",
    val userDescription: String = "",
    var videoUrl: String = "",
    val usersWhoSetAsFavorite: MutableList<String> = mutableListOf(),
) {
    companion object {
        val trimmedVideoPath = mutableStateOf("")
        var localFileUri = mutableStateOf("")
    }
}

