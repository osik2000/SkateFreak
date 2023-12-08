package pl.pawelosinski.skatefreak.auth

class PhoneAuthUserData {
    var isUserLoggedIn: Boolean = false
    var isVerificationCompleted: Boolean = false
    var isAuthInProgress: Boolean = false
    var userVerificationId: String = ""
    var userPhoneNumber: String = ""
    var userVerificationNumber: String = ""
}