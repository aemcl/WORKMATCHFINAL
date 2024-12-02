package com.example.jobmatch

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack  // Correct import
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MainTopBar(pageTitle: String) {
    // Top Bar
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp, top = 40.dp)
    ) {
        Icon(
            imageVector = Icons.Filled.ArrowBack,  // Use the correct `Filled` reference
            contentDescription = "Back"
        )

        Spacer(modifier = Modifier.width(16.dp))

        BasicTextField(
            value = pageTitle,
            onValueChange = {},
            textStyle = MaterialTheme.typography.bodyMedium.copy(
                fontSize = 20.sp,
                color = Color.Black
            ),
            enabled = false
        )
    }
}
