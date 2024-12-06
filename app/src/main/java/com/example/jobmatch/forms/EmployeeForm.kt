package com.example.jobmatch.forms

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.jobmatch.Routes
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

@Composable
fun EmployeeForm(navController: NavController) {
    var profilePicUri by remember { mutableStateOf<Uri?>(null) }
    var fullName by remember { mutableStateOf("") }
    var dateOfBirth by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var workField by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var resumeUri by remember { mutableStateOf<Uri?>(null) }

    val scrollState = rememberScrollState()
    val context = LocalContext.current
    val user = FirebaseAuth.getInstance().currentUser
    val storage = FirebaseStorage.getInstance().reference

    // Launcher for profile picture
    val profilePicLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { profilePicUri = it }
    }

    // Launcher for resume
    val resumeLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { resumeUri = it }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Employee Form",
            fontSize = 24.sp,
            color = Color.Black,
            fontStyle = FontStyle.Italic,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        // Profile Picture
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(Color.Blue)
                .clickable { profilePicLauncher.launch("image/*") },
            contentAlignment = Alignment.Center
        ) {
            if (profilePicUri != null) {
                Image(
                    painter = rememberAsyncImagePainter(profilePicUri),
                    contentDescription = "Profile Picture",
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Profile Placeholder",
                    modifier = Modifier.size(120.dp),
                    tint = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = fullName,
            onValueChange = { fullName = it },
            label = { Text(text = "Full Name") },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = dateOfBirth,
            onValueChange = { dateOfBirth = it },
            label = { Text(text = "Date of Birth") },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = address,
            onValueChange = { address = it },
            label = { Text(text = "Address") },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = workField,
            onValueChange = { workField = it },
            label = { Text(text = "WorkField") },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = phoneNumber,
            onValueChange = { phoneNumber = it },
            label = { Text(text = "Phone Number") },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text(text = "Skills") },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth(),
            maxLines = 5 // For multiline input
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Resume Section
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(text = "Resume")

            Spacer(modifier = Modifier.height(8.dp))

            // Show the selected resume URI or a placeholder
            if (resumeUri != null) {
                Text(
                    text = "Selected Resume: ${resumeUri.toString()}",
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            } else {
                Text(text = "No resume uploaded yet.", color = Color.Gray, fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { resumeLauncher.launch("application/pdf") }, // Allow only PDF files
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Choose Resume")
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                if (fullName.isEmpty() || dateOfBirth.isEmpty() || address.isEmpty() ||
                    phoneNumber.isEmpty() || description.isEmpty()|| workField.isEmpty()
                ) {
                    Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                } else {
                    user?.uid?.let { userId ->
                        val employeeData = hashMapOf(
                            "profilePicUri" to (profilePicUri?.toString() ?: ""),
                            "fullName" to fullName,
                            "description" to description,
                            "dateOfBirth" to dateOfBirth,
                            "address" to address,
                            "workField" to workField,
                            "phoneNumber" to phoneNumber,
                            "resumeUri" to (resumeUri?.toString() ?: ""),
                            "role" to "Employee"
                        )

                        // Upload data and handle result
                        saveEmployeeData(employeeData, userId, navController, context)
                    }
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0XFFff8e2b)),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Text(text = "Submit", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
    }
}

fun saveEmployeeData(employeeData: HashMap<String, String>, userId: String, navController: NavController, context: android.content.Context) {
    val db = FirebaseFirestore.getInstance()
    db.collection("employees").document(userId).set(employeeData)
        .addOnSuccessListener {
            markFormCompleted(userId, context)
            navController.navigate(Routes.employeeMainScreen)
        }
        .addOnFailureListener {
            Toast.makeText(context, "Failed to save data. Try again.", Toast.LENGTH_SHORT).show()
        }
}
fun markFormCompleted(userId: String, context: android.content.Context) {
    val firestore = FirebaseFirestore.getInstance()
    firestore.collection("employees").document(userId).update("formCompleted", true)
        .addOnSuccessListener {
            Toast.makeText(context, "Form completed successfully", Toast.LENGTH_SHORT).show()
        }
        .addOnFailureListener {
            Toast.makeText(context, "Failed to mark form as completed. Try again.", Toast.LENGTH_SHORT).show()
        }
}


