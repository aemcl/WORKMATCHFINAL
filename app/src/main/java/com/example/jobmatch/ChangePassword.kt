package com.example.jobmatch.employee.pages

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth

@Composable
fun ChangePassword(navController: NavController) {
    val auth = FirebaseAuth.getInstance()
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Change Password", style = MaterialTheme.typography.headlineSmall)

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = currentPassword,
            onValueChange = { currentPassword = it },
            label = { Text("Current Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = newPassword,
            onValueChange = { newPassword = it },
            label = { Text("New Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirm New Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        if (errorMessage.isNotEmpty()) {
            Text(errorMessage, color = MaterialTheme.colorScheme.error)
            Spacer(modifier = Modifier.height(8.dp))
        }

        Button(
            onClick = {
                if (newPassword != confirmPassword) {
                    errorMessage = "Passwords do not match"
                    return@Button
                }
                auth.currentUser?.updatePassword(newPassword)
                    ?.addOnSuccessListener {
                        Log.d("ChangePasswordScreen", "Password updated successfully")
                        navController.navigateUp()  // Navigate back to the profile screen
                    }
                    ?.addOnFailureListener { e ->
                        Log.e("ChangePasswordScreen", "Error updating password", e)
                        errorMessage = "Error updating password"
                    }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Update Password")
        }
    }
}
