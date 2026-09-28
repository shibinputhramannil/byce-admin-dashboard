package com.example.ui.owner

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.models.MembershipItem
import com.example.data.services.GymOwnerRepository
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun OwnerMembershipsScreen(
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedStatusFilter by remember { mutableStateOf("All") }
    var expiringDaysFilter by remember { mutableIntStateOf(15) }
    var showExportDialog by remember { mutableStateOf(false) }
    var exportFormat by remember { mutableStateOf("CSV") }

    val allMemberships = remember { GymOwnerRepository.getMemberships() }
    val overview = remember { GymOwnerRepository.getMembershipOverview() }
    val expiringMemberships = remember(expiringDaysFilter) {
        GymOwnerRepository.getExpiringMemberships(expiringDaysFilter)
    }
    var selectedMembershipForDetail by remember { mutableStateOf<MembershipItem?>(null) }

    val filteredMemberships = remember(searchQuery, selectedStatusFilter, allMemberships) {
        allMemberships.filter { item ->
            val matchesQuery = item.customerName.contains(searchQuery, ignoreCase = true) ||
                    item.planName.contains(searchQuery, ignoreCase = true)
            val matchesStatus = if (selectedStatusFilter == "All") true else item.status.equals(selectedStatusFilter, ignoreCase = true)
            matchesQuery && matchesStatus
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
                            text = "MEMBERSHIP PASSES",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = TextSubtle,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Active & Expired Memberships",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextWhite
                        )
                    }

                    // Export Button (Feature 19)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0x20FFFFFF))
                            .border(1.dp, GlassBorderLight, RoundedCornerShape(14.dp))
                            .clickable { showExportDialog = true }
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.FileDownload, contentDescription = "Export", tint = TextWhite, modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "Export", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = TextWhite)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Status counters strip
                LiquidGlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        StatusCounterItem("Active", overview.active.toString(), StatusActiveGreen)
                        StatusCounterItem("Pending", overview.pending.toString(), StatusPendingAmber)
                        StatusCounterItem("Expired", overview.expired.toString(), StatusExpiredRed)
                        StatusCounterItem("Cancelled", overview.cancelled.toString(), StatusCancelledGray)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                OwnerSearchBar(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    placeholder = "Search membership by customer or plan...",
                    filterOptions = listOf("All", "Expiring Soon", "Active", "Pending", "Expired"),
                    selectedFilter = selectedStatusFilter,
                    onFilterSelected = { selectedStatusFilter = it }
                )

                if (selectedStatusFilter == "Expiring Soon") {
                    Spacer(modifier = Modifier.height(10.dp))
                    // Timeframe filters for Expiring Memberships (Feature 4)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(Pair(7, "7 Days"), Pair(15, "15 Days"), Pair(30, "30 Days")).forEach { (days, label) ->
                            val isSelected = expiringDaysFilter == days
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isSelected) Color(0x35FFFFFF) else Color(0x12FFFFFF))
                                    .border(1.dp, if (isSelected) GlassBorderSpecular else Color.Transparent, RoundedCornerShape(12.dp))
                                    .clickable { expiringDaysFilter = days }
                                    .padding(vertical = 7.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Within $label",
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) TextWhite else TextMuted
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = if (selectedStatusFilter == "Expiring Soon") "Showing ${expiringMemberships.size} memberships expiring within $expiringDaysFilter days" else "Showing ${filteredMemberships.size} memberships",
                    fontSize = 12.sp,
                    color = ByceCoolGray
                )

                Spacer(modifier = Modifier.height(4.dp))
            }

            if (selectedStatusFilter == "Expiring Soon") {
                if (expiringMemberships.isEmpty()) {
                    item {
                        OwnerEmptyState(
                            title = "No Expiring Passes",
                            description = "No memberships are expiring within the next $expiringDaysFilter days."
                        )
                    }
                } else {
                    items(expiringMemberships, key = { it.customerId }) { expiring ->
                        ExpiringMembershipCardItem(
                            item = expiring,
                            onViewClick = {
                                val match = allMemberships.firstOrNull { it.customerName == expiring.customerName }
                                selectedMembershipForDetail = match ?: MembershipItem(
                                    id = "m_${expiring.customerId}",
                                    customerName = expiring.customerName,
                                    customerEmail = "${expiring.customerName.lowercase().replace(" ", ".")}@gmail.com",
                                    customerPhone = "+91 98470 00000",
                                    planName = expiring.membershipPlan,
                                    gymName = "Iron House Fitness",
                                    startDate = "01 Jan 2026",
                                    endDate = expiring.expiryDate,
                                    amount = 1499.0,
                                    status = "Expiring Soon"
                                )
                            }
                        )
                    }
                }
            } else if (filteredMemberships.isEmpty()) {
                item {
                    OwnerEmptyState(
                        title = "No Memberships",
                        description = "No memberships match filter '$selectedStatusFilter'."
                    )
                }
            } else {
                items(filteredMemberships, key = { it.id }) { item ->
                    MembershipRowCard(
                        item = item,
                        onViewClick = { selectedMembershipForDetail = item }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(40.dp))
            }
        }

        // Details Modal
        selectedMembershipForDetail?.let { item ->
            MembershipDetailModal(
                item = item,
                onDismiss = { selectedMembershipForDetail = null }
            )
        }

        // Export Dialog (Feature 19)
        if (showExportDialog) {
            ExportModalDialog(
                moduleName = "Memberships",
                selectedFormat = exportFormat,
                onFormatChange = { exportFormat = it },
                onDismiss = { showExportDialog = false }
            )
        }
    }
}

@Composable
private fun StatusCounterItem(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = color)
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = label, fontSize = 11.sp, color = ByceCoolGray)
    }
}

@Composable
private fun ExpiringMembershipCardItem(
    item: com.example.data.models.ExpiringMembershipItem,
    onViewClick: () -> Unit
) {
    LiquidGlassCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(14.dp)
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
                        Text(text = item.customerName.take(2).uppercase(), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(text = item.customerName, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TextWhite)
                        Text(text = item.membershipPlan, fontSize = 11.sp, color = ByceCoolGray)
                    }
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(StatusPendingAmberBg)
                        .border(1.dp, StatusPendingAmber.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(text = "${item.daysRemaining} days left", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = StatusPendingAmber)
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
                Text(text = "Expiry Date: ${item.expiryDate}", fontSize = 12.sp, color = TextMuted)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0x28FFFFFF))
                        .border(1.dp, GlassBorderSpecular, RoundedCornerShape(10.dp))
                        .clickable { onViewClick() }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(text = "View", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                }
            }
        }
    }
}

@Composable
private fun MembershipRowCard(
    item: MembershipItem,
    onViewClick: () -> Unit
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
                Column {
                    Text(
                        text = item.customerName,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )
                    Text(
                        text = item.planName + " · " + item.gymName,
                        fontSize = 12.sp,
                        color = ByceCoolGray
                    )
                }
                OwnerStatusBadge(status = item.status)
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Duration: ${item.startDate} - ${item.endDate}",
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                    Text(
                        text = "Amount: ₹${item.amount.toInt()}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextWhite
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0x20FFFFFF))
                        .clickable { onViewClick() }
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "Details",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )
                }
            }
        }
    }
}

@Composable
private fun MembershipDetailModal(
    item: MembershipItem,
    onDismiss: () -> Unit
) {
    var reminderSent by remember { mutableStateOf(false) }

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
                    Text(text = "Membership Details", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(text = "Customer: ${item.customerName}", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                Text(text = "Email: ${item.customerEmail}", fontSize = 12.sp, color = ByceCoolGray)

                Spacer(modifier = Modifier.height(14.dp))
                Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color(0x20FFFFFF)))
                Spacer(modifier = Modifier.height(12.dp))

                DetailRow("Plan Name", item.planName)
                DetailRow("Facility", item.gymName)
                DetailRow("Start Date", item.startDate)
                DetailRow("End Date", item.endDate)
                DetailRow("Amount Paid", "₹${item.amount.toInt()}")

                // Feature 8: Renewal Management Details
                DetailRow("Renewal Status", if (item.status == "Expired") "Expired - Renewal Required" else "Active Pass")
                DetailRow("Auto-Renew", "Manual (Customer Initiated)")

                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Status", fontSize = 13.sp, color = TextMuted)
                    OwnerStatusBadge(status = item.status)
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (reminderSent) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(StatusActiveGreenBg)
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "✓ Renewal reminder notification queued.", fontSize = 11.sp, color = StatusActiveGreen)
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0x20FFFFFF))
                            .clickable {
                                reminderSent = true
                                GymOwnerRepository.logAdminAction("Kishore Kumar", "Sent renewal reminder to ${item.customerName}", "Memberships")
                            }
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "Send Reminder", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextWhite)
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0x35FFFFFF))
                            .border(1.dp, GlassBorderSpecular, RoundedCornerShape(14.dp))
                            .clickable { onDismiss() }
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "Close", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 13.sp, color = TextMuted)
        Text(text = value, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextWhite)
    }
}
