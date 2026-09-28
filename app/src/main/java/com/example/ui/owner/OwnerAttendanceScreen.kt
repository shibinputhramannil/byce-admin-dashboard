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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.models.AttendanceItem
import com.example.data.services.GymOwnerRepository
import com.example.ui.components.*
import com.example.ui.theme.*

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

    Column(
        modifier = modifier
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

        // Summary Cards (4 Metrics)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            AttendanceKpiBox("Today's Check-ins", summary.todayCheckins.toString(), Icons.Default.QrCodeScanner, Modifier.weight(1f))
            AttendanceKpiBox("Currently Inside", summary.currentlyInside.toString(), Icons.Default.DirectionsRun, Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            AttendanceKpiBox("Completed Visits", summary.completedVisits.toString(), Icons.Default.CheckCircleOutline, Modifier.weight(1f))
            AttendanceKpiBox("Avg Daily Visits", summary.avgDailyVisits.toString(), Icons.Default.BarChart, Modifier.weight(1f))
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
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        // Manual Check-In Dialog
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

        // Export Dialog (Feature 19)
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

@Composable
private fun AttendanceKpiBox(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    LiquidGlassCard(
        modifier = modifier,
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
            Text(text = value, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextWhite)
        }
    }
}

@Composable
private fun AttendanceRecordCard(
    record: AttendanceItem,
    onCheckOut: () -> Unit
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

                OwnerStatusBadge(status = record.status)
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
