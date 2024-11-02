package com.example.jobmatch.employer.pages

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.example.jobmatch.R
import com.example.jobmatch.pages.MainProfile

@Composable
fun EmployerProfile( navController: NavController){
    MainProfile(
        profilePic = R.drawable.jungie1,
        username = "Pirate King",
        email = "pirateking12345@gmail.com",
        "As a dedicated computer science professional, I am proficient in developing software " +
                "solutions and optimizing systems for better performance. My expertise includes programming languages, " +
                "database management, and problem-solving, allowing me to create and manage efficient software applications. " +
                "I excel in both independent and collaborative environments, consistently delivering quality results within deadlines. My passion for technology drives me to stay updated on emerging trends and continuously improve my skills. With strong analytical abilities and attention to detail, " +
                "I am committed to enhancing operational efficiency and contributing to innovative projects.",
        navController)
}