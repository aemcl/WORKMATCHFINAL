package com.example.jobmatch.employee

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.jobmatch.employee.pages.EmployeeHomePage
import com.example.jobmatch.employee.pages.EmployeeProfile
import com.example.jobmatch.NavItem

//sa navigation ni para mo display
@Composable
fun EmployeeMainScreen(navController: NavController, userRole: String, userId: String) {

    val navItemList = listOf(
        NavItem("Home", Icons.Default.Home, 0),
        NavItem("Profile", Icons.Default.AccountCircle, 0)
    )

    var selectedIndex by remember {
        mutableIntStateOf(0)
    }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar {
                navItemList.forEachIndexed { index, navItem ->
                    NavigationBarItem(
                        selected = selectedIndex == index,
                        onClick = { selectedIndex = index },
                        icon = {
                                Icon(imageVector = navItem.icon, contentDescription = "Icon")
                        },
                        label = {
                            Text(text = navItem.label)
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        EmployeeContentScreen(
            modifier = Modifier.padding(innerPadding),
            selectedIndex = selectedIndex,
            navController = navController,
            userRole = userRole,
            userId = userId
        )
    }
}

@Composable
fun EmployeeContentScreen(
    modifier: Modifier = Modifier,
    selectedIndex: Int,
    navController: NavController,
    userRole: String,
    userId: String
) {
    when (selectedIndex) {
        0 -> EmployeeHomePage(navController, userId)
        1 -> EmployeeProfile(navController)
    }
}
