package pl.pawelosinski.skatefreak

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import pl.pawelosinski.skatefreak.local.LocalDataInit
import pl.pawelosinski.skatefreak.local.allTrickInfo
import pl.pawelosinski.skatefreak.local.allTrickRecords
import pl.pawelosinski.skatefreak.local.isDarkMode
import pl.pawelosinski.skatefreak.local.loggedUser
import pl.pawelosinski.skatefreak.repository.UserRepository
import pl.pawelosinski.skatefreak.service.DatabaseService
import pl.pawelosinski.skatefreak.service.databaseService
import pl.pawelosinski.skatefreak.ui.auth.LoginActivity
import pl.pawelosinski.skatefreak.ui.common.LoadingAnimation
import pl.pawelosinski.skatefreak.ui.menu.MainMenuActivity
import pl.pawelosinski.skatefreak.ui.theme.SkateFreakTheme


class MainActivity : ComponentActivity() {

    private lateinit var localDataInit: LocalDataInit

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        localDataInit = LocalDataInit(this)
        localDataInit.loadData()

        databaseService = DatabaseService()

        setContent {
            SkateFreakTheme (darkTheme = isDarkMode) {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    LoadingAnimation()
                }
            }
        }

        loadDataForLoggedUser(onSuccess = {
            val intent = Intent(this, MainMenuActivity::class.java)
            startActivity(intent)
            finish()
        }, onFail = {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        })
    }

    private fun loadDataForLoggedUser(onSuccess: () -> Unit = {}, onFail: () -> Unit = {}) {
        val currentUserID = Firebase.auth.currentUser?.uid ?: ""
        // set logged user as current user
        databaseService.setLoggedUserById(currentUserID, onSuccess = {
            databaseService.getAllTrickInfo(onSuccess = {
                allTrickInfo = it
                // when logged user is set, load all trick records
                databaseService.getAllTrickRecords(onSuccess = {
                    localDataInit.loadAllTrickRecords(trickRecordList = it, context = this, onSuccess = {
                        // when all trick records are loaded, assign all trickRecordCreators to allTrickRecordsCreators
                        UserRepository.getAllTrickRecordCreators(
                            allTrickRecords = allTrickRecords,
                            onSuccess = {
                                if (loggedUser.value.checkRequiredData()) {
                                    onSuccess()
                                    return@getAllTrickRecordCreators
                                }
                                else {
                                    onFail()
                                    return@getAllTrickRecordCreators
                                }
                            }
                        )
                        return@loadAllTrickRecords
                    })
                    return@getAllTrickRecords
                })
                return@getAllTrickInfo
            })
            return@setLoggedUserById
        }, onFail = {
            databaseService.getAllTrickInfo(onSuccess = {
                allTrickInfo = it
                // when logged user is set, load all trick records
                databaseService.getAllTrickRecords(onSuccess = {
                    localDataInit.loadAllTrickRecords(trickRecordList = it, context = this, onSuccess = {
                        // when all trick records are loaded, assign all trickRecordCreators to allTrickRecordsCreators
                        UserRepository.getAllTrickRecordCreators(
                            allTrickRecords = allTrickRecords,
                            onSuccess = {
                                onFail()
                                return@getAllTrickRecordCreators
                            }
                        )
                        return@loadAllTrickRecords
                    })
                    return@getAllTrickRecords
                })
                return@getAllTrickInfo
            })
            return@setLoggedUserById
        })
        return
    }
}


@Preview(showBackground = true, apiLevel = 33, showSystemUi = true)
@Composable
fun LoadingScreenPreview() {
    SkateFreakTheme (darkTheme = true) {
        LoadingAnimation(loadingLabel = "Trwa ładowanie danych...\n(To może chwilę potrwać)")
    }
}
