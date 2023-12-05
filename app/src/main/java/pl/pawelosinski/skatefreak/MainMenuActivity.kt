package pl.pawelosinski.skatefreak

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text
import pl.pawelosinski.skatefreak.service.DataService
import pl.pawelosinski.skatefreak.ui.common.BottomNavigationBar
import pl.pawelosinski.skatefreak.ui.mainScreen.MainScreen
import pl.pawelosinski.skatefreak.ui.theme.SkateFreakTheme

class MainMenuActivity : ComponentActivity() {
    private val dataService = DataService()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SkateFreakTheme {
                BottomNavigationBar()
            }
        }
    }
}
