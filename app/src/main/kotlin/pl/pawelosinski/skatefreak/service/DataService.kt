package pl.pawelosinski.skatefreak.service

import android.content.ContentValues.TAG
import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import pl.pawelosinski.skatefreak.auth.loggedUser
import pl.pawelosinski.skatefreak.model.User

class DataService {

    private val database = Firebase.database


    fun changeUserData() {
        // [START write_message]
        // Write a message to the database
        val myRef = database.getReference("users/${loggedUser.firebaseId}")

        myRef.setValue(loggedUser)


        // [END write_message]

        // [START read_message]
        // Read from the database
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                val user = dataSnapshot.getValue(User::class.java)
                Log.d("ChangeUserData", "loggedUser is: $user")
                loggedUser = user ?: User.getUserFromFirebaseUser(Firebase.auth.currentUser)
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w("ChangeUserData", "Failed to read value.", error.toException())
            }
        })
        // [END read_message]
    }

    fun getUserById(id: String) : User {
        val myRef = database.getReference("users").child(id)
        var user : User? = null


        // Read from the database
        myRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                user = snapshot.getValue(User::class.java)
                loggedUser = user ?: User.getUserFromFirebaseUser(Firebase.auth.currentUser)
                Log.d(TAG, "loggedUser: $user")
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle possible errors.
                Log.w(TAG, "Failed to read value.", databaseError.toException())
            }
        })
        // [END read_message]


        return user ?: User.getUserFromFirebaseUser(Firebase.auth.currentUser)
    }

}