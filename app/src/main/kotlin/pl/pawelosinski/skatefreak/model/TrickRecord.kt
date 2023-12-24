package pl.pawelosinski.skatefreak.model

import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.mutableIntStateOf
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
    val favoriteCounter: MutableIntState = mutableIntStateOf(0),
    val usersWhoLiked: MutableList<String> = mutableListOf(),
    val likeCounter: MutableIntState = mutableIntStateOf(0),
    val usersWhoDisiked: MutableList<String> = mutableListOf(),
    val dislikeCounter: MutableIntState = mutableIntStateOf(0),
) {
    companion object {
        val trimmedVideoPath = mutableStateOf("")
        var localFileUri = mutableStateOf("")
    }
}

