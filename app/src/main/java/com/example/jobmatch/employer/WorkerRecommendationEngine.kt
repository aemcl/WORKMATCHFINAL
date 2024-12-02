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
    val description: String = "",
    val profilePictureUrl: String = "",
    val formCompleted: Boolean = false
)

// Utility functions for computing scores
fun computeSkillMatch(userSkills: String, description: String): Double {
    val userSet = userSkills.split(",").map { it.trim().lowercase() }.toSet()
    val jobSet = description.split(",").map { it.trim().lowercase() }.toSet()

    val intersection = userSet.intersect(jobSet).size
    val union = userSet.union(jobSet).size

    return if (union == 0) 0.0 else intersection.toDouble() / union.toDouble()
}

fun computeLocationScore(userLocation: String, companyAddress: String): Double {
    return if (userLocation.equals(companyAddress, ignoreCase = true)) 1.0 else 0.0
}

fun computeWorkerScore(company: EmployerProfileData, worker: EmployeeProfileData): Double {
    val skillScore = computeSkillMatch(company.description, worker.description)
    val locationScore = computeLocationScore(company.companyAddress, worker.address)

    // Weights for scoring
    val skillWeight = 0.7
    val locationWeight = 0.3

    return (skillScore * skillWeight) + (locationScore * locationWeight)
}

fun recommendWorkers(company: EmployerProfileData, workers: List<EmployeeProfileData>): List<EmployeeProfileData> {
    val threshold = 0.7 // Only recommend workers with a score above this
    return workers
        .map { worker -> worker to computeWorkerScore(company, worker) }
        .filter { it.second >= threshold } // Filter based on the threshold
        .sortedByDescending { it.second }
        .map { it.first }
}

// Composables for displaying UI
@Composable
fun WorkerItem(worker: EmployeeProfileData, navController: NavController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable {
                // Navigate to worker detail screen
                navController.navigate("worker_detail/${worker.email}")
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
                    profilePicUri = document.getString("profilePictureUrl") ?: ""
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}


@Composable
fun RecommendedWorkersScreen(navController: NavController, employerId: String) {
    var employerProfile by remember { mutableStateOf<EmployerProfileData?>(null) }
    var workers by remember { mutableStateOf<List<EmployeeProfileData>>(emptyList()) }

    LaunchedEffect(employerId) {
        val employerRepository = EmployerRepository()
        employerProfile = employerRepository.getEmployerProfile(employerId)
        workers = employerRepository.getEmployeeProfiles()
    }

    when {
        employerProfile != null && workers.isNotEmpty() -> {
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
