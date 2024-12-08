package com.example.jobmatch

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

@Composable
fun SignUp(navController: NavController) {

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var securityQuestion by remember { mutableStateOf("") }
    var securityAnswer by remember { mutableStateOf("") }
    var checked by remember { mutableStateOf(false) }
    var passwordVisible by remember { mutableStateOf(false) }
    var role by remember { mutableStateOf("") }
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Logo",
            modifier = Modifier.size(200.dp)
        )
        Text("Work Match", fontSize = 30.sp)

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Already have an account? Log in.",
            fontSize = 16.sp,
            modifier = Modifier.clickable { navController.navigate(Routes.login) }
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Email Input
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            placeholder = { Text("sample@gmail.com") },
            isError = email.isEmpty()
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Password Input with Toggle Visibility
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                Icon(
                    imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                    contentDescription = "Toggle password visibility",
                    modifier = Modifier.clickable { passwordVisible = !passwordVisible }
                )
            },
            isError = password.isEmpty(),
            modifier = Modifier.fillMaxWidth(0.72f)
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Security Question Dropdown
        SecurityQuestionDropdown(onQuestionSelect = { selectedQuestion ->
            securityQuestion = selectedQuestion
        })

        Spacer(modifier = Modifier.height(4.dp))

        // Security Answer Input
        OutlinedTextField(
            value = securityAnswer,
            onValueChange = { securityAnswer = it },
            label = { Text("Security Answer") },
            isError = securityAnswer.isEmpty()
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Role Selection
        WhatAreYou(roleSelect = { selectedRole ->
            role = selectedRole // Update the selected role
        })


        Spacer(modifier = Modifier.height(8.dp))

        // Terms and Conditions Checkbox
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Checkbox(
                checked = checked,
                onCheckedChange = { checked = it }
            )
            Text(text = "I agree to the", fontSize = 14.sp)
            Spacer(modifier = Modifier.width(4.dp))
            TextButton(onClick = { navController.navigate(Routes.termsAndConditions) }) {
                Text(
                    text = "Terms and Conditions",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
                )
            }
        }


        Spacer(modifier = Modifier.height(16.dp))

        // Sign-Up Button
        Button(
            onClick = {
                coroutineScope.launch {
                    if (email.isEmpty() || password.isEmpty() || securityQuestion.isEmpty() || securityAnswer.isEmpty() || role.isEmpty()) {
                        Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                    } else if (password.length < 8 || password.length > 15) {
                        Toast.makeText(context, "Password must be between 8 and 15 characters", Toast.LENGTH_SHORT).show()
                    } else if (!checked) {
                        Toast.makeText(context, "You must agree to the Terms and Conditions", Toast.LENGTH_SHORT).show()
                    } else {
                        auth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    val user = auth.currentUser
                                    user?.let {
                                        val userData = hashMapOf(
                                            "email" to email,
                                            "securityQuestion" to securityQuestion,
                                            "securityAnswer" to securityAnswer,
                                            "role" to role,
                                            "formCompleted" to true
                                        )
                                        firestore.collection("users").document(it.uid).set(userData)
                                            .addOnSuccessListener {
                                                Toast.makeText(context, "Sign Up successful", Toast.LENGTH_SHORT).show()
                                                if (role == "Employee") {
                                                    navController.navigate(Routes.employeeForm)
                                                } else {
                                                    navController.navigate(Routes.employerForm)
                                                }
                                            }
                                            .addOnFailureListener { e ->
                                                Toast.makeText(context, "Failed to save user data: ${e.message}", Toast.LENGTH_SHORT).show()
                                            }
                                    }
                                } else {
                                    Toast.makeText(context, "Sign Up failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                                }
                            }
                    }
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0XFF2E77AE)),
            modifier = Modifier.width(280.dp),
            enabled = checked && email.isNotEmpty() && password.isNotEmpty() && role.isNotEmpty() &&
                    securityQuestion.isNotEmpty() && securityAnswer.isNotEmpty()
        ) {
            Text(text = "Sign Up")
        }
    }
}
