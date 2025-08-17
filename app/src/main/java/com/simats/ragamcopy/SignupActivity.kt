package com.simats.ragamcopy

import android.os.Bundle
import android.content.Intent
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import java.util.concurrent.TimeUnit

class SignupActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                SignUpScreen()
            }
        }
    }
}

interface ApiService {
    @FormUrlEncoded
    @POST("signup.php")
    fun signup(
        @Field("full_name") fullName: String,
        @Field("email") email: String,
        @Field("phone_number") phone: String,
        @Field("gender") gender: String,
        @Field("dob") dob: String,
        @Field("password") password: String
    ): Call<ResponseBody>
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen() {
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("Male") }
    var dob by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var genderExpanded by remember { mutableStateOf(false) }

    val context = LocalContext.current

    val retrofit = remember {
        Retrofit.Builder()
            .baseUrl("http://172.22.212.134/ragam/") // ✅ change to your server IP
            .client(
                OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .addInterceptor { chain ->
                        val request = chain.request()
                        Log.d("HTTP_REQUEST", "➡ URL: ${request.url}")
                        val response = chain.proceed(request)
                        val peek = response.peekBody(2048).string()
                        Log.d("HTTP_RESPONSE", "⬅ Code: ${response.code}, Body: $peek")
                        response
                    }
                    .build()
            )
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()
    }

    val api = retrofit.create(ApiService::class.java)

    fun isValidEmail(email: String) = Patterns.EMAIL_ADDRESS.matcher(email).matches()
    fun isValidPhone(phone: String) = phone.length >= 10 && phone.all { it.isDigit() }

    fun validateForm(): String? {
        return when {
            firstName.isBlank() -> "Enter your first name"
            lastName.isBlank() -> "Enter your last name"
            email.isBlank() -> "Enter your email"
            !isValidEmail(email) -> "Invalid email address"
            phone.isBlank() -> "Enter your phone number"
            !isValidPhone(phone) -> "Invalid phone number"
            dob.isBlank() -> "Enter your date of birth"
            password.isBlank() -> "Enter your password"
            password.length < 6 -> "Password must be at least 6 characters"
            confirmPassword.isBlank() -> "Confirm your password"
            password != confirmPassword -> "Passwords do not match"
            else -> null
        }
    }

    fun performSignup() {
        val validationError = validateForm()
        if (validationError != null) {
            Toast.makeText(context, validationError, Toast.LENGTH_LONG).show()
            return
        }

        isLoading = true
        val fullName = "$firstName $lastName"

        api.signup(fullName, email, phone, gender, dob, password)
            .enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    isLoading = false
                    if (response.isSuccessful) {
                        val body = response.body()?.string()?.trim() ?: ""
                        Log.d("SIGNUP_RESULT_RAW", body)
                        try {
                            val json = JSONObject(body)
                            val status = json.optString("status")
                            val message = json.optString("message")
                            if (status.equals("success", true)) {
                                Toast.makeText(context, "Signup successful!", Toast.LENGTH_LONG).show()
                                Log.d("SIGNUP_RESULT", "✅ SUCCESS: $message")

                                // ✅ Clear fields
                                firstName = ""
                                lastName = ""
                                email = ""
                                phone = ""
                                dob = ""
                                password = ""
                                confirmPassword = ""

                                // ✅ Navigate to LoginActivity
                                val intent = Intent(context, LoginActivity::class.java)
                                context.startActivity(intent)

                                // ✅ Finish SignupActivity so user can’t go back here
                                if (context is SignupActivity) {
                                    context.finish()
                                }
                            } else {
                                Toast.makeText(context, "Signup failed: $message", Toast.LENGTH_LONG).show()
                                Log.d("SIGNUP_RESULT", "❌ FAIL: $message")
                            }

                        } catch (e: Exception) {
                            Toast.makeText(context, "Invalid server response", Toast.LENGTH_LONG).show()
                            Log.e("SIGNUP_RESULT", "❌ JSON Parse Error", e)
                        }
                    } else {
                        Toast.makeText(context, "Error ${response.code()}", Toast.LENGTH_LONG).show()
                        Log.e("SIGNUP_RESULT", "❌ HTTP ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    isLoading = false
                    Toast.makeText(context, "Network error: ${t.message}", Toast.LENGTH_LONG).show()
                    Log.e("SIGNUP_RESULT", "❌ Network Error", t)
                }
            })
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Create Account", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = firstName,
            onValueChange = { firstName = it },
            label = { Text("First Name") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = lastName,
            onValueChange = { lastName = it },
            label = { Text("Last Name") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )
        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = phone,
            onValueChange = { if (it.all { c -> c.isDigit() }) phone = it },
            label = { Text("Phone") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
        )
        Spacer(Modifier.height(8.dp))

        ExposedDropdownMenuBox(
            expanded = genderExpanded,
            onExpandedChange = { genderExpanded = !genderExpanded }
        ) {
            OutlinedTextField(
                value = gender,
                onValueChange = {},
                label = { Text("Gender") },
                readOnly = true,
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = genderExpanded) }
            )
            ExposedDropdownMenu(
                expanded = genderExpanded,
                onDismissRequest = { genderExpanded = false }
            ) {
                listOf("Male", "Female", "Other").forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            gender = option
                            genderExpanded = false
                        }
                    )
                }
            }
        }
        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = dob,
            onValueChange = { dob = it },
            label = { Text("DOB (YYYY-MM-DD)") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )
        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirm Password") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )
        Spacer(Modifier.height(16.dp))

        Button(
            onClick = { performSignup() },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Text("Signing Up...")
            } else {
                Text("Sign Up")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewSignUpScreen() {
    MaterialTheme {
        SignUpScreen()
    }
}
