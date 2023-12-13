package pl.pawelosinski.skatefreak.service

import android.util.Log
import pl.pawelosinski.skatefreak.local.loggedUser

class TricksService {
    companion object {
        fun addRecordToFavorites(recordID: String) {
            if (loggedUser.value.firebaseId.isEmpty()) {
                Log.e("TricksService", "User is not logged in")

            }
            else {
                if (loggedUser.value.favoriteTrickRecords.contains(recordID)) {
                    loggedUser.value.favoriteTrickRecords.remove(recordID)
                }
                else
                {
                    loggedUser.value.favoriteTrickRecords.add(recordID)
                }
                DatabaseService().updateUserData()
            }
        }
    }
}