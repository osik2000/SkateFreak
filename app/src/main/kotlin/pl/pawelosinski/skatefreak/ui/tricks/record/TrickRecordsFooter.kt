package pl.pawelosinski.skatefreak.ui.tricks.record

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import pl.pawelosinski.skatefreak.model.TrickRecord

/**
 * Spotlight footer
 *
 * @param trickRecord
 */
@Composable
fun TrickRecordsFooter(navController: NavController, trickRecord: TrickRecord) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(start = 18.dp, bottom = 18.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        FooterUserData(
            navController = navController,
            trickRecord = trickRecord,
            modifier = Modifier.weight(8f)
        )

        FooterUserAction(
            modifier = Modifier.weight(2f),
            trickRecord = trickRecord
        )
    }
}