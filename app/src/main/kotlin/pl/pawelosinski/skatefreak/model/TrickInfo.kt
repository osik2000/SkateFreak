package pl.pawelosinski.skatefreak.model

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import pl.pawelosinski.skatefreak.local.allTrickInfo

data class TrickInfo (
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val difficulty: String = "",
    val category: String = "",
    val photoUrl: String = "",
) {
    companion object {
        fun getTrickName(trickID: String, trickInfoList: MutableList<TrickInfo> = allTrickInfo): String {
            return trickInfoList.find { it.id == trickID }?.name ?: "Nieokreślony Trick"
        }
    }
}