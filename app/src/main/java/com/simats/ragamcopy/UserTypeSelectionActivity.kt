package com.simats.ragamcopy

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

class UserTypeSelectionActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                UserTypeSelectionScreen(
                    onStudentClick = {
                        startActivity(Intent(this, LoginActivity::class.java))
                    },
                    onInstructorClick = {
                        startActivity(Intent(this, InstructorLoginActivity::class.java))
                    }
                )
            }
        }
    }
}

@Composable
fun UserTypeSelectionScreen(
    onStudentClick: () -> Unit,
    onInstructorClick: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.bguiwhite),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Select Login Type", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(24.dp))
            Button(onClick = onStudentClick, modifier = Modifier.width(200.dp)) {
                Text("Student Login")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onInstructorClick, modifier = Modifier.width(200.dp)) {
                Text("Instructor Login")
            }
        }
    }
}
