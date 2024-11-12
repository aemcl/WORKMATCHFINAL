package com.example.jobmatch.employer

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun JobDescription(
    jobName: String,
    companyName: String,
    employmentType: String,
    jobDescription: String,
    salary: String
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top
    ) {
        // Job Title
        Text(text = "Job Title: $jobName", fontSize = 24.sp)

        // Company Name
        Text(text = "Company: $companyName", fontSize = 20.sp)

        // Employment Type
        Text(text = "Type: $employmentType", fontSize = 16.sp)

        // Job Description
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Job Description:", fontSize = 20.sp, color = Color.Gray)
        Text(text = jobDescription, fontSize = 16.sp)

        // Salary
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Salary: $salary", fontSize = 20.sp, color = Color.Gray)

        // Apply Now Button
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            // Handle apply action here
        }) {
            Text(text = "Apply Now")
        }
    }
}


