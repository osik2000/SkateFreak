package pl.pawelosinski.skatefreak.ui.home

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import pl.pawelosinski.skatefreak.local.LocalDataInit
import pl.pawelosinski.skatefreak.local.isDarkMode
import pl.pawelosinski.skatefreak.ui.theme.SkateFreakTheme
import pl.pawelosinski.skatefreak.ui.tricks.record.TrickRecordsScreen


@Composable
fun HomeScreen(navController: NavController) {
    SkateFreakTheme (darkTheme = isDarkMode){
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {

            TrickRecordsScreen()
            //}
        }
    }
}

