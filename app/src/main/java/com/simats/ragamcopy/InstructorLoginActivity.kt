package com.simats.ragamcopy

import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

class InstructorLoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            InstructorLoginScreen(
                onSignupClick = {
                    startActivity(Intent(this, InstructorSignupActivity::class.java))
                },
                onLoginSuccess = { userData ->
                    // Pass user data to home activity
                    val intent = Intent(this, InstructorHomeActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        putExtra("instructor_id", userData.optString("id"))
                        putExtra("instructor_name", "${userData.optString("first_name")} ${userData.optString("last_name")}")
                        putExtra("instructor_email", userData.optString("email"))
                        putExtra("login_source", "instructor")
                    }
                    startActivity(intent)
                    finish()
                },
                onLoginFailed = { errorMsg ->
                    Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show()
                }
            )
        }
    }
}

@Composable
fun InstructorLoginScreen(
    onSignupClick: () -> Unit,
    onLoginSuccess: (JSONObject) -> Unit,
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
            Text("Instructor Login", fontSize = 32.sp, color = MaterialTheme.colorScheme.onBackground)
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

                        // 🔧 CREATE JSON BODY (not form data)
                        val jsonObject = JSONObject().apply {
                            put("email", email.trim())
                            put("password", password.trim())
                        }

                        val jsonBody = jsonObject.toString()
                        Log.d("LOGIN_REQUEST", "Sending JSON: $jsonBody")

                        val requestBody = jsonBody.toRequestBody("application/json".toMediaType())

                        val request = Request.Builder()
                            .url("http://172.22.212.134/ragam/instructorlogin.php")
                            .post(requestBody)
                            .addHeader("Content-Type", "application/json")
                            .build()

                        CoroutineScope(Dispatchers.IO).launch {
                            client.newCall(request).enqueue(object : Callback {
                                override fun onFailure(call: Call, e: IOException) {
                                    isLoading = false
                                    Log.e("LOGIN_ERROR", "Network error: ${e.message}")
                                    CoroutineScope(Dispatchers.Main).launch {
                                        onLoginFailed("Network error: ${e.message}")
                                    }
                                }

                                override fun onResponse(call: Call, response: Response) {
                                    isLoading = false
                                    val responseStr = response.body?.string()
                                    Log.d("LOGIN_RESPONSE", "Raw Response: $responseStr")

                                    if (!response.isSuccessful) {
                                        Log.e("LOGIN_ERROR", "HTTP Error: ${response.code}")
                                        CoroutineScope(Dispatchers.Main).launch {
                                            onLoginFailed("Server error: ${response.code}")
                                        }
                                        return
                                    }

                                    if (responseStr.isNullOrEmpty()) {
                                        Log.e("LOGIN_ERROR", "Empty response")
                                        CoroutineScope(Dispatchers.Main).launch {
                                            onLoginFailed("Empty server response")
                                        }
                                        return
                                    }

                                    try {
                                        val json = JSONObject(responseStr)
                                        val status = json.getString("status")
                                        Log.d("LOGIN_RESPONSE", "Status: $status")

                                        if (status == "success") {
                                            Log.d("LOGIN_SUCCESS", "✅ Login successful!")
                                            val userData = json.getJSONObject("user")
                                            CoroutineScope(Dispatchers.Main).launch {
                                                onLoginSuccess(userData)
                                            }
                                        } else {
                                            val message = json.optString("message", "Login failed")
                                            Log.e("LOGIN_ERROR", "Login failed: $message")
                                            CoroutineScope(Dispatchers.Main).launch {
                                                onLoginFailed(message)
                                            }
                                        }
                                    } catch (e: Exception) {
                                        Log.e("LOGIN_ERROR", "JSON parse error: $responseStr", e)
                                        CoroutineScope(Dispatchers.Main).launch {
                                            onLoginFailed("Invalid server response")
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
                    .height(50.dp),
                enabled = !isLoading
            ) {
                Text(if (isLoading) "Logging in..." else "Login")
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = onSignupClick) {
                Text("Don't have an account? Sign Up")
            }

            // 🔧 DEBUG BUTTON (Remove in production)
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedButton(
                onClick = {
                    Log.d("DEBUG_INFO", "Current email: '$email'")
                    Log.d("DEBUG_INFO", "Current password length: ${password.length}")
                    Log.d("DEBUG_INFO", "Email is blank: ${email.isBlank()}")
                    Log.d("DEBUG_INFO", "Password is blank: ${password.isBlank()}")
                }
            ) {
                Text("🔧 Debug Info")
            }
        }
    }
}