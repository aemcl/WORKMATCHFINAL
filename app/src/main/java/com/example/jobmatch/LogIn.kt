package com.example.jobmatch

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
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
fun LogIn(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val context = LocalContext.current  // Access context for Toast

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Logo",
            modifier = Modifier.size(200.dp)
        )
        AppName(30)

        Text(
            text = "Don't have an account? Sign Up.",
            fontSize = 16.sp,
            modifier = Modifier.clickable { navController.navigate(Routes.signup) }
        )

        Spacer(modifier = Modifier.height(30.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text(text = "Email:") },
            placeholder = { Text(text = "sample@gmail.com") } // Placeholder for email
        )

        Spacer(modifier = Modifier.height(2.dp))

        OutlinedTextField(
            value = password,
            onValueChange = {
                // Limit password length
                if (it.length <= 15) password = it
            },
            label = { Text(text = "Password:") },
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(modifier = Modifier.height(5.dp))
        Text(
            text = "Forgot Password?",
            fontSize = 16.sp,
            modifier = Modifier.clickable { navController.navigate(Routes.forgotpass) }
        )

        Spacer(modifier = Modifier.height(30.dp))

        Button(
            onClick = {
                // Validate inputs
                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(context, "Please enter both email and password", Toast.LENGTH_SHORT).show()
                } else if (password.length < 8 || password.length > 15) {
                    Toast.makeText(context, "Password must be between 8 and 15 characters", Toast.LENGTH_SHORT).show()
                } else if (email == "correctEmail" && password == "correctPassword") {
                    Toast.makeText(context, "Login successful", Toast.LENGTH_SHORT).show()
                    navController.navigate(Routes.home)
                } else {
                    Toast.makeText(context, "Invalid email or password", Toast.LENGTH_SHORT).show()
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0XFFff8e2b)),
            modifier = Modifier.width(280.dp)
        ) {
            Text(text = "Log in", fontSize = 18.sp)
        }

        Spacer(modifier = Modifier.height(50.dp))
    }
}
