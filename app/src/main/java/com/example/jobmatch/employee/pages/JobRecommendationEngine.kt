package com.example.jobmatch.employee.pages

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

// Data model for jobs
data class Job(
    val jobName: String = "",
    val jobDescription: String = "",
    val jobSalary: Int = 0,
    val jobAddress: String = "",
    val companyWorkField: String="",

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
@Composable
fun JobItem(job: Job, navController: NavController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
            .clickable {
                // Navigate to the Job Description screen with the selected job
                navController.navigate("job_detail/${job.jobName}") // Pass job name or job ID as argument
            }
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(text = job.jobName, style = MaterialTheme.typography.titleLarge)
            Text(text = "Location: ${job.jobAddress}")
            Text(text = "Salary: ${job.jobSalary}")
            Text(text = "Description: ${job.jobDescription}")
            Text(text = "Company WorkField: ${job.companyWorkField}")
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

@Composable
fun RecommendedJobsScreen(navController: NavController, userId: String) {
    val db = FirebaseFirestore.getInstance()
    var jobs by remember { mutableStateOf<List<Job>>(emptyList()) }
    var userProfile by remember { mutableStateOf<EmployeeProfileData?>(null) }

    LaunchedEffect(userId) {
        val jobRepository = JobRepository()
        jobs = jobRepository.getJobs()
        userProfile = jobRepository.getEmployeeProfile(userId)
    }

    when {
        jobs.isNotEmpty() && userProfile != null -> {
            val recommendedJobs = recommendJobs(userProfile!!, jobs)
            LazyColumn {
                items(recommendedJobs) { job -> JobItem(job, navController) }
            }
        }
        jobs.isEmpty() -> Text("No jobs available")
        userProfile == null -> Text("Loading profile...")
        else -> Text("Loading...")
    }
}

@Composable
fun RelatedJobsScreen(navController: NavController, userId: String) {
    val db = FirebaseFirestore.getInstance()
    var jobs by remember { mutableStateOf<List<Job>>(emptyList()) }
    var userProfile by remember { mutableStateOf<EmployeeProfileData?>(null) }

    LaunchedEffect(userId) {
        val jobRepository = JobRepository()
        jobs = jobRepository.getJobs()
        userProfile = jobRepository.getEmployeeProfile(userId)
    }

    when {
        jobs.isNotEmpty() && userProfile != null -> {
            val recommendedJobs = relatedJobs(userProfile!!, jobs)
            LazyColumn{
                items(recommendedJobs) { job -> JobItem(job, navController) }
            }
        }
        jobs.isEmpty() -> Text("No jobs available")
        userProfile == null -> Text("Loading profile...")
        else -> Text("Loading...")
    }
}