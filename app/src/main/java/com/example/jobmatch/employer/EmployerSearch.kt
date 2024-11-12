@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.jobmatch.employer

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun EmployerSearch(navController: NavController, userRole: String) {
    var text by remember { mutableStateOf("") }
    var active by remember { mutableStateOf(false) }

    // Firebase Firestore instance
    val db = FirebaseFirestore.getInstance()

    // List to hold employee profiles from Firestore
    val employeeItems = remember { mutableStateListOf<String>() }

    // Fetch employees from Firestore
    LaunchedEffect(Unit) {
        db.collection("employees").get()
            .addOnSuccessListener { result ->
                employeeItems.clear()
                for (document in result) {
                    val employeeName = document.getString("name") ?: ""
                    if (employeeName.isNotEmpty()) {
                        employeeItems.add(employeeName)
                    }
                }
            }
            .addOnFailureListener { exception ->
                // Handle errors
            }
    }

    SearchBar(
        modifier = Modifier.fillMaxWidth(),
        query = text,
        onQueryChange = { text = it },
        onSearch = {
            // Handle search action
            active = false
        },
        active = active,
        onActiveChange = { active = it },
        placeholder = { Text(text = "Search Employees") },
        leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = "Search Icon") },
        trailingIcon = {
            if (active) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close Icon",
                    modifier = Modifier.clickable {
                        if (text.isNotEmpty()) {
                            text = ""
                        } else {
                            active = false
                        }
                    }
                )
            }
        }
    ) {
        // Display employee suggestions based on search
        employeeItems.filter { it.contains(text, ignoreCase = true) }.forEach { suggestion ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp)
                    .clickable {
                        // Navigate to the employee's profile screen
                        navController.navigate("employeeProfile/$suggestion") // Replace with actual route
                    }
            ) {
                Icon(imageVector = Icons.Default.History, contentDescription = "History Icon")
                Text(
                    text = suggestion,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }
}
