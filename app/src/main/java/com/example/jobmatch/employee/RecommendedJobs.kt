package com.example.jobmatch.employee

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jobmatch.R

data class JobNames(val name: String, val pic: Int, val highlighted: Boolean = false)

val userList = listOf(
    JobNames("Google", R.drawable.g, false),
    JobNames("Facebook", R.drawable.fb, false)

)

@Composable
fun JobList(job: List<JobNames>) {
    LazyColumn(modifier = Modifier
        .fillMaxSize()
        .padding(8.dp) // Padding around the entire LazyColumn
    ) {
        items(job.size) { index ->
            NamesDesign(job[index])
            Spacer(modifier = Modifier.size(8.dp)) // Space between items
        }
    }
}

@Composable
fun NamesDesign(nameOfJob: JobNames) {
    val backgroundColor = if (nameOfJob.highlighted) {
        Brush.horizontalGradient(listOf(Color(0xFFE0F7FA), Color(0xFFFFFFFF)))
    } else {
        Brush.horizontalGradient(listOf(Color.Transparent, Color.Transparent))
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(brush = backgroundColor)
    ) {
        Image(
            painter = painterResource(id = nameOfJob.pic),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
        )
        Spacer(modifier = Modifier.width(16.dp))

        Column {
            Text(text = nameOfJob.name, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun RecommendedJobs() {

    Text(text = "Recommended Jobs",
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(all = 20.dp)
    )

    JobList(job = userList)
}
