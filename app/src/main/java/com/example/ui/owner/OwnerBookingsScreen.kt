package com.example.ui.owner

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.BookingItem
import com.example.data.models.ShiftReminderSchedule
import com.example.data.services.GymOwnerRepository
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun OwnerBookingsScreen(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All") }
    var selectedShiftFilter by remember { mutableStateOf("All Shifts") }
    var showExportDialog by remember { mutableStateOf(false) }
    var exportFormat by remember { mutableStateOf("CSV") }
    var statusMessage by remember { mutableStateOf<String?>(null) }
    val bookings = remember { mutableStateListOf<BookingItem>().apply { addAll(GymOwnerRepository.getBookings()) } }
    var selectedBookingForDetail by remember { mutableStateOf<BookingItem?>(null) }
    val shiftSchedules = remember(bookings.toList()) { GymOwnerRepository.getShiftReminderSchedules() }

    val filteredBookings = remember(searchQuery, selectedFilter, selectedShiftFilter, bookings.toList()) {
        bookings.filter { item ->
            val matchesQuery = item.customerName.contains(searchQuery, ignoreCase = true) ||
                    item.membershipPlan.contains(searchQuery, ignoreCase = true) ||
                    item.customerEmail.contains(searchQuery, ignoreCase = true)
            val matchesFilter = when (selectedFilter) {
                "Today" -> item.date.equals("Today", ignoreCase = true)
                "Upcoming" -> item.status.equals("Upcoming", ignoreCase = true)
                "Completed" -> item.status.equals("Completed", ignoreCase = true)
                "Cancelled" -> item.status.equals("Cancelled", ignoreCase = true)
                else -> true
            }
            val matchesShift = when (selectedShiftFilter) {
                "Morning Shift" -> item.shift.contains("Morning", ignoreCase = true)
                "Evening Shift" -> item.shift.contains("Evening", ignoreCase = true)
                "Night Shift" -> item.shift.contains("Night", ignoreCase = true)
                else -> true
            }
            matchesQuery && matchesFilter && matchesShift
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "CHECK-IN RESERVATIONS & SHIFTS",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = TextSubtle,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Slot Bookings & Reminders",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextWhite
                        )
                    }

                    // Export Button
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0x20FFFFFF))
                            .border(1.dp, GlassBorderLight, RoundedCornerShape(14.dp))
                            .clickable { showExportDialog = true }
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.FileDownload, contentDescription = "Export", tint = TextWhite, modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "Export", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = TextWhite)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                statusMessage?.let { msg ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(StatusActiveGreenBg)
                            .border(1.dp, StatusActiveGreen.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = msg, fontSize = 12.sp, color = StatusActiveGreen, fontWeight = FontWeight.SemiBold)
                            IconButton(onClick = { statusMessage = null }, modifier = Modifier.size(20.dp)) {
                                Icon(Icons.Default.Close, contentDescription = "Dismiss", tint = StatusActiveGreen, modifier = Modifier.size(14.dp))
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }

                // -------------------------------------------------------------------
                // 30-MINUTE SHIFT REMINDER ENGINE CARD (GMAIL INTEGRATION)
                // -------------------------------------------------------------------
                LiquidGlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFEA4335).copy(alpha = 0.18f))
                                        .border(1.dp, Color(0xFFEA4335).copy(alpha = 0.4f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Email,
                                        contentDescription = null,
                                        tint = Color(0xFFEA4335),
                                        modifier = Modifier.size(17.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "30-MIN SHIFT REMINDER ENGINE",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = TextSubtle,
                                        letterSpacing = 0.8.sp
                                    )
                                    Text(
                                        text = "Gmail Pre-Session Alerts",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextWhite
                                    )
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(StatusActiveGreenBg)
                                    .border(1.dp, StatusActiveGreen.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(StatusActiveGreen))
                                    Spacer(modifier = Modifier.width(5.dp))
                                    Text(text = "Gmail Active", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = StatusActiveGreen)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Members receive an automated reminder message through Gmail exactly 30 minutes before gym start time for their Morning, Evening, or Night shift.",
                            fontSize = 11.sp,
                            color = ByceCoolGray,
                            lineHeight = 15.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // 3 Shift schedule cards: Morning, Evening, Night
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            ShiftScheduleMiniCard(
                                title = "Morning",
                                icon = "🌅",
                                gymStart = "06:00 AM",
                                reminderAlert = "05:30 AM",
                                count = shiftSchedules.firstOrNull { it.shiftName.contains("Morning") }?.activeBookingsCount ?: 0,
                                accentColor = Color(0xFFF59E0B),
                                isSelected = selectedShiftFilter == "Morning Shift",
                                modifier = Modifier.weight(1f),
                                onClick = {
                                    selectedShiftFilter = if (selectedShiftFilter == "Morning Shift") "All Shifts" else "Morning Shift"
                                }
                            )

                            ShiftScheduleMiniCard(
                                title = "Evening",
                                icon = "🌇",
                                gymStart = "04:00 PM",
                                reminderAlert = "03:30 PM",
                                count = shiftSchedules.firstOrNull { it.shiftName.contains("Evening") }?.activeBookingsCount ?: 0,
                                accentColor = Color(0xFFF97316),
                                isSelected = selectedShiftFilter == "Evening Shift",
                                modifier = Modifier.weight(1f),
                                onClick = {
                                    selectedShiftFilter = if (selectedShiftFilter == "Evening Shift") "All Shifts" else "Evening Shift"
                                }
                            )

                            ShiftScheduleMiniCard(
                                title = "Night",
                                icon = "🌙",
                                gymStart = "08:00 PM",
                                reminderAlert = "07:30 PM",
                                count = shiftSchedules.firstOrNull { it.shiftName.contains("Night") }?.activeBookingsCount ?: 0,
                                accentColor = Color(0xFF818CF8),
                                isSelected = selectedShiftFilter == "Night Shift",
                                modifier = Modifier.weight(1f),
                                onClick = {
                                    selectedShiftFilter = if (selectedShiftFilter == "Night Shift") "All Shifts" else "Night Shift"
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Shift Filter Chips Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("All Shifts", "Morning Shift", "Evening Shift", "Night Shift").forEach { shift ->
                        val isSelected = selectedShiftFilter == shift
                        val (icon, color) = when (shift) {
                            "Morning Shift" -> Pair("🌅", Color(0xFFF59E0B))
                            "Evening Shift" -> Pair("🌇", Color(0xFFF97316))
                            "Night Shift" -> Pair("🌙", Color(0xFF818CF8))
                            else -> Pair("⚡", TextWhite)
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) color.copy(alpha = 0.25f) else Color(0x18FFFFFF))
                                .border(1.dp, if (isSelected) color.copy(alpha = 0.8f) else Color.Transparent, RoundedCornerShape(12.dp))
                                .clickable { selectedShiftFilter = shift }
                                .padding(horizontal = 12.dp, vertical = 7.dp)
                        ) {
                            Text(
                                text = "$icon $shift",
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) color else TextMuted
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OwnerSearchBar(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    placeholder = "Search member or email...",
                    filterOptions = listOf("All", "Today", "Upcoming", "Completed", "Cancelled"),
                    selectedFilter = selectedFilter,
                    onFilterSelected = { selectedFilter = it }
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Showing ${filteredBookings.size} bookings in $selectedShiftFilter",
                    fontSize = 12.sp,
                    color = ByceCoolGray
                )

                Spacer(modifier = Modifier.height(4.dp))
            }

            if (filteredBookings.isEmpty()) {
                item {
                    OwnerEmptyState(
                        title = "No Bookings",
                        description = "No reservations match '$selectedFilter' in '$selectedShiftFilter'."
                    )
                }
            } else {
                items(filteredBookings, key = { it.id }) { booking ->
                    BookingRowCard(
                        booking = booking,
                        onViewClick = { selectedBookingForDetail = booking },
                        onSendReminder = {
                            val index = bookings.indexOfFirst { it.id == booking.id }
                            if (index != -1) {
                                bookings[index] = booking.copy(
                                    reminderStatus = "Sent via Gmail (30m prior)"
                                )
                                GymOwnerRepository.sendBookingGmailReminder(booking.id)
                                launchGmailReminder(context, booking)
                                statusMessage = "✓ 30-min reminder dispatched to ${booking.customerEmail} via Gmail!"
                            }
                        },
                        onCancelClick = {
                            val index = bookings.indexOfFirst { it.id == booking.id }
                            if (index != -1) {
                                bookings[index] = booking.copy(status = "Cancelled", arrivalStatus = "Not Arrived")
                                GymOwnerRepository.cancelBooking(booking.id)
                            }
                        },
                        onMarkArrived = {
                            val index = bookings.indexOfFirst { it.id == booking.id }
                            if (index != -1) {
                                bookings[index] = booking.copy(arrivalStatus = "Checked In", status = "Completed")
                                GymOwnerRepository.markBookingArrived(booking.id)
                                statusMessage = "✓ ${booking.customerName} marked as Arrived & Checked In for ${booking.time} slot."
                            }
                        }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(40.dp))
            }
        }

        selectedBookingForDetail?.let { booking ->
            BookingDetailModal(
                booking = booking,
                onDismiss = { selectedBookingForDetail = null },
                onSendReminder = {
                    val index = bookings.indexOfFirst { it.id == booking.id }
                    if (index != -1) {
                        val updated = booking.copy(reminderStatus = "Sent via Gmail (30m prior)")
                        bookings[index] = updated
                        selectedBookingForDetail = updated
                        GymOwnerRepository.sendBookingGmailReminder(booking.id)
                        launchGmailReminder(context, booking)
                        statusMessage = "✓ 30-min reminder sent to ${booking.customerEmail} through Gmail!"
                    }
                },
                onUpdateShift = { newShift ->
                    val index = bookings.indexOfFirst { it.id == booking.id }
                    if (index != -1) {
                        val updated = booking.copy(shift = newShift)
                        bookings[index] = updated
                        selectedBookingForDetail = updated
                        GymOwnerRepository.updateBookingReminderSchedule(booking.id, booking.reminderMinutesBefore, newShift)
                        statusMessage = "✓ Shift updated to $newShift for ${booking.customerName}."
                    }
                },
                onCancelBooking = {
                    val index = bookings.indexOfFirst { it.id == booking.id }
                    if (index != -1) {
                        bookings[index] = booking.copy(status = "Cancelled", arrivalStatus = "Not Arrived")
                        GymOwnerRepository.cancelBooking(booking.id)
                    }
                    selectedBookingForDetail = null
                },
                onMarkArrived = {
                    val index = bookings.indexOfFirst { it.id == booking.id }
                    if (index != -1) {
                        bookings[index] = booking.copy(arrivalStatus = "Checked In", status = "Completed")
                        GymOwnerRepository.markBookingArrived(booking.id)
                        statusMessage = "✓ ${booking.customerName} marked as Arrived & Checked In."
                    }
                    selectedBookingForDetail = null
                }
            )
        }

        if (showExportDialog) {
            ExportModalDialog(
                moduleName = "Slot Bookings",
                selectedFormat = exportFormat,
                onFormatChange = { exportFormat = it },
                onDismiss = { showExportDialog = false }
            )
        }
    }
}

@Composable
private fun ShiftScheduleMiniCard(
    title: String,
    icon: String,
    gymStart: String,
    reminderAlert: String,
    count: Int,
    accentColor: Color,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(if (isSelected) accentColor.copy(alpha = 0.22f) else Color(0x18FFFFFF))
            .border(
                1.dp,
                if (isSelected) accentColor.copy(alpha = 0.8f) else Color(0x22FFFFFF),
                RoundedCornerShape(14.dp)
            )
            .clickable { onClick() }
            .padding(10.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "$icon $title", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(accentColor.copy(alpha = 0.2f))
                        .padding(horizontal = 5.dp, vertical = 2.dp)
                ) {
                    Text(text = "$count", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = accentColor)
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Start: $gymStart", fontSize = 10.sp, color = ByceCoolGray)
            Text(text = "Gmail: $reminderAlert", fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = accentColor)
        }
    }
}

private fun launchGmailReminder(
    context: Context,
    booking: BookingItem
) {
    val subject = "Reminder: Your ${booking.shift} at Iron House Fitness starts in 30 mins!"
    val body = """
        Hi ${booking.customerName},

        This is your automated reminder from Iron House Fitness.

        Your upcoming workout session is scheduled to start in 30 minutes!

        =======================================
        RESERVATION DETAILS:
        • Shift: ${booking.shift}
        • Date: ${booking.date}
        • Slot Time: ${booking.time}
        • Gym: Iron House Fitness, Kozhikode
        • Membership Plan: ${booking.membershipPlan}
        =======================================

        Gym Guidelines:
        • Please arrive 10 minutes prior for barcode/pass check-in.
        • Clean indoor training shoes are mandatory.
        • Bring your workout towel and water bottle.

        See you on the floor!

        Warm regards,
        Iron House Fitness / BYCE Gym Team
    """.trimIndent()

    val emailUri = Uri.parse("mailto:${booking.customerEmail}?subject=${Uri.encode(subject)}&body=${Uri.encode(body)}")
    val gmailIntent = Intent(Intent.ACTION_SENDTO, emailUri).apply {
        setPackage("com.google.android.gm")
    }

    try {
        context.startActivity(gmailIntent)
    } catch (e: Exception) {
        val fallbackIntent = Intent(Intent.ACTION_SENDTO, emailUri)
        try {
            context.startActivity(fallbackIntent)
        } catch (_: Exception) { }
    }
}

@Composable
private fun BookingRowCard(
    booking: BookingItem,
    onViewClick: () -> Unit,
    onSendReminder: () -> Unit,
    onCancelClick: () -> Unit,
    onMarkArrived: () -> Unit
) {
    LiquidGlassCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = booking.customerName,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )
                    Text(
                        text = "${booking.membershipPlan} · ${booking.customerEmail}",
                        fontSize = 11.sp,
                        color = ByceCoolGray
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    ShiftBadge(shift = booking.shift)
                    ArrivalStatusBadge(arrivalStatus = booking.arrivalStatus)
                    OwnerStatusBadge(status = booking.status)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Time & 30-min Reminder Tag
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AccessTime,
                        contentDescription = null,
                        tint = TextWhite,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${booking.date} · ${booking.time}",
                        fontSize = 12.sp,
                        color = TextWhite
                    )
                }

                // 30-minute Reminder Tag
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (booking.reminderStatus.contains("Sent", ignoreCase = true)) StatusActiveGreenBg else Color(0x22EA4335))
                        .border(
                            1.dp,
                            if (booking.reminderStatus.contains("Sent", ignoreCase = true)) StatusActiveGreen.copy(alpha = 0.5f) else Color(0x66EA4335),
                            RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (booking.reminderStatus.contains("Sent", ignoreCase = true)) Icons.Default.CheckCircle else Icons.Default.Mail,
                            contentDescription = null,
                            tint = if (booking.reminderStatus.contains("Sent", ignoreCase = true)) StatusActiveGreen else Color(0xFFEA4335),
                            modifier = Modifier.size(11.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (booking.reminderStatus.contains("Sent", ignoreCase = true)) "Gmail Sent (30m)" else "Gmail 30m Prior",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (booking.reminderStatus.contains("Sent", ignoreCase = true)) StatusActiveGreen else Color(0xFFEA4335)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Quick Send Gmail Reminder Button
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFEA4335).copy(alpha = 0.18f))
                        .border(1.dp, Color(0xFFEA4335).copy(alpha = 0.45f), RoundedCornerShape(12.dp))
                        .clickable { onSendReminder() }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Send, contentDescription = "Gmail Reminder", tint = Color(0xFFEA4335), modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Gmail Reminder",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFEA4335)
                        )
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (booking.arrivalStatus != "Checked In" && booking.status != "Cancelled") {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    Brush.horizontalGradient(
                                        colors = listOf(StatusActiveGreen.copy(alpha = 0.35f), StatusActiveGreen.copy(alpha = 0.15f))
                                    )
                                )
                                .border(1.dp, StatusActiveGreen.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                                .clickable { onMarkArrived() }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Check, contentDescription = "Arrived", tint = StatusActiveGreen, modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "Mark Arrived",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = StatusActiveGreen
                                )
                            }
                        }
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0x20FFFFFF))
                            .clickable { onViewClick() }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "View",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextWhite
                        )
                    }

                    if (booking.status == "Upcoming") {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(StatusExpiredRedBg)
                                .clickable { onCancelClick() }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "Cancel",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = StatusExpiredRed
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ShiftBadge(shift: String) {
    val (bgColor, textColor, icon) = when {
        shift.contains("Morning", ignoreCase = true) -> Triple(Color(0x2AF59E0B), Color(0xFFF59E0B), "🌅")
        shift.contains("Evening", ignoreCase = true) -> Triple(Color(0x2AF97316), Color(0xFFF97316), "🌇")
        shift.contains("Night", ignoreCase = true) -> Triple(Color(0x2A818CF8), Color(0xFF818CF8), "🌙")
        else -> Triple(Color(0x20FFFFFF), TextWhite, "⚡")
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bgColor)
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = "$icon ${shift.replace(" Shift", "")}",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
    }
}

@Composable
private fun ArrivalStatusBadge(arrivalStatus: String) {
    val (bgColor, textColor, label) = when (arrivalStatus) {
        "Checked In" -> Triple(StatusActiveGreenBg, StatusActiveGreen, "✓ Checked In")
        "Not Arrived" -> Triple(StatusPendingAmberBg, StatusPendingAmber, "Not Arrived")
        else -> Triple(Color(0x25FFFFFF), TextWhite, arrivalStatus)
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bgColor)
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
    }
}

@Composable
private fun BookingDetailModal(
    booking: BookingItem,
    onDismiss: () -> Unit,
    onSendReminder: () -> Unit,
    onUpdateShift: (String) -> Unit,
    onCancelBooking: () -> Unit,
    onMarkArrived: () -> Unit = {}
) {
    val shiftStart = booking.time.split("-").firstOrNull()?.trim() ?: "06:00 PM"
    var selectedShift by remember { mutableStateOf(booking.shift) }

    OwnerModalDialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Booking & Shift Details", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(text = booking.customerName, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextWhite)
            Text(text = "Email: ${booking.customerEmail} · Phone: ${booking.customerPhone}", fontSize = 12.sp, color = ByceCoolGray)

            Spacer(modifier = Modifier.height(14.dp))
            Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color(0x20FFFFFF)))
            Spacer(modifier = Modifier.height(12.dp))

            BookingDetailRow("Booking ID", booking.id)
            BookingDetailRow("Plan", booking.membershipPlan)
            BookingDetailRow("Scheduled Date", booking.date)
            BookingDetailRow("Slot Window", booking.time)
            BookingDetailRow("Gym Location", "Iron House Fitness, Kozhikode")
            BookingDetailRow("Arrival Status", booking.arrivalStatus)

            Spacer(modifier = Modifier.height(8.dp))

            // Shift Selector Row
            Text(text = "Booked Shift Window:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextWhite)
            Spacer(modifier = Modifier.height(6.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf("Morning Shift", "Evening Shift", "Night Shift").forEach { shiftOption ->
                    val isCurrent = selectedShift == shiftOption
                    val (icon, color) = when (shiftOption) {
                        "Morning Shift" -> Pair("🌅", Color(0xFFF59E0B))
                        "Evening Shift" -> Pair("🌇", Color(0xFFF97316))
                        else -> Pair("🌙", Color(0xFF818CF8))
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isCurrent) color.copy(alpha = 0.25f) else Color(0x18FFFFFF))
                            .border(1.dp, if (isCurrent) color else Color(0x22FFFFFF), RoundedCornerShape(10.dp))
                            .clickable {
                                selectedShift = shiftOption
                                onUpdateShift(shiftOption)
                            }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "$icon ${shiftOption.replace(" Shift", "")}",
                            fontSize = 11.sp,
                            fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium,
                            color = if (isCurrent) color else TextMuted
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // ----------------------------------------------------------------
            // 30-MINUTE GMAIL REMINDER CONTROL PANEL
            // ----------------------------------------------------------------
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(OwnerModalSurfaceElevated)
                    .border(1.dp, Color(0xFFEA4335).copy(alpha = 0.35f), RoundedCornerShape(14.dp))
                    .padding(14.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = Color(0xFFEA4335), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "30-Min Pre-Gym Reminder", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFFEA4335).copy(alpha = 0.2f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(text = "Via Gmail", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFFEA4335))
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Automated reminder dispatched 30 minutes before gym start time ($shiftStart) for the ${booking.shift}.",
                        fontSize = 11.sp,
                        color = ByceCoolGray,
                        lineHeight = 15.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color(0x18FFFFFF)))
                    Spacer(modifier = Modifier.height(8.dp))

                    BookingDetailRow("Recipient", booking.customerEmail)
                    BookingDetailRow("Lead Time", "30 Minutes Before Gym Start")
                    BookingDetailRow("Reminder Status", booking.reminderStatus)

                    Spacer(modifier = Modifier.height(12.dp))

                    // Open in Gmail Button
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(Color(0xFFEA4335), Color(0xFFC5221F))
                                )
                            )
                            .clickable { onSendReminder() }
                            .padding(vertical = 11.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Send, contentDescription = "Send", tint = TextWhite, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "✉️ Send 30-Min Reminder Through Gmail", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (booking.arrivalStatus != "Checked In" && booking.status != "Cancelled") {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(StatusActiveGreen.copy(alpha = 0.35f), StatusActiveGreen.copy(alpha = 0.15f))
                            )
                        )
                        .border(1.dp, StatusActiveGreen.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                        .clickable { onMarkArrived() }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "✓ Mark Arrived & Check In", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = StatusActiveGreen)
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            if (booking.status == "Upcoming") {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(StatusExpiredRedBg)
                        .border(1.dp, StatusExpiredRed.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                        .clickable { onCancelBooking() }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "Cancel Booking", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = StatusExpiredRed)
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0x22FFFFFF))
                    .border(1.dp, GlassBorderSpecular, RoundedCornerShape(14.dp))
                    .clickable { onDismiss() }
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "Close", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextWhite)
            }
        }
    }
}

@Composable
private fun BookingDetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 13.sp, color = TextMuted)
        Text(text = value, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextWhite)
    }
}
