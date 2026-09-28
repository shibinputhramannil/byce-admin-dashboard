package com.example.ui.owner

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.data.models.AdminActivityLogItem
import com.example.data.services.GymOwnerRepository
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun OwnerActivityLogScreen(
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All") }
    var showExportDialog by remember { mutableStateOf(false) }
    var exportFormat by remember { mutableStateOf("CSV") }

    val logs = remember { GymOwnerRepository.getAdminActivityLogs() }

    val filteredLogs = remember(searchQuery, selectedFilter, logs) {
        logs.filter { item ->
            val matchesQuery = item.adminName.contains(searchQuery, ignoreCase = true) ||
                    item.action.contains(searchQuery, ignoreCase = true) ||
                    item.module.contains(searchQuery, ignoreCase = true)
            val matchesFilter = if (selectedFilter == "All") {
                true
            } else {
                item.module.contains(selectedFilter, ignoreCase = true)
            }
            matchesQuery && matchesFilter
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
                            text = "AUDIT TRAIL & COMPLIANCE",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = TextSubtle,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Admin Activity Log",
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
                            Icon(
                                Icons.Default.FileDownload,
                                contentDescription = "Export",
                                tint = TextWhite,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Export",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextWhite
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // KPI Mini Stats
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MiniActivityKpi("Total Actions", "${logs.size}", TextWhite, Modifier.weight(1f))
                    MiniActivityKpi("Today's Events", "4", StatusActiveGreen, Modifier.weight(1f))
                    MiniActivityKpi("Active Admins", "3", TextWhite, Modifier.weight(1f))
                }

                Spacer(modifier = Modifier.height(16.dp))

                OwnerSearchBar(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    placeholder = "Search admin name, action, or module...",
                    filterOptions = listOf("All", "Attendance", "QR", "Customers", "Payments", "Plans"),
                    selectedFilter = selectedFilter,
                    onFilterSelected = { selectedFilter = it }
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Showing ${filteredLogs.size} audit records",
                    fontSize = 12.sp,
                    color = ByceCoolGray
                )

                Spacer(modifier = Modifier.height(4.dp))
            }

            if (filteredLogs.isEmpty()) {
                item {
                    OwnerEmptyState(
                        title = "No Activity Logs Found",
                        description = "No logged action matches '$searchQuery' under '$selectedFilter'."
                    )
                }
            } else {
                items(filteredLogs, key = { it.id }) { log ->
                    ActivityLogCard(log = log)
                }
            }

            item {
                Spacer(modifier = Modifier.height(40.dp))
            }
        }

        if (showExportDialog) {
            ExportModalDialog(
                moduleName = "Activity Logs",
                selectedFormat = exportFormat,
                onFormatChange = { exportFormat = it },
                onDismiss = { showExportDialog = false }
            )
        }
    }
}

@Composable
private fun MiniActivityKpi(
    label: String,
    value: String,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    LiquidGlassCard(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Text(text = label, fontSize = 10.sp, color = ByceCoolGray)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = value, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = accentColor)
        }
    }
}

@Composable
private fun ActivityLogCard(log: AdminActivityLogItem) {
    LiquidGlassCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val initials = log.adminName.split(" ")
                .mapNotNull { it.firstOrNull()?.toString() }
                .take(2)
                .joinToString("")

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0x25FFFFFF))
                    .border(1.dp, GlassBorderLight, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = initials,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = log.adminName,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )
                    ModuleBadge(module = log.module)
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = log.action,
                    fontSize = 13.sp,
                    color = TextWhite.copy(alpha = 0.9f),
                    lineHeight = 17.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AccessTime,
                        contentDescription = null,
                        tint = ByceCoolGray,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = log.timestamp,
                        fontSize = 11.sp,
                        color = ByceCoolGray
                    )
                }
            }
        }
    }
}

@Composable
private fun ModuleBadge(module: String) {
    val (bgColor, textColor) = when {
        module.contains("QR", ignoreCase = true) -> Pair(StatusActiveGreenBg, StatusActiveGreen)
        module.contains("Attendance", ignoreCase = true) -> Pair(Color(0x2506B6D4), Color(0xFF22D3EE))
        module.contains("Payment", ignoreCase = true) -> Pair(StatusPendingAmberBg, StatusPendingAmber)
        module.contains("Customer", ignoreCase = true) -> Pair(Color(0x25A855F7), Color(0xFFC084FC))
        module.contains("Plan", ignoreCase = true) -> Pair(Color(0x253B82F6), Color(0xFF60A5FA))
        else -> Pair(Color(0x20FFFFFF), TextWhite)
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bgColor)
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = module,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
    }
}
