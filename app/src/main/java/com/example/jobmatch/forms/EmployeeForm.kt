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
import coil.compose.rememberImagePainter
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
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
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
                .clickable { launcher.launch("image/*") },
            contentAlignment = Alignment.Center
        ) {
            if (profilePicUri != null) {
                Image(
                    painter = rememberImagePainter(profilePicUri),
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

        Spacer(modifier = Modifier.height(10.dp))
        OutlinedTextField(
            value = fullName,
            onValueChange = { fullName = it },
            label = { Text(text = "Full Name") },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp)
        )

        Spacer(modifier = Modifier.height(5.dp))

        OutlinedTextField(
            value = dateOfBirth,
            onValueChange = { dateOfBirth = it },
            label = { Text(text = "Date of Birth") },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp)
        )

        Spacer(modifier = Modifier.height(5.dp))

        OutlinedTextField(
            value = address,
            onValueChange = { address = it },
            label = { Text(text = "Address") },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp)
        )

        Spacer(modifier = Modifier.height(5.dp))

        OutlinedTextField(
            value = phoneNumber,
            onValueChange = { phoneNumber = it },
            label = { Text(text = "Phone Number") },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp)
        )

        Spacer(modifier = Modifier.height(5.dp))

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text(text = "Description") },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp),
            maxLines = 5 // For multiline input
        )

        Spacer(modifier = Modifier.height(5.dp))

        OutlinedTextField(
            value = workField,
            onValueChange = { workField = it },
            label = { Text(text = "Work Field") },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp),
            maxLines = 5 // For multiline input
        )

        Spacer(modifier = Modifier.height(5.dp))

        Column(modifier = Modifier.fillMaxWidth()) {
            Text(text = "Resume")

            Spacer(modifier = Modifier.height(5.dp))

            // Show the selected resume filename or a placeholder
            if (resumeUri != null) {
                // Extract the filename from the URI
                val resumeFilename = resumeUri?.lastPathSegment?.substringAfterLast("/") ?: "Unknown File"

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Selected Resume: $resumeFilename",
                        color = Color.Black,
                        fontSize = 14.sp
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No resume uploaded yet.",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = { resumeLauncher.launch("application/pdf") }, // Allow only PDF files
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Choose Resume")
            }
        }

        Spacer(modifier = Modifier.height(10.dp))
        // Other form fields (fullName, dateOfBirth, etc.) omitted for brevity...

        Button(
            onClick = {
                if (fullName.isEmpty() || dateOfBirth.isEmpty() || address.isEmpty() ||
                    phoneNumber.isEmpty() || description.isEmpty() || workField.isEmpty()
                ) {
                    Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                } else {
                    user?.uid?.let { userId ->
                        val employeeData = hashMapOf(
                            "fullName" to fullName,
                            "description" to description,
                            "dateOfBirth" to dateOfBirth,
                            "address" to address,
                            "workField" to workField,
                            "phoneNumber" to phoneNumber,
                            "resumeUri" to (resumeUri?.toString() ?: ""),
                            "role" to "Employee"
                        )

                        // Upload profile picture if selected
                        profilePicUri?.let { uri ->
                            val profilePicRef = storage.child("employee_pics/$userId.jpg")
                            profilePicRef.putFile(uri)
                                .addOnSuccessListener {
                                    profilePicRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                                        employeeData["profilePicUri"] = downloadUrl.toString()
                                        saveEmployeeData(employeeData, userId, navController, context)
                                    }
                                }
                                .addOnFailureListener {
                                    saveEmployeeData(employeeData, userId, navController, context)
                                }
                        } ?: saveEmployeeData(employeeData, userId, navController, context)
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



