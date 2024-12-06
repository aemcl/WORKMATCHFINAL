package com.example.jobmatch.employee

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
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

// Function to fetch and display all posted jobs in a scrollable state
@Composable
fun AllJobsPosted(navController: NavController) {
    var jobs by remember { mutableStateOf<List<Job>>(emptyList()) }
    val scrollState = rememberLazyListState() // Initialize scroll state

    LaunchedEffect(Unit) {
        val jobRepository = JobRepository()
        jobs = jobRepository.getJobs() // Fetch all jobs from the database
    }

    // Ensure the LazyColumn is within a scrollable container
    if (jobs.isNotEmpty()) {
        LazyColumn(
            state = scrollState,            // Attach the scroll state to the LazyColumn
            modifier = Modifier.fillMaxSize() // Ensures the column fills available space
        ) {
            items(jobs) { job ->
                //JobItem(job, navController = navController) // Reuse the JobItem composable to display each job
            }
        }
    } else {
        Text("No jobs available.")
    }
}
