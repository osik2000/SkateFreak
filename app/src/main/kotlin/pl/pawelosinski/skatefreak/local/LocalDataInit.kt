package pl.pawelosinski.skatefreak.local

import android.content.Context

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
}
