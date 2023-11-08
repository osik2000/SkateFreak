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
    private var storedVerificationId: String? = ""
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    private var isPhoneAuthInProgress = false

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
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w(PHONE_TAG, "onVerificationFailed", e)

                when (e) {
                    is FirebaseAuthInvalidCredentialsException -> {
                        // Invalid request
                    }

                    is FirebaseTooManyRequestsException -> {
                        // The SMS quota for the project has been exceeded
                    }

                    is FirebaseAuthMissingActivityForRecaptchaException -> {
                        // reCAPTCHA verification attempted with null Activity
                    }

                }
                isUserLoggedIn = false

                // Show a message and update the UI
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken,
            ) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d(PHONE_TAG, "onCodeSent:$verificationId")

                // Save verification ID and resending token so we can use them later
                storedVerificationId = verificationId
                resendToken = token
                isPhoneAuthInProgress = true
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
                        if(auth.currentUser != null){
                            Text(
                                text = "Witaj ${auth.currentUser?.displayName}!",
                                modifier = Modifier.padding(bottom = 16.dp)
                            )
                            GoogleSignOutButton()
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
                        }
                        else {
                            Text(
                                text = "Zaloguj się",
                                modifier = Modifier.padding(bottom = 16.dp)
                            )
                            PhoneLoginForm(context)
                            GoogleSignInButton()
                        }


                    }
                }
            }
        }
    }

//    override fun onStart() {
//        super.onStart()
//        // Check if user is signed in (non-null) and update UI accordingly.
////        val currentUser = auth.currentUser
////        updateUI(currentUser, this)
//    }


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


    private fun googleSignOut() {
        googleSignInClient.signOut().addOnCompleteListener(this) {
            isUserLoggedIn = false
            userLoggedBy = null
            // Update the UI after sign out (e.g., navigate to the sign-in screen)
            updateUI(null, this)
        }
        auth.signOut()
    }
    // [END signin]

    // PHONE AUTH START
    private fun startPhoneNumberVerification(phoneNumber: String) {
        // [START start_phone_auth]
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber) // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this) // Activity (for callback binding)
            .setCallbacks(callbacks) // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
        isPhoneAuthInProgress = true
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

                    val user = task.result?.user
                    Log.d(PHONE_TAG, "signInWithCredential:success\n" +
                            "isUserLoggedIn: $isUserLoggedIn" +
                            "user: $user" +
                            "user.displayName: ${user?.displayName}" +
                            "user.email: ${user?.email}" +
                            "user.photoUrl: ${user?.photoUrl}" +
                            "user.uid: ${user?.uid}" +
                            "user.providerId: ${user?.providerId}" +
                            "user.phoneNumber: ${user?.phoneNumber}" +
                            "user.metadata: ${user?.metadata}")
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
        if (user != null && !isUserLoggedIn){
            isUserLoggedIn = true
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
        else {
            if (isUserLoggedIn){
                Toast.makeText(
                    context,
                    "Użytkownik jest już zalogowany",
                    Toast.LENGTH_SHORT
                ).show()
                Log.w(
                    userLoggedBy,
                    "signInWithCredential:user is already logged in"
                )
            }
            else {
                Toast.makeText(
                    context,
                    "Nie udało się zalogować",
                    Toast.LENGTH_SHORT
                ).show()
                Log.w(
                    userLoggedBy,
                    "signInWithCredential:failure"
                )
            }
        }
        // refresh activity
        this.recreate()
    }

    companion object {
        private const val GOOGLE_TAG = "GoogleActivity"
        private const val PHONE_TAG = "PhoneAuthActivity"
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
    fun GoogleSignOutButton() {
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            onClick = {
                googleSignOut()
//                val intent = Intent(this, MainActivity::class.java)
//                startActivity(intent)
            }
        ) {
            Text(text = "Sign Out")
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun PhoneLoginForm(context: Context) {
//        val loginService = LoginService()
        Column(
            modifier = Modifier
//            .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            var phone by remember { mutableStateOf("") }
            var verificationCode by remember { mutableStateOf("") }
            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Phone (+48XXXXXXXXX)") },
                singleLine = true
            )
            if(isPhoneAuthInProgress){
                val pattern = remember { Regex("^\\d\\d\\d\\d\\d\\d$") }
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
                    val message : String = if(!isPhoneAuthInProgress){
                        startPhoneNumberVerification(phone)
                        "Wysłano wiadomość z kodem weryfikacyjnym na numer:\n " +
                                "\'$phone\'"
                    } else {
                        verifyPhoneNumberWithCode(storedVerificationId, verificationCode)
                        "Weryfikacja danych..."
                    }
                    Toast.makeText(
                        context,
                        message,
                        Toast.LENGTH_SHORT
                    ).show()
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







