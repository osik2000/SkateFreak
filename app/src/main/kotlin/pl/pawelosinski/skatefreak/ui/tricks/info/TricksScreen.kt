package pl.pawelosinski.skatefreak.ui.tricks.info

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import pl.pawelosinski.skatefreak.local.allTrickInfo
import pl.pawelosinski.skatefreak.model.TrickInfo

@Composable
fun TricksScreen(navController: NavController, trickList: MutableList<TrickInfo> = allTrickInfo) {
    var sortByCategory by remember { mutableStateOf(true) }

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(onClick = { sortByCategory = true }) {
                Text("Grupuj według kategorii")
            }
            Button(onClick = { sortByCategory = false }) {
                Text("Grupuj według poziomu trudności")
            }
        }

        LazyColumn {
            val groupedTricks = if (sortByCategory) {
                trickList.groupBy { it.category }
            } else {
                trickList.groupBy { it.difficulty }
            }

            groupedTricks.forEach { (group, tricks) ->
                item {
                    Text(
                        text = group,
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.padding(16.dp)
                    )
                }
                items(tricks) { trick ->
                    TrickTile(trick = trick) {
                        navController.navigate("trickInfo/${trick.id}")
                    }
                }
            }
        }
    }
}

@Composable
fun TrickTile(trick: TrickInfo, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Tutaj można dodać obrazek, jeśli potrzebny
            Spacer(modifier = Modifier.width(8.dp))
            Text(trick.name)
        }
    }
}