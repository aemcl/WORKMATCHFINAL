package com.example.jobmatch

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth

@Composable
fun LogOut(navController: NavController, userRole: String) {
    val auth = FirebaseAuth.getInstance()

    // Sign the user out from Firebase
    auth.signOut()

    // Clear locally stored user credentials if necessary
    clearLocalCredentials(navController.context)

    // Navigate to login screen
    navController.navigate(Routes.login) {
        // Clear the back stack to prevent navigating back to authenticated screens
        popUpTo(Routes.login) { inclusive = true }  // Clear the stack from login screen onwards
    }
}

// Function to clear locally saved credentials without affecting Firebase data
fun clearLocalCredentials(context: Context) {
    val sharedPrefs = context.getSharedPreferences("credentials", Context.MODE_PRIVATE)
    with(sharedPrefs.edit()) {
        clear()  // Remove all locally saved credentials
        apply()
    }
}
