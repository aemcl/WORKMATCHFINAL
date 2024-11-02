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
fun ForgotPassword(navController: NavController){

    var securityquestion = "What is my Middle Name?" //usba ni para sa database
    var answer by remember {
        mutableStateOf("")
    } //kani pod

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Security Question: ${securityquestion}")

        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = answer,
            onValueChange = {answer = it},
            label = { Text(text = "Write your answer here.")})

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { navController.navigate(Routes.changepass)},
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFAA300)),
                modifier = Modifier
                    .width(280.dp)
                    .height(50.dp)
        ) {
            Text(text = "Verify", fontSize = 20.sp)
        }
    }

}