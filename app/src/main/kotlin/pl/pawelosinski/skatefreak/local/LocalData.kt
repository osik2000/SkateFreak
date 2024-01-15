package pl.pawelosinski.skatefreak.local

import androidx.compose.runtime.mutableStateOf
import pl.pawelosinski.skatefreak.model.TrickInfo
import pl.pawelosinski.skatefreak.model.TrickRecord
import pl.pawelosinski.skatefreak.model.User
import pl.pawelosinski.skatefreak.service.FirebaseAuthService

var loggedUser = mutableStateOf(User())
var isDarkMode = true
var allTrickInfo = mutableListOf<TrickInfo>()
var allTrickRecords = mutableListOf<TrickRecord>()
var allTrickRecordsCreators = mutableListOf<User>()
lateinit var firebaseAuthService: FirebaseAuthService
