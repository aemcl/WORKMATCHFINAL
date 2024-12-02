package com.example.jobmatch

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun Welcome(navController: NavController){
    Column(
        modifier = Modifier.fillMaxSize().background(Color(0xFFE0EAF5)),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Logo",
            modifier = Modifier.size(300.dp)
        )

        AppName(50)

        Spacer(modifier = Modifier.height(20.dp))
        Text(text = "Finding Job and Seeking Employee?",
            fontSize = 20.sp,
            fontStyle = FontStyle.Italic,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(100.dp))

        Button(
            onClick = { navController.navigate(Routes.login) },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0XFF2E77AE)),
            modifier = Modifier.width(200.dp)
        ) {
            Text(text = "Click Here!", fontSize = 18.sp)
        }
         Spacer(modifier = Modifier.height(100.dp))
    }
}

@Composable
fun AppName(nameSize: Int){

    Text(
        text = "Work Match",
        fontSize = nameSize.sp,
        fontWeight = FontWeight.ExtraBold,
    )
}

@Composable
fun AppLogo(imageSize: Int){
    Image(
        painter = painterResource(id = R.drawable.logo),
        contentDescription = "Logo",
        modifier = Modifier.size(imageSize.dp)
    )
}