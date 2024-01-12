package pl.pawelosinski.skatefreak.local

import android.content.Context
import android.util.Log
import pl.pawelosinski.skatefreak.model.TrickRecord
import java.text.ParseException
import java.text.SimpleDateFormat

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

        val format = "EEE MMM dd HH:mm:ss 'GMT'Z yyyy"
        val sdf = SimpleDateFormat(format, java.util.Locale.getDefault())
        val oldDate = sdf.parse("Thu Jan 01 00:00:00 GMT+01:00 1970")

        Log.d("LocalDataInit", "sorting date")
        allTrickRecords.sortBy {
            try {
                sdf.parse(it.date)
            } catch (e: ParseException) {
                e.printStackTrace()
                oldDate
            }
        }
        allTrickRecords.reverse()
    }
}
