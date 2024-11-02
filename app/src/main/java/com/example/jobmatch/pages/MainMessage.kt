package com.example.jobmatch.pages

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.jobmatch.MainTopBar
import com.example.jobmatch.Routes

@Composable
fun Message(navController: NavController){

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "No Messages",
            fontSize = 20.sp,
            color = Color.Gray
        )
    }
    MainTopBar(pageTitle = "Message")
    //Add message button
    Box(modifier = Modifier.fillMaxSize()){
        Icon(
            imageVector = Icons.Default.AddCircle,
            contentDescription = "Add",
            tint = Color(0XFFff8e2b),
            modifier = Modifier
                .size(200.dp)
                .clickable { navController.navigate(Routes.newMessage) }
                .align(Alignment.BottomEnd)
                .padding(start = 100.dp, bottom = 130.dp, end = 30.dp)
        )
        Text(text = "Add New Message", modifier = Modifier
            .align(Alignment.BottomEnd)
            .padding(bottom = 120.dp, end = 20.dp))
    }
}