package com.example.jobmatch.employer

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.example.jobmatch.Routes

@Composable
fun EditEmployerProfile(navController: NavController) {
    val user = FirebaseAuth.getInstance().currentUser
    val db = FirebaseFirestore.getInstance()
    val storage = FirebaseStorage.getInstance().reference

    var companyName by remember { mutableStateOf("") }
    var companyAddress by remember { mutableStateOf("") }
    var companyType by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var profilePictureUri by remember { mutableStateOf<Uri?>(null) }
    var profilePictureUrl by remember { mutableStateOf("") }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { profilePictureUri = it }
    }

    // Load existing data
    LaunchedEffect(Unit) {
        user?.uid?.let { userId ->
            db.collection("employers").document(userId).get().addOnSuccessListener { document ->
                document?.let {
                    companyName = it.getString("companyName") ?: ""
                    companyAddress = it.getString("companyAddress") ?: ""
                    companyType = it.getString("companyType") ?: ""
                    description = it.getString("description") ?: ""
                    profilePictureUrl = it.getString("profilePictureUrl") ?: ""
                }
            }.addOnFailureListener { e ->
                Log.e("EditEmployerProfile", "Error fetching employer data", e)
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Edit Employer Profile", fontSize = 24.sp, color = Color.Black)

            Spacer(modifier = Modifier.height(20.dp))

            // Profile Picture
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clickable { launcher.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                if (profilePictureUri != null) {
                    Image(
                        painter = rememberImagePainter(profilePictureUri),
                        contentDescription = "Profile Picture",
                        modifier = Modifier.size(120.dp).clip(CircleShape)
                    )
                } else if (profilePictureUrl.isNotEmpty()) {
                    Image(
                        painter = rememberImagePainter(profilePictureUrl),
                        contentDescription = "Profile Picture",
                        modifier = Modifier.size(120.dp).clip(CircleShape)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Business,
                        contentDescription = "Profile Placeholder",
                        modifier = Modifier.size(120.dp),
                        tint = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Editable Fields
            CustomTextField(value = companyName, onValueChange = { companyName = it }, label = "Company Name")
            CustomTextField(value = companyAddress, onValueChange = { companyAddress = it }, label = "Company Address")
            CustomTextField(value = companyType, onValueChange = { companyType = it }, label = "Company Type")
            CustomTextField(value = description, onValueChange = { description = it }, label = "Description")

            Spacer(modifier = Modifier.height(20.dp))

            // Save Button
            Button(onClick = {
                user?.uid?.let { userId ->
                    val employerInfo = hashMapOf(
                        "companyName" to companyName,
                        "companyAddress" to companyAddress,
                        "companyType" to companyType,
                        "description" to description
                    )

                    profilePictureUri?.let { uri ->
                        val profilePicRef = storage.child("employer_pics/$userId.jpg")
                        profilePicRef.putFile(uri).addOnSuccessListener {
                            profilePicRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                                employerInfo["profilePictureUrl"] = downloadUrl.toString()
                                saveEmployerData(db, userId, employerInfo, navController)
                            }
                        }.addOnFailureListener { e ->
                            Log.e("EditEmployerProfile", "Error uploading profile picture", e)
                            saveEmployerData(db, userId, employerInfo, navController)
                        }
                    } ?: saveEmployerData(db, userId, employerInfo, navController) // Save without new picture
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

// Helper function to save employer data
fun saveEmployerData(db: FirebaseFirestore, userId: String, employerInfo: Map<String, Any>, navController: NavController) {
    db.collection("employers").document(userId).set(employerInfo)
        .addOnSuccessListener {
            navController.popBackStack()
        }
        .addOnFailureListener { e ->
            Log.e("EditEmployerProfile", "Error saving employer data", e)
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
