package pl.pawelosinski.skatefreak

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
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
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.Firebase
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthMissingActivityForRecaptchaException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.auth
import pl.pawelosinski.skatefreak.auth.PhoneAuthUserData
import pl.pawelosinski.skatefreak.auth.loggedUser
import pl.pawelosinski.skatefreak.model.User
import pl.pawelosinski.skatefreak.service.DataService
import pl.pawelosinski.skatefreak.ui.common.myCommonModifier
import pl.pawelosinski.skatefreak.ui.theme.SkateFreakTheme
import java.util.concurrent.TimeUnit

class LoginActivity : ComponentActivity() {
    private val dataService = DataService()

    private lateinit var auth: FirebaseAuth

    // GOOGLE AUTH VARIABLES
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var signInResultLauncher: ActivityResultLauncher<Intent>

    // PHONE AUTH VARIABLES
    private lateinit var options: PhoneAuthOptions


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize Firebase Auth
        auth = Firebase.auth

        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.web_client_id))
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Initialize the Activity Result Launcher
        signInResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                handleSignInResult(task)
            }
        }

        // Initialize phone auth callbacks
        // [START phone_auth_callbacks]
        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                phoneAuthUserData.isAuthInProgress = true
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
                phoneAuthUserData.isAuthInProgress = true
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w(PHONE_TAG, "onVerificationFailed", e)

                when (e) {
                    is FirebaseAuthInvalidCredentialsException -> {
                        // Invalid request
                        Toast.makeText(
                            this@LoginActivity,
                            "Niepoprawny numer telefonu\n" +
                                    "Wprowadź numer w formacie: +48XXXXXXXXX",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                    is FirebaseTooManyRequestsException -> {
                        // The SMS quota for the project has been exceeded
                        Toast.makeText(
                            this@LoginActivity,
                            "Przekroczono limit SMSów\n" +
                                    "Spróbuj ponownie później",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    is FirebaseAuthMissingActivityForRecaptchaException -> {
                        // reCAPTCHA verification attempted with null Activity
                        Toast.makeText(
                            this@LoginActivity,
                            "Błąd weryfikacji\n" +
                                    "Spróbuj ponownie później...",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                }
                isUserLoggedIn = false
                phoneAuthUserData.isAuthInProgress = false

                // Show a message and update the UI
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken,
            ) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.                Log.d(PHONE_TAG, "onCodeSent:$verificationId")
                Toast.makeText(
                    this@LoginActivity,
                    "Wysłano wiadomość z kodem weryfikacyjnym na numer:\n " +
                            "\'${phoneAuthUserData.userPhoneNumber}\'",
                    Toast.LENGTH_SHORT
                ).show()


                // Save verification ID and resending token so we can use them later
                storedVerificationId = verificationId
                resendToken = token
                phoneAuthUserData.userVerificationNumber = token.toString()
                phoneAuthUserData.userVerificationId = verificationId
                phoneAuthUserData.isUserLoggedIn = false
                phoneAuthUserData.isVerificationCompleted = false
                phoneAuthUserData.isAuthInProgress = true
                updateUI()
            }
        }
        // [END phone_auth_callbacks]


        setContent {
            LocalContext.current
            SkateFreakTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,

                    ) {
                    LoginScreen()
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
//        val currentUser = auth.currentUser

        if (this::options.isInitialized && phoneAuthUserData.isAuthInProgress) PhoneAuthProvider.verifyPhoneNumber(
            options
        )


//        updateUI(currentUser, this)
    }


    // Google Sign in on result
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) { //TODO sprawdzic czy uzywane
//        super.onActivityResult(requestCode, resultCode, data)
//
//        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
//        if (requestCode == RC_SIGN_IN) {
//            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
//            try {
//                // Google Sign In was successful, authenticate with Firebase
//                val account = task.getResult(ApiException::class.java)!!
//                Log.d(GOOGLE_TAG, "firebaseAuthWithGoogle:" + account.id)
//                firebaseAuthWithGoogle(account.idToken!!)
//            } catch (e: ApiException) {
//                // Google Sign In failed, update UI appropriately
//                Log.w(GOOGLE_TAG, "Google sign in failed", e)
//            }
//        }
//    }
//    // [END onactivityresult]


    // [START auth_with_google]
    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(GOOGLE_TAG, "signInWithCredential:success")
                    userLoggedBy = GOOGLE_TAG
                    dataService.getUserById(auth.currentUser?.uid!!, onSuccess = {
                        updateUI()
                    }, onFail = {
                        loggedUser = User.getUserFromFirebaseUser(auth.currentUser)
                        updateUI()
                    })
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(GOOGLE_TAG, "signInWithCredential:failure", task.exception)
                    loggedUser = User()
                    updateUI()
                }
            }
    }
    // [END auth_with_google]

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
            loggedUser = User()
            updateUI()
        }
    }


    // [START signin] //TODO extract to separate class
    private fun googleSignIn() {
        val signInIntent = googleSignInClient.signInIntent
        signInResultLauncher.launch(signInIntent)
    }


    private fun signOut() {
        googleSignInClient.signOut().addOnCompleteListener(this) {
            isUserLoggedIn = false
            userLoggedBy = null
            // Update the UI after sign out (e.g., navigate to the sign-in screen)
            loggedUser = User()
            updateUI()
        }
        phoneAuthUserData = PhoneAuthUserData()
        storedVerificationId = ""
        resendToken = null
        auth.signOut()
    }
    // [END signin]

    // PHONE AUTH START
    private fun startPhoneNumberVerification(phoneNumber: String) {
        phoneAuthUserData.userPhoneNumber = phoneNumber
        // [START start_phone_auth]
        options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber) // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this) // Activity (for callback binding)
            .setCallbacks(callbacks) // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
        // [END start_phone_auth]
    }

    private fun verifyPhoneNumberWithCode(verificationId: String?, code: String) {
        // [START verify_with_code]
        val credential = PhoneAuthProvider.getCredential(verificationId!!, code)
        // [END verify_with_code]
        signInWithPhoneAuthCredential(credential)
    }

    // [START resend_verification]
    private fun resendVerificationCode(
        // TODO zaimplementowac
        phoneNumber: String,
        token: PhoneAuthProvider.ForceResendingToken?,
    ) {
        val optionsBuilder = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber) // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this) // (optional) Activity for callback binding
            // If no activity is passed, reCAPTCHA verification can not be used.
            .setCallbacks(callbacks) // OnVerificationStateChangedCallbacks
        if (token != null) {
            optionsBuilder.setForceResendingToken(token) // callback's ForceResendingToken
        }
        PhoneAuthProvider.verifyPhoneNumber(optionsBuilder.build())
    }
    // [END resend_verification]

    // [START sign_in_with_phone]
    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    phoneAuthUserData.isVerificationCompleted = true
                    dataService.getUserById(task.result?.user?.uid!!, onSuccess = {
                        updateUI()
                    }, onFail = {
                        loggedUser = User.getUserFromFirebaseUser(auth.currentUser)
                        updateUI()
                    })
                    userLoggedBy = PHONE_TAG
                    Log.d(PHONE_TAG, "signInWithCredential:success")
                } else {
                    // Sign in failed, display a message and update the UI
                    Log.w(PHONE_TAG, "signInWithCredential:failure", task.exception)
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid // TODO zaimplementowac
                        Log.w(PHONE_TAG, "signInWithCredential:WRONG CREDENTIALS", task.exception)
                    }
                    // Update UI
                    updateUI()
                }
            }
    }
    // [END sign_in_with_phone]

    private fun updateUI() {
        val userLoggedBy = userLoggedBy ?: "UnknownLoginMethodActivity"
        Log.d(
            userLoggedBy,
            "[UPDATE-UI]\n" +
                    "user: $loggedUser\n" +
                    "isUserLoggedIn: $isUserLoggedIn\n"
        )
        if (loggedUser.firebaseId.isEmpty()) {
            loggedUser = User.getUserFromFirebaseUser(auth.currentUser)
        }
        if (loggedUser.firebaseId.isNotEmpty() && !isUserLoggedIn) {

            isUserLoggedIn = true
            if (phoneAuthUserData.isVerificationCompleted) {
                phoneAuthUserData.isUserLoggedIn = true
            }
            //
            Log.d(
                userLoggedBy,
                "signInWithCredential:success" +
                        "FirebaseId: ${loggedUser.firebaseId}, \n" +
                        "Nickname: ${loggedUser.nickname}, \n" +
                        "Email: ${loggedUser.email}, \n" +
                        "Name: ${loggedUser.name} \n" +
                        "PhotoUrl: ${loggedUser.photoUrl} \n" +
                        "PhoneNumber: ${loggedUser.phoneNumber} \n" +
                        "City: ${loggedUser.city} \n"
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
        // refresh activity
        this.recreate()
    }

    companion object {
        private var userLoggedBy: String? = null
        private var isUserLoggedIn: Boolean = false
        private var isUserDataSet: Boolean = false

        // GOOGLE AUTH VARIABLES
        private const val GOOGLE_TAG = "GoogleActivity"
        private const val PHONE_TAG = "PhoneAuthActivity"

        // PHONE AUTH VARIABLES
        private var phoneAuthUserData = PhoneAuthUserData()
        private var storedVerificationId: String? = ""
        private var resendToken: PhoneAuthProvider.ForceResendingToken? = null
        private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    }


    @Composable
    fun LoginScreen() {
        var isUserDataSet by remember {
            mutableStateOf(isUserDataSet)
        }

        val currentUser by remember {
            mutableStateOf(loggedUser)
        }
        Column(
            modifier = Modifier
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (currentUser.firebaseId.isNotEmpty()) {
                dataService.getUserById(currentUser.firebaseId)
                Log.d("LoginActivity", "Before data check: isUserDataSet: $isUserDataSet")
                if (!isUserDataSet) {
                    isUserDataSet = currentUser.checkRequiredData()
                    Log.d("LoginActivity", "After data check: isUserDataSet: $isUserDataSet")
                }

                Log.d(
                    userLoggedBy, "[###signInWithCredential### - success]\n" +
                            "isUserLoggedIn: $isUserLoggedIn\n" +
                            "user: $currentUser\n" +
                            "user.email: ${currentUser.email}\n" +
                            "user.photoUrl: ${currentUser.photoUrl}\n" +
                            "user.uid: ${currentUser.firebaseId}\n" +
                            "user.phoneNumber: ${currentUser.phoneNumber}\n" +
                            "user.displayName: ${currentUser.name}\n" +
                            "user.nickname: ${currentUser.nickname}\n" +
                            "user.city: ${currentUser.city}\n" +
                            "isUserDataSet: $isUserDataSet\n"
                )
                // Text with auth.displayName when its not empty or auth.phoneNumber
                Text(
                    text = if (!isUserDataSet) {
                        "Aby kontynuować, proszę uzupełnić dane profilu"
                    } else if (currentUser.name.isNotEmpty()) {
                        "Witaj ${currentUser.name}"
                    } else {
                        "Witaj ${currentUser.phoneNumber}"
                    },
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                if (isUserDataSet) {
                    // Button to go to LoggedUserMenuActivity
                    MainMenuButton()
                } else {
                    // Button to go to UserDataActivity
                    Button(
                        onClick = {
                            val intent = Intent(this@LoginActivity, UserSetDataActivity::class.java)
                            startActivity(intent)
                            finish()
                        },
                        modifier = myCommonModifier,
                        shape = MaterialTheme.shapes.small
                    ) {
                        Text("Uzupełnij dane użytkownika")
                    }
                }
                SignOutButton(signOut = { signOut() })
            } else {
                Text(
                    text = "Zaloguj się",
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                PhoneLoginForm()
                GoogleSignInButton()
            }
        }
    }

    @Composable
    fun MainMenuButton() {
        val context = LocalContext.current
        Button(
            onClick = {
                val intent = Intent(context, MainMenuActivity::class.java)
                startActivity(intent)
                finish()
            },
            modifier = myCommonModifier,
            shape = MaterialTheme.shapes.small
        ) {
            Text("Przejdź do menu głównego")
        }
    }

    @Composable
    fun GoogleSignInButton() {
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
                    googleSignIn()
                }
            }
        )
    }

    @Composable
    fun PhoneLoginForm() {
//        val loginService = LoginService()
        val isAuthInProgress by remember {
            mutableStateOf(phoneAuthUserData.isAuthInProgress)
        }
        val isVerificationCompleted by remember {
            mutableStateOf(phoneAuthUserData.isVerificationCompleted)
        }
        val isUserLoggedIn by remember {
            mutableStateOf(phoneAuthUserData.isUserLoggedIn)
        }
        val storedVerificationId by remember {
            mutableStateOf(storedVerificationId)
        }
        Column(
            modifier = Modifier
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            var phone by remember {
                if (phoneAuthUserData.userPhoneNumber.isEmpty()) {
                    mutableStateOf("+48")
                } else {
                    mutableStateOf(phoneAuthUserData.userPhoneNumber)
                }
            }
            var verificationCode by remember { mutableStateOf("") }
//            val pattern = remember { Regex("^\\+48\\d\\d\\d\\d\\d\\d\\d\\d\\d$") }
            OutlinedTextField(
                value = phone,
                onValueChange = {
                    phone = it
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                label = { Text("Phone (+48XXXXXXXXX)") },
                singleLine = true
            )
            Log.d(PHONE_TAG, "isAuthInProgress: ${phoneAuthUserData.isAuthInProgress}")
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
                        resendVerificationCode(phone, resendToken)
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
                        !isVerificationCompleted &&
                        !isUserLoggedIn
                    ) {
                        startPhoneNumberVerification(phone)
                    } else if (!isVerificationCompleted) {
                        verifyPhoneNumberWithCode(storedVerificationId, verificationCode)
                    }
                },
                modifier = myCommonModifier,
                shape = MaterialTheme.shapes.small
            ) {
                Text("Login")
            }
        }
    }
}


@Composable
fun SignOutButton(signOut : () -> Unit = {}) {
    Button(
        modifier = myCommonModifier,
        onClick = {
            signOut()
        },
        shape = MaterialTheme.shapes.small
    ) {
        Text(text = "Sign Out")
    }
}




