
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.jobmatch.employee.pages.Job
import com.example.jobmatch.employee.pages.JobRepository
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JobDescription(navController: NavController, jobName: String) {
    val db = FirebaseFirestore.getInstance()
    var job by remember { mutableStateOf<Job?>(null) }
    val context = LocalContext.current
    // Fetch job details using the jobName passed in the navigation
    LaunchedEffect(jobName) {
        val jobRepository = JobRepository()
        job = jobRepository.getJobs().firstOrNull { it.jobName == jobName } // Find job by jobName
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Job Details") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,  // Use this instead
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        content = { padding ->
            Box(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.background)
                    .padding(padding)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                job?.let { jobDetails ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth() // Occupy full width while wrapping content height
                            .wrapContentHeight()
                            .align(Alignment.TopCenter),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Job Title
                            Text(
                                text = jobDetails.jobName,
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.Bold, // Make the text bold
                                    fontSize = 30.sp // Adjust the font size to make it bigger
                                ),
                                color = MaterialTheme.colorScheme.primary,
                                textAlign = TextAlign.Center
                            )

                            // Job Details
                            Text(
                                text = "Location: ${jobDetails.jobAddress}",
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontSize = 20.sp // Adjust the font size to make it bigger
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Text(
                                text = "Salary: ${jobDetails.jobSalary}",
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontSize = 20.sp // Adjusted font size to match Location text
                                ),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Divider(
                                color = MaterialTheme.colorScheme.surface,
                                thickness = 1.dp
                            )

                            // Job Description
                            Text(
                                text = "Required: ${jobDetails.jobDescription}",
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontSize = 20.sp // Same font size as Location
                                ),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            // Job workField
                            Text(
                                text = "Work Field: ${jobDetails.companyWorkField}",
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontSize = 20.sp // Same font size as Location
                                ),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Divider(
                                color = MaterialTheme.colorScheme.surface,
                                thickness = 1.dp
                            )
                            Text(
                                text = "Job Type: ${jobDetails.jobType}",
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontSize = 20.sp // Same font size as Location
                                ),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Text(
                                text = "Email: ${jobDetails.email}",
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontSize = 20.sp // Same font size as Location
                                ),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.size(8.dp))

                            // Apply Now Button
                            Button(
                                onClick = {
                                    val email = jobDetails.email
                                    if (email.isNullOrEmpty()) {
                                        // Show error message if no email is provided
                                        Toast.makeText(context, "No email address available for this job.", Toast.LENGTH_SHORT).show()
                                    } else {
                                        val subject = "Job Application: ${jobDetails.jobName}"
                                        val body = "Dear Employer,\n\nI am interested in applying for the ${jobDetails.jobName} position. Please find my details attached.\n\nBest regards,\n[Your Name]"

                                        // Create an Intent to send the email using ACTION_SEND
                                        val emailIntent = Intent(Intent.ACTION_SEND).apply {
                                            type = "message/rfc822"
                                            putExtra(Intent.EXTRA_EMAIL, arrayOf(email)) // Use the job's email address
                                            putExtra(Intent.EXTRA_SUBJECT, subject)
                                            putExtra(Intent.EXTRA_TEXT, body)
                                        }

                                        try {
                                            // Check if there's an app available to handle the intent
                                            val resolveInfo = context.packageManager.queryIntentActivities(emailIntent, 0)
                                            if (resolveInfo.isNotEmpty()) {
                                                context.startActivity(Intent.createChooser(emailIntent, "Choose an Email Client"))
                                            } else {
                                                Toast.makeText(context, "No email client found.", Toast.LENGTH_SHORT).show()
                                            }
                                        } catch (e: Exception) {
                                            // Handle any errors during the intent process
                                            Toast.makeText(context, "Failed to send email: ${e.message}", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                },
                                modifier = Modifier.padding(top = 16.dp)
                            ) {
                                Text(
                                    text = "Apply Now",
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    fontSize = 16.sp
                                )
                            }


                        }
                    }
                } ?: run {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    )
}
