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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.firestore.FirebaseFirestore

data class EmployeeItem(
    val name: String,
    val description: String
)

@Composable
fun EmployerSearch(navController: NavController, userRole: String) {
    var text by remember { mutableStateOf("") }
    var active by remember { mutableStateOf(false) }

    // Firebase Firestore instance
    val db = FirebaseFirestore.getInstance()

    // List to hold employee profiles from Firestore
    val employeeItems = remember { mutableStateListOf<EmployeeItem>() }

    // Fetch employees from Firestore
    LaunchedEffect(Unit) {
        db.collection("employees").get()
            .addOnSuccessListener { result ->
                employeeItems.clear()
                for (document in result) {
                    val employeeName = document.getString("name") ?: ""
                    val employeeDescription = document.getString("description") ?: ""
                    if (employeeName.isNotEmpty()) {
                        employeeItems.add(EmployeeItem(employeeName, employeeDescription))
                    }
                }
            }
            .addOnFailureListener { exception ->
                // Handle errors (Log or display an error message)
            }
    }

    SearchBar(
        modifier = Modifier.fillMaxWidth(),
        query = text,
        onQueryChange = { text = it },
        onSearch = {
            active = false  // Close the search bar
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
                            text = ""  // Clear the search text
                        } else {
                            active = false  // Close the search bar
                        }
                    }
                )
            }
        }
    ) {
        // Display employee suggestions based on search in both name and description
        employeeItems.filter {
            it.name.contains(text, ignoreCase = true) ||
                    it.description.contains(text, ignoreCase = true)
        }.forEach { suggestion ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp)
                    .clickable {
                        // Navigate to the employee's profile screen
                        navController.navigate("employeeProfile/${suggestion.name}") // Replace with actual route
                    }
            ) {
                Icon(imageVector = Icons.Default.History, contentDescription = "History Icon")
                Text(
                    text = suggestion.name,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }
}
