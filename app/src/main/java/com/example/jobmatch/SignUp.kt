package com.example.jobmatch

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun SignUp(navController: NavController) {
    var userName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var securityQuestion by remember { mutableStateOf("") }
    var securityAnswer by remember { mutableStateOf("") }
    var checked by remember { mutableStateOf(false) }
    var roleSelected by remember { mutableStateOf("") }
    val scrollState = rememberScrollState()
    val context = LocalContext.current

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
        AppName(30)

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Already have an account? Log in.",
            fontSize = 16.sp,
            modifier = Modifier.clickable { navController.navigate(Routes.login) }
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = userName,
            onValueChange = { userName = it },
            label = { Text("User Name") }
        )

        Spacer(modifier = Modifier.height(4.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            placeholder = { Text("sample@gmail.com") }
        )

        Spacer(modifier = Modifier.height(4.dp))

        OutlinedTextField(
            value = password,
            onValueChange = {
                if (it.length <= 15) password = it
            },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Security Question Dropdown
        SecurityQuestionDropdown(onQuestionSelect = { selectedQuestion ->
            securityQuestion = selectedQuestion
        })

        Spacer(modifier = Modifier.height(4.dp))

        OutlinedTextField(
            value = securityAnswer,
            onValueChange = { securityAnswer = it },
            label = { Text("Security Answer") }
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Role Dropdown
        WhatAreYou(roleSelect = { roleSelected = it })

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(checked = checked, onCheckedChange = { checked = it })
            Text(text = "I agree to the app's Terms and Conditions")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (userName.isEmpty() || email.isEmpty() || password.isEmpty() || securityQuestion.isEmpty() || securityAnswer.isEmpty()) {
                    Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                } else if (password.length < 8 || password.length > 15) {
                    Toast.makeText(context, "Password must be between 8 and 15 characters", Toast.LENGTH_SHORT).show()
                } else if (!checked) {
                    Toast.makeText(context, "You must agree to the Terms and Conditions", Toast.LENGTH_SHORT).show()
                } else {
                    when (roleSelected) {
                        "Employee" -> navController.navigate(Routes.employeeForm)
                        "Employer" -> navController.navigate(Routes.employerForm)
                        else -> navController.navigate(Routes.home)
                    }
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0XFF2E77AE)),
            modifier = Modifier.width(280.dp),
            enabled = checked
        ) {
            Text(text = "Sign Up")
        }
    }
}
