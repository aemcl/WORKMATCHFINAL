package com.example.jobmatch.employee

import android.net.Uri
import android.util.Log
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

@Composable
fun EditEmployeeProfile(navController: NavController) {
    val user = FirebaseAuth.getInstance().currentUser
    val db = FirebaseFirestore.getInstance()
    val storage = FirebaseStorage.getInstance()

    // State holders for fields
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
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
            CustomTextField(value = dateOfBirth, onValueChange = { dateOfBirth = it }, label = "Date of Birth")
            CustomTextField(value = address, onValueChange = { address = it }, label = "Address")
            CustomTextField(value = phoneNumber, onValueChange = { phoneNumber = it }, label = "Phone Number")
            // Resume Upload
            // Resume Upload
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .clip(MaterialTheme.shapes.medium) // Optional rounded corners
                    .background(Color.Blue) // Blue background
                    .clickable { resumePicker.launch("application/pdf") }
                    .padding(16.dp), // Inner padding
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = resumeUri?.lastPathSegment ?: "Upload Resume", // Display file name or prompt
                    color = Color.White, // White text for better contrast
                    style = MaterialTheme.typography.bodyMedium // Updated for Material 3
                )
            }



            Spacer(modifier = Modifier.height(20.dp))

            // Save Button
            Button(onClick = {
                user?.uid?.let { userId ->
                    val employeeInfo = hashMapOf(
                        "fullName" to fullName,
                        "address" to address,
                        "phoneNumber" to phoneNumber,
                        "description" to description,
                        "dateOfBirth" to dateOfBirth,
                    )
                    resumeUri?.let { uri ->
                        val resumeRef = storage.reference.child("employee_resumes/$userId.pdf")
                        resumeRef.putFile(uri).addOnSuccessListener {
                            resumeRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                                employeeInfo["resumeUri"] = downloadUrl.toString()
                                saveEmployeeData(db, userId, employeeInfo, navController)
                            }
                        }.addOnFailureListener { e ->
                            Log.e("EditEmployeeProfile", "Error uploading resume", e)
                            saveEmployeeData(db, userId, employeeInfo, navController)
                        }
                    }

                    profilePicUri?.let { uri ->
                        val profilePicRef = storage.reference.child("employee_pics/$userId.jpg")
                        profilePicRef.putFile(uri).addOnSuccessListener {
                            profilePicRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                                employeeInfo["profilePicUri"] = downloadUrl.toString()
                                saveEmployeeData(db, userId, employeeInfo, navController)
                            }
                        }.addOnFailureListener { e ->
                            Log.e("EditEmployeeProfile", "Error uploading profile picture", e)
                            saveEmployeeData(db, userId, employeeInfo, navController)
                        }
                    } ?: saveEmployeeData(db, userId, employeeInfo, navController)
                }
            }, modifier = Modifier.fillMaxWidth().height(50.dp)) {
                Text(text = "Save Changes")
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Cancel Button
            Button(
                onClick = { navController.navigateUp() },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(text = "Cancel")
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
            navController.navigateUp()  // Navigate back to EmployeeProfile
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
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier
            .padding(vertical = 8.dp)
            .fillMaxWidth()
    )
}
data class EmployeeProfileState(
    var fullName: String = "",
    var email: String = "",
    var address: String = "",
    var phoneNumber: String = "",
    var description: String = "",
    var dateOfBirth: String = "",
    var profilePicUri: Uri? = null,
    var resumeUri: Uri? = null
)

