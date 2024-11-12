package com.example.jobmatch.forms

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
    var phoneNumber by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var resumeUri by remember { mutableStateOf<Uri?>(null) }

    val scrollState = rememberScrollState()
    val context = LocalContext.current
    val user = FirebaseAuth.getInstance().currentUser
    val storage = FirebaseStorage.getInstance().reference

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { profilePicUri = it }
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
            label = { Text(text = "Description") },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth(),
            maxLines = 5 // For multiline input
        )
        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                if (fullName.isEmpty() || dateOfBirth.isEmpty() || address.isEmpty() ||
                    phoneNumber.isEmpty() || description.isEmpty()
                ) {
                    Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                } else {
                    user?.uid?.let { userId ->
                        val employeeData = hashMapOf(
                            "fullName" to fullName,
                            "dateOfBirth" to dateOfBirth,
                            "address" to address,
                            "profilePicUri" to (profilePicUri?.toString() ?: ""),
                            "description" to description,
                            "phoneNumber" to phoneNumber,
                            "resumeUri" to (resumeUri?.toString() ?: ""),
                            "role" to "Employee"
                        )

                        profilePicUri?.let { uri ->
                            val profilePicRef = storage.child("employee_profile_pics/$userId.jpg")
                            profilePicRef.putFile(uri)
                                .addOnSuccessListener {
                                    profilePicRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                                        employeeData["profilePictureUrl"] = downloadUrl.toString()
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
    db.collection("users").document(userId).set(employeeData)
        .addOnSuccessListener {
            markFormAsCompleted(userId, context)
            navController.navigate(Routes.employeeMainScreen)
        }
        .addOnFailureListener {
            Toast.makeText(context, "Failed to save data. Try again.", Toast.LENGTH_SHORT).show()
        }
}


