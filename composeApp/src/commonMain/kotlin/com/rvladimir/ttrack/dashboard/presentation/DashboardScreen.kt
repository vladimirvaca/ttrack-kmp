package com.rvladimir.ttrack.dashboard.presentation

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Update
import androidx.compose.material.icons.filled.Watch
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rvladimir.ttrack.core.navigation.Screen
import com.rvladimir.ttrack.ui.theme.BarChartGreen
import com.rvladimir.ttrack.ui.theme.BrandGreen
import com.rvladimir.ttrack.ui.theme.DarkBackground
import com.rvladimir.ttrack.ui.theme.LightGray
import com.rvladimir.ttrack.ui.theme.OffWhite
import com.rvladimir.ttrack.ui.theme.PurpleIcon
import com.rvladimir.ttrack.ui.theme.PurpleIconBg
import com.rvladimir.ttrack.ui.theme.TextGray
import com.rvladimir.ttrack.ui.theme.TextGreen

/**
 * Dashboard screen — the app's main hub after a successful login.
 */
@Composable
fun DashboardScreen(onNavigate: (route: String) -> Unit = {}) {
    Scaffold(
        bottomBar = { DashboardBottomNavigation() },
        containerColor = OffWhite,
    ) { paddingValues ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 20.dp)
                    .verticalScroll(rememberScrollState()),
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // Header Section
            HeaderSection()

            Spacer(modifier = Modifier.height(24.dp))

            // Last Session Section
            SectionHeader(title = "Last Session", actionText = "View All")
            Spacer(modifier = Modifier.height(12.dp))
            LastSessionCard()

            Spacer(modifier = Modifier.height(24.dp))

            // Quick Start Section
            Text(
                text = "Quick Start",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = DarkBackground,
            )
            Spacer(modifier = Modifier.height(12.dp))
            QuickStartSection(onNavigate = onNavigate)

            Spacer(modifier = Modifier.height(24.dp))

            // Weekly Goal Section
            SectionHeader(title = "Weekly Goal", actionText = "3 of 5 workouts")
            Spacer(modifier = Modifier.height(12.dp))
            WeeklyGoalCard()

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun HeaderSection() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column {
            Text(
                text = "Monday, 24 Oct",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = TextGreen,
            )
            Text(
                text = "Good morning, Alex",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = DarkBackground,
            )
        }
        // Mock Profile Image with Online Status
        Box(contentAlignment = Alignment.BottomEnd) {
            Box(
                modifier =
                    Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFFDAB9)), // Peach color for avatar bg
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(32.dp),
                )
            }
            Box(
                modifier =
                    Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .padding(2.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF22C55E)), // Green status dot
            )
        }
    }
}

@Composable
private fun SectionHeader(
    title: String,
    actionText: String,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = DarkBackground,
        )
        Text(
            text = actionText,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = BrandGreen,
            modifier = Modifier.clickable { /* Handle action */ },
        )
    }
}

@Composable
private fun LastSessionCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            // Faded background icon
            Icon(
                imageVector = Icons.Default.FitnessCenter,
                contentDescription = null,
                tint = BrandGreen.copy(alpha = 0.1f),
                modifier =
                    Modifier
                        .size(120.dp)
                        .align(Alignment.CenterEnd)
                        .padding(end = 16.dp),
            )

            Column(modifier = Modifier.padding(20.dp)) {
                Box(
                    modifier =
                        Modifier
                            .background(BrandGreen.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 12.dp, vertical = 4.dp),
                ) {
                    Text(
                        text = "Yesterday",
                        color = BrandGreen,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Upper Body Strength",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = DarkBackground,
                        )
                        Text(
                            text = "Gym • 4:30 PM",
                            fontSize = 14.sp,
                            color = TextGray,
                            fontWeight = FontWeight.Medium,
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.MoreHoriz,
                        contentDescription = null,
                        tint = TextGray,
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    WorkoutStatItem(
                        icon = Icons.Default.Update,
                        label = "Duration",
                        value = "45m",
                    )
                    WorkoutStatItem(
                        icon = Icons.Default.FitnessCenter,
                        label = "Volume",
                        value = "12.5k",
                    )
                    WorkoutStatItem(
                        icon = Icons.Default.History,
                        label = "PRs",
                        value = "8",
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { /* Handle repeat */ },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = DarkBackground),
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            "Repeat Workout",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.Default.Restore,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun WorkoutStatItem(
    icon: ImageVector,
    label: String,
    value: String,
) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = TextGreen,
                modifier = Modifier.size(14.dp),
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = label,
                fontSize = 12.sp,
                color = TextGray,
                fontWeight = FontWeight.Medium,
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = DarkBackground,
        )
    }
}

@Composable
private fun QuickStartSection(onNavigate: (String) -> Unit = {}) {
    Row(
        modifier = Modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // Empty Session Card
        QuickStartCard(
            title = "Empty Session",
            subtitle = "Start from scratch",
            icon = Icons.Default.Add,
            backgroundColor = BrandGreen,
            iconContainerColor = Color.White.copy(alpha = 0.3f),
            iconTint = DarkBackground,
            textColor = DarkBackground,
            subtitleColor = DarkBackground.copy(alpha = 0.7f),
        )

        // Custom Sets Card
        QuickStartCard(
            title = "Custom Sets",
            subtitle = "Your sets, your pace",
            icon = Icons.Default.Watch,
            backgroundColor = Color.White,
            iconContainerColor = PurpleIconBg,
            iconTint = PurpleIcon,
            textColor = DarkBackground,
            subtitleColor = TextGray,
            onClick = { onNavigate(Screen.Timer.route) },
        )
    }
}

@Composable
private fun QuickStartCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    backgroundColor: Color,
    iconContainerColor: Color,
    iconTint: Color,
    textColor: Color,
    subtitleColor: Color,
    onClick: () -> Unit = {},
) {
    Card(
        modifier =
            Modifier
                .width(160.dp)
                .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Box(
                modifier =
                    Modifier
                        .size(40.dp)
                        .background(iconContainerColor, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = textColor,
            )
            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = subtitleColor,
            )
        }
    }
}

@Composable
private fun WeeklyGoalCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(
                            text = "Progress",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = DarkBackground,
                        )
                        Text(
                            text = "60%",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = BrandGreen,
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = 0.6f,
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(CircleShape),
                        color = BrandGreen,
                        trackColor = LightGray,
                    )
                }

                Spacer(modifier = Modifier.width(24.dp))

                // Mini Bar Chart
                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Bar(height = 12.dp, active = false)
                    Bar(height = 24.dp, active = true)
                    Bar(height = 16.dp, active = false)
                }
            }
        }
    }
}

@Composable
private fun Bar(
    height: androidx.compose.ui.unit.Dp,
    active: Boolean,
) {
    Canvas(
        modifier =
            Modifier
                .width(6.dp)
                .height(24.dp),
    ) {
        drawLine(
            color = if (active) BarChartGreen else LightGray,
            start =
                androidx.compose.ui.geometry
                    .Offset(size.width / 2, size.height),
            end =
                androidx.compose.ui.geometry
                    .Offset(size.width / 2, size.height - height.toPx()),
            strokeWidth = size.width,
            cap = StrokeCap.Round,
        )
    }
}

@Composable
private fun DashboardBottomNavigation() {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 0.dp,
    ) {
        val items =
            listOf(
                Triple("Home", Icons.Default.Home, true),
                Triple("History", Icons.Default.History, false),
                Triple("Exercises", Icons.Default.FitnessCenter, false),
                Triple("Profile", Icons.Default.Person, false),
            )

        items.forEach { (label, icon, selected) ->
            NavigationBarItem(
                selected = selected,
                onClick = { },
                icon = { Icon(icon, contentDescription = label) },
                label = {
                    Text(
                        label,
                        fontSize = 11.sp,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                    )
                },
                colors =
                    NavigationBarItemDefaults.colors(
                        selectedIconColor = BrandGreen,
                        selectedTextColor = BrandGreen,
                        unselectedIconColor = TextGray,
                        unselectedTextColor = TextGray,
                        indicatorColor = Color.Transparent,
                    ),
            )
        }
    }
}
