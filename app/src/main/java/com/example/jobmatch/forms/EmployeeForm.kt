import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.jobmatch.AppLogo
import com.example.jobmatch.AppName
import com.example.jobmatch.Routes
import java.util.Calendar

@Composable
fun EmployeeForm(navController: NavController) {
    var fname by remember { mutableStateOf("") }
    var lname by remember { mutableStateOf("") }
    var dateOfBirth by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var educationalAttainment by remember { mutableStateOf("") }
    var skills by remember { mutableStateOf("") }
    var selectedMonth by remember { mutableStateOf("Month") }
    var selectedDay by remember { mutableStateOf("Day") }
    var selectedYear by remember { mutableStateOf("Year") }

    val currentYear = Calendar.getInstance().get(Calendar.YEAR)
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(40.dp))
        AppLogo(200)
        Spacer(modifier = Modifier.height(8.dp))
        AppName(30)
        Spacer(modifier = Modifier.height(5.dp))

        Text(
            text = "Provide the following information:",
            fontSize = 16.sp,
            fontStyle = FontStyle.Italic,
            color = Color.Gray,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        OutlinedTextField(
            value = fname,
            onValueChange = { fname = it },
            shape = RoundedCornerShape(12.dp),
            label = { Text(text = "First Name") },
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
        )

        OutlinedTextField(
            value = lname,
            onValueChange = { lname = it },
            shape = RoundedCornerShape(12.dp),
            label = { Text(text = "Last Name") },
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
        )

        // Date of Birth Dropdowns
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 7.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            DropdownMenuWithOptions(
                label = "Month",
                options = (1..12).map { it.toString().padStart(2, '0') },
                selectedOption = selectedMonth,
                onOptionSelect = { selectedMonth = it; updateDateOfBirth(selectedMonth, selectedDay, selectedYear) { dateOfBirth = it } }
            )
            DropdownMenuWithOptions(
                label = "Day",
                options = (1..31).map { it.toString().padStart(2, '0') },
                selectedOption = selectedDay,
                onOptionSelect = { selectedDay = it; updateDateOfBirth(selectedMonth, selectedDay, selectedYear) { dateOfBirth = it } }
            )
            DropdownMenuWithOptions(
                label = "Year",
                options = (1950..currentYear).map { it.toString() },
                selectedOption = selectedYear,
                onOptionSelect = { selectedYear = it; updateDateOfBirth(selectedMonth, selectedDay, selectedYear) { dateOfBirth = it } }
            )
        }

        OutlinedTextField(
            value = dateOfBirth,
            onValueChange = { dateOfBirth = it },
            label = { Text("Date of Birth") },
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
            readOnly = true
        )

        OutlinedTextField(
            value = address,
            onValueChange = { address = it },
            shape = RoundedCornerShape(12.dp),
            label = { Text(text = "Address") },
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
        )

        OutlinedTextField(
            value = educationalAttainment,
            onValueChange = { educationalAttainment = it },
            shape = RoundedCornerShape(12.dp),
            label = { Text(text = "Educational Attainment") },
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
        )

        OutlinedTextField(
            value = skills,
            onValueChange = { skills = it },
            shape = RoundedCornerShape(12.dp),
            label = { Text(text = "Skills") },
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { navController.navigate(Routes.employeeMainScreen) },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0XFFff8e2b)),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .padding(horizontal = 32.dp)
        ) {
            Text(text = "Submit", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
fun DropdownMenuWithOptions(
    label: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelect: (String) -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }

    Box {
        OutlinedTextField(
            value = selectedOption,
            onValueChange = {},
            modifier = Modifier
                .width(100.dp)
                .clickable { isExpanded = true },
            label = { Text(text = label) },
            readOnly = true,
            trailingIcon = {
                Icon(
                    imageVector = if (isExpanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                    contentDescription = null,
                    modifier = Modifier.clickable { isExpanded = !isExpanded }
                )
            }
        )

        DropdownMenu(
            expanded = isExpanded,
            onDismissRequest = { isExpanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onOptionSelect(option)
                        isExpanded = false
                    }
                )
            }
        }
    }
}

// Function to update date of birth field from dropdown selections
fun updateDateOfBirth(month: String, day: String, year: String, update: (String) -> Unit) {
    if (month != "Month" && day != "Day" && year != "Year") {
        update("$month/$day/$year")
    }
}
