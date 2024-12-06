package com.example.jobmatch.employer

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.jobmatch.employer.pages.Employee
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject

data class Names(val name: String, val pic: Int, val highlighted: Boolean = false)

@Composable
fun RecommendedWorkers(navController: NavController, employerId: String) {
    var employeeList by remember { mutableStateOf(listOf<Employee>()) }
    val db = FirebaseFirestore.getInstance()

    LaunchedEffect(Unit) {
        db.collection("employees").get()
            .addOnSuccessListener { result ->
                val employees = result.mapNotNull { document ->
                    document.toObject<Employee>()
                }
                employeeList = employees
            }
            .addOnFailureListener { exception ->
                Log.e("RecommendedWorkers", "Error fetching data: ${exception.localizedMessage}")
            }
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        // Heading or Title for the carousel (optional)

        Spacer(modifier = Modifier.height(8.dp))

        // Carousel with LazyRow for horizontal scrolling
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp)  // Add padding around the items
        ) {
            items(employeeList) { employee ->
                EmployeeCard(employee = employee) { employeeId ->
                    navController.navigate("employeeProfile/$employeeId")
                }
                Spacer(modifier = Modifier.width(16.dp))  // Space between items
            }
        }
    }
}

@Composable
fun EmployeeCard(employee: Employee, onClick: (String) -> Unit) {
    Card(
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .width(180.dp)  // Fixed width for each card in the carousel
            .clickable { onClick(employee.fullName) },
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFFFF))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Profile Image or Default Icon
            if (employee.profilePicUri.isNotEmpty()) {
                Image(
                    painter = rememberAsyncImagePainter(employee.profilePicUri),
                    contentDescription = "Profile Picture of ${employee.fullName}",
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Default Profile Picture",
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(Color.LightGray)
                        .padding(8.dp),
                    tint = Color.White
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Name and Description
            Text(
                text = employee.fullName,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = employee.description,
                fontSize = 12.sp,
                color = Color.Gray,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}
