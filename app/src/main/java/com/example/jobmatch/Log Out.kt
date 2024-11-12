package com.example.jobmatch

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth

@Composable
fun LogOut(navController: NavController) {
    val auth = FirebaseAuth.getInstance()

    // Sign the user out
    auth.signOut()

    // Clear locally stored user credentials if necessary
    clearLocalCredentials(navController.context)

    // Redirect to the login screen after logout
    navController.navigate(Routes.login) {
        popUpTo(Routes.employeeMainScreen) { inclusive = true }
    }
}

// Function to clear locally saved credentials without affecting Firebase data
fun clearLocalCredentials(context: Context) {
    val sharedPrefs = context.getSharedPreferences("credentials", Context.MODE_PRIVATE)
    with(sharedPrefs.edit()) {
        clear() // Remove local credentials
        apply()
    }
}
