package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.R
import com.example.ui.theme.*

// ---------------------------------------------------------------------------
// 1. BYCE GYM OWNER TOP BAR & HEADER
// ---------------------------------------------------------------------------
@Composable
fun OwnerHeader(
    gymName: String = "Iron House Fitness",
    location: String = "Kozhikode, Kerala",
    unreadCount: Int = 2,
    ownerInitials: String = "KK",
    onMenuClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    statusBarPadding: Dp = 0.dp
) {
    LiquidGlassCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp, topStart = 0.dp, topEnd = 0.dp),
        shadowElevation = 0.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = statusBarPadding + 14.dp, bottom = 18.dp)
                .padding(horizontal = 20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Menu Icon + Brand Logo & Gym Identity
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onMenuClick,
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color(0x20FFFFFF))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Menu Navigation",
                            tint = TextWhite,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Image(
                        painter = painterResource(id = R.drawable.byce_logo),
                        contentDescription = "BYCE Logo",
                        modifier = Modifier.height(26.dp)
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(20.dp)
                            .background(Color(0x30FFFFFF))
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = "GYM OWNER",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = TextSubtle,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = gymName,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextWhite
                        )
                    }
                }

                // Notifications + Owner Avatar
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Notification Icon with badge
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color(0x20FFFFFF))
                            .clickable { onNotificationClick() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifications",
                            tint = TextWhite,
                            modifier = Modifier.size(20.dp)
                        )
                        if (unreadCount > 0) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .align(Alignment.TopEnd)
                                    .offset(x = (-6).dp, y = 6.dp)
                                    .background(StatusActiveGreen, CircleShape)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    // Owner Avatar (Clean frosted glass with white text)
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color(0x35FFFFFF), Color(0x18FFFFFF))
                                )
                            )
                            .border(1.dp, GlassBorderSpecular, CircleShape)
                            .clickable { onProfileClick() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = ownerInitials,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextWhite
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Subtitle Row: Location & Live Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Location",
                        tint = TextSubtle,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = location,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = ByceCoolGray
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .background(StatusActiveGreen, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Live Dashboard",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextWhite
                    )
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------
// 2. REUSABLE KPI CARD
// ---------------------------------------------------------------------------
@Composable
fun OwnerKpiCard(
    title: String,
    value: String,
    changeText: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    isPositiveTrend: Boolean = true
) {
    LiquidGlassCard(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        shadowElevation = 0.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = title.uppercase(),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = ByceCoolGray,
                    letterSpacing = 0.5.sp
                )
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color(0x22FFFFFF)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = TextWhite,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = value,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = TextWhite
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (isPositiveTrend) Icons.AutoMirrored.Filled.TrendingUp else Icons.Default.TrendingDown,
                    contentDescription = "Trend",
                    tint = if (isPositiveTrend) StatusActiveGreen else StatusExpiredRed,
                    modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = changeText,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (isPositiveTrend) StatusActiveGreen else StatusExpiredRed
                )
            }
        }
    }
}

// ---------------------------------------------------------------------------
// 3. STATUS BADGE
// ---------------------------------------------------------------------------
@Composable
fun OwnerStatusBadge(
    status: String,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor) = when (status.lowercase()) {
        "active", "successful", "completed" -> Pair(StatusActiveGreenBg, StatusActiveGreen)
        "pending", "upcoming" -> Pair(StatusPendingAmberBg, StatusPendingAmber)
        "expired", "failed" -> Pair(StatusExpiredRedBg, StatusExpiredRed)
        "cancelled", "inactive" -> Pair(StatusCancelledGrayBg, TextMuted)
        else -> Pair(Color(0x25FFFFFF), TextWhite)
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .border(0.5.dp, textColor.copy(alpha = 0.35f), RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(5.dp)
                    .clip(CircleShape)
                    .background(textColor)
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = status,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
        }
    }
}

// ---------------------------------------------------------------------------
// 4. SEARCH & FILTER BAR
// ---------------------------------------------------------------------------
@Composable
fun OwnerSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    placeholder: String = "Search...",
    filterOptions: List<String> = emptyList(),
    selectedFilter: String = "",
    onFilterSelected: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        // Search Input
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
                .clip(RoundedCornerShape(22.dp))
                .background(Color(0x18FFFFFF))
                .border(1.dp, GlassBorderLight, RoundedCornerShape(22.dp))
                .padding(horizontal = 14.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = ByceCoolGray,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Box(modifier = Modifier.weight(1f)) {
                    if (query.isEmpty()) {
                        Text(
                            text = placeholder,
                            fontSize = 13.sp,
                            color = TextSubtle
                        )
                    }
                    androidx.compose.foundation.text.BasicTextField(
                        value = query,
                        onValueChange = onQueryChange,
                        singleLine = true,
                        textStyle = androidx.compose.ui.text.TextStyle(
                            color = TextWhite,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                if (query.isNotEmpty()) {
                    IconButton(
                        onClick = { onQueryChange("") },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Clear",
                            tint = ByceCoolGray,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }

        // Filter Pills
        if (filterOptions.isNotEmpty()) {
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                filterOptions.forEach { option ->
                    val isSelected = selectedFilter == option
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (isSelected) Color(0x35FFFFFF) else Color(0x12FFFFFF))
                            .border(
                                1.dp,
                                if (isSelected) GlassBorderSpecular else GlassBorderLight,
                                RoundedCornerShape(16.dp)
                            )
                            .clickable { onFilterSelected(option) }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = option,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) TextWhite else TextMuted
                        )
                    }
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------
// 5. SECTION HEADER WITH ACTION
// ---------------------------------------------------------------------------
@Composable
fun OwnerSectionHeader(
    title: String,
    actionLabel: String? = null,
    onActionClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = TextWhite
        )
        if (actionLabel != null) {
            Text(
                text = actionLabel,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextWhite,
                modifier = Modifier.clickable { onActionClick() }
            )
        }
    }
}

// ---------------------------------------------------------------------------
// 6. LOADING SKELETON, EMPTY STATE & ERROR STATE
// ---------------------------------------------------------------------------
@Composable
fun OwnerLoadingState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator(
            color = TextWhite,
            modifier = Modifier.size(36.dp),
            strokeWidth = 3.dp
        )
        Spacer(modifier = Modifier.height(14.dp))
        Text(
            text = "Loading dashboard data...",
            fontSize = 13.sp,
            color = TextMuted
        )
    }
}

@Composable
fun OwnerEmptyState(
    title: String = "No Records Found",
    description: String = "There are no entries matching your selection.",
    icon: ImageVector = Icons.Default.Inbox,
    modifier: Modifier = Modifier
) {
    LiquidGlassCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color(0x18FFFFFF)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = ByceCoolGray,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = TextWhite
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = description,
                fontSize = 12.sp,
                color = TextSubtle,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

@Composable
fun OwnerErrorState(
    message: String = "Failed to load data from server.",
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    LiquidGlassCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.ErrorOutline,
                contentDescription = "Error",
                tint = StatusExpiredRed,
                modifier = Modifier.size(36.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Connection Error",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = TextWhite
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = message,
                fontSize = 12.sp,
                color = TextSubtle,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color(0x35FFFFFF))
                    .border(1.dp, GlassBorderSpecular, RoundedCornerShape(18.dp))
                    .clickable { onRetry() }
                    .padding(horizontal = 20.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "Retry",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite
                )
            }
        }
    }
}

// ---------------------------------------------------------------------------
// 7. GYM OWNER NAVIGATION DRAWER / MODAL
// ---------------------------------------------------------------------------
data class OwnerNavItem(
    val title: String,
    val icon: ImageVector,
    val badge: String? = null
)

@Composable
fun OwnerNavigationDrawerSheet(
    currentScreenName: String,
    onNavigate: (String) -> Unit,
    onClose: () -> Unit,
    onLogout: () -> Unit
) {
    val navItems = listOf(
        OwnerNavItem("Dashboard", Icons.Default.Dashboard),
        OwnerNavItem("Attendance", Icons.Default.QrCodeScanner, badge = "86"),
        OwnerNavItem("Customers", Icons.Default.People),
        OwnerNavItem("Memberships", Icons.Default.CardMembership),
        OwnerNavItem("Membership Plans", Icons.Default.PriceCheck),
        OwnerNavItem("Payments", Icons.Default.Payment),
        OwnerNavItem("Bookings", Icons.Default.EventAvailable, badge = "42"),
        OwnerNavItem("Gym Profile", Icons.Default.FitnessCenter),
        OwnerNavItem("Gym Location", Icons.Default.PinDrop),
        OwnerNavItem("Staff", Icons.Default.Badge),
        OwnerNavItem("Reports", Icons.Default.BarChart),
        OwnerNavItem("Activity Log", Icons.Default.History),
        OwnerNavItem("Notifications", Icons.Default.Notifications, badge = "2"),
        OwnerNavItem("Settings", Icons.Default.Settings)
    )

    Box(
        modifier = Modifier
            .fillMaxHeight()
            .width(300.dp)
            .background(DarkNavyDepth)
            .border(1.dp, GlassBorderLight)
            .padding(horizontal = 16.dp, vertical = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            // Brand Header in Drawer
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Image(
                    painter = painterResource(id = R.drawable.byce_logo),
                    contentDescription = "BYCE Logo",
                    modifier = Modifier.height(28.dp)
                )
                IconButton(onClick = onClose) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close Drawer",
                        tint = TextWhite
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "GYM OWNER DASHBOARD",
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold,
                color = TextSubtle,
                letterSpacing = 1.sp
            )

            Text(
                text = "Iron House Fitness · Kozhikode",
                fontSize = 12.sp,
                color = ByceCoolGray
            )

            Spacer(modifier = Modifier.height(20.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Color(0x20FFFFFF))
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Navigation List
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(androidx.compose.foundation.rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                navItems.forEach { item ->
                    val isSelected = currentScreenName == item.title
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) Color(0x28FFFFFF) else Color.Transparent)
                            .clickable {
                                onNavigate(item.title)
                                onClose()
                            }
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.title,
                            tint = if (isSelected) TextWhite else TextMuted,
                            modifier = Modifier.size(19.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = item.title,
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) TextWhite else TextMuted,
                            modifier = Modifier.weight(1f)
                        )
                        if (item.badge != null) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(StatusActiveGreen)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = item.badge,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DarkNavy
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Logout Option
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onLogout() }
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Logout,
                    contentDescription = "Logout",
                    tint = StatusExpiredRed,
                    modifier = Modifier.size(19.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Logout",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = StatusExpiredRed
                )
            }
        }
    }
}

// ---------------------------------------------------------------------------
// 7. HIGH-CONTRAST OPAQUE MODAL CONTAINER & DIALOG
// Eliminates translucent "mirror banner" effect and background bleed-through.
// ---------------------------------------------------------------------------
val OwnerModalSurface = Color(0xFF13161A) // 100% Solid Deep Charcoal Surface
val OwnerModalSurfaceElevated = Color(0xFF1B2026) // Solid elevated surface for inner cards/sections

@Composable
fun OwnerModalCard(
    modifier: Modifier = Modifier,
    shape: androidx.compose.ui.graphics.Shape = RoundedCornerShape(24.dp),
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .shadow(
                elevation = 28.dp,
                shape = shape,
                ambientColor = Color.Black,
                spotColor = Color.Black
            )
            .clip(shape)
            .background(OwnerModalSurface)
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0x40FFFFFF),
                        Color(0x18FFFFFF),
                        Color(0x0CFFFFFF)
                    )
                ),
                shape = shape
            )
    ) {
        content()
    }
}

@Composable
fun OwnerModalDialog(
    onDismissRequest: () -> Unit,
    properties: DialogProperties = DialogProperties(usePlatformDefaultWidth = false),
    content: @Composable () -> Unit
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = properties
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            OwnerModalCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
            ) {
                content()
            }
        }
    }
}
