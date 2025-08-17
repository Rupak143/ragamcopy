package com.simats.ragamcopy

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.ragamcopy.SignupActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LoginScreen(
                onSignupClick = {
                    startActivity(Intent(this, SignupActivity::class.java))
                },
                onLoginSuccess = {
                    startActivity(Intent(this, StudentHomeActivity::class.java))
                    finish()
                },
                onLoginFailed = { errorMsg ->
                    Toast.makeText(this, errorMsg, Toast.LENGTH_SHORT).show()
                }
            )
        }
    }
}

@Composable
fun LoginScreen(
    onSignupClick: () -> Unit,
    onLoginSuccess: () -> Unit,
    onLoginFailed: (String) -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    val client = OkHttpClient()

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.bguiwhite),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Login", fontSize = 32.sp, color = MaterialTheme.colorScheme.onBackground)
            Spacer(modifier = Modifier.height(40.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (email.isNotBlank() && password.isNotBlank()) {
                        isLoading = true
                        val formBody = FormBody.Builder()
                            .add("email", email)
                            .add("password", password)
                            .build()

                        val request = Request.Builder()
                            .url("http://172.22.212.134/ragam/login.php") // Your PHP login API
                            .post(formBody)
                            .build()

                        CoroutineScope(Dispatchers.IO).launch {
                            client.newCall(request).enqueue(object : Callback {
                                override fun onFailure(call: Call, e: IOException) {
                                    isLoading = false
                                    CoroutineScope(Dispatchers.Main).launch {
                                        onLoginFailed("Network error: ${e.message}")
                                    }
                                }

                                override fun onResponse(call: Call, response: Response) {
                                    isLoading = false
                                    val responseStr = response.body?.string()
                                    if (!response.isSuccessful || responseStr.isNullOrEmpty()) {
                                        CoroutineScope(Dispatchers.Main).launch {
                                            onLoginFailed("Server error")
                                        }
                                        return
                                    }

                                    val json = JSONObject(responseStr)
                                    if (json.getString("status") == "success") {
                                        CoroutineScope(Dispatchers.Main).launch {
                                            onLoginSuccess()
                                        }
                                    } else {
                                        CoroutineScope(Dispatchers.Main).launch {
                                            onLoginFailed(json.optString("message", "Login failed"))
                                        }
                                    }
                                }
                            })
                        }
                    } else {
                        onLoginFailed("Please fill all fields")
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(if (isLoading) "Logging in..." else "Login")
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = onSignupClick) {
                Text("Don’t have an account? Sign Up")
            }
        }
    }
}
