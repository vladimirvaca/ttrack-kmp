package com.rvladimir.ttrack.dashboard.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rvladimir.ttrack.ui.theme.BrandGreen
import com.rvladimir.ttrack.ui.theme.DarkBackground

/**
 * Dashboard screen — the app's main hub after a successful login.
 *
 * Displays shortcut cards that can navigate to other sections of the app.
 *
 * @param onNavigate Callback invoked with the target route when a navigation
 *   action is triggered from this screen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(onNavigate: (route: String) -> Unit = {}) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier =
                                Modifier
                                    .size(28.dp)
                                    .background(BrandGreen, RoundedCornerShape(4.dp)),
                        )
                        Text(
                            text = "  TTRACK",
                            color = BrandGreen,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp,
                        )
                    }
                },
                colors =
                    TopAppBarDefaults.topAppBarColors(
                        containerColor = DarkBackground,
                        titleContentColor = BrandGreen,
                    ),
            )
        },
        containerColor = Color(0xFFF5F5F5),
    ) { paddingValues ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 24.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = "Dashboard",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = DarkBackground,
            )
            Text(
                text = "What would you like to do today?",
                fontSize = 14.sp,
                color = Color(0xFF6B7280),
            )

            Spacer(modifier = Modifier.height(8.dp))

            DashboardCard(title = "Workouts", description = "Log and track your training sessions")
            DashboardCard(title = "Progress", description = "View your stats and achievements")
            DashboardCard(title = "Nutrition", description = "Monitor your daily nutrition goals")
            DashboardCard(title = "Settings", description = "Manage your account and preferences")
        }
    }
}

/**
 * A single navigation card displayed on the dashboard.
 *
 * @param title The card's primary label.
 * @param description A short description of the section.
 */
@Composable
private fun DashboardCard(
    title: String,
    description: String,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkBackground,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    fontSize = 13.sp,
                    color = Color(0xFF6B7280),
                )
            }
            Box(
                modifier =
                    Modifier
                        .size(10.dp)
                        .background(BrandGreen, RoundedCornerShape(50)),
            )
        }
    }
}
