package pl.pawelosinski.skatefreak

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import pl.pawelosinski.skatefreak.service.LoginService
import pl.pawelosinski.skatefreak.ui.theme.SkateFreakTheme

class LoginActivity : ComponentActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.web_client_id))
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Initialize Firebase Auth
        auth = Firebase.auth

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
                        LoginForm(context)
                        GoogleSignInButton()
                        GoogleSignOutButton()
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }


    // [START onactivityresult]
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e)
            }
        }
    }
    // [END onactivityresult]

    // [START auth_with_google]
    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    updateUI(null)
                }
            }
    }
    // [END auth_with_google]

    // [START signin]
    fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }


    fun signOut() {
        googleSignInClient.signOut().addOnCompleteListener(this) {
            // Update the UI after sign out (e.g., navigate to the sign-in screen)
            updateUI(null)
        }
    }
    // [END signin]

    private fun updateUI(user: FirebaseUser?) {
        if (user != null){
//            context.startActivity(Intent(context, LoggedUserMenuActivity::class.java))
            Log.d(
                TAG,
                "signInWithCredential:success" +
                        "Email: ${user.email}, \n" +
                        "Name: ${user.displayName} \n" +
                        "PhotoUrl: ${user.photoUrl} \n" +
                        "Uid: ${user.uid} \n" +
                        "ProviderId: ${user.providerId} \n" +
                        "PhoneNumber: ${user.phoneNumber} \n" +
                        "Metadata: ${user.metadata} \n" +
                        "isAnonymous: ${user.isAnonymous} \n" +
                        "isEmailVerified: ${user.isEmailVerified} \n"
            )
            var user : FirebaseUser = user
        }
        else {
//            Toast.makeText(
//                context,
//                "Nie udało się zalogować",
//                Toast.LENGTH_SHORT
//            ).show()
            Log.w(
                TAG,
                "signInWithCredential:failure"
            )
        }
    }

    companion object {
        private const val TAG = "GoogleActivity"
        private const val RC_SIGN_IN = 9001
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
                    signIn()
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
                signOut()
            }
        ) {
            Text(text = "Sign Out")
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginForm(context: Context) {
    val loginService = LoginService()
    Column(
        modifier = Modifier
//            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        var email by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            singleLine = true
        )
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true
        )
        Button(
            onClick = {
                var message = "Zalogowano\n " +
                        "Email: $email, " +
                        "Password: $password"
                if (!loginService.login(email, password)) {
                    message = "Nie udało się zalogować"
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



