package com.example.jobmatch.employee

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.jobmatch.employee.pages.Job
import com.example.jobmatch.employee.pages.JobItem
import com.example.jobmatch.employee.pages.JobRepository

// Function to fetch and display all posted jobs in a carousel (horizontal scroll)
@Composable
fun AllJobsPosted(navController: NavController) {
    var jobs by remember { mutableStateOf<List<Job>>(emptyList()) }
    val scrollState = rememberLazyListState() // Initialize scroll state

    LaunchedEffect(Unit) {
        val jobRepository = JobRepository()
        jobs = jobRepository.getJobs() // Fetch all jobs from the database
    }

    // Display a carousel (horizontal scrollable list) of jobs
    if (jobs.isNotEmpty()) {
        LazyRow(
            state = scrollState,            // Attach the scroll state to the LazyRow
            modifier = Modifier.fillMaxSize(), // Ensure the row fills available space
        ) {
            items(jobs) { job ->
                JobItem(job = job, navController = navController) // Display each job in the carousel
            }
        }
    } else {
        Text("No jobs available.")
    }
}
