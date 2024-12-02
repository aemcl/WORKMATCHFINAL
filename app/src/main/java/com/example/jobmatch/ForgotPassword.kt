package com.example.jobmatch
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

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
                    navController.navigate(Routes.changepass)
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
