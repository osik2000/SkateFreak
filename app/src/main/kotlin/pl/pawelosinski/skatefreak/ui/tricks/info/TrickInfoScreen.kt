package pl.pawelosinski.skatefreak.ui.tricks.info

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import pl.pawelosinski.skatefreak.service.LocalDataService

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrickInfoComposable(trickId: String, navController: NavController) {
        val trickInfo = LocalDataService.getTrickInfo(trickId)

        Scaffold(
                topBar = {
                        TopAppBar(title = { Text("Opis Triku - [${trickInfo.name}]") }, navigationIcon = {
                                IconButton(onClick = { navController.navigateUp() }) {
                                        Icon(Icons.Filled.ArrowBack, "Back")
                                }
                        })
                }
        ) { padding ->
                Column(
                        modifier = Modifier
                                .fillMaxSize()
                                .padding(padding)
                                .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                ) {
                        TrickGif(url = trickInfo.photoUrl)

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                                text = trickInfo.name,
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        Text(
                                text = "Poziom trudności: ${trickInfo.difficulty}",
                                style = MaterialTheme.typography.bodyLarge
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                                text = "Kategoria: ${trickInfo.category}",
                                style = MaterialTheme.typography.bodyLarge
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        Text(
                                text = "Opis",
                                style = MaterialTheme.typography.labelMedium
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                                text = trickInfo.description,
                                style = MaterialTheme.typography.bodyLarge
                        )
                }
        }
}
