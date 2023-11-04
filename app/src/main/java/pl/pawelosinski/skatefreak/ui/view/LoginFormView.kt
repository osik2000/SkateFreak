package pl.pawelosinski.skatefreak.ui.view

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.android.gms.common.SignInButton
import pl.pawelosinski.skatefreak.service.LoginService
import pl.pawelosinski.skatefreak.ui.theme.SkateFreakTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginForm() {
    val context = LocalContext.current
    val loginService = LoginService()
    SkateFreakTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
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
            SignInButton(context).apply {
                setSize(SignInButton.SIZE_STANDARD)
                setOnClickListener {
                    Toast.makeText(
                        context,
                        "Sign in button clicked",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}

@Preview(name = "Logowanie")
@Composable
fun LoginFormPreview() {
    SkateFreakTheme {
        LoginForm()
    }
}