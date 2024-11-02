package com.example.jobmatch

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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun ChangePassword(navController: NavController){

    var newPassword by remember {
        mutableStateOf("")
    }

    var confirmpass by remember {
        mutableStateOf("")
    }

    Column (
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        OutlinedTextField(
            value = newPassword,
            onValueChange = {newPassword = it},
            label = { Text(text = "New Password") })

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = confirmpass,
            onValueChange = {confirmpass = it},
            label = { Text(text = "Confirm New Password") }
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(onClick = { navController.navigate(Routes.login)},
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFAA300)),
            modifier = Modifier
                .width(280.dp)
                .height(50.dp)
        ) {
            Text(text = "Save", fontSize = 20.sp)
        }
    }
}