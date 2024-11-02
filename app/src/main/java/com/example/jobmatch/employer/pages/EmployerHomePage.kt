@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.jobmatch.employer.pages

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.jobmatch.MainSearchBar
import com.example.jobmatch.Routes
import com.example.jobmatch.employer.RecommendedWorkers

@Composable
fun EmployerHomePage( navController: NavController) {

    MainSearchBar(navController)
    //List of Workers/Employee
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 100.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        RecommendedWorkers()

        //Add job button
        Box(modifier = Modifier.fillMaxSize()) {
            Icon(
                imageVector = Icons.Default.AddCircle,
                contentDescription = "Add",
                tint = Color(0XFFff8e2b),
                modifier = Modifier
                    .size(200.dp)
                    .clickable { navController.navigate(Routes.workinformation) }
                    .align(Alignment.BottomEnd)
                    .padding(start = 100.dp, bottom = 130.dp, end = 30.dp)
            )
            Text(
                text = "Add Job", modifier = Modifier.align(Alignment.BottomEnd)
                    .padding(start = 100.dp, bottom = 120.dp, end = 42.dp)
            )
        }
    }
}