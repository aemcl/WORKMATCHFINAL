@file:Suppress("DEPRECATION")

package com.example.jobmatch.employer.pages

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.jobmatch.Routes
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

@Composable
fun EmployerProfile(navController: NavController) {
    val user = FirebaseAuth.getInstance().currentUser
    val db = FirebaseFirestore.getInstance()
    val storage = FirebaseStorage.getInstance().reference

    var companyInfo by remember { mutableStateOf<CompanyInfo?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showFullImage by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { uploadProfilePicture(it, user?.uid ?: "", storage, db) }
    }

    LaunchedEffect(Unit) {
        user?.uid?.let { userId ->
            db.collection("employers").document(userId).get().addOnSuccessListener { documentSnapshot ->
                companyInfo = documentSnapshot.toObject(CompanyInfo::class.java)
                isLoading = false
            }.addOnFailureListener { e ->
                Log.e("EmployerProfile", "Error fetching employer data", e)
                errorMessage = "Failed to load profile information."
                isLoading = false
            }
        }
    }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        if (errorMessage != null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = errorMessage ?: "", color = Color.Red, fontSize = 18.sp)
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
            ) {
                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .padding(15.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            modifier = Modifier.clickable { navController.navigateUp() }
                        )
                        Text(
                            text = "Employer Profile",
                            style = MaterialTheme.typography.titleMedium.copy(fontSize = 20.sp, color = Color.Black)
                        )
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Employer Profile",
                            modifier = Modifier.clickable { navController.navigate(Routes.editEmployerProfile) }
                        )
                    }


                    OutlinedCard(
                        border = BorderStroke(5.dp, Color.White),
                        colors = CardDefaults.cardColors(containerColor = Color(0xEE07D7D7)),
                        modifier = Modifier.size(150.dp).padding(top = 16.dp),
                        shape = CircleShape
                    ) {
                        val profilePicUrl = companyInfo?.profilePictureUrl
                        if (profilePicUrl != null) {
                            Image(
                                painter = rememberImagePainter(data = profilePicUrl, builder = { crossfade(true) }),
                                contentDescription = "Profile Picture",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clickable { showFullImage = true }
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Business,
                                contentDescription = "Default Profile Picture",
                                tint = Color.Gray,
                                modifier = Modifier
                                    .size(150.dp)
                                    .clickable { showFullImage = true }
                            )
                        }
                    }

                    if (showFullImage) {
                        Dialog(onDismissRequest = { showFullImage = false }) {
                            Surface(
                                modifier = Modifier.fillMaxSize(),
                                color = Color.Black
                            ) {
                                val profilePicUrl = companyInfo?.profilePictureUrl
                                if (profilePicUrl != null) {
                                    Image(
                                        painter = rememberImagePainter(data = profilePicUrl, builder = { crossfade(true) }),
                                        contentDescription = "Full Profile Picture",
                                        contentScale = ContentScale.Fit,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.Business,
                                        contentDescription = "Default Profile Picture",
                                        tint = Color.Gray,
                                        modifier = Modifier.size(300.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    companyInfo?.let {
                        ProfileInfoBox(label = "Company Name", value = it.companyName)
                        Spacer(modifier = Modifier.height(8.dp))
                        ProfileInfoBox(label = "Company Address", value = it.companyAddress)
                        Spacer(modifier = Modifier.height(8.dp))
                        ProfileInfoBox(label = "Company Type", value = it.companyType)
                        Spacer(modifier = Modifier.height(8.dp))
                        ProfileInfoBox(label = "Description", value = it.description)
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = { navController.navigate("changepass") },
                        modifier = Modifier.wrapContentWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE))
                    ) {
                        Text(text = "Change Password", color = Color.White)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = {
                            FirebaseAuth.getInstance().signOut()
                            navController.navigate("login")
                        },
                        modifier = Modifier.wrapContentWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                    ) {
                        Text(text = "Log Out", color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileInfoBox(label: String, value: String) {
    Box(
        modifier = Modifier
            .border(BorderStroke(1.dp, Brush.horizontalGradient(listOf(Color.Gray, Color.LightGray))), CircleShape)
            .width(250.dp)
            .height(50.dp)
            .padding(12.dp)
    ) {
        Text(
            text = "$label: $value",
            fontSize = 14.sp,
            modifier = Modifier.align(Alignment.CenterStart)
        )
    }
}

data class CompanyInfo(
    val companyName: String = "",
    val companyAddress: String = "",
    val companyType: String = "",
    val description: String = "",
    var profilePictureUrl: String? = null
)

fun uploadProfilePicture(uri: Uri, userId: String, storage: StorageReference, db: FirebaseFirestore) {
    val profilePicRef = storage.child("profile_pics/$userId.jpg")
    profilePicRef.putFile(uri)
        .addOnSuccessListener {
            profilePicRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                Log.d("Profile", "Profile picture uploaded successfully: $downloadUrl")
                db.collection("employers").document(userId).update("profilePictureUrl", downloadUrl.toString())
            }
        }
        .addOnFailureListener { e ->
            Log.e("Profile", "Failed to upload profile picture", e)
        }
}
