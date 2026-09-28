package com.example.ui.owner

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.*
import com.example.ui.theme.*

enum class OwnerScreenRoute(val title: String) {
    DASHBOARD("Dashboard"),
    ATTENDANCE("Attendance"),
    CUSTOMERS("Customers"),
    MEMBERSHIPS("Memberships"),
    PLANS("Membership Plans"),
    PAYMENTS("Payments"),
    BOOKINGS("Bookings"),
    GYM_PROFILE("Gym Profile"),
    GYM_LOCATION("Gym Location"),
    STAFF("Staff"),
    REPORTS("Reports"),
    ACTIVITY_LOG("Activity Log"),
    NOTIFICATIONS("Notifications"),
    SETTINGS("Settings")
}

@Composable
fun GymOwnerMainScreen(
    onLogout: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var currentRoute by remember { mutableStateOf(OwnerScreenRoute.DASHBOARD) }
    var isDrawerOpen by remember { mutableStateOf(false) }

    val statusBarPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val navBarPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        DarkNavy,
                        DarkNavyDepth,
                        Color(0xFF161415)
                    )
                )
            )
    ) {
        // Ambient Moving Blur Orbs
        FloatingBlurBalls()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = navBarPadding + 70.dp)
        ) {
            // Persistent Brand Top Header
            OwnerHeader(
                gymName = "Iron House Fitness",
                location = "Kozhikode, Kerala",
                unreadCount = 2,
                ownerInitials = "KK",
                onMenuClick = { isDrawerOpen = true },
                onNotificationClick = { currentRoute = OwnerScreenRoute.NOTIFICATIONS },
                onProfileClick = { currentRoute = OwnerScreenRoute.SETTINGS },
                statusBarPadding = statusBarPadding
            )

            // Dynamic Screen Content Container with Crossfade
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                Crossfade(
                    targetState = currentRoute,
                    label = "owner_screen_crossfade"
                ) { route ->
                    when (route) {
                        OwnerScreenRoute.DASHBOARD -> OwnerDashboardScreen(
                            onNavigateToCustomers = { currentRoute = OwnerScreenRoute.CUSTOMERS },
                            onNavigateToMemberships = { currentRoute = OwnerScreenRoute.MEMBERSHIPS },
                            onNavigateToPlans = { currentRoute = OwnerScreenRoute.PLANS },
                            onNavigateToPayments = { currentRoute = OwnerScreenRoute.PAYMENTS },
                            onNavigateToBookings = { currentRoute = OwnerScreenRoute.BOOKINGS },
                            onNavigateToAttendance = { currentRoute = OwnerScreenRoute.ATTENDANCE },
                            onNavigateToGym = { currentRoute = OwnerScreenRoute.GYM_PROFILE }
                        )
                        OwnerScreenRoute.ATTENDANCE -> OwnerAttendanceScreen()
                        OwnerScreenRoute.CUSTOMERS -> OwnerCustomersScreen()
                        OwnerScreenRoute.MEMBERSHIPS -> OwnerMembershipsScreen()
                        OwnerScreenRoute.PLANS -> OwnerPlansScreen()
                        OwnerScreenRoute.PAYMENTS -> OwnerPaymentsScreen()
                        OwnerScreenRoute.BOOKINGS -> OwnerBookingsScreen()
                        OwnerScreenRoute.GYM_PROFILE -> OwnerGymScreen(initialTab = "Profile")
                        OwnerScreenRoute.GYM_LOCATION -> OwnerGymScreen(initialTab = "Location")
                        OwnerScreenRoute.STAFF -> OwnerStaffScreen()
                        OwnerScreenRoute.REPORTS -> OwnerReportsScreen()
                        OwnerScreenRoute.ACTIVITY_LOG -> OwnerActivityLogScreen()
                        OwnerScreenRoute.NOTIFICATIONS -> OwnerNotificationsScreen()
                        OwnerScreenRoute.SETTINGS -> OwnerSettingsScreen(onLogoutClick = onLogout)
                    }
                }
            }
        }

        // Floating Bottom Navigation Bar (Primary Quick Access)
        OwnerGlassBottomBar(
            currentRoute = currentRoute,
            onRouteSelected = { currentRoute = it },
            onMoreClick = { isDrawerOpen = true },
            modifier = Modifier.align(Alignment.BottomCenter)
        )

        // Slide-out Navigation Drawer
        AnimatedVisibility(
            visible = isDrawerOpen,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0x80000000))
                    .clickable { isDrawerOpen = false }
            ) {
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .clickable(enabled = false) {}
                ) {
                    OwnerNavigationDrawerSheet(
                        currentScreenName = currentRoute.title,
                        onNavigate = { title ->
                            val match = OwnerScreenRoute.values().firstOrNull { it.title == title }
                            if (match != null) {
                                currentRoute = match
                            }
                        },
                        onClose = { isDrawerOpen = false },
                        onLogout = onLogout
                    )
                }
            }
        }
    }
}

@Composable
private fun OwnerGlassBottomBar(
    currentRoute: OwnerScreenRoute,
    onRouteSelected: (OwnerScreenRoute) -> Unit,
    onMoreClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val items = listOf(
        Pair(OwnerScreenRoute.DASHBOARD, Icons.Default.Dashboard),
        Pair(OwnerScreenRoute.CUSTOMERS, Icons.Default.People),
        Pair(OwnerScreenRoute.MEMBERSHIPS, Icons.Default.CardMembership),
        Pair(OwnerScreenRoute.PAYMENTS, Icons.Default.Payment)
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xF0222021),
                        Color(0xF8161415),
                        Color(0xFF161415)
                    )
                )
            )
            .border(
                0.5.dp,
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0x35FFFFFF),
                        Color(0x10FFFFFF)
                    )
                ),
                RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp)
            )
    ) {
        // Top specular highlight
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .align(Alignment.TopCenter)
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color(0x25FFFFFF),
                            Color(0x50FFFFFF),
                            Color(0x25FFFFFF),
                            Color.Transparent
                        )
                    )
                )
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp, horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { (route, icon) ->
                val isSelected = currentRoute == route
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onRouteSelected(route) }
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = route.title,
                        tint = if (isSelected) TextWhite else TextSubtle,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = route.title,
                        fontSize = 10.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) TextWhite else TextSubtle
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Box(
                        modifier = Modifier
                            .size(4.dp)
                            .clip(CircleShape)
                            .background(if (isSelected) NeonGreen else Color.Transparent)
                    )
                }
            }

            // More / All Menu Drawer trigger
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onMoreClick() }
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "All Modules",
                    tint = TextSubtle,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = "Menu",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextSubtle
                )
                Spacer(modifier = Modifier.height(5.dp))
            }
        }
    }
}
