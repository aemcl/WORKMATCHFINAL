@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.jobmatch.employee

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.jobmatch.Routes
import com.google.firebase.firestore.FirebaseFirestore

// Assuming a basic structure for the Job class
data class Job(
    val jobName: String = "",
    val jobDescription: String = "",
    val companyName: String = ""
)
@Composable
fun EmployeeSearchBar(navController: NavController, userRole: String) {
    var text by remember { mutableStateOf("") }
    var searchResults by remember { mutableStateOf<List<Job>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }

    val db = FirebaseFirestore.getInstance()

    // Function to fetch job results based on the query
    fun fetchJobResults(query: String) {
        if (query.isNotEmpty()) {
            isLoading = true
            db.collection("jobs")
                .orderBy("jobName")
                .startAt(query)
                .endAt(query + "\uf8ff")  // Search for jobs starting with the query
                .get()
                .addOnSuccessListener { documents ->
                    searchResults = documents.mapNotNull { it.toObject(Job::class.java) }
                    isLoading = false
                }
                .addOnFailureListener { exception ->
                    Log.e("EmployeeSearchBar", "Error fetching jobs: ${exception.localizedMessage}")
                    searchResults = emptyList()
                    isLoading = false
                }
        } else {
            searchResults = emptyList()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(
                width = 1.dp,
                color = Color.Gray.copy(alpha = 0.5f),
                shape = RoundedCornerShape(12.dp)
            )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search Icon",
                tint = Color.Gray
            )
            Spacer(modifier = Modifier.width(8.dp))
            TextField(
                value = text,
                onValueChange = {
                    text = it
                    fetchJobResults(it) // Trigger the search as the user types
                },
                placeholder = { Text("Search Job", color = Color.Gray) },
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = MaterialTheme.colorScheme.primary
                ),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }

    // Display loading indicator while fetching data
    if (isLoading) {
        CircularProgressIndicator(modifier = Modifier.padding(16.dp))
    }

    // Display the search results (jobs)
    if (searchResults.isNotEmpty()) {
        LazyColumn {
            items(searchResults) { job ->
                JobItem(job = job, navController = navController) // Reuse JobItem composable to display each job
            }
        }
    } else if (text.isNotEmpty()) {
        Text(
            text = "No jobs found for \"$text\".",
            modifier = Modifier.padding(16.dp),
            color = Color.Gray
        )
    }
}

// Sample JobItem composable for displaying job details
@Composable
fun JobItem(job: Job, navController: NavController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                navController.navigate("${Routes.jobDescription}/${job.jobName}")
            }
            .padding(16.dp)
    ) {
        Column {
            Text(
                text = job.jobName, // Display the job name as it is
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            Text(
                text = job.jobDescription, // Display the job description as it is
                fontSize = 14.sp,
                color = Color.Gray
            )
        }
    }
}