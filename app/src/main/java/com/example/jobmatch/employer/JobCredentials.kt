import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.jobmatch.Routes

@Composable
fun JobCredentials(navController: NavController) {
    var fileName by remember { mutableStateOf("") }
    var fileSize by remember { mutableStateOf(" ") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .background(Color(0xFFEFEFEF), RoundedCornerShape(16.dp))
                .border(1.dp, Color.LightGray, RoundedCornerShape(16.dp))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Drag & drop images and files",
                fontSize = 16.sp,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(8.dp))
            ClickableText(
                text = AnnotatedString("or browse files on your computer"),
                onClick = { /* Open file picker */ },
                style = LocalTextStyle.current.copy(color = Color.Blue, fontSize = 14.sp)
            )
            Spacer(modifier = Modifier.height(16.dp))


            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(8.dp))
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = fileName, color = Color.Black)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = fileSize, fontSize = 12.sp, color = Color.Gray)
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = { /* Remove file action */ }) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Remove file",
                        tint = Color.Red
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Upload Button
            Button(
                onClick = { navController.navigate(Routes.employerMainScreen) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(Color(0xFF7E57C2))
            ) {
                Text(text = "Upload", color = Color.White)
            }
        }
    }
}
