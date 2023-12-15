package pl.pawelosinski.skatefreak.model

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
}