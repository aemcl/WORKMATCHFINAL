package com.example.jobmatch


import JobDescription
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.jobmatch.employee.EditEmployeeProfile
import com.example.jobmatch.employee.EmployeeMainScreen
import com.example.jobmatch.employee.pages.ChangePassword
import com.example.jobmatch.employee.pages.DocumentViewerScreen
import com.example.jobmatch.employee.pages.EmployeeHomePage
import com.example.jobmatch.employee.pages.EmployeeProfile
import com.example.jobmatch.employee.pages.RecommendedJobsScreen
import com.example.jobmatch.employer.EditEmployerProfile
import com.example.jobmatch.employer.EmployerMainScreen
import com.example.jobmatch.employer.EmployerSearch
import com.example.jobmatch.employer.PostJob
import com.example.jobmatch.employer.RecommendedWorkers
import com.example.jobmatch.employer.pages.EmployeeProfileScreen
import com.example.jobmatch.employer.pages.EmployerHomePage
import com.example.jobmatch.employer.pages.EmployerProfile
import com.example.jobmatch.forms.EmployeeForm
import com.example.jobmatch.forms.EmployerForm
import com.google.firebase.auth.FirebaseAuth

@Composable
fun Navigation(){
    val navController = rememberNavController()
    val auth = FirebaseAuth.getInstance() // Initialize FirebaseAuth instance

    // Check if user is logged in
    val isUserLoggedIn = auth.currentUser != null

    // Define the user role; this could be retrieved from the user's profile in the database
    val userRole = "employee" // or "employer" based on the logged-in user

    NavHost(
        navController = navController,
        startDestination = when {
            isUserLoggedIn && userRole == "Employee" -> Routes.employeeMainScreen
            isUserLoggedIn && userRole == "Employer" -> Routes.employerMainScreen
            else -> Routes.welcome
        }
    ) {
        // Define your routes here
        composable(Routes.employeeMainScreen) {
            EmployeeMainScreen(navController,userRole, userId = userRole)
        }
        composable(Routes.employerMainScreen) {
            EmployerMainScreen(navController,userRole)
        }
        composable(Routes.welcome) {
            Welcome(navController)
        }


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

        composable(Routes.employerSearch) {
            EmployerSearch(navController = navController,  userRole = userRole)
        }
        composable(Routes.signup) {
            SignUp(navController)
        }

        composable(Routes.employerMainScreen) {
            EmployerMainScreen(navController = navController, userRole = userRole)
        }

        composable(
            route = "${Routes.recoWorkers}/{employerId}",
            arguments = listOf(
                navArgument("employerId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            // Retrieve the employerId from the backStackEntry arguments
            val employerId = backStackEntry.arguments?.getString("employerId") ?: ""

            // Pass it to the RecommendedWorkers composable
            RecommendedWorkers(navController = navController, employerId = employerId)
        }


        composable(Routes.authentication) {
            Authentication()
        }

        composable(Routes.employeeProfile) {
            EmployeeProfile(navController)
        }

        composable(Routes.employerProfile) {
            EmployerProfile(navController)
        }

        composable("job_detail/{jobName}") { backStackEntry ->
            val jobName = backStackEntry.arguments?.getString("jobName") ?: ""
            JobDescription(navController = navController, jobName = jobName)
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
            LogOut(navController=navController,userRole)
        }
        composable(Routes.employerHomePage) { backStackEntry ->
            backStackEntry.arguments?.getString("employerId") ?: "" // Fetch employerId from arguments
            EmployerHomePage(navController = navController, employerId=userRole)
        }

        composable(Routes.employeeHomePage) {
            EmployeeHomePage(navController, userRole)
        }
        composable(Routes.addJob) {
            PostJob(navController)
        }

        composable("employeeMainScreen") {
            val currentUser = FirebaseAuth.getInstance().currentUser
            if (currentUser != null) {
                val userId = currentUser.uid
                EmployeeMainScreen(navController = navController, userRole = "employee", userId = userId)
            } else {
                navController.navigate(Routes.login)
            }
        }
// In your NavController setup
        composable("employeeProfile/{employeeName}") { backStackEntry ->
            val employeeName = backStackEntry.arguments?.getString("employeeName") ?: ""
            EmployeeProfileScreen(navController, employeeName)
        }

        composable("job_recommendations") {
            RecommendedJobsScreen(navController, userRole)
        }
        composable("documentViewer/{uri}") { backStackEntry ->
            val uri = backStackEntry.arguments?.getString("uri")
            DocumentViewerScreen(uri = uri)
        }

    }
}