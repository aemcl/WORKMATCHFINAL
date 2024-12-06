@file:Suppress("DEPRECATION")
package com.example.jobmatch.employee.pages

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
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
   val context = LocalContext.current
   var isLoading by remember { mutableStateOf(true) }
   var errorMessage by remember { mutableStateOf<String?>(null) }
   var userInfo by remember { mutableStateOf<EmployeeProfileData?>(null) }
   var showFullImage by remember { mutableStateOf(false) }
   var resumeUri by remember { mutableStateOf<Uri?>(null) }
   var showResumeDialog by remember { mutableStateOf(false) }

   // Launchers for file selection
   val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
      uri?.let { uploadProfilePicture(it, user?.uid ?: "", storage, db) }
   }
   val resumeLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
      uri?.let { uploadResume(it, user?.uid ?: "", storage, db) }
   }

   // Fetch user profile data from Firestore
   LaunchedEffect(user?.uid) {
      if (user != null) {
         db.collection("employees").document(user.uid).get()
            .addOnSuccessListener { documentSnapshot ->
               userInfo = documentSnapshot.toObject(EmployeeProfileData::class.java)
               if (userInfo != null) {
                  isLoading = false
               } else {
                  errorMessage = "Failed to load profile data."
                  isLoading = false
               }
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
               .padding(top = 10.dp)
         ) {
            // Wrap the whole profile content in a scrollable column
            Column(
               modifier = Modifier
                  .verticalScroll(rememberScrollState()) // Enable scrolling
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
                     imageVector = Icons.Filled.ArrowBack,
                     contentDescription = "Back",
                     modifier = Modifier.clickable { navController.navigate(Routes.employeeMainScreen) }
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

               // Profile Picture
               OutlinedCard(
                  border = BorderStroke(5.dp, Color.White),
                  colors = CardDefaults.cardColors(containerColor = Color(0xEE07D7D7)),
                  modifier = Modifier.size(150.dp).padding(top = 16.dp),
                  shape = CircleShape
               )
               {
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

               // Show Full Image Dialog
               if (showFullImage) {
                  Dialog(onDismissRequest = { showFullImage = false }) {
                     Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = Color.Black
                     ) {
                        val profilePicUri = userInfo?.profilePicUri
                        if (profilePicUri != null) {
                           Image(
                              painter = rememberImagePainter(data = profilePicUri, builder = { crossfade(true) }),
                              contentDescription = "Full Profile Picture",
                              contentScale = ContentScale.Fit,
                              modifier = Modifier.fillMaxSize()
                           )
                        } else {
                           Icon(
                              imageVector = Icons.Default.Person,
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
                  Spacer(modifier = Modifier.height(2.dp))
                  ProfileInfoBox(label = "Email", value = user?.email ?: "No email available")
                  Spacer(modifier = Modifier.height(2.dp))
                  ProfileInfoBox(label = "Description", value = it.description)
                  Spacer(modifier = Modifier.height(2.dp))
                  ProfileInfoBox(label = "Work Field", value = it.workField)
                  Spacer(modifier = Modifier.height(2.dp))
                  ProfileInfoBox(label = "Date of Birth", value = it.dateOfBirth)
                  Spacer(modifier = Modifier.height(2.dp))
                  ProfileInfoBox(label = "Address", value = it.address)
                  Spacer(modifier = Modifier.height(2.dp))
                  ProfileInfoBox(label = "Phone Number", value = it.phoneNumber)

                  // Handle Resume URI
                  it.resumeUri?.let { uri ->
                     resumeUri = Uri.parse(uri)
                  }

                  Spacer(modifier = Modifier.height(2.dp))

                  // View Documents Button
                  Box(
                     modifier = Modifier
                        .width(200.dp)
                        .height(60.dp)
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .clip(MaterialTheme.shapes.medium)
                        .background(Color.LightGray)
                        .border(BorderStroke(1.dp, Color.Gray), MaterialTheme.shapes.medium)
                        .clickable {
                           showResumeDialog = true
                        },
                     contentAlignment = Alignment.Center
                  ) {
                     Text(
                        text = "View Documents",
                        fontSize = 18.sp,
                        color = Color.Blue
                     )
                  }

                  // Show Document Dialog
                  if (showResumeDialog) {
                     DocumentDialog(uri = resumeUri, onDismiss = { showResumeDialog = false }, navController = navController)
                  }

               }

               Spacer(modifier = Modifier.height(2.dp))

               // Change Password Button
               Button(
                  onClick = { navController.navigate(Routes.changepass) },
                  modifier = Modifier.wrapContentWidth(),
                  colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE)),
               ) {
                  Text(text = "Change Password", color = Color.White)
               }

               Spacer(modifier = Modifier.height(2.dp))

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

@Composable
fun DocumentDialog(uri: Uri?, onDismiss: () -> Unit, navController: NavController) {
   if (uri != null) {
      AlertDialog(
         onDismissRequest = onDismiss,
         title = { Text(text = "Document View") },
         text = { Text(text = "View your resume or document here.") },
         confirmButton = {
            Button(
               onClick = {
                  navController.navigate("documentViewer/${Uri.encode(uri.toString())}")
               }
            ) {
               Text("View Document")
            }
         },
         dismissButton = {
            Button(onClick = onDismiss) {
               Text("Dismiss")
            }
         }
      )
   }
}


@Composable
fun DocumentViewerScreen(uri: String?) {
   if (uri != null) {
      val context = LocalContext.current
      val decodedUri = Uri.parse(uri)

      // Render the PDF using AndroidPdfViewer
      AndroidView(
         factory = { ctx ->
            com.github.barteksc.pdfviewer.PDFView(ctx, null).apply {
               fromUri(decodedUri) // Use the decoded URI
                  .enableSwipe(true) // Allow swipe gestures for navigation
                  .swipeHorizontal(false) // Vertical scrolling
                  .enableAnnotationRendering(true) // Render annotations
                  .password(null) // Use this if PDF has a password
                  .scrollHandle(null) // Use a scroll handle if needed
                  .load()
            }
         },
         modifier = Modifier.fillMaxSize()
      )

      // FloatingActionButton to open in an external PDF viewer
      Box(
         modifier = Modifier.fillMaxSize(),
         contentAlignment = Alignment.Center // Position the button at the center of the screen
      ) {
         FloatingActionButton(
            onClick = {
               val intent = Intent(Intent.ACTION_VIEW, decodedUri).apply {
                  setDataAndType(decodedUri, "application/pdf")
                  flags = Intent.FLAG_ACTIVITY_NO_HISTORY
               }
               context.startActivity(intent)
            },
            modifier = Modifier.padding(16.dp) // Optional padding around the button
         ) {
            Icon(Icons.Default.OpenInNew, contentDescription = "Open in External Viewer")
         }
      }
   } else {
      Box(
         modifier = Modifier.fillMaxSize(),
         contentAlignment = Alignment.Center
      ) {
         Text(text = "Failed to load document.", color = Color.Red, fontSize = 18.sp)
      }
   }
}


fun uploadProfilePicture(uri: Uri, userId: String, storage: StorageReference, db: FirebaseFirestore) {
   val profilePicRef = storage.child("employee_pics/$userId.jpg")
   profilePicRef.putFile(uri).addOnSuccessListener {
      profilePicRef.downloadUrl.addOnSuccessListener { downloadUrl ->
         db.collection("employees").document(userId).update("profilePicUri", downloadUrl.toString())
      }
   }.addOnFailureListener { e ->
      Log.e("Profile", "Failed to upload profile picture", e)
   }
}

fun uploadResume(uri: Uri, userId: String, storage: StorageReference, db: FirebaseFirestore) {
   val resumeRef = storage.child("resumes/$userId.pdf")
   resumeRef.putFile(uri).addOnSuccessListener {
      resumeRef.downloadUrl.addOnSuccessListener { downloadUrl ->
         db.collection("employees").document(userId).update("resumeUri", downloadUrl.toString())
      }
   }
}



data class EmployeeProfileData(
   val fullName: String = "",
   val dateOfBirth: String = "",
   val address: String = "",
   val phoneNumber: String = "",
   val description: String = "",
   val workField: String = "",
   val email: String = "",
   val profilePicUri: String? = null,
   val resumeUri: String? = null
)
