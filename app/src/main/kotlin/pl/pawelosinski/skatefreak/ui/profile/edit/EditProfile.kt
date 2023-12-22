package pl.pawelosinski.skatefreak.ui.profile.edit

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import pl.pawelosinski.skatefreak.local.isDarkMode
import pl.pawelosinski.skatefreak.ui.auth.ChangeUserDataScreen
import pl.pawelosinski.skatefreak.ui.common.Screens
import pl.pawelosinski.skatefreak.ui.theme.SkateFreakTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfile(navController: NavController) {
    SkateFreakTheme(darkTheme = isDarkMode) {
        Scaffold(
            topBar = {
                TopAppBar(title = { Text("Edytuj Profil") }, navigationIcon = {
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
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ProfileAvatarEditor()
                Spacer(Modifier.height(16.dp))

                Button(onClick = { navController.navigate(Screens.EditPhone.route) }) {
                    Text(text = "Zmień numer telefonu", fontSize = 18.sp)
                }

                Spacer(Modifier.height(16.dp))

                ChangeUserDataScreen(firstEdit = false)
            }
        }
    }
}


