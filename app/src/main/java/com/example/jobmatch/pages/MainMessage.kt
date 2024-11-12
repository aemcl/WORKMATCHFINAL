package com.example.jobmatch.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

data class MessageData(val sender: String, val content: String, val date: String)

@Composable
fun Message(navController: NavController) {
    var searchQuery by remember { mutableStateOf("") }
    val messages = remember {
        listOf(
            MessageData("Alice", "Hi there!", "2024-11-01"),
            MessageData("Bob", "How are you?", "2024-11-02"),
            MessageData("Charlie", "Let's meet up!", "2024-11-03")
        )
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Search Bar
        SearchBar(searchQuery) { query -> searchQuery = query }

        Spacer(modifier = Modifier.height(16.dp))

        // Message List
        LazyColumn {
            items(messages.filter { it.sender.contains(searchQuery, ignoreCase = true) }) { message ->
                MessageItem(
                    message = message,
                    onDelete = { /* Handle delete action */ },
                    onArchive = { /* Handle archive action */ }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Add Message Button
        Button(
            onClick = { navController.navigate("newMessage") }, // Navigate to New Message screen
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Text("Add Message")
        }
    }
}

@Composable
fun SearchBar(query: String, onQueryChange: (String) -> Unit) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        label = { Text("Search Messages") },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true
    )
}

@Composable
fun MessageItem(message: MessageData, onDelete: () -> Unit, onArchive: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .background(Color.LightGray.copy(alpha = 0.2f))
            .padding(8.dp)
            .clickable { /* Handle normal click action (e.g., view message details) */ },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = message.sender, fontSize = 14.sp, color = Color.Black)
            Text(text = message.content, fontSize = 16.sp, color = Color.Black)
            Text(text = message.date, fontSize = 12.sp, color = Color.Gray)
        }

        Icon(
            imageVector = Icons.Default.Delete,
            contentDescription = "Delete Message",
            modifier = Modifier
                .size(24.dp)
                .clickable { onDelete() },
            tint = Color.Red
        )
        Icon(
            imageVector = Icons.Default.Archive,
            contentDescription = "Archive Message",
            modifier = Modifier
                .size(24.dp)
                .clickable { onArchive() },
            tint = Color.Blue
        )
    }
}
