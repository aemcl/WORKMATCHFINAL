package com.example.jobmatch.employer

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun PostJob(navController: NavController) {
    val db = FirebaseFirestore.getInstance()

    var jobName by remember { mutableStateOf("") }
    var jobAddress by remember { mutableStateOf("") }
    var jobDescription by remember { mutableStateOf("") }
    var jobSalary by remember { mutableStateOf("") }
    var companyWorkField by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var jobType by remember { mutableStateOf("") }
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
            label = { Text("Required") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = companyWorkField,
            onValueChange = { companyWorkField = it },
            label = { Text("Company WorkField") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = jobSalary,
            onValueChange = { jobSalary = it },
            label = { Text("Salary") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
        )
        OutlinedTextField(
            value = jobType,
            onValueChange = { jobType = it },
            label = { Text("Job Type") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (jobName.isBlank() || jobAddress.isBlank() || jobDescription.isBlank() || jobSalary.isBlank() || companyWorkField.isBlank()) {
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
                    companyWorkField = companyWorkField ,
                    jobType = jobType,
                    email=email,
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
    companyWorkField: String,
    jobType:String,
    email:String,
    db: FirebaseFirestore,
    onComplete: (Boolean) -> Unit
) {
    val jobData = hashMapOf(
        "jobName" to jobName,
        "jobAddress" to jobAddress,
        "jobDescription" to jobDescription,
        "companyWorkField" to companyWorkField,
        "jobSalary" to jobSalary,
        "jobType" to jobType,
        "email" to email,
    )


    db.collection("jobs")
        .add(jobData)
        .addOnSuccessListener { onComplete(true) }
        .addOnFailureListener { onComplete(false) }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostedJobs(navController: NavController) {
    val db = FirebaseFirestore.getInstance()
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()

    // State for managing jobs and loading state
    val postedJobs = remember { mutableStateListOf<Job>() }
    var isLoading by remember { mutableStateOf(true) }

    // Ensure deletedJob is inside PostedJobs and accessible
    var deletedJob by remember { mutableStateOf<Job?>(null) }
    var showSnackbar by remember { mutableStateOf(false) }

    // Get current user email from Firebase Authentication
    val currentUserEmail = auth.currentUser?.email

    if (currentUserEmail != null) {
        // Fetch jobs based on current user's email
        LaunchedEffect(currentUserEmail) {
            db.collection("jobs")
                .whereEqualTo("email", currentUserEmail)
                .get()
                .addOnSuccessListener { result ->
                    postedJobs.clear() // Clear previous jobs
                    if (result.isEmpty) {
                        Log.d("PostedJobs", "No jobs found for this user.")
                    } else {
                        for (document in result) {
                            val job = document.toObject(Job::class.java).copy(jobId = document.id)
                            postedJobs.add(job) // Add job to the list
                        }
                        Log.d("PostedJobs", "Found ${postedJobs.size} jobs")
                    }
                    isLoading = false
                }
                .addOnFailureListener { exception ->
                    Log.e("PostedJobs", "Error fetching jobs", exception)
                    isLoading = false
                    Toast.makeText(context, "Failed to load posted jobs.", Toast.LENGTH_SHORT).show()
                }
        }
    } else {
        // If no user is logged in
        Toast.makeText(context, "User not logged in", Toast.LENGTH_SHORT).show()
        isLoading = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Posted Jobs") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
        snackbarHost = {
            if (showSnackbar && deletedJob != null) {
                Snackbar(
                    action = {
                        TextButton(onClick = {
                            // Undo deletion: Add the job back to the list
                            deletedJob?.let { job ->
                                postedJobs.add(job) // Add the job back to the list
                                deletedJob = null // Clear the deleted job reference
                            }
                        }) {
                            Text("UNDO")
                        }
                    }
                ) {
                    Text("Job deleted")
                }

            }
        }
    ) { padding ->
        // UI Rendering based on loading state
        Box(modifier = Modifier.padding(padding)) {
            when {
                isLoading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                postedJobs.isEmpty() -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No jobs posted yet.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
                else -> {
                    LazyColumn {
                        items(postedJobs) { job ->
                            JobCard(
                                navController = navController,
                                job = job,
                                onDelete = { job ->
                                    // Remove from list and store for undo
                                    postedJobs.remove(job)
                                    deletedJob = job
                                    showSnackbar = true

                                    // Delete job from Firestore
                                    db.collection("jobs").document(job.jobId).delete()
                                        .addOnSuccessListener {
                                            Toast.makeText(context, "Job deleted successfully", Toast.LENGTH_SHORT).show()
                                        }
                                        .addOnFailureListener {
                                            Toast.makeText(context, "Failed to delete job", Toast.LENGTH_SHORT).show()
                                        }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun JobCard(navController: NavController, job: Job, onDelete: (Job) -> Unit) {
    // State to control the delete confirmation dialog
    var showDeleteDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Job Card Design
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                // Job Details
                Text(
                    text = job.jobName,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text("Location: ${job.jobAddress}", style = MaterialTheme.typography.bodyLarge)
                Text("Salary: \$${job.jobSalary}", style = MaterialTheme.typography.bodyLarge)
                Text("Description: ${job.jobDescription}", style = MaterialTheme.typography.bodyLarge)
                Text("Job Type: ${job.jobType}", style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.height(16.dp))

                // Delete Button
                Button(
                    onClick = { showDeleteDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text(text = "Delete", color = MaterialTheme.colorScheme.onError)
                }
            }
        }
    }

    // Confirmation Dialog for Delete
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Confirm Delete") },
            text = { Text("Are you sure you want to delete this job permanently?") },
            confirmButton = {
                TextButton(onClick = {
                    onDelete(job) // Notify parent composable about deletion
                    showDeleteDialog = false
                }) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

// Job Data Class (same as before)
data class Job(
    var jobId:String="",
    var jobName: String = "",
    var jobAddress: String = "",
    var jobDescription: String = "",
    var companyWorkField: String = "",
    var jobSalary: Int = 0,
    var jobType: String = "",
    var email: String = ""
)
