package pl.pawelosinski.skatefreak.ui.menu

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import pl.pawelosinski.skatefreak.local.isDarkMode
import pl.pawelosinski.skatefreak.service.DatabaseService
import pl.pawelosinski.skatefreak.ui.common.BottomNavigationBar
import pl.pawelosinski.skatefreak.ui.theme.SkateFreakTheme

class MainMenuActivity : ComponentActivity() {
//    private val databaseService = DatabaseService()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SkateFreakTheme (darkTheme = isDarkMode) {
                BottomNavigationBar()
            }
        }
    }
}
