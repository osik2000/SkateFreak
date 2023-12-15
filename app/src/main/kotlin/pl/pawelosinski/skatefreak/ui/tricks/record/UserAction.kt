package pl.pawelosinski.skatefreak.ui.tricks.record

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

/**
 * User action
 *
 * @param icon
 */
@Composable
fun UserAction(
    name: String = "UserAction",
    icon: ImageVector,
    colored: Boolean = false,
    color: Color = Color.White,
    onClick: () -> Unit = {}
) {
    Icon(
        imageVector = icon,
        tint = if(colored) color else Color.White,
        modifier = Modifier
            .clip(CircleShape)
            .background(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
            .padding(10.dp)
            .size(28.dp)
            .clickable {
                Log.d(name, "onClick called")
                onClick()
                       },
        contentDescription = null
    )
}