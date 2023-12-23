package pl.pawelosinski.skatefreak.ui.tricks.info

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import pl.pawelosinski.skatefreak.local.allTrickInfo
import pl.pawelosinski.skatefreak.model.TrickInfo
import pl.pawelosinski.skatefreak.ui.profile.ScreenTitle

@Composable
fun TricksScreen(
    navController: NavController,
    title : String = "Lista trików",
    trickList: MutableList<TrickInfo> = allTrickInfo,
    onClick: (TrickInfo) -> Unit = {
        navController.navigate("tricks/${it.id}")
    }) {
    var sortByCategory by remember { mutableStateOf(true) }

    Column (
        modifier = Modifier
            .fillMaxSize()
            .padding(15.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        ScreenTitle(text = title)
        SortingOptions(
            onSortByCategorySelected = { sortByCategory = true },
            onSortByDifficultySelected = { sortByCategory = false }
        )

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
                    TrickTile(trick = trick, onClick = { onClick(trick) })
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

@Composable
fun SortingOptions(onSortByCategorySelected: () -> Unit, onSortByDifficultySelected: () -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    var selectedIndex by remember { mutableIntStateOf(0) }

    val options = listOf("Grupuj wg kategorii", "Grupuj wg trudności")

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text("Sortuj według: ")

        Box(
            modifier = Modifier.clickable {
                expanded = true
            }
        ) {
            Text(options[selectedIndex], color = MaterialTheme.colorScheme.primary)

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.background(MaterialTheme.colorScheme.background)
            ) {
                options.forEachIndexed { index, option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                        selectedIndex = index
                        expanded = false

                        when (index) {
                            0 -> onSortByCategorySelected()
                            1 -> onSortByDifficultySelected()
                        }
                    })
                }
            }
        }
    }
}