// Employer.kt
package com.example.jobmatch.models

import com.google.firebase.firestore.FirebaseFirestore

data class Employer(
    val uid: String = "",       // Unique identifier
    val companyName: String = "",
    val email: String = "",
    val companyLogoUrl: String = "",  // URL for the company logo
    val description: String = "",
    val address: String = "",
    val industry: String = "",
    val role: String = ""
)

object EmployerRepository {
    private val db = FirebaseFirestore.getInstance().collection("employers")

    fun addEmployer(employer: Employer, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        db.document(employer.uid).set(employer)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }
}
