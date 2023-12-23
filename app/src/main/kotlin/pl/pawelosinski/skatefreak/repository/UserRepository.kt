package pl.pawelosinski.skatefreak.repository

import android.util.Log
import pl.pawelosinski.skatefreak.local.allTrickRecordsCreators
import pl.pawelosinski.skatefreak.model.TrickRecord
import pl.pawelosinski.skatefreak.model.User
import pl.pawelosinski.skatefreak.service.DatabaseService

class UserRepository {

    companion object {
        fun getCreatorById(
            id: String,
            creatorsList: MutableList<User> = allTrickRecordsCreators,
            databaseService: DatabaseService = DatabaseService(),
            onSuccess: (User) -> Unit,
            onFail: () -> Unit = {}
        ) {
            // find trough allTrickRecordsCreators
            // if not found, get from database
            val existingCreator = creatorsList.find { it.firebaseId == id }
            if (existingCreator != null) {
                // get user from allTrickRecordsCreators
                Log.d("UserRepository", "getCreatorById: $id" +
                        "getting user from allTrickRecordsCreators...")
                onSuccess(existingCreator)
                return
            } else {
                Log.d("UserRepository", "getCreatorById: $id" +
                        "getting user from database...")
                // get from database
                databaseService.getUserById(id, onSuccess = {
//                    allTrickRecordsCreators.add(it)
                    onSuccess(it)
                }, onFail = {
                    onFail()
                })
            }
        }

        fun getAllTrickRecordCreators(allTrickRecords: MutableList<TrickRecord>, onSuccess: () -> Unit = {}) {
            Log.d("DataService", "getAllTrickRecordCreators")
            val trickRecordCreatorsList = mutableListOf<User>()

            for (trickRecord in allTrickRecords) {
                getCreatorById(trickRecord.userID, trickRecordCreatorsList, onSuccess = {
                    trickRecordCreatorsList.add(it)
                    if (trickRecordCreatorsList.size == allTrickRecords.size) {
                        allTrickRecordsCreators = trickRecordCreatorsList
                        onSuccess()
                    }
                }, onFail = {
                    Log.d("DataService", "getAllTrickRecordCreators: onFail")
                })
            }
        }
    }
}