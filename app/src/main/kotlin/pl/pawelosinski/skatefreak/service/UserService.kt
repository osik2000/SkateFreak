package pl.pawelosinski.skatefreak.service

import android.util.Log
import pl.pawelosinski.skatefreak.local.loggedUser
import pl.pawelosinski.skatefreak.ui.common.myToast

class UserService {
    companion object {
        fun setUserPublicProfile(isPublic: Boolean) {
            loggedUser.value.isPublic = isPublic
            databaseService.updateUserData(
                onSuccess = {
                    Log.d("UserService", "setUserPublicProfile: onSuccess \n" +
                            "isPublic = $isPublic")
                },
            )
        }
    }
}