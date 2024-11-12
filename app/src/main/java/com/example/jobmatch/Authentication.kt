package com.example.jobmatch

import androidx.compose.ui.semantics.Role
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import androidx.navigation.NavController

class Authentication {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    // Sign Up with Email, Password, and Role
    suspend fun signUp(email: String, password: String, role: String): FirebaseUser? {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            result.user?.let { user ->
                // Store the role in Firestore under the user document
                db.collection("users").document(user.uid).set(mapOf("role" to role)).await()
            }
            result.user
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // Log In with Email and Password, and Role
    suspend fun logIn(email: String, password: String, navController: NavController): FirebaseUser? {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            result.user?.let { user ->
                val role = getUserRole(user.uid) // Get role from Firestore
                if (role != null) {
                    when (role) {
                        "Employee" -> {
                            // Navigate to Employee Home and load Employee-specific data
                            navController.navigate(Routes.employeeMainScreen) // Ensure Routes is defined in your project
                        }
                        "Employer" -> {
                            // Navigate to Employer Home and load Employer-specific data
                            navController.navigate(Routes.employerMainScreen) // Ensure Routes is defined in your project
                        }
                        else -> {
                            // Handle case where role is invalid
                            // For example, navigate to an error screen or log out the user
                        }
                    }
                }
            }
            result.user
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // Log Out
    fun logOut() {
        auth.signOut()
    }

    // Get Current User
    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }

    // Get User Role from Firestore
    suspend fun getUserRole(userId: String): String? {
        return try {
            val document = db.collection("users").document(userId).get().await()
            document.getString("role") // Corrected key to 'role' instead of 'Role'
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
