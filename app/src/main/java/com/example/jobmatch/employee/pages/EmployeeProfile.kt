package com.example.jobmatch.employee.pages

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.example.jobmatch.R
import com.example.jobmatch.pages.MainProfile

@Composable
fun EmployeeProfile(navController: NavController){
   MainProfile(
      profilePic = R.drawable.gambe,
      username = "Cristian Gambe", email = "cristiangambe56789@gmail.com",
      "I am a reliable and skilled employee in the computer field, specializing in programming, " +
              "software development, and troubleshooting technical issues. I have hands-on experience working with various " +
              "technologies, which allows me to contribute effectively to both team-based and independent projects. My focus " +
              "on continuous learning ensures I stay current with the latest trends and advancements in the tech " +
              "industry. I am detail-oriented and committed to delivering high-quality work, meeting deadlines, and " +
              "supporting overall business goals. My problem-solving abilities and collaborative mindset make me a valuable asset to any organization.",
      navController)
}