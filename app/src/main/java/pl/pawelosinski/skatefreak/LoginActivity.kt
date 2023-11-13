package pl.pawelosinski.skatefreak

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
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
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.auth
import pl.pawelosinski.skatefreak.auth.PhoneAuthUserData
import pl.pawelosinski.skatefreak.ui.theme.SkateFreakTheme
import java.util.concurrent.TimeUnit

class LoginActivity : ComponentActivity() {

    private lateinit var auth: FirebaseAuth
    private var userLoggedBy: String? = null
    private var isUserLoggedIn: Boolean = false

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
                            Toast.LENGTH_SHORT
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
                updateUI(null, this@LoginActivity)
            }
        }
        // [END phone_auth_callbacks]


        setContent {
            val context = LocalContext.current
            SkateFreakTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,

                    ) {
                    Column(
                        modifier = Modifier
                            //.fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (auth.currentUser != null) {
                            //TODO check if user data is all set

                            Log.d(
                                PHONE_TAG, "[###signInWithCredential### - success]\n" +
                                        "isUserLoggedIn: $isUserLoggedIn\n" +
                                        "user: $auth.currentUser\n" +
                                        "user.displayName: ${auth.currentUser?.displayName}\n" +
                                        "user.email: ${auth.currentUser?.email}\n" +
                                        "user.photoUrl: ${auth.currentUser?.photoUrl}\n" +
                                        "user.uid: ${auth.currentUser?.uid}\n" +
                                        "user.providerId: ${auth.currentUser?.providerId}\n" +
                                        "user.phoneNumber: ${auth.currentUser?.phoneNumber}\n" +
                                        "user.metadata: ${auth.currentUser?.metadata}\n" +
                                        "isDisplayNameNull: ${auth.currentUser?.displayName == null}\n" +
                                        "isPhoneNumberNull: ${auth.currentUser?.phoneNumber == null}\n"
                            )
                            // Text with auth.displayName when its not empty or auth.phoneNumber
                            Text(
                                text = if (auth.currentUser?.displayName?.isNotEmpty() == true) {
                                    "Witaj ${auth.currentUser?.displayName}"
                                } else {
                                    "Witaj ${auth.currentUser?.phoneNumber}"
                                },
                                modifier = Modifier.padding(bottom = 16.dp)
                            )

                            SignOutButton()
                            // Button to go to LoggedUserMenuActivity
                            Button(
                                onClick = {
                                    val intent = Intent(context, LoggedUserMenuActivity::class.java)
                                    startActivity(intent)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 16.dp)
                            ) {
                                Text("Przejdź do menu głównego")
                            }
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
            }
        }
    }

    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
//        val currentUser = auth.currentUser

        if (this::options.isInitialized && phoneAuthUserData.isAuthInProgress) PhoneAuthProvider.verifyPhoneNumber(options)


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
                    val user = auth.currentUser
                    updateUI(user, this)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(GOOGLE_TAG, "signInWithCredential:failure", task.exception)
                    updateUI(null, this)
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
            updateUI(null, this)
        }
    }


    // [START signin]
    private fun googleSignIn() {
        val signInIntent = googleSignInClient.signInIntent
        signInResultLauncher.launch(signInIntent)
    }


    private fun signOut() {
        googleSignInClient.signOut().addOnCompleteListener(this) {
            isUserLoggedIn = false
            userLoggedBy = null
            // Update the UI after sign out (e.g., navigate to the sign-in screen)
            updateUI(null, this)
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
//    private fun resendVerificationCode( // TODO zaimplementowac
//        phoneNumber: String,
//        token: PhoneAuthProvider.ForceResendingToken?,
//    ) {
//        val optionsBuilder = PhoneAuthOptions.newBuilder(auth)
//            .setPhoneNumber(phoneNumber) // Phone number to verify
//            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
//            .setActivity(this) // (optional) Activity for callback binding
//            // If no activity is passed, reCAPTCHA verification can not be used.
//            .setCallbacks(callbacks) // OnVerificationStateChangedCallbacks
//        if (token != null) {
//            optionsBuilder.setForceResendingToken(token) // callback's ForceResendingToken
//        }
//        PhoneAuthProvider.verifyPhoneNumber(optionsBuilder.build())
//    }
    // [END resend_verification]

    // [START sign_in_with_phone]
    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    phoneAuthUserData.isVerificationCompleted = true

                    val user = task.result?.user

                    userLoggedBy = "PhoneAuthActivity"
                    updateUI(user, this)
                } else {
                    // Sign in failed, display a message and update the UI
                    Log.w(PHONE_TAG, "signInWithCredential:failure", task.exception)
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid // TODO zaimplementowac
                        Log.w(PHONE_TAG, "signInWithCredential:WRONG CREDENTIALS", task.exception)
                    }
                    // Update UI
                    updateUI(null, this)
                }
            }
    }
    // [END sign_in_with_phone]

    private fun updateUI(user: FirebaseUser?, context: Context?) {
        val userLoggedBy = userLoggedBy ?: "UnknownLoginMethodActivity"
        Log.d(
            userLoggedBy,
            "user: $user" +
                    "isUserLoggedIn: $isUserLoggedIn"
        )
        if (user != null && !isUserLoggedIn) {
            isUserLoggedIn = true
            if (phoneAuthUserData.isVerificationCompleted) {
                phoneAuthUserData.isUserLoggedIn = true
            }
            //
            Log.d(
                userLoggedBy,
                "signInWithCredential:success" +
                        "Email: ${user.email}, \n" +
                        "Name: ${user.displayName} \n" +
                        "PhotoUrl: ${user.photoUrl} \n" +
                        "Uid: ${user.uid} \n" +
                        "ProviderId: ${user.providerId} \n" +
                        "PhoneNumber: ${user.phoneNumber} \n" +
                        "Metadata: ${user.metadata} \n"
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
        private const val GOOGLE_TAG = "GoogleActivity"
        private const val PHONE_TAG = "PhoneAuthActivity"
        private var phoneAuthUserData = PhoneAuthUserData()
        // PHONE AUTH VARIABLES
        private var storedVerificationId: String? = ""
        private var resendToken: PhoneAuthProvider.ForceResendingToken? = null
        private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    }

    @Composable
    fun GoogleSignInButton() {
        // Reference to the GoogleSignInClient from the activity

        AndroidView(
            modifier = Modifier.fillMaxWidth(),
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
    fun SignOutButton() {
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            onClick = {
                signOut()
//                val intent = Intent(this, MainActivity::class.java)
//                startActivity(intent)
            }
        ) {
            Text(text = "Sign Out")
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun PhoneLoginForm() {
//        val loginService = LoginService()
        Column(
            modifier = Modifier
//            .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            var phone by remember {
                if(phoneAuthUserData.userPhoneNumber.isEmpty()) {
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
            if (phoneAuthUserData.isAuthInProgress) {
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
            }

            Button(
                onClick = {
                    if (
                        !phoneAuthUserData.isAuthInProgress &&
                        !phoneAuthUserData.isVerificationCompleted &&
                        !phoneAuthUserData.isUserLoggedIn
                    ) {
                        startPhoneNumberVerification(phone)
                    } else if (!phoneAuthUserData.isVerificationCompleted) {
                        verifyPhoneNumberWithCode(storedVerificationId, verificationCode)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                Text("Login")
            }
        }
    }

}






