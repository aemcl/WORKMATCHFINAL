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
import coil.compose.rememberAsyncImagePainter
import com.example.jobmatch.Routes
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

@Composable
fun EmployerForm(navController: NavController) {
    var companyName by remember { mutableStateOf("") }
    var companyAddress by remember { mutableStateOf("") }
    var companyType by remember { mutableStateOf("") }

    var description by remember { mutableStateOf("") }
    var companyWorkField by remember { mutableStateOf("") }
    var profilePictureUri by remember { mutableStateOf<Uri?>(null) }

    val scrollState = rememberScrollState()
    val context = LocalContext.current
    val user = FirebaseAuth.getInstance().currentUser
    val storage = FirebaseStorage.getInstance().reference

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { profilePictureUri = it }
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
            text = "Employer Form",
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
            if (profilePictureUri != null) {
                Image(
                    painter = rememberAsyncImagePainter(profilePictureUri),
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
            value = companyName,
            onValueChange = { companyName = it },
            shape = RoundedCornerShape(12.dp),
            label = { Text(text = "Company Name:") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = companyAddress,
            onValueChange = { companyAddress = it },
            shape = RoundedCornerShape(12.dp),
            label = { Text(text = "Company Address:") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = companyType,
            onValueChange = { companyType = it },
            shape = RoundedCornerShape(12.dp),
            label = { Text(text = "Company Type:") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            shape = RoundedCornerShape(12.dp),
            label = { Text(text = "Description:") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(20.dp))
        OutlinedTextField(
            value = companyWorkField,
            onValueChange = { companyWorkField = it },
            shape = RoundedCornerShape(12.dp),
            label = { Text(text = "Company WorkField:") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                if (companyName.isEmpty() || companyAddress.isEmpty() || companyType.isEmpty() || description.isEmpty() || companyWorkField.isEmpty()) {
                    Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                } else {
                    user?.uid?.let { userId ->
                        val employerData = hashMapOf(
                            "companyName" to companyName,
                            "companyAddress" to companyAddress,
                            "companyType" to companyType,
                            "companyWorkField" to companyWorkField,
                            "description" to description,
                            "role" to "Employer"
                        )

                        profilePictureUri?.let { uri ->
                            val profilePicRef = storage.child("employer_profile_pics/$userId.jpg")
                            profilePicRef.putFile(uri)
                                .addOnSuccessListener {
                                    profilePicRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                                        employerData["profilePictureUrl"] = downloadUrl.toString()
                                        saveEmployerData(employerData, userId, navController, context)
                                    }
                                }
                                .addOnFailureListener {
                                    saveEmployerData(employerData, userId, navController, context)
                                }
                        } ?: saveEmployerData(employerData, userId, navController, context)
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

fun saveEmployerData(employerData: HashMap<String, String>, userId: String, navController: NavController, context: android.content.Context) {
    val db = FirebaseFirestore.getInstance()
    db.collection("employers").document(userId).set(employerData)
        .addOnSuccessListener {
            markFormAsCompleted(userId, context)
            navController.navigate(Routes.employerMainScreen)
        }
        .addOnFailureListener {
            Toast.makeText(context, "Failed to save data. Try again.", Toast.LENGTH_SHORT).show()
        }
}

fun markFormAsCompleted(userId: String, context: android.content.Context) {
    val firestore = FirebaseFirestore.getInstance()
    firestore.collection("employers").document(userId).update("formCompleted", true)
        .addOnSuccessListener {
            Toast.makeText(context, "Form completed successfully", Toast.LENGTH_SHORT).show()
        }
        .addOnFailureListener {
            Toast.makeText(context, "Failed to mark form as completed. Try again.", Toast.LENGTH_SHORT).show()
        }
}
