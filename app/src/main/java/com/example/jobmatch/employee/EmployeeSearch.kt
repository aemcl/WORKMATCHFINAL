@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.jobmatch.employee

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun EmployeeSearch(navController: NavController,userRole: String) {
    var text by remember { mutableStateOf("") }
    var active by remember { mutableStateOf(false) }

    // Firebase Firestore instance
    val db = FirebaseFirestore.getInstance()

    // List to hold job/company suggestions from Firestore
    val jobItems = remember { mutableStateListOf<String>() }

    // Fetch job opportunities or popular companies from Firestore
    LaunchedEffect(Unit) {
        db.collection("jobOpportunities").get()
            .addOnSuccessListener { result ->
                jobItems.clear()
                for (document in result) {
                    val jobTitle = document.getString("title") ?: ""
                    val companyName = document.getString("company") ?: ""
                    if (jobTitle.isNotEmpty() && companyName.isNotEmpty()) {
                        jobItems.add("$companyName - $jobTitle")
                    }
                }
            }
            .addOnFailureListener { exception ->
                // Log error for debugging
                Log.e("EmployeeSearch", "Error fetching data: ${exception.localizedMessage}")
            }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        SearchBar(
            modifier = Modifier.fillMaxWidth(),
            query = text,
            onQueryChange = { text = it },
            onSearch = {
                // Handle search action
                active = false
            },
            active = active,
            onActiveChange = { active = it },
            placeholder = { Text(text = "Search Jobs or Companies") },
            leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = "Search Icon") },
            trailingIcon = {
                if (active) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close Icon",
                        modifier = Modifier.clickable {
                            if (text.isNotEmpty()) {
                                text = ""
                            } else {
                                active = false
                            }
                        }
                    )
                }
            },
            content = { /* You can add any additional elements here when search is active, if desired */ }
        )


        Spacer(modifier = Modifier.height(16.dp))

        // Carousel-style LazyRow to display popular job opportunities or companies
        Text(
            text = "Popular Companies Hiring",
            fontSize = 18.sp,
            modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
        )
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            items(jobItems) { job ->
                Column(
                    modifier = Modifier
                        .width(200.dp)
                        .padding(vertical = 8.dp)
                        .clickable {
                            // Navigate to job or company details
                            navController.navigate("jobDescription/$job")
                        },
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = "Company Logo",
                        modifier = Modifier.size(48.dp)
                    )
                    Text(
                        text = job,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(top = 8.dp),
                        maxLines = 2
                    )
                }
            }
        }
    }
}
