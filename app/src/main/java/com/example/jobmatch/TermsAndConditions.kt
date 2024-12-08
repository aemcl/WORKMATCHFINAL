package com.example.jobmatch
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class) // Opt-in for the experimental API
@Composable
fun TermsAndConditions(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Terms and Conditions") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Text(
                text = "Terms and Conditions",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(bottom = 16.dp)
                    .align(Alignment.CenterHorizontally) // Centers the text horizontally
            )
            Text(
                text = """
Last Updated: December 7 2024

Thank you for using Work Match . Please read these Terms and Conditions  carefully before using the App. By accessing or using the App, you agree to be bound by these Terms. If you do not agree to these Terms, do not use the App.

 1. Acceptance of Terms

By downloading, installing, or using the App, you agree to these Terms and any other policies referenced herein. These Terms constitute a legally binding agreement between you and Work Match Company.

 2. Use of the App

- You must be at least 18 years old or have the consent of a legal guardian to use the App.
- You agree to use the App only for lawful purposes and in compliance with all applicable laws and regulations.
- You are responsible for maintaining the confidentiality of your account and password and for restricting access to your device.

 3. User Content

- You may submit, upload, or share content through the App .
- You retain ownership of your User Content but grant Work Match Company a non-exclusive, worldwide, royalty-free license to use, modify, reproduce, and distribute your User Content solely for the purpose of operating and improving the App.
- You represent and warrant that your User Content does not infringe any third-party rights or violate any laws.

 4. Privacy

Your use of the App is subject to our Privacy Policy, which outlines how we collect, use, and protect your information. By using the App, you agree to the terms of the Privacy Policy.

 5. Intellectual Property

- All content, features, and functionality of the App, including but not limited to text, graphics, logos, and software, are the exclusive property of Work Match Company or its licensors.
- You may not copy, modify, distribute, or create derivative works based on any part of the App without prior written consent.

 6. Prohibited Activities

You agree not to:

- Use the App for any illegal or unauthorized purposes.
- Engage in any activity that disrupts or interferes with the App’s operation or security.
- Attempt to reverse engineer or gain unauthorized access to the App’s source code.

 7. Disclaimer of Warranties

The App is provided on an "as is" and "as available" basis. Work Match Company makes no warranties, expressed or implied, regarding the App, including but not limited to its accuracy, reliability, or fitness for a particular purpose.

 8. Limitation of Liability

To the fullest extent permitted by law, Work Match Company shall not be liable for any damages arising out of or related to your use of the App, including but not limited to direct, indirect, incidental, or consequential damages.

 9. Changes to the Terms

Work Match Company reserves the right to modify these Terms at any time. We will notify you of any changes by updating the "Last Updated" date at the top of these Terms. Your continued use of the App after any changes constitutes your acceptance of the revised Terms.

 10. Governing Law

These Terms are governed by the laws of BISU Clarin Campus, without regard to its conflict of laws principles. Any disputes arising under these Terms shall be resolved exclusively in the courts of BISU Clarin Campus.

 11. Contact Us

If you have any questions or concerns about these Terms, please contact us at cristian.gambe@bisu.edu.ph

---

By checking the Box, you acknowledge that you have read, understood, and agree to be bound by these Terms and Conditions.

By the Developer: Jungie Lobedica and Cristian Gambe.

                """.trimIndent(),
                fontSize = 14.sp,
                lineHeight = 20.sp
            )
        }
    }
}
