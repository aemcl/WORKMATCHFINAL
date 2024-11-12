package com.example.jobmatch

object Routes {
    const val welcome = "welcome"
    const val login = "login"
    const val logOut= "log out"
    const val forgotpass = "forgotpass"
    const val changepass = "changepass"
    const val employeeSearch = "Employee search"
    const val employerSearch = "Employer search"
    const val signup = "signup"
    const val createProfile = "createProfile"
    const val employerMainScreen = "employerMainScreen"
    const val employeeMainScreen = "employeeMainScreen"
    const val workInformation = "workInformation"
    const val jobCredentials = "jobCredentials"
    const val recoWorkers = "recommendedWorkers"
    const val newMessage = "newMessage"
    const val employeeForm = "employeeForm"
    const val employerForm = "employerForm"
    const val fullProfilePicture = "full_profile_picture"

    const val jobOffer = "jobOffer"
    const val employeeHomePage = "employeeHome"
    const val jobDescription = "jobDescription"
    const val authentication = "authentication"
    const val employeeProfile = "Employee Profile"
    const val employerProfile = "Employer Profile"
    const val editEmployeeProfile = "Edit Employee Profile"
    const val editEmployerProfile = "Edit Employer Profile"
    fun jobDescription(jobDetails: String) = "jobDescription/$jobDetails"

}
