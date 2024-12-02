package com.example.jobmatch
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

@Composable
fun LogIn(navController: NavController) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()
    val coroutineScope = rememberCoroutineScope()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var rememberMe by remember { mutableStateOf(false) }
    val userId = auth.currentUser?.uid
    Log.d("FirebaseAuth", "User ID: $userId")

    // Load saved credentials on start
    LaunchedEffect(Unit) {
        val savedCredentials = loadCredentials(context)
        email = savedCredentials.first
        password = savedCredentials.second
        rememberMe = savedCredentials.third
    }

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
            placeholder = { Text(text = "sample@gmail.com") },
            leadingIcon = {
                Icon(imageVector = Icons.Filled.Email, contentDescription = "Email Icon")
            },
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text(text = "Password:") },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            leadingIcon = {
                Icon(imageVector = Icons.Filled.Lock, contentDescription = "Password Icon")
            },
            trailingIcon = {
                Icon(
                    imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                    contentDescription = "Toggle password visibility",
                    modifier = Modifier.clickable { passwordVisible = !passwordVisible }
                )
            },
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(5.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = rememberMe,
                onCheckedChange = { rememberMe = it }
            )
            Text(text = "Remember Me", fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Forgot Password?",
            fontSize = 16.sp,
            modifier = Modifier.clickable { navController.navigate(Routes.forgotpass) }
        )

        Spacer(modifier = Modifier.height(30.dp))

        Button(
            onClick = {
                coroutineScope.launch {
                    if (email.isEmpty() || password.isEmpty()) {
                        Toast.makeText(context, "Please enter both email and password", Toast.LENGTH_SHORT).show()
                    } else if (password.length < 8 || password.length > 15) {
                        Toast.makeText(context, "Password must be between 8 and 15 characters", Toast.LENGTH_SHORT).show()
                    } else {
                        auth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    // Save credentials if "Remember Me" is checked
                                    if (rememberMe) {
                                        saveCredentials(context, email, password, rememberMe)
                                    } else {
                                        clearCredentials(context)
                                    }
                                    val userId = task.result?.user?.uid
                                    if (userId != null) {
                                        firestore.collection("users").document(userId).get()
                                            .addOnSuccessListener { document ->
                                                if (document.exists()) {
                                                    val role = document.getString("role")
                                                    val formCompleted = document.getBoolean("formCompleted") ?: false
                                                    if (role != null && formCompleted) {
                                                        navigateBasedOnRole(role, navController)
                                                    } else {
                                                        Toast.makeText(context, "Role or form data incomplete", Toast.LENGTH_SHORT).show()
                                                    }
                                                } else {
                                                    Toast.makeText(context, "User document does not exist", Toast.LENGTH_SHORT).show()
                                                }
                                            }
                                            .addOnFailureListener { e ->
                                                Toast.makeText(context, "Error retrieving role: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                                            }

                                    }
                                } else {
                                    Toast.makeText(context, "Invalid email or password", Toast.LENGTH_SHORT).show()
                                }
                            }
                        auth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    Log.d("FirebaseAuth", "Login successful")
                                } else {
                                    Log.e("FirebaseAuth", "Error: ${task.exception?.message}")
                                }
                            }

                    }
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0XFFff8e2b)),
            modifier = Modifier.width(280.dp)
        ) {
            Text(text = "Log in", fontSize = 18.sp)
        }

        Spacer(modifier = Modifier.height(30.dp))
    }
}

// Function to save credentials
fun saveCredentials(context: Context, email: String, password: String, rememberMe: Boolean ) {
    val sharedPrefs = context.getSharedPreferences("credentials", Context.MODE_PRIVATE)
    with(sharedPrefs.edit()) {
        putString("email", email)
        putString("password", password)
        putBoolean("rememberMe", rememberMe)
        apply()
    }
}

// Function to load saved credentials
fun loadCredentials(context: Context): Triple<String, String, Boolean> {
    val sharedPrefs = context.getSharedPreferences("credentials", Context.MODE_PRIVATE)
    val email = sharedPrefs.getString("email", "") ?: ""
    val password = sharedPrefs.getString("password", "") ?: ""
    val rememberMe = sharedPrefs.getBoolean("rememberMe", false)
    return Triple(email, password, rememberMe)
}

// Function to clear saved credentials
fun clearCredentials(context: Context) {
    val sharedPrefs = context.getSharedPreferences("credentials", Context.MODE_PRIVATE)
    with(sharedPrefs.edit()) {
        clear()
        apply()
    }
}

// Function to navigate based on user role
fun navigateBasedOnRole(role: String, navController: NavController) {
    when (role) {
        "Employee" -> navController.navigate(Routes.employeeMainScreen)
        "Employer" -> navController.navigate(Routes.employerMainScreen)
        else -> {
            // Handle unknown roles or errors if necessary
        }
    }
}
