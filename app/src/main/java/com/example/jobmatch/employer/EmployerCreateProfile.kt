package com.example.jobmatch.employer

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.jobmatch.R
import com.example.jobmatch.Routes

@Composable
fun EmployerCompanyProfile(navController: NavController){

    var companyName by remember {
        mutableStateOf("")
    }
    var employerName by remember {
        mutableStateOf("")
    }
    var contactnumber by remember {
        mutableStateOf("")
    }
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "LOGO",
            modifier = Modifier.size(200.dp)
        )
        Text(text = "Create Profile", fontSize = 24.sp)

        Spacer(modifier = Modifier.height(4.dp))

        OutlinedTextField(
            value = companyName,
            onValueChange = { companyName = it },
            label = { Text(text = "Company Name:") })

        Spacer(modifier = Modifier.height(2.dp))

        OutlinedTextField(
            value = employerName,
            onValueChange = { employerName = it },
            label = { Text(text = "Employer Name:") },
        )

        Spacer(modifier = Modifier.height(6.dp))

        OutlinedTextField(
            value = contactnumber ,
            onValueChange ={ contactnumber = it},
            label = { Text(text = "Contact Number")}
        )

        Button(
            onClick = { navController.navigate(Routes.jobOffer)
            }
        ) {
            Text(text = ">")
        }
    }
}