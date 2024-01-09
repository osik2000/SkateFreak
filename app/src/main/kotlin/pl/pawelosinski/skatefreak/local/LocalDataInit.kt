package pl.pawelosinski.skatefreak.local

import android.content.Context
import pl.pawelosinski.skatefreak.model.TrickRecord

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

    fun loadAllTrickRecords(trickRecordList: MutableList<TrickRecord>, context: Context, onSuccess: () -> Unit = {}) {
        allTrickRecords = trickRecordList
        ThumbnailCacheManager.preloadTrickRecordImages(allTrickRecords, context, onSuccess)
        //loadCurrentRecordData()
        //allTrickRecords.sortByDescending { it.usernamesWhoLiked.size }
    }
//
//    companion object{
//        fun loadCurrentRecordData(index: Int = 0, trickRecords: MutableList<TrickRecord> = allTrickRecords) {
//            Log.d("LocalDataInit", "loadCurrentRecordData: $index" +
//                    "allTrickRecordsCreators.size > index && allTrickRecordsCreators[index].firebaseId.isNotEmpty(): " +
//                    "${allTrickRecordsCreators.size > index && allTrickRecordsCreators[index].firebaseId.isNotEmpty()}")
//            if(allTrickRecordsCreators.size > index && allTrickRecordsCreators[index].firebaseId.isNotEmpty()) {
//                currentRecordCreator.value = allTrickRecordsCreators[index]
//            }
//            else {
//                databaseService.getUserById(allTrickRecords[index].userID, onSuccess = {
//                    allTrickRecordsCreators.add(index, it)
//                    currentRecordCreator.value = it
//                })
//            }
//            currentRecordLikes.value = trickRecords[index].usersWhoSetAsFavorite.size.toString()
//        }
//    }
}
