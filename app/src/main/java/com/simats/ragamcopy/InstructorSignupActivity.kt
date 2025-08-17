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
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import java.util.concurrent.TimeUnit

// ✅ Data class matching your JSON request format
data class InstructorSignupRequest(
    val first_name: String,
    val last_name: String,
    val email: String,
    val phone: String,
    val gender: String,
    val dob: String,
    val education: String,
    val password: String,
    val confirm_password: String
)

// ✅ API interface (now uses @Body for JSON)
interface InstructorApiService {
    @POST("instructorsignup.php")
    fun instructorSignup(
        @Body request: InstructorSignupRequest
    ): Call<ResponseBody>
}

class InstructorSignupActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                InstructorSignUpScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InstructorSignUpScreen() {
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("Male") }
    var dob by remember { mutableStateOf("") }
    var education by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var genderExpanded by remember { mutableStateOf(false) }

    val context = LocalContext.current

    val retrofit = remember {
        Retrofit.Builder()
            .baseUrl("http://172.22.212.134/ragam/") // ✅ Your server URL
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
            .addConverterFactory(GsonConverterFactory.create()) // ✅ Use Gson for JSON
            .build()
    }

    val api = retrofit.create(InstructorApiService::class.java)

    fun isValidEmail(email: String) = Patterns.EMAIL_ADDRESS.matcher(email).matches()
    fun isValidPhone(phone: String) = phone.length >= 10 && phone.all { it.isDigit() }

    fun validateForm(): String? {
        return when {
            firstName.isBlank() -> "Enter your first name"
            lastName.isBlank() -> "Enter your last name"
            email.isBlank() -> "Enter your email"
            !isValidEmail(email) -> "Invalid email address"
            phone.isBlank() -> "Enter your phone number"
            !isValidPhone(phone) -> "Phone number must be at least 10 digits"
            dob.isBlank() -> "Enter your date of birth"
            education.isBlank() -> "Enter your education details"
            password.isBlank() -> "Enter your password"
            password.length < 6 -> "Password must be at least 6 characters"
            confirmPassword.isBlank() -> "Confirm your password"
            password != confirmPassword -> "Passwords do not match"
            else -> null
        }
    }

    fun performInstructorSignup() {
        val validationError = validateForm()
        if (validationError != null) {
            Toast.makeText(context, validationError, Toast.LENGTH_LONG).show()
            return
        }

        isLoading = true

        // ✅ Build request object with JSON format
        val request = InstructorSignupRequest(
            first_name = firstName,
            last_name = lastName,
            email = email,
            phone = phone,
            gender = gender,
            dob = dob,
            education = education,
            password = password,
            confirm_password = confirmPassword
        )

        api.instructorSignup(request).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                isLoading = false
                if (response.isSuccessful) {
                    val body = response.body()?.string()?.trim() ?: ""
                    Log.d("INSTRUCTOR_SIGNUP_RAW", body)
                    try {
                        val json = JSONObject(body)
                        val status = json.optString("status")
                        val message = json.optString("message")

                        if (status.equals("success", true)) {
                            Toast.makeText(
                                context,
                                "Instructor account created successfully! 🎉",
                                Toast.LENGTH_LONG
                            ).show()
                            Log.d("INSTRUCTOR_SIGNUP", "✅ SUCCESS: $message")

                            // Clear all fields
                            firstName = ""
                            lastName = ""
                            email = ""
                            phone = ""
                            gender = "Male"
                            dob = ""
                            education = ""
                            password = ""
                            confirmPassword = ""

                            // Navigate to login
                            val intent = Intent(context, InstructorLoginActivity::class.java)
                            context.startActivity(intent)
                            if (context is InstructorSignupActivity) {
                                context.finish()
                            }
                        } else {
                            Toast.makeText(context, "Signup failed: $message", Toast.LENGTH_LONG)
                                .show()
                            Log.d("INSTRUCTOR_SIGNUP", "❌ FAIL: $message")
                        }
                    } catch (e: Exception) {
                        Toast.makeText(context, "Invalid server response", Toast.LENGTH_LONG).show()
                        Log.e("INSTRUCTOR_SIGNUP", "❌ JSON Parse Error", e)
                    }
                } else {
                    Toast.makeText(
                        context,
                        "Error ${response.code()}: ${response.message()}",
                        Toast.LENGTH_LONG
                    ).show()
                    Log.e("INSTRUCTOR_SIGNUP", "❌ HTTP ${response.code()}")
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                isLoading = false
                Toast.makeText(context, "Network error: ${t.message}", Toast.LENGTH_LONG).show()
                Log.e("INSTRUCTOR_SIGNUP", "❌ Network Error", t)
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
        Text(
            text = "Create Instructor Account",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // --- Input fields ---
        OutlinedTextField(
            value = firstName,
            onValueChange = { firstName = it },
            label = { Text("First Name") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = lastName,
            onValueChange = { lastName = it },
            label = { Text("Last Name") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email Address") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            singleLine = true
        )
        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = phone,
            onValueChange = { if (it.all { c -> c.isDigit() } && it.length <= 15) phone = it },
            label = { Text("Phone Number") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            singleLine = true
        )
        Spacer(Modifier.height(12.dp))

        // Gender Dropdown
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
        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = dob,
            onValueChange = { dob = it },
            label = { Text("Date of Birth") },
            placeholder = { Text("YYYY-MM-DD") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = education,
            onValueChange = { education = it },
            label = { Text("Education Qualification") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            singleLine = true
        )
        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirm Password") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            singleLine = true,
            isError = password.isNotBlank() && confirmPassword.isNotBlank() && password != confirmPassword
        )
        if (password.isNotBlank() && confirmPassword.isNotBlank() && password != confirmPassword) {
            Text(
                text = "⚠️ Passwords do not match",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
        Spacer(Modifier.height(24.dp))

        Button(
            onClick = { performInstructorSignup() },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Spacer(Modifier.width(12.dp))
                Text("Creating Account...")
            } else {
                Text("Create Instructor Account", style = MaterialTheme.typography.titleMedium)
            }
        }

        Spacer(Modifier.height(16.dp))

        TextButton(
            onClick = {
                val intent = Intent(context, InstructorLoginActivity::class.java)
                context.startActivity(intent)
                if (context is InstructorSignupActivity) {
                    context.finish()
                }
            }
        ) {
            Text("Already have an account? Sign In")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewInstructorSignUpScreen() {
    MaterialTheme {
        InstructorSignUpScreen()
    }
}
