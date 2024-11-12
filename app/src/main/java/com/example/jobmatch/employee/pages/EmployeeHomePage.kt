package com.example.jobmatch.employee.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.jobmatch.employee.EmployeeSearch
import com.example.jobmatch.employee.JobOfferList
import com.example.jobmatch.employee.RecommendedJobs


@Composable
fun EmployeeHomePage(navController: NavController, userRole: String) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 20.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Search bar
        EmployeeSearch(navController = navController, userRole = userRole)

        // Recommended jobs
        RecommendedJobs(navController = navController)

        // Job Offers
        JobOfferList(navController = navController) // Call the JobOfferList composable here
    }
}
