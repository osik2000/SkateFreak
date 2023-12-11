package pl.pawelosinski.skatefreak.service

import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import pl.pawelosinski.skatefreak.local.allTrickRecords
import pl.pawelosinski.skatefreak.local.allTricks
import pl.pawelosinski.skatefreak.local.loggedUser
import pl.pawelosinski.skatefreak.model.TrickInfo
import pl.pawelosinski.skatefreak.model.User

lateinit var databaseService: DatabaseService

class DatabaseService {

    private val database = Firebase.database
    private val storage = Firebase.storage


    fun changeUserData() {
        // Write a message to the database
        val myRef = database.getReference("users/${loggedUser.firebaseId}")
        myRef.setValue(loggedUser).addOnSuccessListener {
            Log.d("DataService", "User Data saved successfully.")
        }.addOnFailureListener {
            Log.e("DataService", "Error writing data", it)
        }
    }

    fun setLoggedUserById(id: String, onSuccess : () -> Unit = {}, onFail : () -> Unit = {}) {
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

    fun getUserById(id: String, onSuccess : (User) -> Unit = {User()}, onFail : () -> Unit = {}) : User {
        Log.d("DataService", "getUserById: $id")

        var user = User()
        database.getReference("users").child(id).get().addOnSuccessListener {
            user = it.getValue(User::class.java) ?: User.getUserFromFirebaseUser(Firebase.auth.currentUser)
            Log.d("DataService", "Got value $loggedUser")
            onSuccess(user)
        }.addOnFailureListener{
            Log.e("DataService", "Error getting data", it)
            user = User.getUserFromFirebaseUser(Firebase.auth.currentUser)
            onFail()
        }
        return user
    }

    fun getTrickInfo(
        id: String,
        onSuccess : () -> Unit = {},
        onFail : () -> Unit = {}
    ) : TrickInfo {
        Log.d("DataService", "getTrickInfo: $id")
        var trickInfo = TrickInfo()

        database.getReference("tricks/info").child(id).get().addOnSuccessListener {
            trickInfo = it.getValue(TrickInfo::class.java) ?: TrickInfo()
            Log.d("DataService", "Got value $trickInfo")
            onSuccess()
        }.addOnFailureListener{
            Log.e("DataService", "Error getting data", it)
            onFail()
        }
        return trickInfo
    }


    fun setDefaultTrickInfo() {
        // Write a message to the database

        val tricksMap = allTricks.associateBy { it.id }

        val myRef = database.getReference("tricks/info/")
        myRef.setValue(tricksMap).addOnSuccessListener {
            Log.d("DataService", "template tricks saved successfully.")
        }.addOnFailureListener {
            Log.e("DataService", "Error writing template tricks", it)
        }
    }
    fun setDefaultTrickRecord() {
        // Write a message to the database

        val trickRecordsMap = allTrickRecords.associateBy { it.id }

        val myRef = database.getReference("tricks/records/")
        myRef.setValue(trickRecordsMap).addOnSuccessListener {
            Log.d("DataService", "template trick records saved successfully.")
        }.addOnFailureListener {
            Log.e("DataService", "Error writing template trick records", it)
        }
    }

    fun getUrlOfStorageFile(path: String) {
        val storageRef = storage.reference

        storageRef.child(path).downloadUrl.addOnSuccessListener {
            // Got the download URL for 'users/me/profile.png'
            Log.d("DataService", "Got the download URL for $path: $it")
        }.addOnFailureListener {
            // Handle any errors
            Log.e("DataService", "Error getting download URL for $path", it)
        }
    }

    fun addTrickRecordToFavorites(userID: String, trickRecordID: String, onSuccess: (String) -> Unit = {}){
        val myRef = database.getReference("users/$userID/favoriteTrickRecords")
        Log.d("DataService", "addTrickRecordToFavorites to users/$userID/favoriteTrickRecords")

        // Read current favorites list
        myRef.get().addOnSuccessListener { dataSnapshot ->
            val favoritesList =
                dataSnapshot.children.mapNotNull { it.getValue(String::class.java) }.toMutableList()
            Log.d("DataService", "Got value $favoritesList")

            // if trick record is already in favorites, remove it from favorites
            if (favoritesList.contains(trickRecordID)) {
                Log.d("DataService", "Trick record already in favorites. Removing this one from favorites")
                favoritesList.remove(trickRecordID)
                myRef.setValue(favoritesList).addOnSuccessListener {
                    Log.d("DataService", "Trick record removed from favorites.")
                    onSuccess("Usunięto z ulubionych.")
                }.addOnFailureListener {
                    Log.e("DataService", "Trick has not been removed from favorites", it)
                }
                return@addOnSuccessListener
            }

            // if trick record is not in favorites, add it to favorites

            favoritesList.add(trickRecordID)
            myRef.setValue(favoritesList).addOnSuccessListener {
                Log.d("DataService", "Trick record added to favorites successfully.")
                onSuccess("Dodano do ulubionych.")
            }.addOnFailureListener {
                Log.e("DataService", "Error adding trick record to favorites", it)
            }
        }.addOnFailureListener {
            Log.e("DataService", "Error getting data.", it)
//            myRef.setValue(listOf(trickRecordID)).addOnSuccessListener {
//                Log.d("DataService", "First Trick record added to favorites successfully.")
//            }.addOnFailureListener {
//                Log.e("DataService", "Error adding trick record to favorites", it)
//            }
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