package com.example.ui.owner

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.*
import com.example.data.services.GymOwnerRepository
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun OwnerDashboardScreen(
    onNavigateToCustomers: () -> Unit = {},
    onNavigateToMemberships: () -> Unit = {},
    onNavigateToPlans: () -> Unit = {},
    onNavigateToPayments: () -> Unit = {},
    onNavigateToBookings: () -> Unit = {},
    onNavigateToAttendance: () -> Unit = {},
    onNavigateToGym: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val kpis = remember { GymOwnerRepository.getDashboardKpis() }
    val membershipOverview = remember { GymOwnerRepository.getMembershipOverview() }
    val todayOps = remember { GymOwnerRepository.getTodayOperations() }
    val occupancy = remember { GymOwnerRepository.getGymOccupancy() }
    val alerts = remember { GymOwnerRepository.getOperationalAlerts() }
    var selectedExpiryFilter by remember { mutableStateOf(15) }
    val expiringMemberships = remember(selectedExpiryFilter) {
        GymOwnerRepository.getExpiringMemberships(selectedExpiryFilter)
    }
    val pendingPaymentsList = remember { GymOwnerRepository.getPendingPayments().take(3) }

    var selectedRevenueFilter by remember { mutableStateOf("30 Days") }
    val revenueData = remember(selectedRevenueFilter) {
        GymOwnerRepository.getRevenueChartData(selectedRevenueFilter)
    }

    val recentCustomers = remember { GymOwnerRepository.getCustomers().take(4) }
    val recentPayments = remember { GymOwnerRepository.getPayments().take(4) }
    val upcomingBookings = remember { GymOwnerRepository.getBookings().take(3) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        // ----------------------------------------------------
        // WELCOME BANNER
        // ----------------------------------------------------
        LiquidGlassCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "WELCOME BACK",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = TextSubtle,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Kishore Kumar",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextWhite
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0x20FFFFFF))
                            .border(1.dp, GlassBorderLight, RoundedCornerShape(12.dp))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "Thu, 24 Sep 2026",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextWhite
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Here is the summary of your gym's operational metrics, customer memberships, and revenue flow for today.",
                    fontSize = 13.sp,
                    color = TextMuted,
                    lineHeight = 18.sp
                )
            }
        }

        // ----------------------------------------------------
        // FEATURE 1: TODAY'S OPERATIONS
        // ----------------------------------------------------
        OwnerSectionHeader(
            title = "Today's Operations",
            actionLabel = "Live Attendance",
            onActionClick = onNavigateToAttendance
        )
        Spacer(modifier = Modifier.height(8.dp))

        LiquidGlassCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
            ) {
                Text(
                    text = "TODAY'S OPERATIONS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextSubtle,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TodayOpsMetricBox("Check-ins", todayOps.todayCheckins.toString(), Icons.Default.QrCodeScanner, Modifier.weight(1f))
                    Spacer(modifier = Modifier.width(8.dp))
                    TodayOpsMetricBox("Bookings", todayOps.todayBookings.toString(), Icons.Default.EventAvailable, Modifier.weight(1f))
                    Spacer(modifier = Modifier.width(8.dp))
                    TodayOpsMetricBox("Inside Now", todayOps.activeMembersInside.toString(), Icons.Default.DirectionsRun, Modifier.weight(1f))
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TodayOpsMetricBox("New Members", todayOps.newMembershipsToday.toString(), Icons.Default.PersonAddAlt1, Modifier.weight(1f))
                    Spacer(modifier = Modifier.width(8.dp))
                    TodayOpsMetricBox("Expiring Soon", todayOps.expiringSoonCount.toString(), Icons.Default.HourglassTop, Modifier.weight(1f))
                    Spacer(modifier = Modifier.width(8.dp))
                    TodayOpsMetricBox("Revenue", "₹${todayOps.todayRevenue.toInt()}", Icons.Default.CurrencyRupee, Modifier.weight(1f))
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // ----------------------------------------------------
        // KPI METRIC CARDS (2x2 Grid)
        // ----------------------------------------------------
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            OwnerKpiCard(
                title = "Total Customers",
                value = "1,248",
                changeText = "+12% vs last mo",
                icon = Icons.Default.People,
                modifier = Modifier.weight(1f)
            )
            OwnerKpiCard(
                title = "Active Members",
                value = "986",
                changeText = "+8% vs last mo",
                icon = Icons.Default.CardMembership,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            OwnerKpiCard(
                title = "Monthly Revenue",
                value = "₹4,82,500",
                changeText = "+15.4% vs last mo",
                icon = Icons.Default.CurrencyRupee,
                modifier = Modifier.weight(1f)
            )
            OwnerKpiCard(
                title = "Bookings Today",
                value = "42",
                changeText = "On schedule",
                icon = Icons.Default.EventAvailable,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // ----------------------------------------------------
        // FEATURE 11: GYM CAPACITY & LIVE OCCUPANCY
        // ----------------------------------------------------
        LiquidGlassCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "CURRENT GYM OCCUPANCY",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = TextSubtle,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = "${occupancy.currentInside} / ${occupancy.capacity}",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextWhite
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "${occupancy.percentage}% capacity",
                                fontSize = 12.sp,
                                color = TextMuted,
                                modifier = Modifier.padding(bottom = 2.dp)
                            )
                        }
                    }
                    OwnerStatusBadge(status = occupancy.status)
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Occupancy progress bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0x20FFFFFF))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(occupancy.percentage / 100f)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(4.dp))
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(StatusActiveGreen, Color(0x8034D399))
                                )
                            )
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Daily visits: ${occupancy.dailyVisits}",
                        fontSize = 11.sp,
                        color = ByceCoolGray
                    )
                    Text(
                        text = "Peak: ${occupancy.peakPeriod}",
                        fontSize = 11.sp,
                        color = ByceCoolGray
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // ----------------------------------------------------
        // FEATURE 16: OPERATIONAL ALERTS
        // ----------------------------------------------------
        OwnerSectionHeader(title = "Operational Alerts")
        Spacer(modifier = Modifier.height(8.dp))

        alerts.forEach { alert ->
            LiquidGlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 3.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (alert.urgency == "High") Icons.Default.Warning else Icons.Default.Info,
                        contentDescription = null,
                        tint = if (alert.urgency == "High") StatusPendingAmber else TextSubtle,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = alert.message,
                        fontSize = 12.sp,
                        color = TextWhite,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ----------------------------------------------------
        // FEATURE 17: QUICK ACTIONS
        // ----------------------------------------------------
        OwnerSectionHeader(title = "Quick Actions")
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            QuickActionButton(
                label = "Attendance",
                icon = Icons.Default.FactCheck,
                onClick = onNavigateToAttendance,
                modifier = Modifier.weight(1f)
            )
            QuickActionButton(
                label = "Expiring",
                icon = Icons.Default.HourglassBottom,
                onClick = onNavigateToMemberships,
                modifier = Modifier.weight(1f)
            )
            QuickActionButton(
                label = "Pending Dues",
                icon = Icons.Default.ReceiptLong,
                onClick = onNavigateToPayments,
                modifier = Modifier.weight(1f)
            )
            QuickActionButton(
                label = "Gym Profile",
                icon = Icons.Default.FitnessCenter,
                onClick = onNavigateToGym,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            QuickActionButton(
                label = "Add Customer",
                icon = Icons.Default.PersonAdd,
                onClick = onNavigateToCustomers,
                modifier = Modifier.weight(1f)
            )
            QuickActionButton(
                label = "Create Plan",
                icon = Icons.Default.AddCard,
                onClick = onNavigateToPlans,
                modifier = Modifier.weight(1f)
            )
            QuickActionButton(
                label = "Bookings",
                icon = Icons.Default.CalendarToday,
                onClick = onNavigateToBookings,
                modifier = Modifier.weight(1f)
            )
            QuickActionButton(
                label = "QR Pass",
                icon = Icons.Default.QrCode2,
                onClick = onNavigateToGym,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ----------------------------------------------------
        // REVENUE OVERVIEW & CHART
        // ----------------------------------------------------
        OwnerSectionHeader(
            title = "Revenue Overview",
            actionLabel = "Detailed Report",
            onActionClick = onNavigateToPayments
        )
        Spacer(modifier = Modifier.height(8.dp))

        LiquidGlassCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
            ) {
                // Filter Tabs (7 Days, 30 Days, 3 Months, 1 Year)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("7 Days", "30 Days", "3 Months", "1 Year").forEach { tab ->
                        val isSelected = selectedRevenueFilter == tab
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) Color(0x35FFFFFF) else Color(0x12FFFFFF))
                                .border(1.dp, if (isSelected) GlassBorderSpecular else Color.Transparent, RoundedCornerShape(12.dp))
                                .clickable { selectedRevenueFilter = tab }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = tab,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) TextWhite else TextMuted
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Bar Chart Visualizer
                val maxAmount = revenueData.maxOfOrNull { it.amount } ?: 1.0
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .padding(top = 10.dp, bottom = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    revenueData.forEach { point ->
                        val barRatio = (point.amount / maxAmount).toFloat().coerceIn(0.1f, 1.0f)
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Bottom,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "₹${(point.amount / 1000).toInt()}k",
                                fontSize = 10.sp,
                                color = TextWhite,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(0.55f)
                                    .fillMaxHeight(barRatio)
                                    .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                                    .background(
                                        Brush.verticalGradient(
                                            colors = listOf(Color(0x99FFFFFF), Color(0x30FFFFFF))
                                        )
                                    )
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = point.label,
                                fontSize = 10.sp,
                                color = ByceCoolGray,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ----------------------------------------------------
        // MEMBERSHIP OVERVIEW
        // ----------------------------------------------------
        OwnerSectionHeader(
            title = "Membership Overview",
            actionLabel = "All Memberships",
            onActionClick = onNavigateToMemberships
        )
        Spacer(modifier = Modifier.height(8.dp))

        LiquidGlassCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
            ) {
                // Breakdown Badges
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    MembershipStatusCounter("Active", membershipOverview.active.toString(), StatusActiveGreen)
                    MembershipStatusCounter("Pending", membershipOverview.pending.toString(), StatusPendingAmber)
                    MembershipStatusCounter("Expired", membershipOverview.expired.toString(), StatusExpiredRed)
                    MembershipStatusCounter("Cancelled", membershipOverview.cancelled.toString(), StatusCancelledGray)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Distribution Bar
                val total = (membershipOverview.active + membershipOverview.pending + membershipOverview.expired + membershipOverview.cancelled).toFloat()
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(RoundedCornerShape(5.dp))
                ) {
                    Box(modifier = Modifier.weight(membershipOverview.active / total).fillMaxHeight().background(StatusActiveGreen))
                    Box(modifier = Modifier.weight(membershipOverview.pending / total).fillMaxHeight().background(StatusPendingAmber))
                    Box(modifier = Modifier.weight(membershipOverview.expired / total).fillMaxHeight().background(StatusExpiredRed))
                    Box(modifier = Modifier.weight(membershipOverview.cancelled / total).fillMaxHeight().background(StatusCancelledGray))
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ----------------------------------------------------
        // FEATURE 4: MEMBERSHIPS EXPIRING SOON
        // ----------------------------------------------------
        OwnerSectionHeader(
            title = "Memberships Expiring Soon",
            actionLabel = "View All",
            onActionClick = onNavigateToMemberships
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Expiry Filter Pills
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf(Pair(7, "7 Days"), Pair(15, "15 Days"), Pair(30, "30 Days")).forEach { (days, label) ->
                val isSelected = selectedExpiryFilter == days
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) Color(0x35FFFFFF) else Color(0x12FFFFFF))
                        .border(1.dp, if (isSelected) GlassBorderSpecular else Color.Transparent, RoundedCornerShape(12.dp))
                        .clickable { selectedExpiryFilter = days }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) TextWhite else TextMuted
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (expiringMemberships.isEmpty()) {
            LiquidGlassCard(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
                Box(modifier = Modifier.fillMaxWidth().padding(20.dp), contentAlignment = Alignment.Center) {
                    Text(text = "No memberships expiring within $selectedExpiryFilter days.", fontSize = 12.sp, color = TextMuted)
                }
            }
        } else {
            expiringMemberships.forEach { item ->
                LiquidGlassCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = item.customerName, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TextWhite)
                            Text(text = "${item.membershipPlan} · Expires in ${item.daysRemaining} days (${item.expiryDate})", fontSize = 11.sp, color = ByceCoolGray)
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0x28FFFFFF))
                                .border(1.dp, GlassBorderSpecular, RoundedCornerShape(10.dp))
                                .clickable { onNavigateToMemberships() }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(text = "View", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ----------------------------------------------------
        // FEATURE 9: OUTSTANDING / PENDING PAYMENTS
        // ----------------------------------------------------
        OwnerSectionHeader(
            title = "Pending Payments & Dues",
            actionLabel = "View All",
            onActionClick = onNavigateToPayments
        )
        Spacer(modifier = Modifier.height(8.dp))

        pendingPaymentsList.forEach { pending ->
            LiquidGlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = pending.customerName, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextWhite)
                        Text(text = "${pending.membershipPlan} · ${pending.dueDate}", fontSize = 11.sp, color = ByceCoolGray)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(text = "₹${pending.amount.toInt()}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = StatusPendingAmber)
                        Spacer(modifier = Modifier.height(2.dp))
                        OwnerStatusBadge(status = pending.status)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ----------------------------------------------------
        // RECENT CUSTOMERS
        // ----------------------------------------------------
        OwnerSectionHeader(
            title = "Recent Customers",
            actionLabel = "View All",
            onActionClick = onNavigateToCustomers
        )
        Spacer(modifier = Modifier.height(8.dp))

        recentCustomers.forEach { customer ->
            LiquidGlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color(0x22FFFFFF)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = customer.avatarInitials,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextWhite
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = customer.name,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextWhite
                        )
                        Text(
                            text = "${customer.membershipPlan} · Joined ${customer.joinedDate}",
                            fontSize = 11.sp,
                            color = ByceCoolGray
                        )
                    }

                    OwnerStatusBadge(status = customer.status)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ----------------------------------------------------
        // RECENT PAYMENTS
        // ----------------------------------------------------
        OwnerSectionHeader(
            title = "Recent Payments",
            actionLabel = "View All",
            onActionClick = onNavigateToPayments
        )
        Spacer(modifier = Modifier.height(8.dp))

        recentPayments.forEach { payment ->
            LiquidGlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0x20FFFFFF)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CurrencyRupee,
                            contentDescription = "Payment",
                            tint = TextWhite,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = payment.customerName,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextWhite
                        )
                        Text(
                            text = "${payment.planName} · ${payment.date}",
                            fontSize = 11.sp,
                            color = ByceCoolGray
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "₹${payment.amount.toInt()}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextWhite
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        OwnerStatusBadge(status = payment.status)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ----------------------------------------------------
        // UPCOMING BOOKINGS
        // ----------------------------------------------------
        OwnerSectionHeader(
            title = "Upcoming Bookings",
            actionLabel = "View All",
            onActionClick = onNavigateToBookings
        )
        Spacer(modifier = Modifier.height(8.dp))

        upcomingBookings.forEach { booking ->
            LiquidGlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0x20FFFFFF)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccessTime,
                            contentDescription = "Time",
                            tint = TextWhite,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = booking.customerName,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextWhite
                        )
                        Text(
                            text = "${booking.shift} · ${booking.date} · ${booking.time}",
                            fontSize = 11.sp,
                            color = ByceCoolGray
                        )
                    }

                    OwnerStatusBadge(status = booking.status)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun QuickActionButton(
    label: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    LiquidGlassCard(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(Color(0x22FFFFFF)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = TextWhite,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = TextWhite,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun MembershipStatusCounter(
    label: String,
    count: String,
    accentColor: Color
) {
    Column(horizontalAlignment = Alignment.Start) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .background(accentColor, CircleShape)
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = label,
                fontSize = 11.sp,
                color = ByceCoolGray
            )
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = count,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = TextWhite
        )
    }
}

@Composable
private fun TodayOpsMetricBox(
    label: String,
    value: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0x18FFFFFF))
            .border(1.dp, GlassBorderLight, RoundedCornerShape(14.dp))
            .padding(vertical = 12.dp, horizontal = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(imageVector = icon, contentDescription = null, tint = TextWhite, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = value, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextWhite)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = label, fontSize = 10.sp, color = ByceCoolGray, maxLines = 1)
        }
    }
}
