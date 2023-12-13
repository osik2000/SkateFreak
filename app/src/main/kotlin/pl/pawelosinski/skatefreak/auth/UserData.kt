package pl.pawelosinski.skatefreak.auth

import androidx.compose.runtime.mutableStateOf

class PhoneAuthUserData {
    var isUserLoggedIn = mutableStateOf(false)
    var isVerificationCompleted = mutableStateOf(false)
    var isAuthInProgress = mutableStateOf(false)
    var userVerificationId = mutableStateOf("")
    var userPhoneNumber = mutableStateOf("")
    var userVerificationNumber = mutableStateOf("")
}