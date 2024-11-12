@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.jobmatch.employer.pages

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.jobmatch.Routes
import com.example.jobmatch.employer.EmployerSearch
import com.example.jobmatch.employer.RecommendedWorkers

@Composable
fun EmployerHomePage(navController: NavController, userRole: String) {
    // Employer search bar with userRole parameter
    EmployerSearch(navController = navController, userRole = userRole)

    // Recommended Workers and Add Job Button
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 20.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Recommended workers list
        RecommendedWorkers()

        // Add job button with label
        Box(modifier = Modifier.fillMaxSize()) {
            Icon(
                imageVector = Icons.Default.AddCircle,
                contentDescription = "Add Job",
                tint = Color(0XFFff8e2b),
                modifier = Modifier
                    .size(80.dp)
                    .clickable { navController.navigate(Routes.workInformation) }
                    .align(Alignment.BottomEnd)
                    .padding(end = 30.dp, bottom = 20.dp)
            )
            Text(
                text = "Add Job",
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 42.dp, bottom = 10.dp),
                color = Color(0xFFff8e2b)
            )
        }
    }
}
