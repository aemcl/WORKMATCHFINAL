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

@Composable
fun WhatAreYou(roleSelect: (String) -> Unit) {
    val roleOptions = listOf("Employee", "Employer")
    DropdownMenuWithOptions(
        options = roleOptions,
        label = "What are you?",
        onOptionSelect = { roleSelect(it) }
    )
}

@Composable
fun SecurityQuestionDropdown(onQuestionSelect: (String) -> Unit) {
    val securityQuestions = listOf(
        "What is your pet's name?",
        "What is your mother's maiden name?",
        "What is your first school's name?",
        "What is your favorite book?",
        "What is the name of your best friend?"
    )
    DropdownMenuWithOptions(
        options = securityQuestions,
        label = "Security Questions",
        onOptionSelect = { onQuestionSelect(it) }
    )
}

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
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), // Padding around the dropdown
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // OutlinedTextField for selecting options
        OutlinedTextField(
            value = selectedText,
            onValueChange = {},
            modifier = Modifier
                .fillMaxWidth(0.72f) // 90% of the parent width for better spacing
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
