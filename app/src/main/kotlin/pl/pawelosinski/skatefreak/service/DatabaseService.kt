package pl.pawelosinski.skatefreak.service

import android.net.Uri
import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import pl.pawelosinski.skatefreak.local.allTrickInfo
import pl.pawelosinski.skatefreak.local.allTrickRecords
import pl.pawelosinski.skatefreak.local.loggedUser
import pl.pawelosinski.skatefreak.model.TrickInfo
import pl.pawelosinski.skatefreak.model.TrickRecord
import pl.pawelosinski.skatefreak.model.User
import pl.pawelosinski.skatefreak.ui.common.deleteTempFile
import java.io.File


lateinit var databaseService: DatabaseService

class DatabaseService {

    private val database = Firebase.database
    private val storage = Firebase.storage


    fun updateUserData(onSuccess: () -> Unit = {}, onFail: () -> Unit = {}) {
        // Write a message to the database
        val myRef = database.getReference("users/${loggedUser.value.firebaseId}")
        myRef.setValue(loggedUser.value).addOnSuccessListener {
            Log.d("DataService", "User Data saved successfully.")
            onSuccess()
        }.addOnFailureListener {
            Log.e("DataService", "Error writing data", it)
            onFail()
        }
    }

    fun setLoggedUserById(id: String, onSuccess: () -> Unit = {}, onFail: () -> Unit = {}) {
        Log.d("DataService", "getUserById: $id")

        if(id.isEmpty()) {
            onFail()
            return
        }

        database.getReference("users").child(id).get().addOnSuccessListener { dataSnapshot ->
            Log.d("DataService", "[setLoggedUserById] \nGot value ${dataSnapshot.getValue(User::class.java)}")
            dataSnapshot.getValue(User::class.java)?.let {
                loggedUser.value = it
                onSuccess()
                return@addOnSuccessListener
            }
            onFail()
        }.addOnFailureListener {
            Log.e("DataService", "Error getting data", it)
            onFail()
        }
    }

    fun getUserById(id: String, onSuccess: (User) -> Unit = { User() }, onFail: () -> Unit = {}) {
        Log.d("DataService", "getUserById: $id")

        var user: User
        database.getReference("users").child(id).get().addOnSuccessListener {
            Log.d("DataService", "[getUserById] \nGot value ${it.getValue(User::class.java)}")
            user = it.getValue(User::class.java) ?: User.getUserFromFirebaseUser(
                Firebase.auth.currentUser,
                loggedUser.value.accountType
            )
            onSuccess(user)
        }.addOnFailureListener {
            Log.e("DataService", "Error getting data", it)
            user = User.getUserFromFirebaseUser(
                Firebase.auth.currentUser,
                loggedUser.value.accountType
            )
            onFail()
        }
    }

    fun getAllTrickRecords(
        onSuccess: (MutableList<TrickRecord>) -> Unit = {},
        onFail: () -> Unit = {}
    ) {
        Log.d("DataService", "getAllTrickRecords")
        val trickRecordsRef = database.getReference("tricks/records")
        val trickRecordList = mutableListOf<TrickRecord>()

        trickRecordsRef.get().addOnSuccessListener {
            for (postSnapshot in it.children) {
                val trickRecord = postSnapshot.getValue(TrickRecord::class.java)

                if (trickRecord != null) {
                    trickRecordList.add(trickRecord)
                }
            }
            onSuccess(trickRecordList)
        }.addOnFailureListener {
            Log.e("DataService", "Error getting data", it)
            onFail()
        }
    }

    fun getAllTrickInfo(onSuccess: (MutableList<TrickInfo>) -> Unit = {}, onFail: () -> Unit = {}) {
        Log.d("DataService", "getAllTrickRecords")
        val trickInfoRef = database.getReference("tricks/info")
        val trickInfoList = mutableListOf<TrickInfo>()

        trickInfoRef.get().addOnSuccessListener {
            for (postSnapshot in it.children) {
                val trickInfo = postSnapshot.getValue(TrickInfo::class.java)

                if (trickInfo != null) {
                    trickInfoList.add(trickInfo)
                }
            }
            onSuccess(trickInfoList)
        }.addOnFailureListener {
            Log.e("DataService", "Error getting data", it)
            onFail()
        }
    }

    fun getTrickInfo(
        id: String,
        onSuccess: () -> Unit = {},
        onFail: () -> Unit = {}
    ): TrickInfo {
        Log.d("DataService", "getTrickInfo: $id")
        var trickInfo = TrickInfo()

        database.getReference("tricks/info").child(id).get().addOnSuccessListener {
            trickInfo = it.getValue(TrickInfo::class.java) ?: TrickInfo()
            Log.d("DataService", "Got value $trickInfo")
            onSuccess()
        }.addOnFailureListener {
            Log.e("DataService", "Error getting data", it)
            onFail()
        }
        return trickInfo
    }


    fun setDefaultTrickInfo() {
        // Write a message to the database

        val tricksMap = allTrickInfo.associateBy { it.id }

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

    fun addTrickRecordToFavorites(
        trickRecord: TrickRecord,
        onSuccess: (String) -> Unit = {}
    ) {
        val userID = loggedUser.value.firebaseId
        val userFavoritesRef = database.getReference("users/$userID/favoriteTrickRecords")
        val trickRecordUsersWhoSetAsFavoriteRef =
            database.getReference("tricks/records/${trickRecord.id}/usersWhoSetAsFavorite")

        if (trickRecord.usersWhoSetAsFavorite.contains(userID) && loggedUser.value.favoriteTrickRecords.contains(
                trickRecord.id
            )
        ) {
            Log.d(
                "DataService",
                "Trick record already in favorites. Removing this one from favorites"
            )
            loggedUser.value.favoriteTrickRecords.remove(trickRecord.id)
            userFavoritesRef.setValue(loggedUser.value.favoriteTrickRecords).addOnSuccessListener {
                Log.d("DataService", "Trick record removed from favorites. (userFavoritesRef)")
                trickRecord.usersWhoSetAsFavorite.remove(userID)
                trickRecord.favoriteCounter.intValue--
                trickRecordUsersWhoSetAsFavoriteRef.setValue(trickRecord.usersWhoSetAsFavorite)
                    .addOnSuccessListener {
                        Log.d(
                            "DataService",
                            "Trick record removed from favorites. (trickRecordusersWhoSetAsFavoriteRef)"
                        )
                        onSuccess("Usunięto z ulubionych.")
                    }.addOnFailureListener {
                    Log.e(
                        "DataService",
                        "Trick has not been removed from favorites (trickRecordusersWhoSetAsFavoriteRef)",
                        it
                    )
                }
            }.addOnFailureListener {
                Log.e(
                    "DataService",
                    "Trick has not been removed from favorites (userFavoritesRef)",
                    it
                )
            }
        } else {
            Log.d("DataService", "Trick record not in favorites. Adding this one to favorites")
            loggedUser.value.favoriteTrickRecords.add(trickRecord.id)
            userFavoritesRef.setValue(loggedUser.value.favoriteTrickRecords).addOnSuccessListener {
                Log.d("DataService", "Trick record added to favorites. (userFavoritesRef)")
                trickRecord.usersWhoSetAsFavorite.add(userID)
                trickRecord.favoriteCounter.intValue++
                trickRecordUsersWhoSetAsFavoriteRef.setValue(trickRecord.usersWhoSetAsFavorite)
                    .addOnSuccessListener {
                        Log.d(
                            "DataService",
                            "Trick record added to favorites. (trickRecordusersWhoSetAsFavoriteRef)"
                        )
                        onSuccess("Dodano do ulubionych.")
                    }.addOnFailureListener {
                    Log.e(
                        "DataService",
                        "Trick has not been added to favorites (trickRecordusersWhoSetAsFavoriteRef)",
                        it
                    )
                }
            }.addOnFailureListener {
                Log.e("DataService", "Trick has not been added to favorites (userFavoritesRef)", it)
            }
        }
    }


    fun uploadUserAvatar(
        userID: String,
        file: Uri,
        onComplete: (String) -> Unit = {},
        onFail: () -> Unit = {}
    ) {
        val storageRef = storage.reference
        val fileExtension = file.toString().substring(file.toString().lastIndexOf("."))
        val avatarRef = storageRef.child("userAvatar/$userID$fileExtension")

        val uploadTask = avatarRef.putFile(file)
        uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            avatarRef.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("DataService", "Avatar URL: ${task.result}")
                val downloadUri = task.result
                loggedUser.value.photoUrl = downloadUri.toString()
                onComplete(downloadUri.toString())
                updateUserData()
            } else {
                Log.d("DataService", "Avatar URL failed: ${task.exception}")
                onFail()
            }
        }
    }

    private fun uploadTrickRecordVideo(
        userID: String,
        trickRecordID: String,
        file: Uri,
        onComplete: (String) -> Unit = {},
        onFail: () -> Unit = {}
    ) {
        val storageRef = storage.reference
        val fileExtension = file.toString().substring(file.toString().lastIndexOf("."))
        val videoRef = storageRef.child("trickRecord/video/$userID/$trickRecordID$fileExtension")

        val uploadTask = videoRef.putFile(file)
        uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            videoRef.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("DataService", "Video URL: ${task.result}")
                val downloadUri = task.result
                TrickRecord.localFileUri.value = ""
                TrickRecord.trimmedVideoPath.value = ""
                onComplete(downloadUri.toString())
            } else {
                Log.d("DataService", "Avatar URL failed: ${task.exception}")
                onFail()
            }
        }
    }

    fun uploadTrickRecord(record: TrickRecord, onSuccess: () -> Unit = {}, onFail: () -> Unit = {}) {
        val localFile = record.videoUrl
        val databaseReference = database.getReference("your_collection_name")
        val uniqueId = databaseReference.push().key
        if(uniqueId == null) {
            Log.e("DataService", "Error writing data. Unique ID is null")
            return
        }
        record.id = uniqueId
        val myRef = database.getReference("tricks/records/${record.id}")


        val fileUri = Uri.fromFile(File(record.videoUrl))

        uploadTrickRecordVideo(record.userID, record.id, fileUri, onComplete = { videoUrl ->
            deleteTempFile(localFile)
            record.videoUrl = videoUrl
            myRef.setValue(record).addOnSuccessListener {
                Log.d("DataService", "Trick record saved successfully.")
                onSuccess()
            }.addOnFailureListener {
                Log.e("DataService", "Error writing data", it)
                onFail()
            }
        }, onFail = {
            Log.e("DataService", "Error writing data")
            onFail()
        })


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