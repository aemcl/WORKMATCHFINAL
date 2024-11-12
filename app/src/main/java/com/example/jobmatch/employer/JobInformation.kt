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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
fun JobInformation(navController: NavController){

    var jobtype by remember {
        mutableStateOf("")
    }
    var workingtime by remember {
        mutableStateOf("")
    }
    var salary by remember {
        mutableStateOf("")
    }
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Enter Job Information", fontSize = 24.sp)

        Spacer(modifier = Modifier.height(4.dp))

        OutlinedTextField(
            value = jobtype,
            onValueChange = { jobtype = it },
            label = { Text(text = "Full time/Part time:") })

        Spacer(modifier = Modifier.height(2.dp))

        OutlinedTextField(
            value = workingtime,
            onValueChange = { workingtime = it },
            label = { Text(text = "Working Time:") },
        )

        Spacer(modifier = Modifier.height(6.dp))

        OutlinedTextField(
            value = salary ,
            onValueChange ={ salary = it},
            label = { Text(text = "Salary") }
        )

        Spacer(modifier = Modifier.height(10.dp))
        Button(
            onClick = { navController.navigate(Routes.jobCredentials) /*Log.i("Credential", "UserName:$UserName email:$email Password:$password")*/
            }
        ) {
            Text(text = "Submit")
        }
    }
}