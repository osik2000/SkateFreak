package pl.pawelosinski.skatefreak.ui.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import pl.pawelosinski.skatefreak.R
import pl.pawelosinski.skatefreak.local.isDarkMode
import pl.pawelosinski.skatefreak.local.loggedUser
import pl.pawelosinski.skatefreak.ui.theme.SkateFreakTheme


@Composable
fun ProfileScreen(navController: NavController) {

    SkateFreakTheme (darkTheme = isDarkMode){
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(15.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                ScreenTitle(text = "Mój Profil")
                UserAvatar()
                UserDataTextRow("Imię i nazwisko", loggedUser.name)
                UserDataTextRow("Nick", loggedUser.nickname)
                UserDataTextRow("Email", loggedUser.email)
                UserDataTextRow("Numer Telefonu", loggedUser.phoneNumber)
                UserDataTextRow("Miasto", loggedUser.city)
            }
        }
    }
}

@Composable
fun UserAvatar() {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(loggedUser.photoUrl)
            .crossfade(true)
            .build(), // Ładujemy obraz z URL
        placeholder = painterResource(R.drawable.baseline_skateboarding_20), // Placeholder
        contentDescription = "Profile Picture",
        modifier = Modifier
            .width(200.dp)
            .height(200.dp)
            .clip(MaterialTheme.shapes.large),
        contentScale = ContentScale.Fit
    )
}

@Composable
fun UserDataTextRow(name: String = "", value: String = "") {
    Text(
        "${name}: ${value}",
        style = MaterialTheme.typography.bodyLarge,
        modifier = Modifier.padding(vertical = 20.dp)
    )
}

@Composable
fun ScreenTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleLarge,
        modifier = Modifier.padding(vertical = 20.dp)
    )
}

//@Composable
//fun ProfileScreen(navController: NavController) {
//    SkateFreakTheme (darkTheme = isDarkMode){
//        Surface(
//            modifier = Modifier.fillMaxSize(),
//            color = MaterialTheme.colorScheme.background
//        ) {
//            Column(
//                modifier = Modifier.fillMaxSize().padding(15.dp),
//                horizontalAlignment = Alignment.CenterHorizontally,
//                verticalArrangement = Arrangement.Center
//            ) {
//                Box(
//                    modifier = Modifier.fillMaxWidth()
//                        .height(200.dp)
//                        .padding(horizontal = 15.dp, vertical = 10.dp)
//                        .clip(MaterialTheme.shapes.large)
//                ) {
//                    Icon(
//                        imageVector = Icons.Default.Person,
//                        contentDescription = "profile_screen_bg",
//                        modifier = Modifier.fillMaxSize()
//                    )
//                }
//                Text(
//                    "Profile Screen",
//                    style = MaterialTheme.typography.titleLarge,
//                    modifier = Modifier.padding(vertical = 20.dp)
//                )
//            }
//        }
//    }
//}