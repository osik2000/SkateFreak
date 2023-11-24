package pl.pawelosinski.skatefreak.auth

import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import pl.pawelosinski.skatefreak.model.User
import pl.pawelosinski.skatefreak.service.DataService


var loggedUser: User = Firebase.auth.currentUser.let {
    val dataService = DataService()
    dataService.getUserById(it?.uid ?: "")
    User.getUserFromFirebaseUser(it)
}

class PhoneAuthUserData {
    var isUserLoggedIn: Boolean = false
    var isVerificationCompleted: Boolean = false
    var isAuthInProgress: Boolean = false
    var userVerificationId: String = ""
    var userPhoneNumber: String = ""
    var userVerificationNumber: String = ""
}