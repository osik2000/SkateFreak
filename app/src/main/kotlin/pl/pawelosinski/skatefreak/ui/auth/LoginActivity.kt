package pl.pawelosinski.skatefreak.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.common.SignInButton
import pl.pawelosinski.skatefreak.auth.PhoneAuthUserData
import pl.pawelosinski.skatefreak.local.firebaseAuthService
import pl.pawelosinski.skatefreak.local.isDarkMode
import pl.pawelosinski.skatefreak.local.loggedUser
import pl.pawelosinski.skatefreak.service.FirebaseAuthService
import pl.pawelosinski.skatefreak.service.databaseService
import pl.pawelosinski.skatefreak.ui.common.myButtonModifier16dp
import pl.pawelosinski.skatefreak.ui.common.myButtonModifier8dp
import pl.pawelosinski.skatefreak.ui.menu.MainMenuActivity
import pl.pawelosinski.skatefreak.ui.theme.SkateFreakTheme

class LoginActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebaseAuthService = FirebaseAuthService(this)

        setContent {
            SkateFreakTheme(darkTheme = isDarkMode) {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                    ) {
                    LoginScreen(firebaseAuthService)
                }
            }
        }
    }
}

@Composable
fun LoginScreen(firebaseAuthService: FirebaseAuthService) {
    val isUserDataSet by remember {
        mutableStateOf(FirebaseAuthService.isUserDataSet)
    }
    val isUserLoggedIn by remember {
        mutableStateOf(FirebaseAuthService.isUserLoggedIn)
    }

    SkateFreakTheme(darkTheme = isDarkMode) {
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background,
            ) {
            Column(
                modifier = Modifier
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (isUserLoggedIn.value && loggedUser.value.firebaseId.isNotEmpty()) {
                    databaseService.setLoggedUserById(loggedUser.value.firebaseId)
                    Log.d("LoginActivity", "Before data check: isUserDataSet: $isUserDataSet")
                    if (!isUserDataSet.value) {
                        isUserDataSet.value = loggedUser.value.checkRequiredData()
                        Log.d("LoginActivity", "After data check: isUserDataSet: $isUserDataSet")
                    }

                    if (!isUserDataSet.value) {
                        Log.d("LoginActivity", "isUserDataSet: ${isUserDataSet.value}")
                        Text(
                            text = "Aby kontynuować, proszę uzupełnić dane profilu",
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                    } else if (loggedUser.value.name.isNotEmpty()) {
                        Text(
                            text = "Witaj ${loggedUser.value.name}",
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                    } else {
                        Text(
                            text = "Witaj ${loggedUser.value.phoneNumber}",
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                    }
                    if (isUserDataSet.value) {
                        // Button to go to LoggedUserMenuActivity
                        MainMenuButton(firebaseAuthService)
                    } else {
                        // Button to go to UserDataActivity
                        Button(
                            onClick = {
                                FirebaseAuthService.phoneAuthUserData.value = PhoneAuthUserData()
                                val intent = Intent(
                                    firebaseAuthService.currentActivity.value,
                                    UserSetDataActivity::class.java
                                )
                                firebaseAuthService.currentActivity.value.startActivity(intent)
                                firebaseAuthService.currentActivity.value.finish()
                            },
                            modifier = myButtonModifier16dp,
                            shape = MaterialTheme.shapes.small
                        ) {
                            Text("Uzupełnij dane użytkownika")
                        }
                    }
                    SignOutButton(signOut = { firebaseAuthService.signOut() })
                } else {
                    Text(
                        text = "Zaloguj się",
                        modifier = Modifier.padding(bottom = 32.dp),
                        style = MaterialTheme.typography.titleLarge
                    )
                    PhoneLoginForm(firebaseAuthService)
                    GoogleSignInButton(firebaseAuthService)
                }
            }
        }
    }
}

@Composable
fun MainMenuButton(firebaseAuthService: FirebaseAuthService) {
    val context = LocalContext.current
    Button(
        onClick = {
            val intent = Intent(context, MainMenuActivity::class.java)
            firebaseAuthService.currentActivity.value.startActivity(intent)
            firebaseAuthService.currentActivity.value.finish()
        },
        modifier = myButtonModifier16dp,
        shape = MaterialTheme.shapes.small
    ) {
        Text("Przejdź do menu głównego")
    }
}

@Composable
fun SignOutButton(signOut: () -> Unit = {}) {
    Button(
        modifier = myButtonModifier16dp,
        onClick = {
            signOut()
        },
        shape = MaterialTheme.shapes.small
    ) {
        Text(text = "Wyloguj się", fontSize = 18.sp)
    }
}


@Composable
fun GoogleSignInButton(firebaseAuthService: FirebaseAuthService) {
    // Reference to the GoogleSignInClient from the activity
    AndroidView(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 0.dp, top = 8.dp, start = 16.dp, end = 16.dp)
            .border(1.dp, MaterialTheme.colorScheme.onSurface, MaterialTheme.shapes.small),
        factory = { context ->
            SignInButton(context).apply {
                setSize(SignInButton.SIZE_WIDE)
            }
        },
        update = { signInButton ->
            signInButton.setOnClickListener {
                // Start the sign-in process when the Google sign-in button is clicked
                firebaseAuthService.googleSignIn()
            }
        }
    )
}


@Composable
fun PhoneLoginForm(firebaseAuthService: FirebaseAuthService) {
    val isAuthInProgress by remember {
        mutableStateOf(FirebaseAuthService.phoneAuthUserData.value.isAuthInProgress.value)
    }
    val isVerificationCompleted by remember {
        mutableStateOf(FirebaseAuthService.phoneAuthUserData.value.isVerificationCompleted)
    }
    val isUserLoggedIn by remember {
        mutableStateOf(FirebaseAuthService.phoneAuthUserData.value.isUserLoggedIn)
    }
    val storedVerificationId by remember {
        mutableStateOf(FirebaseAuthService.storedVerificationId.value)
    }
    var userPhoneNumber by remember {
        if (FirebaseAuthService.phoneAuthUserData.value.userPhoneNumber.value.isEmpty()) {
            mutableStateOf("+48")
        } else {
            mutableStateOf(FirebaseAuthService.phoneAuthUserData.value.userPhoneNumber.value)
        }
    }
    val resendToken by remember {
        mutableStateOf(FirebaseAuthService.resendToken)
    }
    var verificationCode by remember { mutableStateOf("") }
    Column(
        modifier = Modifier
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = userPhoneNumber,
            onValueChange = {
                userPhoneNumber = it
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            label = { Text("Numer telefonu (+48XXXXXXXXX)") },
            singleLine = true
        )
        Log.d(FirebaseAuthService.PHONE_TAG, "isAuthInProgress: $isAuthInProgress")
        if (isAuthInProgress) {
            val pattern = remember { Regex("^\\d?\\d?\\d?\\d?\\d?\\d?$") }
            OutlinedTextField(
                value = verificationCode,
                onValueChange = {
                    if (it.isEmpty() || it.matches(pattern)) {
                        verificationCode = it
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                label = { Text("Kod Weryfikacyjny SMS") },
                singleLine = true
            )
        }

        Button(
            onClick = {
                if (
                    !isAuthInProgress &&
                    !isVerificationCompleted.value &&
                    !isUserLoggedIn.value
                ) {
                    firebaseAuthService.startPhoneNumberVerification(userPhoneNumber)
                } else if (!isVerificationCompleted.value) {
                    if (verificationCode.matches(Regex("^\\d\\d\\d\\d\\d\\d$"))) {
                        firebaseAuthService.verifyPhoneNumberWithCode(
                            storedVerificationId,
                            verificationCode
                        )
                    } else {
                        Toast.makeText(
                            firebaseAuthService.currentActivity.value,
                            "Niepoprawny kod weryfikacyjny",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            },
            modifier = myButtonModifier8dp,
            shape = MaterialTheme.shapes.small
        ) {
            if(!isAuthInProgress){
                Text("Zaloguj się")
            } else {
                Text("Zweryfikuj kod")
            }
        }

        if(isAuthInProgress) {
            Button(
                onClick = {
                    firebaseAuthService.resendVerificationCode(userPhoneNumber, resendToken.value)
                },
                modifier = myButtonModifier8dp,
                shape = MaterialTheme.shapes.small
            ) {
                Text("Wyślij kod ponownie")
            }
        }
    }
}

