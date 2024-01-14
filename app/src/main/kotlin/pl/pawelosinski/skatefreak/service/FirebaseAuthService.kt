package pl.pawelosinski.skatefreak.service

import android.app.Activity
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.mutableStateOf
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
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
import pl.pawelosinski.skatefreak.local.loggedUser
import pl.pawelosinski.skatefreak.model.User
import pl.pawelosinski.skatefreak.model.User.Companion.ACCOUNT_TYPE_GOOGLE
import pl.pawelosinski.skatefreak.model.User.Companion.ACCOUNT_TYPE_PHONE
import pl.pawelosinski.skatefreak.ui.common.myToast
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
                    myToast(
                        currentActivity.value, "Niepoprawny numer telefonu" +
                                "Wprowadź numer w formacie +48XXXXXXXXX"
                    )
                }

                is FirebaseTooManyRequestsException -> {
                    // The SMS quota for the project has been exceeded
                    myToast(
                        currentActivity.value,
                        "Przekroczono limit SMSów\n" +
                                "Spróbuj ponownie później"
                    )
                }

                is FirebaseAuthMissingActivityForRecaptchaException -> {
                    // reCAPTCHA verification attempted with null Activity
                    myToast(
                        currentActivity.value, "Błąd weryfikacji\n" +
                                "Spróbuj ponownie później..."
                    )
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
            myToast(
                currentActivity.value,
                "Wysłano wiadomość z kodem weryfikacyjnym na numer:\n " +
                        "\'${phoneAuthUserData.value.userPhoneNumber.value}\'"
            )


            // Save verification ID and resending token so we can use them later
            storedVerificationId.value = verificationId
            resendToken.value = token
            phoneAuthUserData.value.userVerificationNumber.value = token.toString()
            phoneAuthUserData.value.userVerificationId.value = verificationId
            phoneAuthUserData.value.isUserLoggedIn.value = false
            phoneAuthUserData.value.isVerificationCompleted.value = false
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
        myToast(activity as Context, "Wysłano ponownie kod weryfikacyjny")
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

    fun changePhoneNumberWithCode(verificationId: String?, code: String, onComplete: () -> Unit = {}, onFail: () -> Unit = {}) {
        // [START verify_with_code]
        val credential = PhoneAuthProvider.getCredential(verificationId!!, code)
        // [END verify_with_code]
        auth.currentUser?.updatePhoneNumber(credential)?.addOnCompleteListener(currentActivity.value) { task ->
            if (task.isSuccessful) {
                Log.d(PHONE_TAG, "changePhoneNumberWithCode:success")
                onComplete()
            } else {
                Log.w(PHONE_TAG, "changePhoneNumberWithCode:failure", task.exception)
                if (task.exception is FirebaseAuthInvalidCredentialsException) {
                    // The verification code entered was invalid
                    myToast(currentActivity.value, "Niepoprawny kod weryfikacyjny")
                    Log.w(PHONE_TAG, "changePhoneNumberWithCode:WRONG CREDENTIALS", task.exception)
                }
                onFail()
            }
        }
    }

    // [START sign_in_with_phone]
    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(currentActivity.value) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    phoneAuthUserData.value.isVerificationCompleted.value =
                        true // TODO PHONE SEND TO DATABASE
                    databaseService.setLoggedUserById(task.result?.user?.uid!!, onSuccess = {
                        updateUI()
                    }, onFail = {
                        loggedUser.value = User.getUserFromFirebaseUser(
                            auth.currentUser,
                            accountType = ACCOUNT_TYPE_PHONE
                        )
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
                        loggedUser.value = User.getUserFromFirebaseUser(
                            auth.currentUser,
                            accountType = ACCOUNT_TYPE_GOOGLE
                        )
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
        val userLoggedBy =
            if (userLoggedBy.value.isEmpty()) userLoggedBy.value else "UpdateUI"
        Log.d(
            userLoggedBy,
            "[UPDATE-UI]\n" +
                    "user: ${loggedUser.value}\n" +
                    "isUserLoggedIn: $isUserLoggedIn\n\n"
        )
        if (loggedUser.value.firebaseId.isNotEmpty() && !isUserLoggedIn.value) {

            isUserLoggedIn.value = true
            if (phoneAuthUserData.value.isVerificationCompleted.value) {
                phoneAuthUserData.value.isUserLoggedIn.value = true
            }
            //
            Log.d(
                userLoggedBy,
                "signInWithCredential:success\n\n" +
                        loggedUser.value.toString() + "\n\n"
            )
        } else {
            Log.d(
                userLoggedBy,
                "user already logged in: $isUserLoggedIn"
            )
        }
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

        var storedVerificationId = mutableStateOf("")
        var resendToken = mutableStateOf<PhoneAuthProvider.ForceResendingToken?>(null)
    }
}







