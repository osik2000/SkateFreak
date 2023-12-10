package pl.pawelosinski.skatefreak.model

import android.util.Log
import com.google.firebase.auth.FirebaseUser

data class User(
    val firebaseId: String = "",
    var name: String = "",
    var email: String = "",
    var phoneNumber: String = "",
    var photoUrl: String = "",
    var nickname: String = "",
    var city: String = "",
    var favoriteTrickRecords: List<String> = listOf(),
) {

    override fun toString(): String {
        return "User(firebaseId='$firebaseId',\n" +
                "name='$name',\n" +
                "email='$email',\n" +
                "phoneNumber='$phoneNumber',\n" +
                "photoUrl='$photoUrl',\n" +
                "nickname='$nickname',\n" +
                "city='$city')\n" +
                "favoriteTrickRecords=$favoriteTrickRecords"
    }

    fun checkRequiredData() : Boolean {
        val isUserDataSet = !(
                firebaseId.isEmpty() ||
                        name.isEmpty() ||
                        email.isEmpty() ||
                        phoneNumber.isEmpty() ||
                        nickname.isEmpty() ||
                        city.isEmpty()
                )
        Log.d("UserLog", "CheckingRequiredData: $isUserDataSet")
        return isUserDataSet
    }

    companion object {
        fun getUserFromFirebaseUser(firebaseUser: FirebaseUser?): User {
            if (firebaseUser == null || firebaseUser.uid.isEmpty()) {
                Log.d("User", "getUserFromFirebaseUser: firebaseUser is null")
            }
            return User(
                firebaseId = firebaseUser?.uid ?: "",
                photoUrl = firebaseUser?.photoUrl.toString(),
                nickname = "",
                email = firebaseUser?.email ?: "",
                name = firebaseUser?.displayName ?: "",
                phoneNumber = firebaseUser?.phoneNumber ?: "",
                city = "",
                favoriteTrickRecords = listOf()
            )
        }
    }

}