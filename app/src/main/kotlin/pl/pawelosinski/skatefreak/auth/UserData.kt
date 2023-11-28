package pl.pawelosinski.skatefreak.auth

import pl.pawelosinski.skatefreak.model.User


var loggedUser: User = User()

class PhoneAuthUserData {
    var isUserLoggedIn: Boolean = false
    var isVerificationCompleted: Boolean = false
    var isAuthInProgress: Boolean = false
    var userVerificationId: String = ""
    var userPhoneNumber: String = ""
    var userVerificationNumber: String = ""
}