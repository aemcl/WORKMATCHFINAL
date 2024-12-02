package com.example.jobmatch.employer

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun PostJob(navController: NavController) {
    val db = FirebaseFirestore.getInstance()

    var jobName by remember { mutableStateOf("") }
    var jobAddress by remember { mutableStateOf("") }
    var jobDescription by remember { mutableStateOf("") }
    var jobSalary by remember { mutableStateOf("") }
    var isSaving by remember { mutableStateOf(false) }

    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(text = "Post a New Job", style = MaterialTheme.typography.headlineSmall)

        OutlinedTextField(
            value = jobName,
            onValueChange = { jobName = it },
            label = { Text("Job Name") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = jobAddress,
            onValueChange = { jobAddress = it },
            label = { Text("Job Address") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = jobDescription,
            onValueChange = { jobDescription = it },
            label = { Text("Job Description") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = jobSalary,
            onValueChange = { jobSalary = it },
            label = { Text("Salary") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (jobName.isBlank() || jobAddress.isBlank() || jobDescription.isBlank() || jobSalary.isBlank()) {
                    Toast.makeText(context, "All fields are required.", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                val salary = jobSalary.toIntOrNull()
                if (salary == null) {
                    Toast.makeText(context, "Enter a valid salary.", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                isSaving = true
                saveJobToFirebase(
                    jobName = jobName,
                    jobAddress = jobAddress,
                    jobDescription = jobDescription,
                    jobSalary = salary,
                    db = db
                ) { success ->
                    isSaving = false
                    if (success) {
                        navController.navigateUp()
                    } else {
                        Toast.makeText(
                            context,
                            "Failed to save job. Please try again.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            },
            enabled = !isSaving,
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Text("Post Job")
        }
    }
}

fun saveJobToFirebase(
    jobName: String,
    jobAddress: String,
    jobDescription: String,
    jobSalary: Int,
    db: FirebaseFirestore,
    onComplete: (Boolean) -> Unit
) {
    val jobData = hashMapOf(
        "jobName" to jobName,
        "jobAddress" to jobAddress,
        "jobDescription" to jobDescription,
        "jobSalary" to jobSalary
    )

    db.collection("jobs")
        .add(jobData)
        .addOnSuccessListener { onComplete(true) }
        .addOnFailureListener { onComplete(false) }
}