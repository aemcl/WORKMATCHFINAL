package com.example.jobmatch

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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