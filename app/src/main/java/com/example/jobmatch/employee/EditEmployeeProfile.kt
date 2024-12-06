package com.example.jobmatch.employee

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.jobmatch.Routes
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

@Composable
fun EditEmployeeProfile(navController: NavController) {
    val user = FirebaseAuth.getInstance().currentUser
    val db = FirebaseFirestore.getInstance()
    val storage = FirebaseStorage.getInstance().reference

    // State holders for fields
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var workField by remember { mutableStateOf("") }
    var dateOfBirth by remember { mutableStateOf("") }
    var profilePicUri by remember { mutableStateOf<Uri?>(null) }
    var resumeUri by remember { mutableStateOf<Uri?>(null) }

    val resumePicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        resumeUri = uri
        Log.d("EditEmployeeProfile", "Selected Resume URI: $resumeUri")
    }

    // Profile Picture Picker
    val profilePicPicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        profilePicUri = uri
    }

    // Load current data into fields
    LaunchedEffect(user?.uid) {
        user?.uid?.let { userId ->
            db.collection("employees").document(userId).get()
                .addOnSuccessListener { document ->
                    document?.let {
                        fullName = it.getString("fullName") ?: ""
                        email = it.getString("email") ?: ""
                        address = it.getString("address") ?: ""
                        phoneNumber = it.getString("phoneNumber") ?: ""
                        description = it.getString("description") ?: ""
                        workField = it.getString("workField") ?: ""
                        dateOfBirth = it.getString("dateOfBirth") ?: ""
                        profilePicUri = it.getString("profilePicUri")?.let { uriStr -> Uri.parse(uriStr) }
                        resumeUri = it.getString("resumeUri")?.let { uriStr -> Uri.parse(uriStr) }
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("EditEmployeeProfile", "Error loading profile data", e)
                }
        }
    }

    Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Edit Employee Profile", fontSize = 24.sp, color = Color.Black)
            Spacer(modifier = Modifier.height(20.dp))

            // Profile Picture
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clickable { profilePicPicker.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                if (profilePicUri != null) {
                    Image(
                        painter = rememberAsyncImagePainter(profilePicUri),
                        contentDescription = "Profile Picture",
                        modifier = Modifier.size(120.dp).clip(CircleShape)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Profile Placeholder",
                        modifier = Modifier.size(120.dp),
                        tint = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            CustomTextField(value = fullName, onValueChange = { fullName = it }, label = "Full Name")
            CustomTextField(value = description, onValueChange = { description = it }, label = "Description")
            CustomTextField(value = workField, onValueChange = { workField = it }, label = "Work Field")
            CustomTextField(value = dateOfBirth, onValueChange = { dateOfBirth = it }, label = "Date of Birth")
            CustomTextField(value = address, onValueChange = { address = it }, label = "Address")
            CustomTextField(value = phoneNumber, onValueChange = { phoneNumber = it }, label = "Phone Number")

            // Resume Upload
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .background(Color.Blue)
                    .clickable { resumePicker.launch("application/pdf") }
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = resumeUri?.lastPathSegment ?: "Upload Resume",
                    color = Color.White,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Save Button
            Button(onClick = {
                user?.uid?.let { userId ->
                    val employeeInfo: MutableMap<String, Any> =
                        hashMapOf(
                            "fullName" to fullName,
                            "address" to address,
                            "phoneNumber" to phoneNumber,
                            "description" to description,
                            "workField" to workField,
                            "dateOfBirth" to dateOfBirth,
                        )

                    // Handle resume upload and profile picture upload with error handling
                    val tasks: MutableList<Task<*>> = mutableListOf()

                    resumeUri?.let { uri ->
                        val resumeRef = storage.child("employee_resumes/$userId.pdf")
                        val uploadTask = resumeRef.putFile(uri)

                        // Add a listener to handle the upload completion
                        uploadTask.addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                resumeRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                                    employeeInfo["resumeUri"] = downloadUrl.toString()
                                }.addOnFailureListener { e ->
                                    Log.e("EditEmployeeProfile", "Error getting download URL for resume", e)
                                }
                            } else {
                                Log.e("EditEmployeeProfile", "Error uploading resume", task.exception!!)
                            }
                        }
                        tasks.add(uploadTask) // Add the upload task to the list
                    }

                    profilePicUri?.let { uri ->
                        val profilePicRef = storage.child("employee_pics/$userId.jpg")
                        val uploadTask = profilePicRef.putFile(uri)

                        // Add a listener to handle the upload completion
                        uploadTask.addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                profilePicRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                                    employeeInfo["profilePicUri"] = downloadUrl.toString()
                                }.addOnFailureListener { e ->
                                    Log.e("EditEmployeeProfile", "Error getting download URL for profile picture", e)
                                }
                            } else {
                                Log.e("EditEmployeeProfile", "Error uploading profile picture", task.exception!!)
                            }
                        }
                        tasks.add(uploadTask) // Add the upload task to the list
                    }

                    // Wait for all uploads to complete before saving data
                    Tasks.whenAllComplete(tasks).addOnCompleteListener {
                        saveEmployeeData(db, userId, employeeInfo, navController)
                    }
                }
            }, modifier = Modifier.fillMaxWidth().height(50.dp)) {
                Text(text = "Save Changes")
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Cancel Button
            Button(
                onClick = { navController.navigateUp() },
                colors = ButtonDefaults.buttonColors(containerColor= Color.Gray),
                modifier=Modifier.fillMaxWidth().height(50.dp)
            ) {
                Text(text="Cancel")
            }
        }
    }
}

// Helper function to save profile data
fun saveEmployeeData(db: FirebaseFirestore, userId: String, updatedInfo: Map<String, Any>, navController: NavController) {
    db.collection("employees").document(userId)
        .update(updatedInfo)
        .addOnSuccessListener {
            Log.d("EditEmployeeProfile", "Profile updated successfully")
            navController.navigate(Routes.employeeMainScreen)  // Navigate back to EmployeeProfile
        }
        .addOnFailureListener { e ->
            Log.e("EditEmployeeProfile", "Error updating profile", e)
        }
}

// Custom TextField for reuse
@Composable
fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String
) {
    OutlinedTextField(
        value=value,
        onValueChange=onValueChange,
        label={ Text(label) },
        modifier=Modifier.padding(vertical=8.dp).fillMaxWidth()
    )
}

