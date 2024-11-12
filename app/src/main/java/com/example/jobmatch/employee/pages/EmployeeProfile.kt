@file:Suppress("DEPRECATION")
package com.example.jobmatch.employee.pages

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
import androidx.compose.material.icons.filled.Person
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
import com.example.jobmatch.employer.pages.ProfileInfoBox
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

@Composable
fun EmployeeProfile(navController: NavController) {
   val user = FirebaseAuth.getInstance().currentUser
   val db = FirebaseFirestore.getInstance()
   val storage = FirebaseStorage.getInstance().reference

   var isLoading by remember { mutableStateOf(true) }
   var errorMessage by remember { mutableStateOf<String?>(null) }
   var userInfo by remember { mutableStateOf<EmployeeProfileData?>(null) }
   var showFullImage by remember { mutableStateOf(false) }
   // Launchers for file selection
   val profilePictureLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
      uri?.let { uploadProfilePicture(it, user?.uid ?: "", storage, db) }
   }
   val resumeLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
      uri?.let { uploadResume(it, user?.uid ?: "", storage, db) }
   }

   // Fetch user profile data from Firestore
   LaunchedEffect(user?.uid) {
      user?.uid?.let { userId ->
         db.collection("employees").document(userId).get()
            .addOnSuccessListener { documentSnapshot ->
               userInfo = documentSnapshot.toObject(EmployeeProfileData::class.java)
               isLoading = false
            }
            .addOnFailureListener { e ->
               Log.e("EmployeeProfile", "Error fetching user data", e)
               errorMessage = "Failed to load profile information."
               isLoading = false
            }
      }
   }

   // UI loading or error state handling
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
                     text = "Employee Profile",
                     style = MaterialTheme.typography.titleMedium.copy(fontSize = 20.sp, color = Color.Black)
                  )
                  Icon(
                     imageVector = Icons.Default.Edit,
                     contentDescription = "Edit Employee Profile",
                     modifier = Modifier.clickable { navController.navigate(Routes.editEmployeeProfile) }
                  )
               }


               OutlinedCard(
                  border = BorderStroke(5.dp, Color.White),
                  colors = CardDefaults.cardColors(containerColor = Color(0xEE07D7D7)),
                  modifier = Modifier.size(150.dp).padding(top = 16.dp),
                  shape = CircleShape
               ) {
                  val profilePicUrl = userInfo?.profilePicUri
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
                        imageVector = Icons.Default.Person,
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
                        val profilePicUrl = userInfo?.profilePicUri
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

               userInfo?.let {
                  ProfileInfoBox(label = "Full Name", value = it.fullName)
                  Spacer(modifier = Modifier.height(8.dp))
                  ProfileInfoBox(label = "Date of Birth", value = it.dateOfBirth)
                  Spacer(modifier = Modifier.height(8.dp))
                  ProfileInfoBox(label = "Address", value = it.address)
                  Spacer(modifier = Modifier.height(8.dp))
                  ProfileInfoBox(label = "Description", value = it.bio)
                  Spacer(modifier = Modifier.height(8.dp))
                  ProfileInfoBox(label = "Phone Number", value = it.phoneNumber)

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
// Employee profile data model
data class EmployeeProfileData(
   val fullName: String = "",
   val email: String = "",
   val address: String = "",
   val bio: String = "",
   val dateOfBirth: String = "",
   val phoneNumber: String = "",
   val profilePicUri: String? = null,
   val resumeUri: String? = null
)
fun uploadProfilePicture(uri: Uri, userId: String, storage: StorageReference, db: FirebaseFirestore) {
   val profileRef = storage.child("employees/$userId/profilePicture.jpg")
   profileRef.putFile(uri)
      .addOnSuccessListener {
         profileRef.downloadUrl.addOnSuccessListener { downloadUri ->
            db.collection("employees").document(userId)
               .update("profilePicture", downloadUri.toString())
         }
      }
      .addOnFailureListener { e ->
         Log.e("EmployeeProfile", "Profile picture upload failed", e)
      }
}

fun uploadResume(uri: Uri, userId: String, storage: StorageReference, db: FirebaseFirestore) {
   val resumeRef = storage.child("employees/$userId/resume.pdf")
   resumeRef.putFile(uri)
      .addOnSuccessListener {
         resumeRef.downloadUrl.addOnSuccessListener { downloadUri ->
            db.collection("employees").document(userId)
               .update("resumeUri", downloadUri.toString())
         }
      }
      .addOnFailureListener { e ->
         Log.e("EmployeeProfile", "Resume upload failed", e)
      }
}