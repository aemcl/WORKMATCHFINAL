package com.example.jobmatch.employee.pages

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.accompanist.pager.*
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

// Data model for jobs
data class Job(
    val jobName: String = "",
    val jobDescription: String = "",
    val jobSalary: Int = 0,
    val jobAddress: String = "",
    val companyWorkField: String="",
    val jobType: String="",
    val email: String="",
)

// Utility functions for computing scores
fun computeSkillMatch(userSkills: String, jobSkills: String): Double {
    val userSet = userSkills.split(",").map { it.trim().lowercase().uppercase() }.toSet()
    val jobSet = jobSkills.split(",").map { it.trim().lowercase().uppercase() }.toSet()

    val intersection = userSet.intersect(jobSet).size
    val union = userSet.union(jobSet).size

    return if (union == 0) 0.0 else intersection.toDouble() / union.toDouble()
}

fun computeLocationScore(userLocation: String, jobLocation: String): Double {
    return if (userLocation.equals(jobLocation, ignoreCase = true)) 1.0 else 0.0
}
fun computeWorkFieldScore(userWorkField: String, companyWorkField: String): Double {
    return if (userWorkField.equals(companyWorkField, ignoreCase = true)) 1.0 else 0.0
}
fun computeJobScore(user: EmployeeProfileData, job: Job): Double {
    val skillScore = computeSkillMatch(user.description, job.jobDescription)
    val locationScore = computeLocationScore(user.address, job.jobAddress)
    val workFieldScore= computeWorkFieldScore(user.workField, job.companyWorkField )
    // Weights for scoring
    val skillWeight = 0.7
    val locationWeight = 0.3
    val workFieldWeight = 0.3

    return (skillScore * skillWeight) + (locationScore * locationWeight) + (workFieldScore * workFieldWeight)
}
fun clusterJobs(jobs: List<Job>): Map<String, List<Job>> {
    // Placeholder for feature extraction logic
    // For example, you could use TF-IDF or embeddings to convert descriptions into vectors.

    // Here we simulate clustering by grouping jobs by their company work field as a simple example.
    return jobs.groupBy { it.companyWorkField.lowercase() }
}

// Function to find similar jobs within the same cluster
fun findSimilarJobs(job: Job, clusteredJobs: Map<String, List<Job>>): List<Job> {
    val clusterId = clusteredJobs.entries.firstOrNull { it.value.contains(job) }?.key

    return if (clusterId != null) {
        clusteredJobs[clusterId]?.filter { it != job } ?: emptyList() // Exclude the original job
    } else {
        emptyList()
    }
}
fun recommendJobs(user: EmployeeProfileData, jobs: List<Job>): List<Job> {
    val threshold = 0.7 // Only recommend jobs with a score above this
    val clusteredJobs = clusterJobs(jobs)
    return jobs
        .map { job -> job to computeJobScore(user, job) }
        .filter { it.second >= threshold } // Filter based on the threshold
        .sortedByDescending { it.second }
        .flatMap { (job, _) ->
            // Find similar jobs within the same cluster for each recommended job
            findSimilarJobs(job, clusteredJobs)
        }
        .distinct() // Remove duplicates if any
}
fun relatedJobs(user: EmployeeProfileData, jobs: List<Job>): List<Job> {
    val threshold = 0.5 // Only recommend jobs with a score above this

    // Cluster jobs first
    val clusteredJobs = clusterJobs(jobs)

    return jobs
        .map { job -> job to computeJobScore(user, job) }
        .filter { it.second >= threshold } // Filter based on the threshold
        .sortedByDescending { it.second }
        .flatMap { (job, _) ->
            // Find similar jobs within the same cluster for each related job
            findSimilarJobs(job, clusteredJobs).plus(job)
        }
        .distinct() // Remove duplicates if any
}



// Function to find related jobs based on skill similarity
fun findRelatedJobs(user: EmployeeProfileData, jobs: List<Job>, maxRelated: Int = 0): List<Job> {
    val threshold =0.5
    return jobs
        .map { job -> job to computeJobScore(user, job) }
        .filter { it.second > threshold} // Only include jobs with common skills
        .sortedByDescending { it.second } // Sort by skill match
        .take(maxRelated) // Limit the number of related jobs displayed
        .map { it.first }
}

// Composable for displaying UI
@OptIn(ExperimentalMaterial3Api::class) // Opt-in to the experimental Badge API
@Composable
fun JobItem(job: Job, navController: NavController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp, horizontal = 16.dp)
            .clickable {
                // Navigate to the Job Description screen with the selected job
                navController.navigate("job_detail/${job.jobName}")
            },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 8.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Job Name
            Text(
                text = job.jobName,
                style = MaterialTheme.typography.titleMedium, // Material 3 typography
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(5.dp))

            // Location
            Text(
                text = "Location: ${job.jobAddress}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Salary
            Text(
                text = "Salary: ${job.jobSalary}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Description
            Text(
                text = "Required: ${job.jobDescription}",
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Company WorkField
            Text(
                text = "Related: ${job.companyWorkField}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
            // Job Type - Badge
            Text(
                text = "Job Type: ${job.jobType}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Email: ${job.email}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}


class JobRepository {
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    suspend fun getJobs(): List<Job> {
        return try {
            val querySnapshot = db.collection("jobs").get().await()
            querySnapshot.documents.map { document ->
                Job(
                    jobName = document.getString("jobName") ?: "",
                    jobDescription = document.getString("jobDescription") ?: "",
                    jobSalary = document.getLong("jobSalary")?.toInt() ?: 0,
                    jobAddress = document.getString("jobAddress") ?: "",
                    jobType = document.getString("jobType") ?: "",
                    companyWorkField= document.getString("companyWorkField") ?: "",
                    email = document.getString("email") ?: "",
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList<Job>()
        }
    }

    suspend fun getEmployeeProfile(userId: String): EmployeeProfileData? {
        return try {
            db.collection("employees").document(userId).get().await().toObject(EmployeeProfileData::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
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
