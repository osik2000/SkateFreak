package pl.pawelosinski.skatefreak.model

data class TrickRecord(
    val id: String = "",
    val userID: String = "",
    val trickID: String = "",
    val date: String = "",
    val userDescription: String = "",
    val videoUrl: String = "",
    val isFavorite: Boolean = false, //TODO chyba nie
) {
}