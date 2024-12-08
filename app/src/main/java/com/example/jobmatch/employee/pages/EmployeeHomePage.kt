package com.example.jobmatch.employee.pages

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.jobmatch.Routes
import com.example.jobmatch.employee.AllJobsPosted
import com.example.jobmatch.employee.EmployeeSearchBar
import com.example.jobmatch.employee.RecommendedJobsScreen
import com.example.jobmatch.employee.RelatedJobsScreen
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmployeeHomePage(navController: NavController, userId: String) {
    var searchQuery by remember { mutableStateOf("") }
    var searchResults by remember { mutableStateOf<List<Job>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var recentSearches by remember { mutableStateOf<List<String>>(emptyList()) }
    val db = FirebaseFirestore.getInstance()
    val scrollState = rememberScrollState()

    // Fetch recent searches when the composable launches
    LaunchedEffect(Unit) {
        fetchRecentSearches(db) { searches ->
            recentSearches = searches
        }
    }

    // Function to fetch job results based on a query
    fun fetchJobResults(query: String) {
        if (query.isNotEmpty()) {
            isLoading = true
            db.collection("jobs")
                .orderBy("jobName")
                .startAt(query)
                .endAt(query + "\uf8ff")
                .get()
                .addOnSuccessListener { documents ->
                    searchResults = documents.mapNotNull { it.toObject(Job::class.java) }
                    isLoading = false
                }
                .addOnFailureListener { exception ->
                    Log.e("EmployeeHomePage", "Error fetching jobs: ${exception.localizedMessage}")
                    searchResults = emptyList()
                    isLoading = false
                }
        } else {
            searchResults = emptyList()
        }
    }

    Scaffold(
        topBar = {
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color.White)
        ) {
            // Employee Search Bar
            EmployeeSearchBar(
                navController = navController,
                userRole = userId // Pass the userId or userRole as necessary
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Show loading indicator while fetching data
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.padding(16.dp))
            }

            // Display search results
            if (searchQuery.isNotEmpty()) {
                SectionTitle("Search Results")
                if (searchResults.isEmpty()) {
                    Text(
                        text = "No jobs found matching \"$searchQuery\".",
                        modifier = Modifier.padding(16.dp),
                        color = Color.Gray
                    )
                } else {
                    LazyColumn {
                        items(searchResults) { job ->
                            Job(job, navController)
                        }
                    }
                }
            } else {
                // Display recent searches and recommended jobs
                if (recentSearches.isNotEmpty()) {
                    SectionTitle("Recent Searches")
                    LazyColumn {
                        items(recentSearches) { searchTerm ->
                            RecentSearchItem(searchTerm) { selectedTerm ->
                                searchQuery = selectedTerm
                                fetchJobResults(selectedTerm)
                            }
                        }
                    }
                }

                // Recommended Jobs Section
                    SectionTitle("Recommended Jobs")
                    RecommendedJobsScreen(navController, userId)

                    Spacer(modifier = Modifier.height(2.dp))

                    SectionTitle("Related Jobs")
                    RelatedJobsScreen(navController, userId)

                    Spacer(modifier = Modifier.height(2.dp))

                    SectionTitle("All Jobs")
                    AllJobsPosted(navController)

            }
        }
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(8.dp)
    )
}

@Composable
fun Job(job: Job, navController: NavController) {
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
                text = job.jobName ?: "Unknown Job",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            Text(
                text = job.jobDescription ?: "No Description",
                fontSize = 14.sp,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun RecentSearchItem(searchTerm: String, onClick: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(searchTerm) }
            .padding(8.dp)
    ) {
        Text(
            text = searchTerm,
            fontSize = 16.sp,
            color = Color.Gray
        )
    }
}

private fun fetchRecentSearches(db: FirebaseFirestore, onResult: (List<String>) -> Unit) {
    db.collection("recentSearches")
        .get()
        .addOnSuccessListener { documents ->
            val searches = documents.mapNotNull { it.getString("searchTerm") }
            onResult(searches)
        }
        .addOnFailureListener { exception ->
            Log.e("EmployeeHomePage", "Error fetching recent searches: ${exception.localizedMessage}")
            onResult(emptyList())
        }
}
