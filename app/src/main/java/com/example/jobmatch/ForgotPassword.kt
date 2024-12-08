package com.example.jobmatch

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class) // Opt-in for the experimental API
@Composable
fun ForgotPassword(navController: NavController) {
    // Firebase setup
    val user = FirebaseAuth.getInstance().currentUser
    val db = FirebaseFirestore.getInstance()

    var securityQuestion by remember { mutableStateOf("") }
    var answer by remember { mutableStateOf("") }
    var correctAnswer by remember { mutableStateOf("") }  // The actual answer from Firestore

    // Fetch security question and answer from Firestore
    LaunchedEffect(Unit) {
        user?.uid?.let { userId ->
            db.collection("users").document(userId).get().addOnSuccessListener { document ->
                if (document.exists()) {
                    val securityData = document.get("securityQuestion") as? String
                    val storedAnswer = document.get("securityAnswer") as? String
                    if (securityData != null && storedAnswer != null) {
                        securityQuestion = securityData
                        correctAnswer = storedAnswer
                    }
                }
            }.addOnFailureListener { e ->
                Log.e("ForgotPassword", "Error fetching security question", e)
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Security Question: $securityQuestion")

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = answer,
            onValueChange = { answer = it },
            label = { Text(text = "Write your answer here.") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                // Verify the answer against the one fetched from Firebase
                if (answer == correctAnswer) {
                    // Correct answer, proceed to password reset
                    navController.navigate(Routes.updatePassword)
                } else {
                    // Incorrect answer, show error message
                    Log.e("ForgotPassword", "Incorrect answer")
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFAA300)),
            modifier = Modifier
                .width(280.dp)
                .height(50.dp)
        ) {
            Text(text = "Verify", fontSize = 20.sp)
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class) // Opt-in for the experimental API
@Composable
fun UpdatePassword(navController: NavController) {
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordUpdateError by remember { mutableStateOf<String?>(null) }
    var isPasswordVisible by remember { mutableStateOf(false) } // State to toggle password visibility
    var isConfirmPasswordVisible by remember { mutableStateOf(false) } // State to toggle confirm password visibility
    val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser

    // Function to validate and submit the password update
    fun validateAndSubmit() {
        when {
            newPassword.isBlank() -> passwordUpdateError = "New password is required"
            confirmPassword.isBlank() -> passwordUpdateError = "Please confirm the new password"
            newPassword != confirmPassword -> passwordUpdateError = "New passwords do not match"
            newPassword.length < 6 -> passwordUpdateError = "Password must be at least 6 characters long"
            else -> {
                // Update the password in Firebase Authentication
                user?.updatePassword(newPassword)?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        passwordUpdateError = null
                        // Navigate back to login screen after successful password update
                        navController.navigate("login") {
                            popUpTo("login") { inclusive = true } // Pop back to login screen
                        }
                    } else {
                        passwordUpdateError = "Failed to update password: ${task.exception?.message}"
                    }
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Update Password") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(text = "Update Password", fontSize = 20.sp, modifier = Modifier.padding(bottom = 16.dp))

            Spacer(modifier = Modifier.height(16.dp))

            // New Password
            OutlinedTextField(
                value = newPassword,
                onValueChange = { newPassword = it },
                label = { Text("New Password") },
                visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                isError = passwordUpdateError != null,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(onDone = { validateAndSubmit() }),
                trailingIcon = {
                    IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                        Icon(
                            imageVector = Icons.Default.Visibility,
                            contentDescription = "Toggle password visibility"
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Confirm New Password
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Confirm New Password") },
                visualTransformation = if (isConfirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                isError = passwordUpdateError != null,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(onDone = { validateAndSubmit() }),
                trailingIcon = {
                    IconButton(onClick = { isConfirmPasswordVisible = !isConfirmPasswordVisible }) {
                        Icon(
                            imageVector = Icons.Default.Visibility,
                            contentDescription = "Toggle confirm password visibility"
                        )
                    }
                }
            )

            // Error Message
            if (passwordUpdateError != null) {
                Text(
                    text = passwordUpdateError!!,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Submit Button
            Button(
                onClick = { validateAndSubmit() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Update Password")
            }
        }
    }
}

