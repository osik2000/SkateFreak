package pl.pawelosinski.skatefreak.ui.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Skateboarding
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import pl.pawelosinski.skatefreak.R
import pl.pawelosinski.skatefreak.local.isDarkMode
import pl.pawelosinski.skatefreak.local.loggedUser
import pl.pawelosinski.skatefreak.model.User
import pl.pawelosinski.skatefreak.ui.common.Screens
import pl.pawelosinski.skatefreak.ui.common.avatarModifier
import pl.pawelosinski.skatefreak.ui.theme.SkateFreakTheme

@Composable
fun ProfileScreen(user: User = loggedUser.value, navController: NavController) {
    val isMyProfile = user.firebaseId == loggedUser.value.firebaseId

    val isPrivateOrEmpty = (!user.isPublic && !isMyProfile) || user == User()

    SkateFreakTheme(darkTheme = isDarkMode) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                ScreenTitle(text = if(isMyProfile) "Mój Profil" else "Profil użytkownika")
                Spacer(Modifier.height(16.dp))
                UserAvatar(user = user)
                Spacer(Modifier.height(16.dp))
                if (isPrivateOrEmpty) {
                    Text(
                        text = "Profil prywatny lub nie istnieje",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                }
                else {
                    UserDataSection(user = user)
                    Spacer(Modifier.height(16.dp))
                    if (isMyProfile){
                        EditProfileButton(navController)
                    }
                }
            }
        }
    }
}

@Composable
fun UserAvatar(user: User = loggedUser.value) {
    val isDefaultAvatar = user.photoUrl == "null" || user.photoUrl.isEmpty()


    if (isDefaultAvatar) {
        // Używamy Icon dla domyślnego awatara
        Icon(
            imageVector = Icons.Filled.Skateboarding,
            contentDescription = "Profile Picture",
            tint = MaterialTheme.colorScheme.onBackground,
            modifier = avatarModifier(borderColor = MaterialTheme.colorScheme.primary)
        )
    } else {
        // Używamy AsyncImage dla awatara użytkownika
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(user.photoUrl)
                .crossfade(true)
                .build(),
            placeholder = painterResource(R.drawable.rounded_person_24),
            contentDescription = "Profile Picture",
            modifier = avatarModifier(borderColor = MaterialTheme.colorScheme.primary),
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
fun UserDataSection(user: User = loggedUser.value) {
    Column(
        horizontalAlignment = Alignment.Start,
        modifier = Modifier.fillMaxWidth()
    ) {
        UserDataTextRow("Nick", user.nickname)
        UserDataTextRow("Imię i nazwisko", user.name)
        UserDataTextRow("Email", user.email)
        UserDataTextRow("Numer Telefonu", user.phoneNumber)
        UserDataTextRow("Miasto", user.city)
        UserDataTextRow(
            "Profil:",
            if(user.isPublic) "Publiczny" else "Prywatny"
        )
    }
}

@Composable
fun UserDataTextRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
fun EditProfileButton(navController: NavController) {
    Button(
        onClick = { navController.navigate(Screens.EditProfile.route) },
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("Edytuj Profil", fontSize = 18.sp)
    }
}

@Composable
fun ScreenTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.headlineMedium,
        modifier = Modifier.padding(vertical = 20.dp)
    )
}
