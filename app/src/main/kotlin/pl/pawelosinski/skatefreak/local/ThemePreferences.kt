package pl.pawelosinski.skatefreak.local

import android.content.Context

class ThemePreferences(context: Context) {
    private val sharedPreferences = context.getSharedPreferences("ThemePrefs", Context.MODE_PRIVATE)

    fun saveThemeSelection(theme: String) {
        sharedPreferences.edit().putString("SelectedTheme", theme).apply()
    }

    fun getThemeSelection(): String {
        return sharedPreferences.getString("SelectedTheme", "Light") ?: "Dark"
    }
}