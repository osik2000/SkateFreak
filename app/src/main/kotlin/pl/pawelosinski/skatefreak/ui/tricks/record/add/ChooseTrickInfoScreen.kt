package pl.pawelosinski.skatefreak.ui.tricks.record.add

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import pl.pawelosinski.skatefreak.local.allTrickInfo
import pl.pawelosinski.skatefreak.local.isDarkMode
import pl.pawelosinski.skatefreak.model.TrickRecord
import pl.pawelosinski.skatefreak.ui.common.myToast
import pl.pawelosinski.skatefreak.ui.theme.SkateFreakTheme
import pl.pawelosinski.skatefreak.ui.tricks.info.TricksScreen

@Composable
fun ChooseTrickInfoScreen(navController: NavController) {
    SkateFreakTheme (darkTheme = isDarkMode) {
        Surface(
            modifier = Modifier
                .fillMaxSize(),
//                .verticalScroll(rememberScrollState()),
            color = MaterialTheme.colorScheme.background
        ) {
            val context = LocalContext.current
            val trickList = allTrickInfo
            TricksScreen(navController = navController, title = "Wybierz trik", trickList = trickList, onClick = {
                TrickRecord.chosenTrickInfo.value = it
                myToast(context, "Wybrano: ${it.name}")
                TrickRecord.whileAdding.value = true
                navController.navigateUp()
            })
        }
    }

}