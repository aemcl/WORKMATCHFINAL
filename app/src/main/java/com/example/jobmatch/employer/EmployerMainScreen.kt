package com.example.jobmatch.employer

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
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
import com.example.jobmatch.NavItem
import com.example.jobmatch.employer.pages.EmployerHomePage
import com.example.jobmatch.employer.pages.EmployerProfile
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmployerMainScreen(navController: NavController, userRole: String) {

    val navItemList = listOf(
        NavItem("Home", Icons.Default.Home, 0),
        NavItem("Profile", Icons.Default.AccountCircle, 0)
    )

    var selectedIndex by remember { mutableIntStateOf(0) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar {
                navItemList.forEachIndexed { index, navItem ->
                    NavigationBarItem(
                        selected = selectedIndex == index,
                        onClick = { selectedIndex = index },
                        icon = {
                            BadgedBox(badge = {
                                if (navItem.badgeCount > 0)
                                    Badge { Text(text = navItem.badgeCount.toString()) }
                            }) {
                                Icon(imageVector = navItem.icon, contentDescription = "Icon")
                            }
                        },
                        label = { Text(text = navItem.label) }
                    )
                }
            }
        }
    ) { innerPadding ->
        EmployerContentScreen(
            modifier = Modifier.padding(innerPadding),
            selectedIndex = selectedIndex,
            navController = navController,
            employerId = userRole
        )
    }
}

@Composable
fun EmployerContentScreen(
    modifier: Modifier = Modifier,
    selectedIndex: Int,
    navController: NavController,
    employerId: String
) {
    when (selectedIndex) {
        0 -> EmployerHomePage(navController = navController, employerId = employerId)
        1 -> EmployerProfile(navController)
    }
}
