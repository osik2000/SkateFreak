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

    fun toDTO(trickRecord: TrickRecord = this): TrickRecordDTO {
        return TrickRecordDTO(
            id = trickRecord.id,
            userID = trickRecord.userID,
            trickID = trickRecord.trickID,
            date = trickRecord.date,
            title = trickRecord.title,
            userDescription = trickRecord.userDescription,
            videoUrl = trickRecord.videoUrl,
            usersWhoSetAsFavorite = trickRecord.usersWhoSetAsFavorite,
            favoriteCounter = trickRecord.favoriteCounter.intValue.toString(),
            usersWhoLiked = trickRecord.usersWhoLiked,
            likeCounter = trickRecord.likeCounter.intValue.toString(),
            usersWhoDisiked = trickRecord.usersWhoDisiked,
            dislikeCounter = trickRecord.dislikeCounter.intValue.toString(),
        )
    }
    companion object {
        val trimmedVideoPath = mutableStateOf("")
        var localFileUri = mutableStateOf("")
    }
}

data class TrickRecordDTO(
    var id: String = "",
    val userID: String = "",
    val trickID: String = "",
    val date: String = "",
    val title: String = "",
    val userDescription: String = "",
    var videoUrl: String = "",
    val usersWhoSetAsFavorite: MutableList<String> = mutableListOf(),
    var favoriteCounter: String = "0",
    val usersWhoLiked: MutableList<String> = mutableListOf(),
    val likeCounter: String = "0",
    val usersWhoDisiked: MutableList<String> = mutableListOf(),
    val dislikeCounter: String = "0",
) {
    fun toTrickRecord(trickRecordDTO: TrickRecordDTO = this): TrickRecord {
        return TrickRecord(
            id = trickRecordDTO.id,
            userID = trickRecordDTO.userID,
            trickID = trickRecordDTO.trickID,
            date = trickRecordDTO.date,
            title = trickRecordDTO.title,
            userDescription = trickRecordDTO.userDescription,
            videoUrl = trickRecordDTO.videoUrl,
            usersWhoSetAsFavorite = trickRecordDTO.usersWhoSetAsFavorite,
            favoriteCounter = mutableIntStateOf(Integer.valueOf(trickRecordDTO.favoriteCounter)),
            usersWhoLiked = trickRecordDTO.usersWhoLiked,
            likeCounter = mutableIntStateOf(Integer.valueOf(trickRecordDTO.likeCounter)),
            usersWhoDisiked = trickRecordDTO.usersWhoDisiked,
            dislikeCounter = mutableIntStateOf(Integer.valueOf(trickRecordDTO.dislikeCounter)),
        )
    }
}

