package pl.pawelosinski.skatefreak.ui.profile.edit

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import pl.pawelosinski.skatefreak.local.firebaseAuthService
import pl.pawelosinski.skatefreak.local.isDarkMode
import pl.pawelosinski.skatefreak.ui.theme.SkateFreakTheme

@Composable
fun PhoneChangeScreen(navController: NavController) {
    SkateFreakTheme (darkTheme = isDarkMode){
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            PhoneChangeForm(
                firebaseAuthService = firebaseAuthService
            ) {
                navController.navigateUp()
            }
        }
    }

}