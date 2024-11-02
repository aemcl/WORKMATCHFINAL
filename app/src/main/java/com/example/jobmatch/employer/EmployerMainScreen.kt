package com.example.jobmatch.employer

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.jobmatch.BottomBar
import com.example.jobmatch.ContentScreen
import com.example.jobmatch.MainSearchBar
import com.example.jobmatch.Routes
import com.example.jobmatch.employer.pages.EmployerHomePage
import com.example.jobmatch.employer.pages.EmployerMessagePage
import com.example.jobmatch.employer.pages.EmployerNotificationPage
import com.example.jobmatch.employer.pages.EmployerProfile

@Composable
fun EmployerMainScreen(navController: NavController){


    val navItemList = listOf(
        NavItem("Home", Icons.Default.Home, 0),
        NavItem("Message", Icons.Default.Email, 5),
        NavItem("Notification", Icons.Default.Notifications, 5),
        NavItem("Profile", Icons.Default.AccountCircle, 0)
    )

    var selectedIndex by remember {
        mutableIntStateOf(0)
    }



    Scaffold(
        modifier = Modifier.fillMaxSize(),
        // .background(color = Color(0XFF2E77AE))

        bottomBar = {
            NavigationBar {
                navItemList.forEachIndexed{ index, navItem ->
                    NavigationBarItem(
                        selected = selectedIndex == index,
                        onClick = { selectedIndex = index},
                        icon = {
                            BadgedBox(badge = {
                                if( navItem.badgeCount>0)
                                    Badge(){
                                        Text(text = navItem.badgeCount.toString())
                                    }
                            } ) {
                                Icon(imageVector = navItem.icon, contentDescription = "Icon")
                            }
                        },
                        label = {
                            Text(text = navItem.label)
                        }
                    )
                }
            }
        }
    ) {
            innerPadding ->
        EmployerContentScreen(modifier = Modifier.padding(innerPadding), selectedIndex, navController)
    }
}

@Composable
fun EmployerContentScreen (modifier: Modifier = Modifier, selectedIndex : Int, navController: NavController){
    when(selectedIndex){
        0 -> EmployerHomePage(navController)
        1 -> EmployerMessagePage(navController)
        2 -> EmployerNotificationPage()
        3 -> EmployerProfile(navController)
    }
}
