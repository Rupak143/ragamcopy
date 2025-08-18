package com.simats.ragamcopy

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.TimeUnit
import kotlin.math.log

// ✅ Retrofit API interface
interface CreateCourseApi {
    @Multipart
    @POST("createcourse.php")
    fun createCourse(
        @Part("course_title") courseTitle: RequestBody,
        @Part("course_description") courseDesc: RequestBody,
        @Part("created_by_email") email: RequestBody,
        @Part video: MultipartBody.Part
    ): Call<ResponseBody>
}

class CreateCourseActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                CreateCourseScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateCourseScreen() {
    var courseTitle by remember { mutableStateOf("") }
    var courseDescription by remember { mutableStateOf("") }
    var videoUri by remember { mutableStateOf<Uri?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    val context = LocalContext.current

    // ✅ Video picker
    val videoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            videoUri = uri
            Toast.makeText(context, "Video Selected: ${uri.lastPathSegment}", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "No video selected", Toast.LENGTH_SHORT).show()
        }
    }

    // ✅ Retrofit instance
    val retrofit = remember {
        Retrofit.Builder()
            .baseUrl("http://172.22.212.134/ragam/") // <-- Your server URL
            .client(
                OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .build()
            )
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()
    }
    val api = retrofit.create(CreateCourseApi::class.java)

    // ✅ Upload function
    fun uploadCourse() {
        if (courseTitle.isBlank() || courseDescription.isBlank()) {
            Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }
        if (videoUri == null) {
            Toast.makeText(context, "Please select a video first", Toast.LENGTH_SHORT).show()
            return
        }

        isLoading = true

        // Convert Uri to File
        val file = getFileFromUri(context, videoUri!!)
        val videoRequest = file.asRequestBody("video/*".toMediaTypeOrNull())
        val videoPart = MultipartBody.Part.createFormData("video", file.name, videoRequest)

        val titlePart = RequestBody.create("text/plain".toMediaTypeOrNull(), courseTitle)
        val descPart = RequestBody.create("text/plain".toMediaTypeOrNull(), courseDescription)
        val emailPart = RequestBody.create("text/plain".toMediaTypeOrNull(), "instructor@example.com") // Replace with actual instructor email

        api.createCourse(titlePart, descPart, emailPart, videoPart).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                isLoading = false
                if (response.isSuccessful) {
                    val respString = response.body()?.string()?.trim() ?: ""
                    Log.d("CREATE_COURSE_RESPONSE", respString)
                    Toast.makeText(context, "Course uploaded successfully!", Toast.LENGTH_LONG).show()
                } else {
                    Log.e("CREATE_COURSE_ERROR", "HTTP ${response.code()} - ${response.message()}")
                    Toast.makeText(context, "Error ${response.code()}: ${response.message()}", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                isLoading = false
                Log.e("CREATE_COURSE_FAILURE", t.message ?: "Unknown error")
                Toast.makeText(context, "Network error: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })

    }

    // ✅ UI
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Create Course",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        OutlinedTextField(
            value = courseTitle,
            onValueChange = { courseTitle = it },
            label = { Text("Course Title") },
            placeholder = { Text("Enter course title") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = courseDescription,
            onValueChange = { courseDescription = it },
            label = { Text("Course Description") },
            placeholder = { Text("Enter course description") },
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
        )
        Spacer(Modifier.height(24.dp))
        Button(
            onClick = { videoPickerLauncher.launch("video/*") },
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Text("Upload Video")
        }
        Spacer(Modifier.height(16.dp))
        Button(
            onClick = { uploadCourse() },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp), color = MaterialTheme.colorScheme.onPrimary)
                Spacer(Modifier.width(12.dp))
                Text("Uploading...")
            } else {
                Text("Launch Course")
            }
        }
    }
}

// ✅ Uri -> File conversion inside same file
fun getFileFromUri(context: Context, uri: Uri): File {
    val inputStream = context.contentResolver.openInputStream(uri)
    val tempFile = File.createTempFile("upload", ".mp4", context.cacheDir)
    val outputStream = FileOutputStream(tempFile)
    inputStream?.copyTo(outputStream)
    inputStream?.close()
    outputStream.close()
    return tempFile
}
