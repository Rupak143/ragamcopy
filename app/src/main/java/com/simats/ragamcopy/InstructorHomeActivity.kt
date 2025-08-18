// InstructorHomeActivity.kt
package com.simats.ragamcopy

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

class InstructorHomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                InstructorHomeScreen()
            }
        }
    }
}

@Composable
fun InstructorHomeScreen() {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = { context.startActivity(Intent(context, CreateCourseActivity::class.java)) },
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
        ) { Text("Create Course") }

        Button(
            onClick = { context.startActivity(Intent(context, MyCoursesActivity::class.java)) },
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
        ) { Text("My Courses") }

        Button(
            onClick = { context.startActivity(Intent(context, ViewStudentsActivity::class.java)) },
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
        ) { Text("View Students") }
    }
}
