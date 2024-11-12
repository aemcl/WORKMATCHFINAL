package com.example.jobmatch.employee

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.jobmatch.R
import com.example.jobmatch.Routes
import com.google.firebase.firestore.FirebaseFirestore

data class JobNames(
    val name: String,
    val pic: Int,
    val companyName: String,
    val employmentType: String,
    val jobDescription: String,
    val salary: String
)

@Composable
fun RecommendedJobs(navController: NavController) {
    // State for storing job recommendations
    var jobList by remember { mutableStateOf(listOf<JobNames>()) }

    // Fetch jobs from Firestore when the Composable is first displayed
    LaunchedEffect(Unit) {
        val db = FirebaseFirestore.getInstance()
        db.collection("jobOpportunities").get()
            .addOnSuccessListener { result ->
                val jobs = result.map { document ->
                    JobNames(
                        name = document.getString("jobTitle") ?: "Unknown Job",
                        pic = R.drawable.fb, // Placeholder image; update as needed
                        companyName = document.getString("companyName") ?: "Unknown Company",
                        employmentType = document.getString("employmentType") ?: "Full-Time",
                        jobDescription = document.getString("jobDescription") ?: "No description provided.",
                        salary = document.getString("salary") ?: "Not specified"
                    )
                }
                jobList = jobs
            }
            .addOnFailureListener { exception ->
                Log.e("RecommendedJobs", "Error fetching job data: ${exception.localizedMessage}")
            }
    }

    // Display Recommended Jobs title
    Text(
        text = "Recommended Jobs",
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(all = 20.dp)
    )

    // Display the job list
    JobList(job = jobList, navController = navController)
}

@Composable
fun JobList(job: List<JobNames>, navController: NavController) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        items(job) { jobItem ->
            NamesDesign(jobItem, navController)
            Spacer(modifier = Modifier.size(8.dp))
        }
    }
}

@Composable
fun NamesDesign(nameOfJob: JobNames, navController: NavController) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable {
                // Navigate with job details as route parameters
                navController.navigate(Routes.jobDescription(
                    "${nameOfJob.name}/${nameOfJob.companyName}/${nameOfJob.employmentType}/${nameOfJob.jobDescription}/${nameOfJob.salary}"
                ))
            }
            .background(Color.LightGray)
    ) {
        Image(
            painter = painterResource(id = nameOfJob.pic),
            contentDescription = null,
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
        )
        Spacer(modifier = Modifier.width(16.dp))

        Column {
            Text(text = nameOfJob.name, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Text(text = nameOfJob.companyName, fontSize = 14.sp, color = Color.Gray)
        }
    }
}
