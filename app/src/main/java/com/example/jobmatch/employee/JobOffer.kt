@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.jobmatch.employee

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.jobmatch.R
import com.google.firebase.firestore.FirebaseFirestore

data class JobOffer(
    val companyName: String = "",
    val companyLogo: Int = R.drawable.fb // Placeholder logo; update as necessary
)

@Composable
fun JobOfferList(navController: NavController) {
    var jobOffers by remember { mutableStateOf(listOf<JobOffer>()) }

    // Fetch data from Firebase
    LaunchedEffect(Unit) {
        val db = FirebaseFirestore.getInstance()
        db.collection("jobOffers").get()
            .addOnSuccessListener { result ->
                val offers = result.map { document ->
                    JobOffer(
                        companyName = document.getString("companyName") ?: "Unknown",
                        companyLogo = R.drawable.g // Use a placeholder image
                    )
                }
                jobOffers = offers
            }
            .addOnFailureListener { exception ->
                Log.e("JobOfferList", "Error fetching data: ${exception.localizedMessage}")
            }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        items(jobOffers) { jobOffer ->
            JobOfferCard(jobOffer = jobOffer, navController = navController)
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun JobOfferCard(jobOffer: JobOffer, navController: NavController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable {
                // Handle navigation to job description screen
            },
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            Image(
                painter = painterResource(id = jobOffer.companyLogo),
                contentDescription = "${jobOffer.companyName} Logo",
                modifier = Modifier
                    .size(40.dp)
                    .padding(end = 16.dp)
            )

            Text(
                text = jobOffer.companyName,
                style = MaterialTheme.typography.bodyLarge,
                fontSize = 18.sp,
                color = Color.Black
            )
        }
    }
}
