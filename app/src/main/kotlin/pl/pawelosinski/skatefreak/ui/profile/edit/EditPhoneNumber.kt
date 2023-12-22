package pl.pawelosinski.skatefreak.ui.profile.edit

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import pl.pawelosinski.skatefreak.local.loggedUser
import pl.pawelosinski.skatefreak.service.FirebaseAuthService
import pl.pawelosinski.skatefreak.service.FirebaseAuthService.Companion.phoneAuthUserData
import pl.pawelosinski.skatefreak.service.databaseService
import pl.pawelosinski.skatefreak.ui.auth.SaveButton


@Composable
fun PhoneChangeForm(firebaseAuthService: FirebaseAuthService, onComplete: () -> Unit = {}) {
//        val loginService = LoginService()
    var enabled : Boolean by remember {
        mutableStateOf(true)
    }
    var phoneInputEnabled : Boolean by remember {
        mutableStateOf(true)
    }
    val isAuthInProgress by remember {
        mutableStateOf(phoneAuthUserData.value.isAuthInProgress.value)
    }
    val isVerificationCompleted by remember {
        mutableStateOf(phoneAuthUserData.value.isVerificationCompleted)
    }
    val isUserLoggedIn by remember {
        mutableStateOf(phoneAuthUserData.value.isUserLoggedIn)
    }
    val storedVerificationId by remember {
        mutableStateOf(FirebaseAuthService.storedVerificationId.value)
    }
    var userPhoneNumber by remember {
        if (phoneAuthUserData.value.userPhoneNumber.value.isEmpty()) {
            if(loggedUser.value.phoneNumber.isEmpty()){
                mutableStateOf("+48")
            } else {
                mutableStateOf(loggedUser.value.phoneNumber)
            }
        } else {
            mutableStateOf(phoneAuthUserData.value.userPhoneNumber.value)
        }
    }
    val resendToken by remember {
        mutableStateOf(FirebaseAuthService.resendToken)
    }
    var verificationCode by remember { mutableStateOf("") }

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            OutlinedTextField(
                enabled = phoneInputEnabled,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp),
                value = userPhoneNumber,
                onValueChange = {
                    userPhoneNumber = if (it.length <= 3 && !it.startsWith("+48")) {
                        "+48".substring(0, it.length - 1)
                    } else {
                        it
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                label = { Text("Numer Telefonu (+48XXXXXXXXX)") },
                singleLine = true
            )
            SaveButton(
                enabled = enabled,
                onClick = {
                    Log.d(
                        "PhoneUpdateForm",
                        "isAuthInProgress: $isAuthInProgress" +
                                "\nisVerificationCompleted: $isVerificationCompleted" +
                                "\nisUserLoggedIn: $isUserLoggedIn" +
                                "\nstoredVerificationId: $storedVerificationId" +
                                "\nuserPhoneNumber: $userPhoneNumber" +
                                "\nresendToken: $resendToken"
                    )
                    if (
                        !isAuthInProgress &&
                        !isVerificationCompleted.value
                    ) {
                        firebaseAuthService.startPhoneNumberVerification(userPhoneNumber)
                    } else if (!isVerificationCompleted.value) {
                        if (verificationCode.matches(Regex("^\\d\\d\\d\\d\\d\\d$"))) {
                            firebaseAuthService.changePhoneNumberWithCode(
                                storedVerificationId,
                                verificationCode,
                                onComplete = {
                                    loggedUser.value.phoneNumber = userPhoneNumber
                                    phoneAuthUserData.value.isAuthInProgress.value = false
                                    phoneAuthUserData.value.isVerificationCompleted.value = false
                                    databaseService.updateUserData(
                                        onSuccess = {
                                            Toast.makeText(
                                                firebaseAuthService.currentActivity.value,
                                                "Numer telefonu został zmieniony",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            onComplete()
                                        },
                                        onFail = {
                                            Toast.makeText(
                                                firebaseAuthService.currentActivity.value,
                                                "Niepoprawny kod weryfikacyjny",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    )
                                },
                                onFail = {
                                    Toast.makeText(
                                        firebaseAuthService.currentActivity.value,
                                        "Nie udało się zmienić numeru telefonu.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            )
                        } else {
                            Toast.makeText(
                                firebaseAuthService.currentActivity.value,
                                "Niepoprawny kod weryfikacyjny",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                }
            })
        }

//        Log.d(FirebaseAuthService.PHONE_TAG, "isAuthInProgress: $isAuthInProgress")
        if (isAuthInProgress) {
            phoneInputEnabled = false
            val pattern = remember { Regex("^\\d?\\d?\\d?\\d?\\d?\\d?$") }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                OutlinedTextField(
                    enabled = true,
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp),
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
                ResendButton (
                    enabled = true
                ) {
                    firebaseAuthService.resendVerificationCode(userPhoneNumber, resendToken.value)
                }
            }
        }
    }
}

@Composable
fun ResendButton(
    enabled : Boolean = true,
    onClick: () -> Unit
) {
    IconButton(
        enabled = enabled,
        onClick = onClick
    ) {
        Icon(imageVector = Icons.Default.Refresh, contentDescription = null)
    }
}
