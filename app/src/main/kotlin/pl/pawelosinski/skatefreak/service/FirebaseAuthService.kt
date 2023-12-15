package pl.pawelosinski.skatefreak.service

import android.app.Activity
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthMissingActivityForRecaptchaException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import pl.pawelosinski.skatefreak.R
import pl.pawelosinski.skatefreak.auth.PhoneAuthUserData
import pl.pawelosinski.skatefreak.local.isDarkMode
import pl.pawelosinski.skatefreak.local.loggedUser
import pl.pawelosinski.skatefreak.model.User
import pl.pawelosinski.skatefreak.service.FirebaseAuthService.Companion.PHONE_TAG
import pl.pawelosinski.skatefreak.service.FirebaseAuthService.Companion.isUserDataSet
import pl.pawelosinski.skatefreak.service.FirebaseAuthService.Companion.isUserLoggedIn
import pl.pawelosinski.skatefreak.service.FirebaseAuthService.Companion.phoneAuthUserData
import pl.pawelosinski.skatefreak.service.FirebaseAuthService.Companion.resendToken
import pl.pawelosinski.skatefreak.service.FirebaseAuthService.Companion.storedVerificationId
import pl.pawelosinski.skatefreak.service.FirebaseAuthService.Companion.userLoggedBy
import pl.pawelosinski.skatefreak.ui.auth.UserSetDataActivity
import pl.pawelosinski.skatefreak.ui.common.myCommonModifier
import pl.pawelosinski.skatefreak.ui.common.myToast
import pl.pawelosinski.skatefreak.ui.menu.MainMenuActivity
import pl.pawelosinski.skatefreak.ui.theme.SkateFreakTheme
import java.util.concurrent.TimeUnit

class FirebaseAuthService(val activity: ComponentActivity) {
    var currentActivity = mutableStateOf(activity)
    var auth: FirebaseAuth = FirebaseAuth.getInstance()

    // Initialize the Activity Result Launcher
    private val signInResultLauncher = currentActivity.value.registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            handleSignInResult(task)
        }
    }


    private var callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            phoneAuthUserData.value.isAuthInProgress.value = true
            // This callback will be invoked in two situations:
            // 1 - Instant verification. In some cases the phone number can be instantly
            //     verified without needing to send or enter a verification code.
            // 2 - Auto-retrieval. On some devices Google Play services can automatically
            //     detect the incoming verification SMS and perform verification without
            //     user action.
            Log.d(PHONE_TAG, "onVerificationCompleted:$credential")
            signInWithPhoneAuthCredential(credential)
        }

        override fun onVerificationFailed(e: FirebaseException) {
            phoneAuthUserData.value.isAuthInProgress.value = true
            // This callback is invoked in an invalid request for verification is made,
            // for instance if the the phone number format is not valid.
            Log.w(PHONE_TAG, "onVerificationFailed", e)

            when (e) {
                is FirebaseAuthInvalidCredentialsException -> {
                    // Invalid request
                    myToast(currentActivity.value, "Niepoprawny numer telefonu" +
                            "Wprowadź numer w formacie +48XXXXXXXXX")
                }

                is FirebaseTooManyRequestsException -> {
                    // The SMS quota for the project has been exceeded
                    myToast(currentActivity.value,
                        "Przekroczono limit SMSów\n" +
                                "Spróbuj ponownie później")
                }

                is FirebaseAuthMissingActivityForRecaptchaException -> {
                    // reCAPTCHA verification attempted with null Activity
                    myToast(currentActivity.value, "Błąd weryfikacji\n" +
                                "Spróbuj ponownie później...")
                }

            }
            isUserLoggedIn.value = false
            phoneAuthUserData.value.isAuthInProgress.value = false

            // Show a message and update the UI
        }

        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken,
        ) {
            // The SMS verification code has been sent to the provided phone number, we
            // now need to ask the user to enter the code and then construct a credential
            // by combining the code with a verification ID.                Log.d(PHONE_TAG, "onCodeSent:$verificationId")
            myToast(currentActivity.value,
                "Wysłano wiadomość z kodem weryfikacyjnym na numer:\n " +
                        "\'${phoneAuthUserData.value.userPhoneNumber.value}\'")


            // Save verification ID and resending token so we can use them later
            storedVerificationId.value = verificationId
            resendToken.value = token
            phoneAuthUserData.value.userVerificationNumber.value = token.toString()
            phoneAuthUserData.value.userVerificationId.value = verificationId
            phoneAuthUserData.value.isUserLoggedIn.value = false
            phoneAuthUserData.value.isVerificationCompleted.value= false
            phoneAuthUserData.value.isAuthInProgress.value = true
            currentActivity.value.recreate()
            //updateUI()
        }
    }
    // [END phone_auth_callbacks]

    // Inicjalizacja klienta logowania Google
    private val googleSignInClient by lazy {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(currentActivity.value.getString(R.string.web_client_id))
            .build()
        GoogleSignIn.getClient(currentActivity.value, gso)
    }

    // Define the handleSignInResult method
    private fun handleSignInResult(task: Task<GoogleSignInAccount>) {
        try {
            val account = task.getResult(ApiException::class.java)
            // Handle the successful sign-in attempt
            firebaseAuthWithGoogle(account.idToken!!)
        } catch (e: ApiException) {
            // Handle the unsuccessful sign-in attempt
            Log.w(GOOGLE_TAG, "Google sign in failed", e)
            // Update your UI here to remove sign-in related elements or to show a message
            loggedUser.value = User()
            updateUI()
        }
    }

    fun verifyPhoneNumberWithCode(verificationId: String?, code: String) {
        // [START verify_with_code]
        val credential = PhoneAuthProvider.getCredential(verificationId!!, code)
        // [END verify_with_code]
        signInWithPhoneAuthCredential(credential)
    }


    // [START resend_verification]
    fun resendVerificationCode(
        // TODO zaimplementowac
        phoneNumber: String,
        token: PhoneAuthProvider.ForceResendingToken?,
    ) {
        val optionsBuilder = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber) // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(currentActivity.value) // (optional) Activity for callback binding
            // If no activity is passed, reCAPTCHA verification can not be used.
            .setCallbacks(callbacks) // OnVerificationStateChangedCallbacks
        if (token != null) {
            optionsBuilder.setForceResendingToken(token) // callback's ForceResendingToken
        }
        PhoneAuthProvider.verifyPhoneNumber(optionsBuilder.build())
    }
    // [END resend_verification]

    fun googleSignIn() {
        val signInIntent = googleSignInClient.signInIntent
        signInResultLauncher.launch(signInIntent)
    }


    fun signOut(onComplete: () -> Unit = {}) {
        // Implementacja wylogowania
        googleSignInClient.signOut().addOnCompleteListener(currentActivity.value) { task ->
            if (task.isSuccessful) {
                Toast.makeText(currentActivity.value, "Wylogowano", Toast.LENGTH_SHORT).show()
                Log.d(GOOGLE_TAG, "signOut:success")
                auth.signOut()
                phoneAuthUserData.value = PhoneAuthUserData()
                storedVerificationId.value = ""
                resendToken.value = null
                isUserLoggedIn.value = false
                userLoggedBy.value = ""
                isUserDataSet.value = false
                // Update the UI after sign out (e.g., navigate to the sign-in screen)
                loggedUser.value = User()
//                activity.finish()
                onComplete()
            }
        }
    }

    // [START sign_in_with_phone]
    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(currentActivity.value) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    phoneAuthUserData.value.isVerificationCompleted.value = true // TODO PHONE SEND TO DATABASE
                    databaseService.getUserById(task.result?.user?.uid!!, onSuccess = {
                        loggedUser.value = it
                        updateUI()
                    }, onFail = {
                        loggedUser.value = User.getUserFromFirebaseUser(auth.currentUser, phoneNumber = phoneAuthUserData.value.userPhoneNumber.value)
                        updateUI()
                    })
                    userLoggedBy.value = PHONE_TAG
                    Log.d(PHONE_TAG, "signInWithCredential:success")
                } else {
                    // Sign in failed, display a message and update the UI
                    Log.w(PHONE_TAG, "signInWithCredential:failure", task.exception)
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid // TODO zaimplementowac
                        myToast(currentActivity.value, "Niepoprawny kod weryfikacyjny")
                        Log.w(PHONE_TAG, "signInWithCredential:WRONG CREDENTIALS", task.exception)
                    }
                    // Update UI
                    updateUI()
                }
            }
    }
    // [END sign_in_with_phone]

    fun startPhoneNumberVerification(phoneNumber: String) {
        phoneAuthUserData.value.userPhoneNumber.value = phoneNumber
        phoneAuthUserData.value.isAuthInProgress.value = true
        // [START start_phone_auth]
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber) // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(currentActivity.value) // Activity (for callback binding)
            .setCallbacks(callbacks) // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
        // [END start_phone_auth]
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(currentActivity.value) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(GOOGLE_TAG, "signInWithCredential:success")
                    userLoggedBy.value = GOOGLE_TAG
                    databaseService.setLoggedUserById(auth.currentUser?.uid!!, onSuccess = {
                        updateUI()
                    }, onFail = {
                        loggedUser.value = User.getUserFromFirebaseUser(auth.currentUser)
                        updateUI()
                    })
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(GOOGLE_TAG, "signInWithCredential:failure", task.exception)
                    loggedUser.value = User()
                    updateUI()
                }
            }
    }

    private fun updateUI() {
        val userLoggedBy = if(userLoggedBy.value.isEmpty()) userLoggedBy.value else "UnknownLoginMethodActivity"
        Log.d(
            userLoggedBy,
            "[UPDATE-UI]\n" +
                    "user: ${loggedUser.value}\n" +
                    "isUserLoggedIn: $isUserLoggedIn\n"
        )
        if (loggedUser.value.firebaseId.isEmpty()) {
            loggedUser.value = User.getUserFromFirebaseUser(auth.currentUser)
        }
        if (loggedUser.value.firebaseId.isNotEmpty() && !isUserLoggedIn.value) {

            isUserLoggedIn.value = true
            if (phoneAuthUserData.value.isVerificationCompleted.value) {
                phoneAuthUserData.value.isUserLoggedIn.value = true
            }
            //
            Log.d(
                userLoggedBy,
                "signInWithCredential:success" +
                        "FirebaseId: ${loggedUser.value.firebaseId}, \n" +
                        "Nickname: ${loggedUser.value.nickname}, \n" +
                        "Email: ${loggedUser.value.email}, \n" +
                        "Name: ${loggedUser.value.name} \n" +
                        "PhotoUrl: ${loggedUser.value.photoUrl} \n" +
                        "PhoneNumber: ${loggedUser.value.phoneNumber} \n" +
                        "City: ${loggedUser.value.city} \n"
            )
        }
        else {
            Log.d(
                userLoggedBy,
                "user already logged in: $isUserLoggedIn"
            )
        }
        Log.w(
            userLoggedBy,
            "signInWithCredential:RECREATING ACTIVITY"
        )

//        if (isUserLoggedIn.value && isUserDataSet.value) {
//
//        }
        // refresh activity
//        activity.recreate()
    }




    companion object {
//        private const val RC_SIGN_IN = 9001


        var userLoggedBy = mutableStateOf("")
        var isUserLoggedIn = mutableStateOf(false)
        var isUserDataSet = mutableStateOf(false)

        // GOOGLE AUTH VARIABLES
        private const val GOOGLE_TAG = "GoogleActivity"
        const val PHONE_TAG = "PhoneAuthActivity"

        // PHONE AUTH VARIABLES
        var phoneAuthUserData = mutableStateOf(PhoneAuthUserData())

        var storedVerificationId= mutableStateOf("")
        var resendToken = mutableStateOf<PhoneAuthProvider.ForceResendingToken?>(null)
    }

    // Dodatkowe metody związane z autoryzacją Firebase...
}
@Composable
fun LoginScreen(firebaseAuthService: FirebaseAuthService) {
    val isUserDataSet by remember {
        mutableStateOf(isUserDataSet)
    }

    var loggedUser by remember {
        mutableStateOf(loggedUser.value)
    }
    val isUserLoggedIn by remember {
        mutableStateOf(isUserLoggedIn)
    }

//    val isAuthInProgress by remember {
//        mutableStateOf(phoneAuthUserData.value.isAuthInProgress.value)
//    }
//    var verificationCode by remember { mutableStateOf("") }

    SkateFreakTheme(darkTheme = isDarkMode) {
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background,

            ) {
            Column(
                modifier = Modifier
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (isUserLoggedIn.value) {
                    databaseService.getUserById(loggedUser.firebaseId, onSuccess = {
                        loggedUser = it
                        pl.pawelosinski.skatefreak.local.loggedUser.value = it
                    }, onFail = {
                        loggedUser = User.getUserFromFirebaseUser(firebaseAuthService.auth.currentUser)
                    })
                    Log.d("LoginActivity", "Before data check: isUserDataSet: $isUserDataSet")
                    if (!isUserDataSet.value) {
                        isUserDataSet.value = loggedUser.checkRequiredData()
                        Log.d("LoginActivity", "After data check: isUserDataSet: $isUserDataSet")
                    }

                    Log.d(
                        userLoggedBy.value, "[###signInWithCredential### - success]\n" +
                                "isUserLoggedIn: $isUserLoggedIn\n" +
                                "user: $loggedUser\n" +
                                "user.email: ${loggedUser.email}\n" +
                                "user.photoUrl: ${loggedUser.photoUrl}\n" +
                                "user.uid: ${loggedUser.firebaseId}\n" +
                                "user.phoneNumber: ${loggedUser.phoneNumber}\n" +
                                "user.displayName: ${loggedUser.name}\n" +
                                "user.nickname: ${loggedUser.nickname}\n" +
                                "user.city: ${loggedUser.city}\n" +
                                "isUserDataSet: $isUserDataSet\n"
                    )
                    // Text with auth.displayName when its not empty or auth.phoneNumber
                    if (!isUserDataSet.value) {
                        Log.d("LoginActivity", "isUserDataSet: $isUserDataSet")
                        Text(
                            text = "Aby kontynuować, proszę uzupełnić dane profilu",
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                    } else if (loggedUser.name.isNotEmpty()) {
                        Text(
                            text = "Witaj ${loggedUser.name}",
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                    } else {
                        Text(
                            text = "Witaj ${loggedUser.phoneNumber}",
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                    }

                    if (isUserDataSet.value) {
                        // Button to go to LoggedUserMenuActivity
                        MainMenuButton(firebaseAuthService)
                    } else {
                        // Button to go to UserDataActivity
                        Button(
                            onClick = {
                                val intent = Intent(firebaseAuthService.currentActivity.value, UserSetDataActivity::class.java) //TODO
                                firebaseAuthService.currentActivity.value.startActivity(intent)
                                firebaseAuthService.currentActivity.value.finish()
                            },
                            modifier = myCommonModifier,
                            shape = MaterialTheme.shapes.small
                        ) {
                            Text("Uzupełnij dane użytkownika")
                        }
                    }
                    SignOutButton(signOut = { firebaseAuthService.signOut() })
                } else {
                    Text(
                        text = "Zaloguj się",
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    PhoneLoginForm(firebaseAuthService)
                    GoogleSignInButton(firebaseAuthService)
                }
            }
        }
    }

}



@Composable
fun MainMenuButton(firebaseAuthService: FirebaseAuthService) {
    val context = LocalContext.current
    Button(
        onClick = {
            val intent = Intent(context, MainMenuActivity::class.java)
            firebaseAuthService.currentActivity.value.startActivity(intent)
            firebaseAuthService.currentActivity.value.finish()
        },
        modifier = myCommonModifier,
        shape = MaterialTheme.shapes.small
    ) {
        Text("Przejdź do menu głównego")
    }
}


@Composable
fun SignOutButton(signOut : () -> Unit = {
    // Configure Google Sign In
}) {
    Button(
        modifier = myCommonModifier,
        onClick = {
            signOut()
        },
        shape = MaterialTheme.shapes.small
    ) {
        Text(text = "Wyloguj się")
    }
}


@Composable
fun GoogleSignInButton(firebaseAuthService: FirebaseAuthService) {
    // Reference to the GoogleSignInClient from the activity

    AndroidView(
        modifier = myCommonModifier
            .border(1.dp, MaterialTheme.colorScheme.onSurface, MaterialTheme.shapes.small),
        factory = { context ->
            SignInButton(context).apply {
                setSize(SignInButton.SIZE_WIDE)
            }
        },
        update = { signInButton ->
            signInButton.setOnClickListener {
                // Start the sign-in process when the Google sign-in button is clicked
                firebaseAuthService.googleSignIn()
            }
        }
    )
}




@Composable
fun PhoneLoginForm(firebaseAuthService: FirebaseAuthService) {
//        val loginService = LoginService()
    val isAuthInProgress by remember {
        mutableStateOf(phoneAuthUserData.value.isAuthInProgress.value)
    }
    val isVerificationCompleted by remember {
        mutableStateOf(phoneAuthUserData.value.isVerificationCompleted)
    }
    val isUserLoggedIn by remember {
        mutableStateOf(phoneAuthUserData.value.isUserLoggedIn)
    }
    val storedVerificationId by remember {
        mutableStateOf(storedVerificationId.value)
    }
    var userPhoneNumber by remember {
        if (phoneAuthUserData.value.userPhoneNumber.value.isEmpty()) {
            mutableStateOf("+48")
        } else {
            mutableStateOf(phoneAuthUserData.value.userPhoneNumber.value)
        }
    }
    val resendToken by remember {
        mutableStateOf(resendToken)
    }
    var verificationCode by remember { mutableStateOf("") }
    Column(
        modifier = Modifier
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
//            val pattern = remember { Regex("^\\+48\\d\\d\\d\\d\\d\\d\\d\\d\\d$") }
        OutlinedTextField(
            value = userPhoneNumber,
            onValueChange = {
                userPhoneNumber = it
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            label = { Text("Phone (+48XXXXXXXXX)") },
            singleLine = true
        )
        Log.d(PHONE_TAG, "isAuthInProgress: $isAuthInProgress")
        if (isAuthInProgress) {
            val pattern = remember { Regex("^\\d?\\d?\\d?\\d?\\d?\\d?$") }
            OutlinedTextField(
                value = verificationCode,
                onValueChange = {
                    if (it.isEmpty() || it.matches(pattern)) {
                        verificationCode = it
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                label = { Text("Kod Weryfikacyjny SMS") },
                singleLine = true
            )
            Button(
                onClick = {
                    firebaseAuthService.resendVerificationCode(userPhoneNumber, resendToken.value)
                },
                modifier = myCommonModifier,
                shape = MaterialTheme.shapes.small
            ) {
                Text("Wyślij ponownie kod weryfikacyjny")
            }
        }

        Button(
            onClick = {
                if (
                    !isAuthInProgress &&
                    !isVerificationCompleted.value &&
                    !isUserLoggedIn.value
                ) {
                    firebaseAuthService.startPhoneNumberVerification(userPhoneNumber)
                } else if (!isVerificationCompleted.value) {
                    if(verificationCode.matches(Regex("^\\d\\d\\d\\d\\d\\d$"))) {
                        firebaseAuthService.verifyPhoneNumberWithCode(storedVerificationId, verificationCode)
                    } else {
                        Toast.makeText(
                            firebaseAuthService.currentActivity.value,
                            "Niepoprawny kod weryfikacyjny",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            },
            modifier = myCommonModifier,
            shape = MaterialTheme.shapes.small
        ) {
            Text("Login")
        }
    }
}






