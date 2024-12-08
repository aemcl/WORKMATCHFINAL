package com.example.jobmatch.employee

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.jobmatch.employee.pages.EmployeeProfileData
import com.example.jobmatch.employee.pages.Job
import com.example.jobmatch.employee.pages.JobItem
import com.example.jobmatch.employee.pages.JobRepository
import com.example.jobmatch.employee.pages.recommendJobs
import com.example.jobmatch.employee.pages.relatedJobs
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.rememberPagerState
import com.google.firebase.firestore.FirebaseFirestore

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
@OptIn(ExperimentalPagerApi::class)
@Composable
fun RecommendedJobsScreen(navController: NavController, userId: String) {
    val db = FirebaseFirestore.getInstance()
    var jobs by remember { mutableStateOf<List<Job>>(emptyList()) }
    var userProfile by remember { mutableStateOf<EmployeeProfileData?>(null) }

    // Fetch jobs and user profile
    LaunchedEffect(userId) {
        val jobRepository = JobRepository()
        jobs = jobRepository.getJobs()
        userProfile = jobRepository.getEmployeeProfile(userId)
    }

    // When jobs and user profile are ready
    when {
        jobs.isNotEmpty() && userProfile != null -> {
            val recommendedJobs = recommendJobs(userProfile!!, jobs)

            Column(
                modifier = Modifier.wrapContentHeight() // Content wraps height without extra space
            ) {
                // Horizontal Pager for carousel
                val pagerState = rememberPagerState()

                HorizontalPager(
                    count = recommendedJobs.size,
                    state = pagerState,
                    modifier = Modifier
                        .height(150.dp) // Adjust height to fit carousel snugly
                ) { pageIndex ->
                    val job = recommendedJobs[pageIndex]
                    JobItem(job, navController)
                }

                // Pager Indicator
                HorizontalPagerIndicator(
                    pagerState = pagerState,
                    modifier = Modifier.align(Alignment.CenterHorizontally), // Center indicator below carousel
                    activeColor = MaterialTheme.colorScheme.primary,
                    inactiveColor = Color.Gray
                )
            }
        }
        jobs.isEmpty() -> Text(
            text = "No jobs available",
            modifier = Modifier.wrapContentHeight(),
            style = MaterialTheme.typography.bodyLarge
        )
        userProfile == null -> Text(
            text = "Loading profile...",
            modifier = Modifier.wrapContentHeight(),
            style = MaterialTheme.typography.bodyLarge
        )
        else -> Text(
            text = "Loading...",
            modifier = Modifier.wrapContentHeight(),
            style = MaterialTheme.typography.bodyLarge
        )
    }
}


@OptIn(ExperimentalPagerApi::class)
@Composable
fun RelatedJobsScreen(navController: NavController, userId: String) {
    val db = FirebaseFirestore.getInstance()
    var jobs by remember { mutableStateOf<List<Job>>(emptyList()) }
    var userProfile by remember { mutableStateOf<EmployeeProfileData?>(null) }

    // Fetch jobs and user profile
    LaunchedEffect(userId) {
        val jobRepository = JobRepository()
        jobs = jobRepository.getJobs()
        userProfile = jobRepository.getEmployeeProfile(userId)
    }

    // When jobs and user profile are ready
    when {
        jobs.isNotEmpty() && userProfile != null -> {
            val relatedJobs = relatedJobs(userProfile!!, jobs)

            Column(
                modifier = Modifier.wrapContentHeight() // Adjust height to content without extra padding
            ) {
                // Horizontal Pager for carousel
                val pagerState = rememberPagerState()

                HorizontalPager(
                    count = relatedJobs.size,
                    state = pagerState,
                    modifier = Modifier
                        .height(150.dp) // Compact height for carousel
                ) { pageIndex ->
                    val job = relatedJobs[pageIndex]
                    JobItem(job, navController)
                }

                // Pager Indicator
                HorizontalPagerIndicator(
                    pagerState = pagerState,
                    modifier = Modifier.align(Alignment.CenterHorizontally), // Center indicator below carousel
                    activeColor = MaterialTheme.colorScheme.primary,
                    inactiveColor = Color.Gray
                )
            }
        }
        jobs.isEmpty() -> Text(
            text = "No jobs available",
            modifier = Modifier.wrapContentHeight(), // Compactly wrap height
            style = MaterialTheme.typography.bodyLarge
        )
        userProfile == null -> Text(
            text = "Loading profile...",
            modifier = Modifier.wrapContentHeight(), // Compactly wrap height
            style = MaterialTheme.typography.bodyLarge
        )
        else -> Text(
            text = "Loading...",
            modifier = Modifier.wrapContentHeight(), // Compactly wrap height
            style = MaterialTheme.typography.bodyLarge
        )
    }
}
