package pl.pawelosinski.skatefreak.service

class LoginService {
    private var isUserLoggedIn: Boolean = false

    fun login(email: String, password: String): Boolean {
        isUserLoggedIn = (email.isNotEmpty() && password.isNotEmpty())
        return isUserLoggedIn
    }
}