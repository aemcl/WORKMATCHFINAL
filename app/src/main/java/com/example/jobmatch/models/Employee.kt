package com.example.jobmatch.models

import com.google.firebase.firestore.FirebaseFirestore

data class Employee(
    val uid: String = "",       // Unique identifier
    val fullName: String = "",
    val email: String = "",
    val birthDate: String = "",
    val address: String = "",
    val profilePicUri: String = "",  // URL for the profile picture
    val bio: String = "",
    val phoneNumber: String = "",
    val resumeUri: String = "",
    val role: String = ""
)

object EmployeeRepository {
    private val db = FirebaseFirestore.getInstance().collection("employees")

    fun addEmployee(employee: Employee, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        db.document(employee.uid).set(employee)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e ->
                onFailure(e)
            }
    }

    // Example additional methods

    fun updateEmployee(uid: String, updatedData: Map<String, Any>, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        db.document(uid).update(updatedData)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }

    fun getEmployee(uid: String, onSuccess: (Employee?) -> Unit, onFailure: (Exception) -> Unit) {
        db.document(uid).get()
            .addOnSuccessListener { document ->
                val employee = document.toObject(Employee::class.java)
                onSuccess(employee)
            }
            .addOnFailureListener { onFailure(it) }
    }

    fun deleteEmployee(uid: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        db.document(uid).delete()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }
}
