package com.example.jobmatch.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun NewMessage() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp, top = 40.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back"
            )

            Spacer(modifier = Modifier.width(16.dp))

            BasicTextField(
                value = "New Message",
                onValueChange = {},
                textStyle = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 20.sp,
                    color = Color.Black
                ),
                enabled = false
            )
        }

        // Adjust the spacer height here
        Spacer(modifier = Modifier.weight(1f))

        // Message box, input
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp, bottom = 50.dp), // Reduced bottom padding
            verticalAlignment = Alignment.CenterVertically
        ) {
            val message = remember { mutableStateOf("") }

            BasicTextField(
                value = message.value,
                onValueChange = { message.value = it },
                modifier = Modifier
                    .weight(1f)
                    .background(Color(0xFFEFEFEF))
                    .padding(16.dp),
                textStyle = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 20.sp,
                    color = Color.Black
                ),
                decorationBox = { innerTextField ->
                    if (message.value.isEmpty()) {
                        Text(
                            text = "Enter Message",
                            color = Color.Gray,
                            fontSize = 16.sp
                        )
                    }
                    innerTextField()
                }
            )

            Spacer(modifier = Modifier.width(8.dp))

            Icon(
                imageVector = Icons.AutoMirrored.Filled.Send,
                contentDescription = "Send",
                tint = Color.Gray
            )
        }
    }
}
