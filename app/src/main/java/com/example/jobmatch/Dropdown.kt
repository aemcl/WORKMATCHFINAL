@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.jobmatch

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize

// Role selection dropdown
@Composable
fun WhatAreYou(roleSelect: (String) -> Unit) {
    val roleOptions = listOf("Employee", "Employer")
    DropdownMenuWithOptions(
        options = roleOptions,
        label = "What are you?",
        onOptionSelect = { roleSelect(it) }
    )
}

// Security question dropdown
@Composable
fun SecurityQuestionDropdown(onQuestionSelect: (String) -> Unit) {
    val securityQuestions = listOf(
        "Gwapo ba ka?",
        "Kinsa imong Crush?"
    )
    DropdownMenuWithOptions(
        options = securityQuestions,
        label = "Security Questions",
        onOptionSelect = { onQuestionSelect(it) }
    )
}

// Generic dropdown menu component
@Composable
fun DropdownMenuWithOptions(
    options: List<String>,
    label: String,
    onOptionSelect: (String) -> Unit
) {
    var selectedText by remember { mutableStateOf("") }
    var textfieldSize by remember { mutableStateOf(Size.Zero) }
    var isExpanded by remember { mutableStateOf(false) }

    val icon = if (isExpanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp), // Padding around the dropdown
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // OutlinedTextField for selecting options
        OutlinedTextField(
            value = selectedText,
            onValueChange = {},
            modifier = Modifier
                .fillMaxWidth(0.72f) // 72% of the parent width for better spacing
                .onGloballyPositioned { coordinates ->
                    textfieldSize = coordinates.size.toSize()
                },
            label = { Text(text = label) },
            trailingIcon = {
                Icon(
                    icon, contentDescription = "Toggle Dropdown",
                    Modifier.clickable { isExpanded = !isExpanded }
                )
            },
            readOnly = true
        )

        // DropdownMenu for displaying options
        DropdownMenu(
            expanded = isExpanded,
            onDismissRequest = { isExpanded = false },
            modifier = Modifier.width(with(LocalDensity.current) { textfieldSize.width.toDp() })
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(text = option) },
                    onClick = {
                        selectedText = option
                        isExpanded = false
                        onOptionSelect(option)
                    }
                )
            }
        }
    }
}

