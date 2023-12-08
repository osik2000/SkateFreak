package pl.pawelosinski.skatefreak.service

import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import pl.pawelosinski.skatefreak.local.loggedUser
import pl.pawelosinski.skatefreak.model.User


class DatabaseService {

    private val database = Firebase.database


    fun changeUserData() {
        // Write a message to the database
        val myRef = database.getReference("users/${loggedUser.firebaseId}")
        myRef.setValue(loggedUser).addOnSuccessListener {
            Log.d("DataService", "User Data saved successfully.")
        }.addOnFailureListener() {
            Log.e("DataService", "Error writing data", it)
        }
    }

    fun getUserById(id: String, onSuccess : () -> Unit = {}, onFail : () -> Unit = {}) {
        Log.d("DataService", "getUserById: $id")

        database.getReference("users").child(id).get().addOnSuccessListener {
            loggedUser = it.getValue(User::class.java) ?: User.getUserFromFirebaseUser(Firebase.auth.currentUser)
            Log.d("DataService", "Got value $loggedUser")
            onSuccess()
        }.addOnFailureListener{
            Log.e("DataService", "Error getting data", it)
            loggedUser = User.getUserFromFirebaseUser(Firebase.auth.currentUser)
            onFail()
        }
    }

//    fun getUserByPhoneNumber(phoneNumber: String) : User {
//        val myRef = database.getReference("users")
//        var user: User? = null
//
//        myRef.orderByChild("phoneNumber").equalTo(phoneNumber).addListenerForSingleValueEvent(object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                user = snapshot.getValue(User::class.java)
//                loggedUser = user ?: User.getUserFromFirebaseUser(Firebase.auth.currentUser)
//                Log.d(TAG, "loggedUser: $user")
//            }
//
//            override fun onCancelled(databaseError: DatabaseError) {
//                // Handle possible errors.
//                Log.w(TAG, "Failed to read value.", databaseError.toException())
//            }
//        })
//
//        return user ?: User.getUserFromFirebaseUser(Firebase.auth.currentUser)
//    }


}