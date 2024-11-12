package com.example.jobmatch.employee

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.UUID

@Composable
fun EditEmployeeProfile(navController: NavController) {
    val user = FirebaseAuth.getInstance().currentUser
    val db = FirebaseFirestore.getInstance()
    val storage = FirebaseStorage.getInstance()
    val coroutineScope = rememberCoroutineScope()

    // State holders for fields
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var bio by remember { mutableStateOf("") }
    var dateOfBirth by remember { mutableStateOf("") }
    var resumeUri by remember { mutableStateOf<Uri?>(null) }
    var profilePicUri by remember { mutableStateOf<Uri?>(null) }

    // Profile Picture and Resume Pickers
    val profilePicPicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        profilePicUri = uri
    }
    val resumePicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        resumeUri = uri
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
                        bio = it.getString("bio") ?: ""
                        dateOfBirth = it.getString("dateOfBirth") ?: ""
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("EditEmployeeProfile", "Error loading profile data", e)
                }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Edit Employee Profile", fontSize = 20.sp, style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))

        // Profile Info Custom Text Fields
        CustomTextField(value = fullName, onValueChange = { fullName = it }, label = "Full Name")
        CustomTextField(value = email, onValueChange = { email = it }, label = "Email")
        CustomTextField(value = address, onValueChange = { address = it }, label = "Address")
        CustomTextField(value = phoneNumber, onValueChange = { phoneNumber = it }, label = "Phone Number")
        CustomTextField(value = bio, onValueChange = { bio = it }, label = "Bio")
        CustomTextField(value = dateOfBirth, onValueChange = { dateOfBirth = it }, label = "Date of Birth")

        Spacer(modifier = Modifier.height(20.dp))

        // Profile Picture Picker
        Button(onClick = { profilePicPicker.launch("image/*") }, modifier = Modifier.fillMaxWidth()) {
            Text("Select Profile Picture")
        }

        // Resume Picker
        Button(onClick = { resumePicker.launch("application/pdf") }, modifier = Modifier.fillMaxWidth()) {
            Text("Select Resume (PDF)")
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Save button
        Button(
            onClick = {
                coroutineScope.launch {
                    user?.uid?.let { userId ->
                        // Collect the updated profile info
                        val updatedInfo = mutableMapOf(
                            "fullName" to fullName,
                            "email" to email,
                            "address" to address,
                            "phoneNumber" to phoneNumber,
                            "bio" to bio,
                            "dateOfBirth" to dateOfBirth
                        )

                        // Upload files if selected
                        val profilePicUrl = profilePicUri?.let { uploadFile(storage, it, "profilePictures/$userId") }
                        val resumeUrl = resumeUri?.let { uploadFile(storage, it, "resumes/$userId") }

                        // Wait for the upload results
                        profilePicUrl?.let { updatedInfo["profilePicture"] = it }
                        resumeUrl?.let { updatedInfo["resumeUri"] = it }

                        // Save the profile data in Firestore
                        saveProfileData(db, userId, updatedInfo, navController)
                    }
                }
            },
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
        ) {
            Text("Save Changes")
        }

        Button(onClick = { navController.navigate("changepass") }, modifier = Modifier.fillMaxWidth()) {
            Text("Change Password")
        }
    }
}

// Helper function to save profile data
private fun saveProfileData(db: FirebaseFirestore, userId: String, updatedInfo: Map<String, Any>, navController: NavController) {
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

// Helper function to upload files to Firebase Storage
private suspend fun uploadFile(storage: FirebaseStorage, uri: Uri, path: String): String? {
    return withContext(Dispatchers.IO) {
        val fileRef = storage.reference.child("$path/${UUID.randomUUID()}")
        fileRef.putFile(uri).await()
        fileRef.downloadUrl.await().toString()
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
