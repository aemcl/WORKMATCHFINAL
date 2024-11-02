package com.example.jobmatch

import EmployeeForm
import JobCredentials
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.SearchBar
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.jobmatch.employee.EmployeeMainScreen
import com.example.jobmatch.employer.EmployerCompanyProfile
import com.example.jobmatch.employer.EmployerMainScreen
import com.example.jobmatch.employer.JobInformation
import com.example.jobmatch.employer.RecommendedWorkers
import com.example.jobmatch.employer.pages.EmployerHomePage

import com.example.jobmatch.forms.EmployerForm
import com.example.jobmatch.pages.EditProfile
import com.example.jobmatch.pages.NewMessage
import com.google.firebase.Firebase
import com.google.firebase.database.database
import com.google.firebase.FirebaseApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            val navController = rememberNavController()
            NavHost(navController = navController, startDestination = Routes.welcome, builder = {
                composable(Routes.welcome) {
                    Welcome(navController)
                }

                composable(Routes.home) {
                    Home(navController)
                }

                composable(Routes.login) {
                    LogIn(navController)
                }

                composable(Routes.forgotpass) {
                    ForgotPassword(navController)
                }

                composable(Routes.changepass) {
                    ChangePassword(navController)
                }

                composable(Routes.edit) {
                    Edit(navController)
                }

                composable(Routes.search) {
                    MainSearchBar(navController)
                }

                composable(Routes.signup) {
                    SignUp(navController)
                }

                composable(Routes.createprofile) {
                    EmployerCompanyProfile(navController)
                }

                composable(Routes.employerMainScreen) {
                    EmployerMainScreen(navController)
                }

                composable(Routes.employeeMainScreen) {
                    EmployeeMainScreen(navController)
                }

                composable(Routes.workinformation) {
                    JobInformation(navController)
                }

                composable(Routes.jobcredentials) {
                   JobCredentials(navController)
                }

                composable(Routes.recoWorkers) {
                    RecommendedWorkers()
                }

                composable(Routes.newMessage) {
                    NewMessage()
                }

                composable(Routes.employeeForm) {
                    EmployeeForm(navController)
                }

                composable(Routes.employerForm) {
                    EmployerForm(navController)
                }

            })
        }
    }
}

