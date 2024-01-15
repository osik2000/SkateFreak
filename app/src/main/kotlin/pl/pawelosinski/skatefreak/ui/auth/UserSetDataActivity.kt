package pl.pawelosinski.skatefreak.ui.auth

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.SaveAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import pl.pawelosinski.skatefreak.local.firebaseAuthService
import pl.pawelosinski.skatefreak.local.isDarkMode
import pl.pawelosinski.skatefreak.local.loggedUser
import pl.pawelosinski.skatefreak.model.User
import pl.pawelosinski.skatefreak.model.User.Companion.ACCOUNT_TYPE_PHONE
import pl.pawelosinski.skatefreak.service.DatabaseService
import pl.pawelosinski.skatefreak.service.databaseService
import pl.pawelosinski.skatefreak.ui.common.MyDivider
import pl.pawelosinski.skatefreak.ui.common.myToast
import pl.pawelosinski.skatefreak.ui.menu.MainMenuActivity
import pl.pawelosinski.skatefreak.ui.profile.edit.PhoneChangeForm
import pl.pawelosinski.skatefreak.ui.theme.SkateFreakTheme

class UserSetDataActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebaseAuthService.currentActivity.value = this
        setContent {
            SkateFreakTheme(darkTheme = isDarkMode) {
                // A surface container using the 'background' color from the theme
                Surface(
                    color = MaterialTheme.colorScheme.background,
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    ChangeUserDataScreen(firstEdit = loggedUser.value.accountType != ACCOUNT_TYPE_PHONE)
                }
            }
        }
    }
}

@SuppressLint("UnrememberedMutableState")
@Composable
fun ChangeUserDataScreen(firstEdit: Boolean = false) {
    val userData = loggedUser.value.copy()
    Log.d("todoremove", "ChangeUserDataScreen: userData = $userData")
    Log.d("todoremove", "ChangeUserDataScreen: loggeduser = ${loggedUser.value.nickname}")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
    ) {

        if (firstEdit) {
            SectionHeader()
            PhoneChangeForm(firebaseAuthService = firebaseAuthService)
            MyDivider()
        }
        ChangeField(
            label = "Nick",
            defaultValue = userData.nickname,
            onSave = {
                Log.d("todoremove", "ChangeUserDataScreen: userData111 przed = ${userData.nickname}")
                Log.d("todoremove", "ChangeUserDataScreen: loggeduser przed = ${loggedUser.value.nickname}")
                userData.nickname = it
                Log.d("todoremove", "ChangeUserDataScreen: userData111   po = ${userData.nickname}")
                Log.d("todoremove", "ChangeUserDataScreen: loggeduser   po = ${loggedUser.value.nickname}")
            }
        )
        MyDivider()
        ChangeField(
            label = "Imię i nazwisko",
            defaultValue = userData.name,
            onSave = {
                userData.name = it
            }
        )
        MyDivider()
        ChangeField(
            label = "Miasto",
            defaultValue = userData.city,
            onSave = {
                userData.city = it
            }
        )
        MyDivider()
        SaveAllButton(databaseService, LocalContext.current as ComponentActivity, userData)
    }
}

@Composable
private fun ChangeField(label: String, defaultValue: String, onSave: (String) -> Unit) {
    var value by remember { mutableStateOf(defaultValue) }
    var isInEditMode by remember { mutableStateOf(value.isEmpty()) }
    val context = LocalContext.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        OutlinedTextField(
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp),
            value = value,
            onValueChange = {
                value = it
            },
            label = { Text(label) },
            enabled = isInEditMode
        )
        if(!(loggedUser.value.accountType == ACCOUNT_TYPE_PHONE && label == "Numer Telefonu")) {
            if (isInEditMode) {
                SaveButton {
                    if (label == "Nick") {
                        val nick =  value.lowercase().replace("\\s".toRegex(), "").trim()
                        if(nick == loggedUser.value.nickname) {
                            isInEditMode = false
                            return@SaveButton
                        }
                        databaseService.checkIfNicknameExists(nick, onSuccess = {
                            value = nick
                            onSave(nick)
                            isInEditMode = false
                        }, onFail = {
                            Log.d("UserSetDataActivity", "Nickname already exists")
                            myToast(
                                context,
                                "Podany nick jest już zajęty"
                            )
                        })
                    } else {
                        value = value.trim()
                        onSave(value.trim())
                        isInEditMode = false
                    }
                }
            } else {
                EditButton(onClick = { isInEditMode = true})
            }
        }
    }
}

@Composable
private fun SectionHeader() {
    Text(
        text = "Tworzenie konta",
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
        textAlign = TextAlign.Center
    )
}



@Composable
fun SaveAllButton(databaseService: DatabaseService, componentActivity: ComponentActivity, userData: User) {
    val context = LocalContext.current
    Log.d("todoremove", "ChangeUserDataScreen: userData222 = $userData")
    Log.d("todoremove", "ChangeUserDataScreen: loggeduser = ${loggedUser.value.nickname}")

    Row (
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(16.dp) // Zaokrąglenie
            )
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.onBackground,
                shape = RoundedCornerShape(16.dp) // Zaokrąglenie obramowania
            )
            .padding(8.dp)
            .clickable {
                if (!userData.checkRequiredData()) {
                    Log.d("UserSetDataActivity", "User data is incomplete:\n${loggedUser.value}")
                    Toast
                        .makeText(context, "Uzupełnij wszystkie dane", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    Log.d("UserSetDataActivity", "User data is complete:\n${loggedUser.value}")
                    loggedUser.value = userData
                    databaseService.updateUserData(onSuccess = {
                        myToast(context, "Dane użytkownika zaaktualizowane")
                    }, onFail = {
                        Log.d("UserSetDataActivity", "User data update failed")
                        myToast(
                            context,
                            "Dane użytkownika nie zostały zaaktualizowane.\n Spróbuj ponownie."
                        )
                    })
                    //Navigate to MainMenuActivity
                    val intent = Intent(context, MainMenuActivity::class.java)
                    componentActivity.startActivity(intent)
                    componentActivity.finish()
                }
            },
    ) {
        Text(
            text = "Zapisz wszystkie dane",
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.padding(24.dp, 0.dp,0.dp,0.dp)

        )
        Icon(
            modifier = Modifier.padding(16.dp),
            imageVector = Icons.Default.SaveAlt,
            contentDescription = null
        )
    }
}

@Composable
fun EditButton(onClick: () -> Unit, enabled: Boolean = true) {
    IconButton(onClick = onClick, enabled = enabled) {
        Icon(imageVector = Icons.Default.Edit, contentDescription = null)
    }
}

@Composable
fun SaveButton(
    enabled : Boolean = true, onClick: () -> Unit) {
    IconButton(
        enabled = enabled,
        onClick = { onClick() }
    ) {
        Icon(imageVector = Icons.Default.Save, contentDescription = null)
    }
}
