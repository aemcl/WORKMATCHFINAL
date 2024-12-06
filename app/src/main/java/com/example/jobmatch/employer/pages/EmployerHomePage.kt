@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.jobmatch.employer.pages

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.jobmatch.Routes
import com.example.jobmatch.employer.RecommendedWorkersScreen
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject

// Data class for Employee


@Composable
fun EmployerHomePage(navController: NavController, employerId: String) {
    val db = FirebaseFirestore.getInstance()
    val searchText = remember { mutableStateOf("") }
    val recommendedEmployees = remember { mutableStateOf<List<Employee>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Fetch employee data from Firestore
    LaunchedEffect(Unit) {
        isLoading = true // Set loading state to true at the start
        try {
            db.collection("employees")
                .get()
                .addOnSuccessListener { querySnapshot ->
                    val employees = querySnapshot.documents.mapNotNull { it.toObject<Employee>() }
                    recommendedEmployees.value = employees // Update the state
                    isLoading = false // Loading complete
                }
                .addOnFailureListener { e ->
                    Log.e("FirestoreError", "Error fetching employees: ", e)
                    errorMessage = "Error fetching employee data."
                    isLoading = false // Ensure loading stops even on failure
                }
        } catch (e: Exception) {
            Log.e("FirestoreError", "Unexpected error: ", e)
            errorMessage = "Unexpected error occurred."
            isLoading = false
        }
    }
    // UI rendering
    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {
        // Search bar
        OutlinedTextField(
            value = searchText.value,
            onValueChange = { searchText.value = it },
            label = { Text("Search Employee") },
            leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = "Search Icon") },
            trailingIcon = {
                if (searchText.value.isNotEmpty()) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Clear Text",
                        modifier = Modifier.clickable { searchText.value = "" }
                    )
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Recommended Employees",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        RecommendedWorkersScreen(navController, employerId)
        Spacer(modifier = Modifier.height(16.dp))


        Text(
            text = "All Employees",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Show loading indicator while fetching data
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            if (errorMessage != null) {
                Text(
                    text = errorMessage!!,
                    color = Color.Red,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            } else {
                // Filter employees based on search text
                val filteredEmployees = recommendedEmployees.value.filter {
                    it.fullName.contains(searchText.value, ignoreCase = true)
                }

                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    items(filteredEmployees) { employee ->
                        EmployeeCard(employee) { selectedEmployeeId ->
                            // Navigate to EmployeeProfileScreen with employeeId
                            navController.navigate("employeeProfile/${employee.fullName}")
                        }
                    }
                }
            }
        }
    }

    // Floating Action Button (FAB) for adding a job
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(end = 40.dp, bottom = 120.dp),
        contentAlignment = Alignment.BottomEnd
    ) {
        FloatingActionButton(
            onClick = { navController.navigate(Routes.addJob) },
            containerColor = Color(0xFF007bff)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add Job",
                tint = Color.White
            )
        }
    }
}

@Composable
fun EmployeeCard(employee: Employee, onClick: (String) -> Unit) {
    Card(
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick(employee.fullName) }, // Use employee.fullName or actual ID
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFFFF))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Profile Picture
            if (employee.profilePicUri.isNotEmpty()) {
                Image(
                    painter = rememberAsyncImagePainter(employee.profilePicUri),
                    contentDescription = "Profile Picture of ${employee.fullName}",
                    modifier = Modifier
                        .size(50.dp)
                        .background(Color(0xFFD4C4FB), shape = CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                // Fallback to a default image
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Default Profile Picture",
                    modifier = Modifier
                        .size(50.dp)
                        .background(Color(0xFFD4C4FB), shape = CircleShape)
                        .padding(8.dp), // Adjust padding to ensure the icon fits well inside the background
                    tint = Color.White // Change the color of the icon (optional)
                )

            }

            Spacer(modifier = Modifier.width(12.dp))

            // Employee Details
            Column {
                Text(text = employee.fullName, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = employee.description, fontSize = 14.sp, color = Color.Gray)
            }
        }
    }
}


@Composable
fun EmployeeProfileScreen(navController: NavController, employeeId: String) {
    val db = FirebaseFirestore.getInstance()
    var employee by remember { mutableStateOf<Employee?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var showFullImage by remember { mutableStateOf(false) }
    val context = LocalContext.current // Access the current context for launching intents

    // Fetch employee details from Firestore based on the passed employeeId
    LaunchedEffect(employeeId) {
        db.collection("employees").whereEqualTo("fullName", employeeId).get()
            .addOnSuccessListener { result ->
                val doc = result.documents.firstOrNull()
                employee = doc?.toObject<Employee>()
                isLoading = false
            }
            .addOnFailureListener {
                isLoading = false
                // Handle error (e.g., show a toast or error message)
            }
    }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else if (employee != null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .background(Color(0xFFF9F9F9)) // Background color for the profile
        ) {
            // Profile Picture with a clickable image for enlarged view
            Box(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(8.dp)
            ) {
                if (employee!!.profilePicUri.isEmpty()) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Default Profile Icon",
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .border(2.dp, Color.Gray, CircleShape)
                            .background(Color.LightGray)
                            .clickable {
                                showFullImage = true
                            },
                        tint = Color.Gray
                    )
                } else {
                    val painter = rememberAsyncImagePainter(employee!!.profilePicUri)
                    Image(
                        painter = painter,
                        contentDescription = "Profile Picture of ${employee!!.fullName}",
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .border(2.dp, Color.Gray, CircleShape)
                            .clickable {
                                showFullImage = true
                            },
                        contentScale = ContentScale.Crop
                    )
                }
            }


            Spacer(modifier = Modifier.height(16.dp))

            // Name and Description section
            Text(
                text = employee!!.fullName,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = employee!!.description,
                fontSize = 16.sp,
                color = Color.Gray,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Additional Information Section
            Column(modifier = Modifier.fillMaxWidth()) {
                // Address
                Text(text = "Address:", fontWeight = FontWeight.Bold)
                Text(text = employee!!.address, color = Color.Gray)
                Spacer(modifier = Modifier.height(16.dp))

                // Date of Birth
                Text(text = "Date of Birth:", fontWeight = FontWeight.Bold)
                Text(text = employee!!.dateOfBirth, color = Color.Gray)
                Spacer(modifier = Modifier.height(16.dp))

                // Phone Number
                Text(text = "Phone Number:", fontWeight = FontWeight.Bold)
                Text(text = employee!!.phoneNumber, color = Color.Gray)
                Spacer(modifier = Modifier.height(16.dp))

                // Resume Link
                Text(text = "Resume:", fontWeight = FontWeight.Bold)
                Text(
                    text = employee!!.resumeUri.ifEmpty { "No resume uploaded" },
                    color = if (employee!!.resumeUri.isNotEmpty()) Color.Blue else Color.Gray,
                    modifier = Modifier.clickable(enabled = employee!!.resumeUri.isNotEmpty()) {
                        // Open resume URL if available
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(employee!!.resumeUri))
                        context.startActivity(intent)
                    }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Back button
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
            }
        }
    }

    // Show full image in a dialog
    if (showFullImage) {
        Dialog(onDismissRequest = { showFullImage = false }) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.Black.copy(alpha = 0.8f)) // Background for better visibility
                    .clickable { showFullImage = false } // Close the dialog when clicking on the image
            ) {
                if (employee!!.profilePicUri.isEmpty()) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Default Profile Icon",
                        modifier = Modifier
                            .size(200.dp)
                            .align(Alignment.Center)
                            .background(Color.Gray, shape = CircleShape)
                            .padding(16.dp), // Inner padding for a better look
                        tint = Color.White
                    )
                } else {
                    Image(
                        painter = rememberAsyncImagePainter(employee!!.profilePicUri),
                        contentDescription = "Full Image of ${employee!!.fullName}",
                        modifier = Modifier.fillMaxWidth(),
                        contentScale = ContentScale.Fit
                    )
                }
            }

        }
    }
}

// Updated Employee data class
data class Employee(
    val fullName: String = "",
    val description: String = "",
    val workField: String = "",
    val profilePicUri: String = "",
    val address: String = "",
    val dateOfBirth: String = "",
    val phoneNumber: String = "",
    val resumeUri: String = ""
)

