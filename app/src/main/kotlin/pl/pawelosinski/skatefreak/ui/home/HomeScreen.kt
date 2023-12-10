package pl.pawelosinski.skatefreak.ui.home

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
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
//            Column(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .padding(15.dp),
//                horizontalAlignment = Alignment.CenterHorizontally,
//                verticalArrangement = Arrangement.Center
//            ) {
//                Box(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .height(200.dp)
//                        .padding(horizontal = 15.dp, vertical = 10.dp)
//                        .clip(MaterialTheme.shapes.large)
//                ) {
//                    Icon(
//                        imageVector = Icons.Default.Home,
//                        contentDescription = "home_screen_bg",
//                        modifier = Modifier.fillMaxSize()
//                    )
//                }
//                Text(
//                    "Home Screen",
//                    style = MaterialTheme.typography.titleLarge,
//                    modifier = Modifier.padding(vertical = 20.dp)
//                )
//                TrickRecordComposable(trickRecord = allTrickRecords[0])
                TrickRecordsScreen()
            //}
        }
    }
}

