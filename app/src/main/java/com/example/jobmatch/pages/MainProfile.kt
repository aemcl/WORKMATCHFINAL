package com.example.jobmatch.pages
import androidx.compose.foundation.border

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.jobmatch.Routes

@Composable
fun MainProfile(
    @DrawableRes profilePic: Int,
    username: String,
    email: String,
    profileDescription: String,
    navController: NavController
) {
    val scrollState = rememberScrollState()
    var isExpanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(scrollState)
                .padding(top = 10.dp, bottom = 60.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp, top = 30.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    modifier = Modifier.clickable { navController.navigate(Routes.employeeMainScreen) } // Back navigation
                )

                Spacer(modifier = Modifier.width(16.dp))

                BasicTextField(
                    value = "Profile",
                    onValueChange = {},
                    textStyle = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 20.sp,
                        color = Color.Black
                    ),
                    enabled = false
                )

                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit Profile",
                    modifier = Modifier
                        .padding(end = 25.dp)
                        .clickable { navController.navigate(Routes.editProfile) } // Edit profile navigation
                )
            }

            OutlinedCard(
                border = BorderStroke(5.dp, Color.White),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFEBDA98)),
                modifier = Modifier
                    .size(180.dp)
                    .padding(top = 16.dp),
                shape = CircleShape
            ) {
                Image(
                    painter = painterResource(profilePic),
                    contentDescription = "Profile Picture",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Username Box with border
            Box(
                modifier = Modifier
                    .border(
                        width = 2.dp,
                        brush = Brush.horizontalGradient(listOf(Color.Gray, Color.LightGray)),
                        shape = CircleShape
                    )
                    .width(300.dp)
                    .padding(12.dp)
            ) {
                Text(
                    text = "Username: $username",
                    fontSize = 14.sp,
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Email Box with border
            Box(
                modifier = Modifier
                    .border(
                        width = 2.dp,
                        brush = Brush.horizontalGradient(listOf(Color.Gray, Color.LightGray)),
                        shape = CircleShape
                    )
                    .width(300.dp)
                    .padding(12.dp)
            ) {
                Text(
                    text = "Email Address: $email",
                    fontSize = 14.sp,
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            OutlinedCard(
                border = BorderStroke(2.dp, Color.LightGray),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    text = "Description:\n\n$profileDescription",
                    textAlign = TextAlign.Justify,
                    fontSize = 18.sp,
                    modifier = Modifier
                        .padding(16.dp)
                        .clickable { isExpanded = !isExpanded },
                    maxLines = if (isExpanded) Int.MAX_VALUE else 4,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(30.dp))

            Button(
                onClick = { navController.navigate(Routes.changepass) },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
                modifier = Modifier
                    .width(280.dp)
                    .height(50.dp)
            ) {
                Text(text = "Change Password")
            }

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = { navController.navigate(Routes.login) },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                modifier = Modifier
                    .width(280.dp)
                    .height(50.dp)
            ) {
                Text(text = "Log Out")
            }
        }
    }
}
