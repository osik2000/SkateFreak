package pl.pawelosinski.skatefreak.sharedPreferences

import android.os.Bundle
import androidx.activity.ComponentActivity

class PreferencesInitialization :  ComponentActivity(){
    private lateinit var themePreferences: ThemePreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        themePreferences = ThemePreferences(this)
        val selectedTheme = themePreferences.getThemeSelection()

        // Ustaw motyw na podstawie wyboru użytkownika
        setTheme(selectedTheme)

        // ... reszta kodu onCreate
    }

    private fun setTheme(theme: String) {
        when (theme) {
            "Light" -> {

            }
            "Dark" -> {

            }
        }
    }

    // Przykład zapisywania wyboru motywu
    fun onThemeSelected(theme: String) {
        themePreferences.saveThemeSelection(theme)
        setTheme(theme)
    }
}