package pl.pawelosinski.skatefreak.ui.common


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import pl.pawelosinski.skatefreak.R
import pl.pawelosinski.skatefreak.ui.theme.SkateFreakTheme

@Composable
fun RoundedIcon(
    resource: Int = R.drawable.baseline_skateboarding_24,
    borderColor: Color = MaterialTheme.colorScheme.primary,
    borderWidth: Dp = 2.dp,
    size: Dp = 120.dp
) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(borderColor, CircleShape)
    ) {
        Image(
            painter = painterResource(resource),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.onBackground, CircleShape)
                .border(borderWidth, borderColor, CircleShape)
                .padding(10.dp)
        )
    }
}

@Preview(showBackground = true, apiLevel = 33, showSystemUi = true)
@Composable
fun IconPrev() {
    SkateFreakTheme (darkTheme = true) {
        RoundedIcon()
    }
}