package com.example.jobmatch.employer

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.jobmatch.employee.pages.EmployeeProfileData
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

// Data model for Employer's Company
data class EmployerProfileData(
    val companyName: String = "",
    val companyAddress: String = "",
    val companyType: String = "",
    val companyDescription: String = "",
    val companyWorkField: String = "",
    val profilePictureUrl: String = "",
    val formCompleted: Boolean = false
)
fun computeCompanyType(jobSkills: String, userSkills: String): Double {
    val userSet = userSkills.split(",").map { it.trim().lowercase() }.toSet()
    val jobSet = jobSkills.split(",").map { it.trim().lowercase() }.toSet()

    val intersection = userSet.intersect(jobSet).size
    val union = userSet.union(jobSet).size

    return if (union == 0) 0.0 else intersection.toDouble() / union.toDouble()
}

// Function to compute location score
fun computeLocationScore(jobLocation: String, userLocation: String): Double {
    return if (jobLocation.equals(userLocation, ignoreCase = true)) 1.0 else 0.0
}

// Function to compute work field score
fun computeWorkFieldScore(companyWorkField: String, userWorkField: String): Double {
    return if (companyWorkField.equals(userWorkField, ignoreCase = true)) 1.0 else 0.0
}

// Function to compute match score for a worker
fun computeWorkerScore(employer: EmployerProfileData, worker: EmployeeProfileData): Double {
    val skillScore = computeCompanyType(employer.companyDescription, worker.description)
    val locationScore = computeLocationScore(employer.companyAddress, worker.address)
    val workFieldScore = computeWorkFieldScore(employer.companyWorkField, worker.workField)

    // Weights for scoring
    val skillWeight = 0.7
    val locationWeight = 0.2
    val workFieldWeight = 0.1

    return (skillScore * skillWeight) + (locationScore * locationWeight) + (workFieldScore * workFieldWeight)
}

// Function to recommend workers based on scores
fun recommendWorkers(employer: EmployerProfileData, workers: List<EmployeeProfileData>): List<EmployeeProfileData> {
    val threshold = 0.7 // Only recommend workers with a score above this threshold
    return workers
        .map { worker -> worker to computeWorkerScore(employer, worker) }
        .filter { it.second >= threshold }
        .sortedByDescending { it.second }
        .map { it.first } // Return only the workers
}

// Composable for displaying a worker item
@Composable
fun WorkerItem(worker: EmployeeProfileData, navController: NavController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable {
                // Navigate to worker detail screen
                navController.navigate("worker_detail/${worker.fullName}")
            }
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = rememberAsyncImagePainter(worker.profilePicUri),
                contentDescription = "Worker Profile Picture",
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = worker.fullName, style = MaterialTheme.typography.titleLarge)
                Text(text = "Address: ${worker.address}")
                Text(text = "Skills: ${worker.description}")
            }
        }
    }
}

// Composable to display recommended workers
@Composable
fun RecommendedWorkersScreen(navController: NavController, userId: String) {
    val db = FirebaseFirestore.getInstance()
    var employerProfile by remember { mutableStateOf<EmployerProfileData?>(null) }
    var workers by remember { mutableStateOf<List<EmployeeProfileData>>(emptyList()) }

    LaunchedEffect(userId) {
        val employerRepository = EmployerRepository()
        workers = employerRepository.getEmployeeProfiles()
        employerProfile = employerRepository.getEmployerProfile(userId)
    }

    when {
        workers.isNotEmpty() && employerProfile != null -> {
            val recommendedWorkers = recommendWorkers(employerProfile!!, workers)
            LazyColumn {
                items(recommendedWorkers) { worker ->
                    WorkerItem(worker, navController)
                }
            }
        }
        employerProfile == null -> Text("Loading employer profile...")
        workers.isEmpty() -> Text("No workers available")
        else -> Text("Loading...")
    }
}

// Repository to handle Firestore integration
class EmployerRepository {
    private val db = FirebaseFirestore.getInstance()

    suspend fun getEmployerProfile(userId: String): EmployerProfileData? {
        return try {
            db.collection("employers").document(userId).get().await().toObject(EmployerProfileData::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun getEmployeeProfiles(): List<EmployeeProfileData> {
        return try {
            val querySnapshot = db.collection("employees").get().await()
            querySnapshot.documents.map { document ->
                EmployeeProfileData(
                    fullName = document.getString("name") ?: "",
                    address = document.getString("address") ?: "",
                    description = document.getString("skills") ?: "",
                    profilePicUri = document.getString("profilePictureUrl") ?: "",
                    workField = document.getString("workField") ?: ""
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}
