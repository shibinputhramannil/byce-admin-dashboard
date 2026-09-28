package com.example.ui.owner

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.models.AttendanceItem
import com.example.data.services.GymOwnerRepository
import com.example.ui.components.*
import com.example.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun OwnerAttendanceScreen(
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    var selectedFilter by remember { mutableStateOf("Today") }
    var searchQuery by remember { mutableStateOf("") }
    var refreshTrigger by remember { mutableIntStateOf(0) }
    var showManualCheckInDialog by remember { mutableStateOf(false) }
    var showExportDialog by remember { mutableStateOf(false) }
    var exportFormat by remember { mutableStateOf("CSV") }

    // Modal dialog states for the 4 KPI cards
    var showTodayCheckinsModal by remember { mutableStateOf(false) }
    var showCurrentlyInsideModal by remember { mutableStateOf(false) }
    var showCompletedVisitsModal by remember { mutableStateOf(false) }
    var showDailyVisitsAnalyticsModal by remember { mutableStateOf(false) }
    var memberQrForDetail by remember { mutableStateOf<AttendanceItem?>(null) }

    val summary = remember(refreshTrigger) { GymOwnerRepository.getAttendanceSummary() }
    val attendanceList = remember(refreshTrigger) { GymOwnerRepository.getAttendanceRecords() }

    val filteredList = attendanceList.filter { item ->
        val matchesTab = when (selectedFilter) {
            "Today" -> item.date == "Today"
            "Yesterday" -> item.date == "Yesterday"
            else -> true
        }
        val matchesSearch = item.customerName.contains(searchQuery, ignoreCase = true) ||
                item.membershipPlan.contains(searchQuery, ignoreCase = true) ||
                item.customerId.contains(searchQuery, ignoreCase = true)
        matchesTab && matchesSearch
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            // Page Title & Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "CHECK-IN & VISITS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextSubtle,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Attendance / Check-ins",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
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

                    // Manual Check-in Button
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0x35FFFFFF))
                            .border(1.dp, GlassBorderSpecular, RoundedCornerShape(14.dp))
                            .clickable { showManualCheckInDialog = true }
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.PersonAdd, contentDescription = "Check-in", tint = TextWhite, modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "Check-in", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Summary Cards (4 Interactive Metrics)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                AttendanceKpiBox(
                    title = "Today's Check-ins",
                    value = summary.todayCheckins.toString(),
                    icon = Icons.Default.QrCodeScanner,
                    subLabel = "Tap for Member QRs",
                    modifier = Modifier.weight(1f),
                    onClick = { showTodayCheckinsModal = true }
                )
                AttendanceKpiBox(
                    title = "Currently Inside",
                    value = summary.currentlyInside.toString(),
                    icon = Icons.Default.DirectionsRun,
                    subLabel = "Tap for Live Timers",
                    modifier = Modifier.weight(1f),
                    onClick = { showCurrentlyInsideModal = true }
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                AttendanceKpiBox(
                    title = "Completed Visits",
                    value = summary.completedVisits.toString(),
                    icon = Icons.Default.CheckCircleOutline,
                    subLabel = "Tap for History",
                    modifier = Modifier.weight(1f),
                    onClick = { showCompletedVisitsModal = true }
                )
                AttendanceKpiBox(
                    title = "Avg Daily Visits",
                    value = summary.avgDailyVisits.toString(),
                    icon = Icons.Default.BarChart,
                    subLabel = "Tap for Graph",
                    modifier = Modifier.weight(1f),
                    onClick = { showDailyVisitsAnalyticsModal = true }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Search Bar & Filter Tabs
            OwnerSearchBar(
                query = searchQuery,
                onQueryChange = { searchQuery = it },
                placeholder = "Search by customer name, plan or ID...",
                selectedFilter = selectedFilter,
                filterOptions = listOf("Today", "Yesterday", "Date Range"),
                onFilterSelected = { selectedFilter = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Records List
            if (filteredList.isEmpty()) {
                LiquidGlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.EventBusy, contentDescription = null, tint = TextMuted, modifier = Modifier.size(36.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = "No check-ins found for selected filter", fontSize = 13.sp, color = TextMuted)
                        }
                    }
                }
            } else {
                filteredList.forEach { record ->
                    AttendanceRecordCard(
                        record = record,
                        onCheckOut = {
                            GymOwnerRepository.recordCheckOut(record.id)
                            refreshTrigger++
                        },
                        onViewQr = {
                            memberQrForDetail = record
                        }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }

        // --------------------------------------------------------------------
        // 1. TODAY'S CHECK-INS MODAL (SHOWS INDIVIDUAL MEMBER QR PASSES)
        // --------------------------------------------------------------------
        if (showTodayCheckinsModal) {
            TodayCheckinsModal(
                checkins = attendanceList.filter { it.date == "Today" },
                onSelectMemberQr = { memberQrForDetail = it },
                onDismiss = { showTodayCheckinsModal = false }
            )
        }

        // --------------------------------------------------------------------
        // 1b. MEMBER INDIVIDUAL PERSONAL DIGITAL QR PASS MODAL
        // --------------------------------------------------------------------
        memberQrForDetail?.let { member ->
            MemberPersonalQrModal(
                member = member,
                onDismiss = { memberQrForDetail = null }
            )
        }

        // --------------------------------------------------------------------
        // 2. CURRENTLY INSIDE MODAL (WITH LIVE TICKING WORKOUT DURATION TIMER)
        // --------------------------------------------------------------------
        if (showCurrentlyInsideModal) {
            CurrentlyInsideModal(
                insideMembers = attendanceList.filter { it.status == "Checked In" && it.date == "Today" },
                onCheckOut = { recordId ->
                    GymOwnerRepository.recordCheckOut(recordId)
                    refreshTrigger++
                },
                onViewQr = { memberQrForDetail = it },
                onDismiss = { showCurrentlyInsideModal = false }
            )
        }

        // --------------------------------------------------------------------
        // 3. COMPLETED VISITS MODAL
        // --------------------------------------------------------------------
        if (showCompletedVisitsModal) {
            CompletedVisitsModal(
                completedMembers = attendanceList.filter { it.status == "Completed" },
                onDismiss = { showCompletedVisitsModal = false }
            )
        }

        // --------------------------------------------------------------------
        // 4. AVG DAILY VISITS ANALYTICS MODAL (WITH GRAPH & SORTING TABS)
        // --------------------------------------------------------------------
        if (showDailyVisitsAnalyticsModal) {
            DailyVisitsAnalyticsModal(
                avgDailyVisits = summary.avgDailyVisits,
                onDismiss = { showDailyVisitsAnalyticsModal = false }
            )
        }

        // Manual Check-in Dialog
        if (showManualCheckInDialog) {
            ManualCheckInDialog(
                onDismiss = { showManualCheckInDialog = false },
                onConfirm = { id, name, plan ->
                    GymOwnerRepository.recordCheckIn(id, name, plan)
                    refreshTrigger++
                    showManualCheckInDialog = false
                }
            )
        }

        // Export Dialog
        if (showExportDialog) {
            ExportModalDialog(
                moduleName = "Attendance & Check-in Records",
                selectedFormat = exportFormat,
                onFormatChange = { exportFormat = it },
                onDismiss = { showExportDialog = false }
            )
        }
    }
}

// ---------------------------------------------------------------------------
// KPI CARD COMPOSABLE (WITH TAP HINT)
// ---------------------------------------------------------------------------
@Composable
private fun AttendanceKpiBox(
    title: String,
    value: String,
    icon: ImageVector,
    subLabel: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    LiquidGlassCard(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = title, fontSize = 11.sp, color = TextSubtle, fontWeight = FontWeight.Medium)
                Icon(imageVector = icon, contentDescription = null, tint = TextWhite, modifier = Modifier.size(16.dp))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = value, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = TextWhite)
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(4.dp)
                        .background(StatusActiveGreen, CircleShape)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = subLabel,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = StatusActiveGreen
                )
            }
        }
    }
}

// ---------------------------------------------------------------------------
// ATTENDANCE RECORD ROW CARD
// ---------------------------------------------------------------------------
@Composable
private fun AttendanceRecordCard(
    record: AttendanceItem,
    onCheckOut: () -> Unit,
    onViewQr: () -> Unit = {}
) {
    LiquidGlassCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0x22FFFFFF)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = record.customerName.take(2).uppercase(),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextWhite
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = record.customerName,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextWhite
                        )
                        Text(
                            text = "ID: ${record.customerId} · ${record.membershipPlan}",
                            fontSize = 11.sp,
                            color = ByceCoolGray
                        )
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                    // View Member QR Pass button
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0x20FFFFFF))
                            .clickable { onViewQr() }
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.QrCode2, contentDescription = "QR Pass", tint = TextWhite, modifier = Modifier.size(13.dp))
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(text = "QR", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                        }
                    }
                    OwnerStatusBadge(status = record.status)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color(0x15FFFFFF)))
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AccessTime, contentDescription = null, tint = TextSubtle, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "In: ${record.checkInTime}" + (if (record.checkOutTime != null) " · Out: ${record.checkOutTime}" else " · Currently Inside"),
                        fontSize = 11.sp,
                        color = if (record.checkOutTime == null) StatusActiveGreen else TextMuted
                    )
                }

                if (record.status == "Checked In") {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0x28FFFFFF))
                            .border(1.dp, GlassBorderSpecular, RoundedCornerShape(10.dp))
                            .clickable { onCheckOut() }
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text(text = "Check Out", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                    }
                } else if (record.bookedSlot != null) {
                    Text(text = "Slot: ${record.bookedSlot}", fontSize = 11.sp, color = ByceCoolGray)
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------
// 1. TODAY'S CHECK-INS MODAL (SHOWS MEMBER PASSES & QR CODES)
// ---------------------------------------------------------------------------
@Composable
private fun TodayCheckinsModal(
    checkins: List<AttendanceItem>,
    onSelectMemberQr: (AttendanceItem) -> Unit,
    onDismiss: () -> Unit
) {
    var query by remember { mutableStateOf("") }
    val filtered = checkins.filter {
        it.customerName.contains(query, ignoreCase = true) || it.membershipPlan.contains(query, ignoreCase = true)
    }

    Dialog(onDismissRequest = onDismiss) {
        LiquidGlassCard(
            modifier = Modifier.fillMaxWidth().wrapContentHeight(),
            shape = RoundedCornerShape(24.dp)
        ) {
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
                    Column {
                        Text(text = "Today's Check-ins", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                        Text(text = "${checkins.size} Total Visitors Today", fontSize = 11.sp, color = ByceCoolGray)
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Each member has their own verified digital QR pass. Tap the QR button on any member to inspect their digital pass.",
                    fontSize = 11.sp,
                    color = TextSubtle,
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                LiquidGlassTextField(
                    value = query,
                    onValueChange = { query = it },
                    label = "Filter Today's Check-ins",
                    placeholder = "Search member name..."
                )

                Spacer(modifier = Modifier.height(14.dp))

                filtered.forEach { item ->
                    LiquidGlassCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(Color(0x25FFFFFF)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = item.customerName.take(2).uppercase(), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(text = item.customerName, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                                    Text(text = "${item.membershipPlan} · In: ${item.checkInTime}", fontSize = 11.sp, color = ByceCoolGray)
                                }
                            }

                            // Distinct QR Pass Button
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(
                                        Brush.horizontalGradient(
                                            colors = listOf(Color(0x3534D399), Color(0x2034D399))
                                        )
                                    )
                                    .border(1.dp, StatusActiveGreen.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                                    .clickable { onSelectMemberQr(item) }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.QrCode2, contentDescription = "View QR", tint = StatusActiveGreen, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(text = "View QR", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = StatusActiveGreen)
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0x25FFFFFF))
                        .border(1.dp, GlassBorderSpecular, RoundedCornerShape(14.dp))
                        .clickable { onDismiss() }
                        .padding(vertical = 11.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "Close", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------
// 1b. MEMBER INDIVIDUAL PERSONAL DIGITAL QR PASS MODAL
// ---------------------------------------------------------------------------
@Composable
private fun MemberPersonalQrModal(
    member: AttendanceItem,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        LiquidGlassCard(
            modifier = Modifier.fillMaxWidth().wrapContentHeight(),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Member Digital Pass", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(StatusActiveGreenBg)
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(text = "✓ AUTHENTICATED ACCESS PASS", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = StatusActiveGreen)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // High-resolution Personal QR Code Display
                Box(
                    modifier = Modifier
                        .size(200.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.White)
                        .padding(14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.QrCode2,
                            contentDescription = "Personal QR",
                            tint = DarkNavy,
                            modifier = Modifier.size(140.dp)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = member.customerName,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = DarkNavy
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Payload: BYCE:MEMBER:${member.customerId}:${member.customerName.replace(" ", "")}:2026",
                    fontSize = 9.sp,
                    color = TextMuted
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Pass Metadata
                LiquidGlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Column(modifier = Modifier.fillMaxWidth().padding(14.dp)) {
                        DetailLine("Member Name", member.customerName)
                        DetailLine("Member ID", member.customerId)
                        DetailLine("Membership Plan", member.membershipPlan)
                        DetailLine("Check-in Time", member.checkInTime)
                        DetailLine("Gate Turnstile", "Scanner Terminal 01 (Front Desk)")
                        DetailLine("Access Tier", "Full Gym + Cardio Zone")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Members present this unique digital QR code on their BYCE app at the gym entrance scanner to record check-in and unlock turnstiles.",
                    fontSize = 11.sp,
                    color = ByceCoolGray,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    lineHeight = 15.sp
                )

                Spacer(modifier = Modifier.height(18.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color(0xFF6B9330), Color(0xFF4E7320))
                            )
                        )
                        .clickable { onDismiss() }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "Close Pass", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------
// 2. CURRENTLY INSIDE MODAL (WITH LIVE TICKING WORKOUT DURATION TIMER)
// ---------------------------------------------------------------------------
@Composable
private fun CurrentlyInsideModal(
    insideMembers: List<AttendanceItem>,
    onCheckOut: (String) -> Unit,
    onViewQr: (AttendanceItem) -> Unit,
    onDismiss: () -> Unit
) {
    // Live ticking timer every second
    var elapsedSeconds by remember { mutableIntStateOf(0) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(1000L)
            elapsedSeconds++
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        LiquidGlassCard(
            modifier = Modifier.fillMaxWidth().wrapContentHeight(),
            shape = RoundedCornerShape(24.dp)
        ) {
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
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(StatusActiveGreen, CircleShape)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "Currently Inside Gym", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                        }
                        Text(text = "${insideMembers.size} Active Members Inside · Capacity: 100", fontSize = 11.sp, color = ByceCoolGray)
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Live workout duration tracking for members currently inside. Members scanned the gym's front desk QR pass upon arrival.",
                    fontSize = 11.sp,
                    color = TextSubtle,
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (insideMembers.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "No members currently inside.", fontSize = 13.sp, color = TextMuted)
                    }
                } else {
                    insideMembers.forEachIndexed { index, member ->
                        // Base duration simulation + live ticking seconds
                        val baseMinutes = when (index) {
                            0 -> 85
                            1 -> 52
                            2 -> 34
                            else -> 18
                        }
                        val totalSec = (baseMinutes * 60) + elapsedSeconds
                        val hrs = totalSec / 3600
                        val mins = (totalSec % 3600) / 60
                        val secs = totalSec % 60
                        val timerText = String.format("%02dh %02dm %02ds", hrs, mins, secs)

                        LiquidGlassCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 5.dp),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(text = member.customerName, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                                        Text(text = "ID: ${member.customerId} · ${member.membershipPlan}", fontSize = 11.sp, color = ByceCoolGray)
                                    }

                                    // Live Ticking Timer Pill
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(Color(0x3034D399))
                                            .border(1.dp, StatusActiveGreen.copy(alpha = 0.6f), RoundedCornerShape(10.dp))
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.Timer, contentDescription = "Timer", tint = StatusActiveGreen, modifier = Modifier.size(13.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(text = timerText, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, color = StatusActiveGreen)
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                // QR Explanation Badge
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.QrCodeScanner, contentDescription = null, tint = ByceCoolGray, modifier = Modifier.size(12.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Scanned Front Desk QR at ${member.checkInTime} · Gate Scanner Turnstile",
                                        fontSize = 10.sp,
                                        color = ByceCoolGray
                                    )
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(Color(0x20FFFFFF))
                                            .clickable { onViewQr(member) }
                                            .padding(vertical = 7.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.QrCode2, contentDescription = null, tint = TextWhite, modifier = Modifier.size(13.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(text = "Member Pass", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = TextWhite)
                                        }
                                    }

                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(Color(0x28FFFFFF))
                                            .border(1.dp, GlassBorderSpecular, RoundedCornerShape(10.dp))
                                            .clickable { onCheckOut(member.id) }
                                            .padding(vertical = 7.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(text = "Check Out Now", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0x25FFFFFF))
                        .border(1.dp, GlassBorderSpecular, RoundedCornerShape(14.dp))
                        .clickable { onDismiss() }
                        .padding(vertical = 11.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "Close", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------
// 3. COMPLETED VISITS MODAL
// ---------------------------------------------------------------------------
@Composable
private fun CompletedVisitsModal(
    completedMembers: List<AttendanceItem>,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        LiquidGlassCard(
            modifier = Modifier.fillMaxWidth().wrapContentHeight(),
            shape = RoundedCornerShape(24.dp)
        ) {
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
                    Column {
                        Text(text = "Completed Visits", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                        Text(text = "${completedMembers.size} Recorded Checkout Sessions Today", fontSize = 11.sp, color = ByceCoolGray)
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Full historical log of members who completed their gym workout and checked out today.",
                    fontSize = 11.sp,
                    color = TextSubtle
                )

                Spacer(modifier = Modifier.height(14.dp))

                completedMembers.forEach { member ->
                    LiquidGlassCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = member.customerName, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(StatusActiveGreenBg)
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(text = "✓ Completed", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = StatusActiveGreen)
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(text = "${member.membershipPlan} · ID: ${member.customerId}", fontSize = 11.sp, color = ByceCoolGray)

                            Spacer(modifier = Modifier.height(6.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "In: ${member.checkInTime} ➔ Out: ${member.checkOutTime ?: "11:35 AM"}",
                                    fontSize = 11.sp,
                                    color = TextWhite
                                )
                                Text(
                                    text = "Duration: 1h 31m",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = ByceCoolGray
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0x25FFFFFF))
                        .border(1.dp, GlassBorderSpecular, RoundedCornerShape(14.dp))
                        .clickable { onDismiss() }
                        .padding(vertical = 11.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "Close", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------
// 4. AVG DAILY VISITS ANALYTICS MODAL (WITH INTERACTIVE GRAPH & SORTING TABS)
// ---------------------------------------------------------------------------
@Composable
private fun DailyVisitsAnalyticsModal(
    avgDailyVisits: Int,
    onDismiss: () -> Unit
) {
    var selectedSortTab by remember { mutableStateOf("This Week") }

    val graphData = when (selectedSortTab) {
        "Today" -> listOf(
            Pair("6 AM", 24),
            Pair("8 AM", 36),
            Pair("11 AM", 14),
            Pair("2 PM", 8),
            Pair("5 PM", 32),
            Pair("7 PM", 44),
            Pair("9 PM", 18)
        )
        "Yesterday" -> listOf(
            Pair("6 AM", 28),
            Pair("8 AM", 38),
            Pair("11 AM", 12),
            Pair("2 PM", 10),
            Pair("5 PM", 30),
            Pair("7 PM", 42),
            Pair("9 PM", 15)
        )
        "This Week" -> listOf(
            Pair("Mon", 94),
            Pair("Tue", 108),
            Pair("Wed", 102),
            Pair("Thu", 86),
            Pair("Fri", 110),
            Pair("Sat", 72),
            Pair("Sun", 45)
        )
        "This Month" -> listOf(
            Pair("W1", 740),
            Pair("W2", 810),
            Pair("W3", 790),
            Pair("W4", 825)
        )
        else -> listOf(
            Pair("Jun", 98),
            Pair("Jul", 104),
            Pair("Aug", 108),
            Pair("Sep", 112)
        )
    }

    Dialog(onDismissRequest = onDismiss) {
        LiquidGlassCard(
            modifier = Modifier.fillMaxWidth().wrapContentHeight(),
            shape = RoundedCornerShape(24.dp)
        ) {
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
                    Column {
                        Text(text = "Daily Visits Analytics", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                        Text(text = "Average: $avgDailyVisits Visits/Day", fontSize = 11.sp, color = ByceCoolGray)
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Sorting / Timeframe Tabs
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    listOf("Today", "Yesterday", "This Week", "This Month", "All Time").forEach { tab ->
                        val isSelected = selectedSortTab == tab
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) Color(0x35FFFFFF) else Color(0x12FFFFFF))
                                .border(1.dp, if (isSelected) GlassBorderSpecular else Color.Transparent, RoundedCornerShape(10.dp))
                                .clickable { selectedSortTab = tab }
                                .padding(vertical = 7.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = tab,
                                fontSize = 9.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) TextWhite else TextMuted
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Interactive Bar Chart Graphic
                LiquidGlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Footfall Distribution ($selectedSortTab)", fontSize = 12.sp, color = ByceCoolGray)
                            Text(
                                text = if (selectedSortTab == "This Month") "Total: 3,165" else if (selectedSortTab == "This Week") "Total: 617" else "Peak: 44",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = StatusActiveGreen
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        val maxVal = graphData.maxOfOrNull { it.second } ?: 1
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            graphData.forEach { point ->
                                val ratio = (point.second.toFloat() / maxVal).coerceIn(0.12f, 1f)
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Bottom,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = "${point.second}",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextWhite
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Box(
                                        modifier = Modifier
                                            .width(14.dp)
                                            .fillMaxHeight(ratio)
                                            .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                            .background(
                                                Brush.verticalGradient(
                                                    colors = listOf(
                                                        Color(0xFF34D399),
                                                        Color(0xFF059669)
                                                    )
                                                )
                                            )
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = point.first,
                                        fontSize = 9.sp,
                                        color = ByceCoolGray
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Analytics Insights
                LiquidGlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Column(modifier = Modifier.fillMaxWidth().padding(14.dp)) {
                        DetailLine("Peak Workout Window", "06:00 PM - 08:30 PM (Evening Rush)")
                        DetailLine("Morning Workout Window", "06:00 AM - 08:30 AM (Morning Peak)")
                        DetailLine("Avg Workout Duration", "1 hour 24 minutes")
                        DetailLine("Weekday vs Weekend", "Weekday footfall +38% higher")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0x25FFFFFF))
                        .border(1.dp, GlassBorderSpecular, RoundedCornerShape(14.dp))
                        .clickable { onDismiss() }
                        .padding(vertical = 11.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "Close", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------
// DETAIL LINE HELPER
// ---------------------------------------------------------------------------
@Composable
private fun DetailLine(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 11.sp, color = TextMuted)
        Text(text = value, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = TextWhite)
    }
}

// ---------------------------------------------------------------------------
// MANUAL CHECK-IN DIALOG
// ---------------------------------------------------------------------------
@Composable
private fun ManualCheckInDialog(
    onDismiss: () -> Unit,
    onConfirm: (id: String, name: String, plan: String) -> Unit
) {
    var customerName by remember { mutableStateOf("") }
    var customerId by remember { mutableStateOf("") }
    var membershipPlan by remember { mutableStateOf("Monthly") }

    Dialog(onDismissRequest = onDismiss) {
        LiquidGlassCard(
            modifier = Modifier.fillMaxWidth().wrapContentHeight(),
            shape = RoundedCornerShape(22.dp)
        ) {
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
                    Text(text = "Manual Gym Check-in", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                LiquidGlassTextField(
                    value = customerName,
                    onValueChange = { customerName = it },
                    label = "Customer Name",
                    placeholder = "e.g. Rahul Menon"
                )

                Spacer(modifier = Modifier.height(10.dp))

                LiquidGlassTextField(
                    value = customerId,
                    onValueChange = { customerId = it },
                    label = "Customer ID (Optional)",
                    placeholder = "e.g. c1"
                )

                Spacer(modifier = Modifier.height(10.dp))

                LiquidGlassTextField(
                    value = membershipPlan,
                    onValueChange = { membershipPlan = it },
                    label = "Active Membership Plan",
                    placeholder = "e.g. Yearly, Monthly"
                )

                Spacer(modifier = Modifier.height(20.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0x35FFFFFF))
                        .border(1.dp, GlassBorderSpecular, RoundedCornerShape(16.dp))
                        .clickable {
                            if (customerName.isNotBlank()) {
                                onConfirm(customerId.ifBlank { "c_${System.currentTimeMillis() % 1000}" }, customerName, membershipPlan)
                            }
                        }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "Confirm Check-in", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------
// REUSABLE EXPORT DIALOG (FEATURE 19)
// ---------------------------------------------------------------------------
@Composable
fun ExportModalDialog(
    moduleName: String,
    selectedFormat: String,
    onFormatChange: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var isExportTriggered by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        LiquidGlassCard(
            modifier = Modifier.fillMaxWidth().wrapContentHeight(),
            shape = RoundedCornerShape(22.dp)
        ) {
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
                    Text(text = "Export $moduleName", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Select your preferred file format to download the records for this module.",
                    fontSize = 12.sp,
                    color = ByceCoolGray
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    listOf("CSV", "PDF").forEach { fmt ->
                        val isSelected = selectedFormat == fmt
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) Color(0x35FFFFFF) else Color(0x15FFFFFF))
                                .border(1.dp, if (isSelected) GlassBorderSpecular else Color.Transparent, RoundedCornerShape(12.dp))
                                .clickable { onFormatChange(fmt) }
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = fmt, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = if (isSelected) TextWhite else TextMuted)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                if (isExportTriggered) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(StatusActiveGreenBg)
                            .border(1.dp, StatusActiveGreen.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
                            .padding(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "✓ $selectedFormat export scheduled. Backend download initiated.",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = StatusActiveGreen
                        )
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0x35FFFFFF))
                        .border(1.dp, GlassBorderSpecular, RoundedCornerShape(16.dp))
                        .clickable {
                            isExportTriggered = true
                            GymOwnerRepository.logAdminAction("Kishore Kumar", "Exported $moduleName ($selectedFormat)", "Export")
                        }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "Download $selectedFormat", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                }
            }
        }
    }
}
