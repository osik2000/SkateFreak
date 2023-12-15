package pl.pawelosinski.skatefreak.local

import android.content.Context
import pl.pawelosinski.skatefreak.model.TrickRecord
import pl.pawelosinski.skatefreak.service.databaseService

class LocalDataInit (context : Context) {
    // Theme
    private val themePreferences: ThemePreferences = ThemePreferences(context)
    private val selectedTheme = themePreferences.getThemeSelection()

    fun loadData() {
        loadTheme(selectedTheme)
    }

    private fun loadTheme(theme: String) {
        when (theme) {
            "Light" -> {
                isDarkMode = false
            }
            "Dark" -> {
                isDarkMode = true
            }
        }
    }

    fun loadAllTrickRecords(trickRecordList: MutableList<TrickRecord>) {
        allTrickRecords = trickRecordList
        //allTrickRecords.sortByDescending { it.usernamesWhoLiked.size }
    }

    companion object{
        fun loadCurrentRecordData(index: Int = 0) {
            currentRecordLikes.value = allTrickRecords[index].usersWhoSetAsFavorite.size.toString()
            databaseService.getUserById(allTrickRecords[index].userID, onSuccess = {
                currentRecordCreator.value = it
            })
        }
    }
}
