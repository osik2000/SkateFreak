package pl.pawelosinski.skatefreak.model

import com.google.firebase.auth.FirebaseUser

class User(
    val firebaseId: String,
    var name: String,
    var email: String,
    var phoneNumber: String,
    var photoUrl: String,
    val nickname: String,
    var city: String
) {

    override fun toString(): String {
        return "User(firebaseId='$firebaseId', " +
                "name='$name', " +
                "email='$email', " +
                "phoneNumber='$phoneNumber', " +
                "photoUrl='$photoUrl', " +
                "nickname='$nickname', " +
                "city='$city')"
    }

    fun checkRequiredData() : Boolean {
        return !(firebaseId.isEmpty() ||
                name.isEmpty() ||
                email.isEmpty() ||
                phoneNumber.isEmpty() ||
                nickname.isEmpty()
                )
    }

    companion object {
        fun getUserFromFirebaseUser(firebaseUser: FirebaseUser?): User {
            if (firebaseUser == null || firebaseUser.uid.isEmpty()) {
                throw NoSuchElementException("FirebaseUser is null")
            }
            else {
                return User(
                    firebaseId = firebaseUser?.uid ?: "",
                    name = firebaseUser?.displayName ?: "",
                    email = firebaseUser?.email ?: "",
                    phoneNumber = firebaseUser?.phoneNumber ?: "",
                    photoUrl = firebaseUser?.photoUrl.toString(),
                    nickname = firebaseUser?.displayName ?: "",
                    city = ""
                )
            }
        }
    }

}

fun FirebaseUser.toUser(firebaseUser: FirebaseUser): User {
    return User(
        firebaseId = firebaseUser.uid,
        name = firebaseUser.displayName ?: "",
        email = firebaseUser.email ?: "",
        phoneNumber = firebaseUser.phoneNumber ?: "",
        photoUrl = firebaseUser.photoUrl.toString(),
        nickname = firebaseUser.displayName ?: "",
        city = ""
    )
}