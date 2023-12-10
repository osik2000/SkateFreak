package pl.pawelosinski.skatefreak.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import pl.pawelosinski.skatefreak.local.allTrickRecords
import pl.pawelosinski.skatefreak.local.isDarkMode
import pl.pawelosinski.skatefreak.ui.theme.SkateFreakTheme
import pl.pawelosinski.skatefreak.ui.tricks.TrickRecordComposable
import pl.pawelosinski.skatefreak.ui.tricks.TrickRecordsScreen


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

