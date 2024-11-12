package com.example.jobmatch

import JobCredentials
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.jobmatch.employee.EmployeeMainScreen
import com.example.jobmatch.employee.EmployeeSearch
import com.example.jobmatch.employee.pages.ChangePassword
import com.example.jobmatch.employee.EditEmployeeProfile
import com.example.jobmatch.employee.pages.EmployeeProfile
import com.example.jobmatch.employer.EditEmployerProfile
import com.example.jobmatch.employer.EmployerCompanyProfile
import com.example.jobmatch.employer.EmployerMainScreen
import com.example.jobmatch.employer.EmployerSearch
import com.example.jobmatch.employer.JobDescription
import com.example.jobmatch.employer.JobInformation
import com.example.jobmatch.employer.RecommendedWorkers
import com.example.jobmatch.employer.pages.EmployerProfile
import com.example.jobmatch.forms.EmployeeForm
import com.example.jobmatch.forms.EmployerForm
import com.example.jobmatch.pages.NewMessage
import com.google.firebase.auth.FirebaseAuth


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            val auth = FirebaseAuth.getInstance() // Initialize FirebaseAuth instance

            // Check if user is logged in
            val isUserLoggedIn = auth.currentUser != null

            // Define the user role; this could be retrieved from the user's profile in the database
            val userRole = "employee" // or "employer" based on the logged-in user

            NavHost(
                navController = navController,
                startDestination = if (isUserLoggedIn) Routes.employeeMainScreen else Routes.welcome
            ) {
                composable(Routes.welcome) {
                    Welcome(navController)
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

                composable(Routes.employeeSearch) {
                    EmployeeSearch(navController = navController,  userRole = userRole)
                }
                composable(Routes.employerSearch) {
                    EmployerSearch(navController = navController,  userRole = userRole)
                }
                composable(Routes.signup) {
                    SignUp(navController)
                }

                composable(Routes.createProfile) {
                    EmployerCompanyProfile(navController)
                }

                composable(Routes.employerMainScreen) {
                    EmployerMainScreen(navController = navController, userRole = userRole)
                }

                composable(Routes.employeeMainScreen) {
                    EmployeeMainScreen(navController = navController, userRole = userRole)
                }

                composable(Routes.workInformation) {
                    JobInformation(navController)
                }

                composable(Routes.jobCredentials) {
                    JobCredentials(navController)
                }

                composable(Routes.recoWorkers) {
                    RecommendedWorkers()
                }

                composable(Routes.newMessage) {
                    NewMessage()
                }

                composable(Routes.authentication) {
                    Authentication()
                }

                composable(Routes.employeeProfile) {
                    EmployeeProfile(navController = navController) // No need to pass context
                }

                composable(Routes.employerProfile) {
                    EmployerProfile(navController)
                }

                composable(Routes.jobDescription) {
                    JobDescription(
                        jobName = "",
                        companyName = "",
                        employmentType = "",
                        jobDescription = "",
                        salary = ""
                    )
                }

                composable(Routes.employeeForm) {
                    EmployeeForm(navController)
                }

                composable(Routes.employerForm) {
                    EmployerForm(navController)
                }

                composable(Routes.editEmployeeProfile) {
                    EditEmployeeProfile(navController)
                }

                composable(Routes.editEmployerProfile) {
                    EditEmployerProfile(navController)
                }
                composable(Routes.logOut) {
                    LogOut(navController=navController)
                }

            }
        }
    }
}
