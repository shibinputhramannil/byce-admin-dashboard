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
import com.example.data.models.CustomerItem
import com.example.data.services.GymOwnerRepository
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun OwnerCustomersScreen(
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedStatusFilter by remember { mutableStateOf("All") }
    var inactiveDaysFilter by remember { mutableIntStateOf(14) }
    var showExportDialog by remember { mutableStateOf(false) }
    var exportFormat by remember { mutableStateOf("CSV") }
    val allCustomers = remember { GymOwnerRepository.getCustomers() }
    val inactiveCustomers = remember(inactiveDaysFilter) {
        GymOwnerRepository.getInactiveCustomers(inactiveDaysFilter)
    }

    var selectedCustomerForDetail by remember { mutableStateOf<CustomerItem?>(null) }
    var showAddCustomerDialog by remember { mutableStateOf(false) }

    val filteredCustomers = remember(searchQuery, selectedStatusFilter, allCustomers) {
        allCustomers.filter { customer ->
            val matchesQuery = customer.name.contains(searchQuery, ignoreCase = true) ||
                    customer.email.contains(searchQuery, ignoreCase = true) ||
                    customer.phone.contains(searchQuery)
            val matchesStatus = if (selectedStatusFilter == "All") true else customer.status.equals(selectedStatusFilter, ignoreCase = true)
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
                            text = "CUSTOMERS DIRECTORY",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = TextSubtle,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Customer Management",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextWhite
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        // Export Button (Feature 19)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color(0x20FFFFFF))
                                .border(1.dp, GlassBorderLight, RoundedCornerShape(16.dp))
                                .clickable { showExportDialog = true }
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.FileDownload, contentDescription = "Export", tint = TextWhite, modifier = Modifier.size(15.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = "Export", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = TextWhite)
                            }
                        }

                        // Add Customer Button
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color(0x35FFFFFF))
                                .border(1.dp, GlassBorderSpecular, RoundedCornerShape(16.dp))
                                .clickable { showAddCustomerDialog = true }
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Add Customer",
                                    tint = TextWhite,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Add Customer",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextWhite
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                OwnerSearchBar(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    placeholder = "Search customers by name, email, phone...",
                    filterOptions = listOf("All", "Active", "Inactive", "Pending", "Expired"),
                    selectedFilter = selectedStatusFilter,
                    onFilterSelected = { selectedStatusFilter = it }
                )

                if (selectedStatusFilter == "Inactive") {
                    Spacer(modifier = Modifier.height(10.dp))
                    // Timeframe filters for Inactive Customers (Feature 5)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(Pair(7, "7 Days"), Pair(14, "14 Days"), Pair(30, "30 Days")).forEach { (days, label) ->
                            val isSelected = inactiveDaysFilter == days
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isSelected) Color(0x35FFFFFF) else Color(0x12FFFFFF))
                                    .border(1.dp, if (isSelected) GlassBorderSpecular else Color.Transparent, RoundedCornerShape(12.dp))
                                    .clickable { inactiveDaysFilter = days }
                                    .padding(vertical = 7.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Absent > $label",
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
                    text = if (selectedStatusFilter == "Inactive") "Showing ${inactiveCustomers.size} absent/inactive members" else "Showing ${filteredCustomers.size} customers",
                    fontSize = 12.sp,
                    color = ByceCoolGray
                )

                Spacer(modifier = Modifier.height(6.dp))
            }

            if (selectedStatusFilter == "Inactive") {
                if (inactiveCustomers.isEmpty()) {
                    item {
                        OwnerEmptyState(
                            title = "No Inactive Members",
                            description = "All members have visited the gym within the last $inactiveDaysFilter days."
                        )
                    }
                } else {
                    items(inactiveCustomers, key = { it.customerId }) { inactive ->
                        InactiveCustomerCardItem(
                            item = inactive,
                            onViewClick = {
                                val match = allCustomers.firstOrNull { it.id == inactive.customerId }
                                selectedCustomerForDetail = match ?: CustomerItem(
                                    id = inactive.customerId,
                                    name = inactive.customerName,
                                    email = "${inactive.customerName.lowercase().replace(" ", ".")}@gmail.com",
                                    phone = "+91 98470 00000",
                                    membershipPlan = inactive.membershipPlan,
                                    startDate = "01 Jan 2026",
                                    endDate = inactive.membershipExpiry,
                                    status = inactive.status,
                                    joinedDate = "01 Jan 2026",
                                    avatarInitials = inactive.customerName.take(2).uppercase()
                                )
                            }
                        )
                    }
                }
            } else if (filteredCustomers.isEmpty()) {
                item {
                    OwnerEmptyState(
                        title = "No Customers Found",
                        description = "No customers matched your filter '$selectedStatusFilter' or search query."
                    )
                }
            } else {
                items(filteredCustomers, key = { it.id }) { customer ->
                    CustomerCardItem(
                        customer = customer,
                        onViewClick = { selectedCustomerForDetail = customer },
                        onEditClick = { selectedCustomerForDetail = customer }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(40.dp))
            }
        }

        // Customer Details Modal (with CRM Notes & Activity Timeline)
        selectedCustomerForDetail?.let { customer ->
            CustomerDetailDialog(
                customer = customer,
                onDismiss = { selectedCustomerForDetail = null }
            )
        }

        // Add Customer Modal
        if (showAddCustomerDialog) {
            AddCustomerDialog(
                onDismiss = { showAddCustomerDialog = false },
                onAdd = { newCustomer ->
                    GymOwnerRepository.addCustomer(newCustomer)
                    showAddCustomerDialog = false
                }
            )
        }

        // Export Dialog (Feature 19)
        if (showExportDialog) {
            ExportModalDialog(
                moduleName = "Customer Directory",
                selectedFormat = exportFormat,
                onFormatChange = { exportFormat = it },
                onDismiss = { showExportDialog = false }
            )
        }
    }
}

@Composable
private fun CustomerCardItem(
    customer: CustomerItem,
    onViewClick: () -> Unit,
    onEditClick: () -> Unit
) {
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
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(Color(0x22FFFFFF))
                        .border(1.dp, GlassBorderLight, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = customer.avatarInitials,
                        fontSize = 14.sp,
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
                            text = customer.name,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextWhite
                        )
                        OwnerStatusBadge(status = customer.status)
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = customer.email,
                        fontSize = 12.sp,
                        color = ByceCoolGray
                    )
                    Text(
                        text = customer.phone,
                        fontSize = 12.sp,
                        color = TextMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Color(0x18FFFFFF))
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Plan: ${customer.membershipPlan}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextWhite
                    )
                    Text(
                        text = "Valid: ${customer.startDate} - ${customer.endDate}",
                        fontSize = 11.sp,
                        color = ByceCoolGray
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
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

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0x15FFFFFF))
                            .clickable { onEditClick() }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "Edit",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextWhite
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CustomerDetailDialog(
    customer: CustomerItem,
    onDismiss: () -> Unit
) {
    var activeDetailTab by remember { mutableStateOf("Overview") }
    var notesRefresh by remember { mutableIntStateOf(0) }
    var newNoteText by remember { mutableStateOf("") }
    val notes = remember(customer.id, notesRefresh) { GymOwnerRepository.getCustomerNotes(customer.id) }
    val activities = remember(customer.id) { GymOwnerRepository.getCustomerActivityTimeline(customer.id) }

    Dialog(onDismissRequest = onDismiss) {
        LiquidGlassCard(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
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
                    Text(
                        text = "Customer Profile",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Color(0x28FFFFFF)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = customer.avatarInitials,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextWhite
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = customer.name,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextWhite
                        )
                        Text(text = customer.email, fontSize = 11.sp, color = ByceCoolGray)
                        Text(text = customer.phone, fontSize = 11.sp, color = TextMuted)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Detail Sub-tabs: Overview | CRM Notes | Activity
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("Overview", "CRM Notes", "Activity").forEach { tab ->
                        val isSelected = activeDetailTab == tab
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) Color(0x35FFFFFF) else Color(0x12FFFFFF))
                                .border(1.dp, if (isSelected) GlassBorderSpecular else Color.Transparent, RoundedCornerShape(10.dp))
                                .clickable { activeDetailTab = tab }
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

                Spacer(modifier = Modifier.height(14.dp))

                when (activeDetailTab) {
                    "Overview" -> {
                        Text(
                            text = "MEMBERSHIP DETAILS",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextSubtle,
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Current Plan", fontSize = 13.sp, color = TextMuted)
                            Text(text = customer.membershipPlan, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Status", fontSize = 13.sp, color = TextMuted)
                            OwnerStatusBadge(status = customer.status)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Start Date", fontSize = 13.sp, color = TextMuted)
                            Text(text = customer.startDate, fontSize = 13.sp, color = TextWhite)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "End Date", fontSize = 13.sp, color = TextMuted)
                            Text(text = customer.endDate, fontSize = 13.sp, color = TextWhite)
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color(0x18FFFFFF)))
                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "RECENT ACTIVITY SUMMARY",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextSubtle,
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Last checked in: Yesterday, 07:15 AM · Iron House Gym",
                            fontSize = 12.sp,
                            color = TextWhite
                        )
                    }

                    "CRM Notes" -> {
                        // FEATURE 6: CUSTOMER CRM NOTES
                        Text(
                            text = "STAFF CRM NOTES",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextSubtle,
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        if (notes.isEmpty()) {
                            Text(text = "No notes recorded yet for this customer.", fontSize = 12.sp, color = TextMuted)
                        } else {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                notes.forEach { noteItem ->
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(Color(0x18FFFFFF))
                                            .padding(10.dp)
                                    ) {
                                        Column {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(text = noteItem.createdBy, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                                                IconButton(
                                                    onClick = {
                                                        GymOwnerRepository.deleteCustomerNote(customer.id, noteItem.id)
                                                        notesRefresh++
                                                    },
                                                    modifier = Modifier.size(20.dp)
                                                ) {
                                                    Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = TextMuted, modifier = Modifier.size(14.dp))
                                                }
                                            }
                                            Text(text = noteItem.note, fontSize = 12.sp, color = TextWhite)
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(text = noteItem.createdAt, fontSize = 10.sp, color = ByceCoolGray)
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Add Note Input
                        LiquidGlassTextField(
                            value = newNoteText,
                            onValueChange = { newNoteText = it },
                            label = "Add Staff Note",
                            placeholder = "e.g. Customer requested membership renewal info"
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0x28FFFFFF))
                                .border(1.dp, GlassBorderSpecular, RoundedCornerShape(12.dp))
                                .clickable {
                                    if (newNoteText.isNotBlank()) {
                                        GymOwnerRepository.addCustomerNote(customer.id, newNoteText)
                                        newNoteText = ""
                                        notesRefresh++
                                    }
                                }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "+ Save CRM Note", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                        }
                    }

                    "Activity" -> {
                        // FEATURE 7: CUSTOMER ACTIVITY TIMELINE
                        Text(
                            text = "ACTIVITY HISTORY",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextSubtle,
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            activities.forEach { act ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(28.dp)
                                            .clip(CircleShape)
                                            .background(Color(0x25FFFFFF)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        val icon = when (act.type) {
                                            "CheckIn" -> Icons.Default.DirectionsRun
                                            "Payment" -> Icons.Default.CurrencyRupee
                                            "Booking" -> Icons.Default.EventAvailable
                                            "Note" -> Icons.Default.EditNote
                                            else -> Icons.Default.CardMembership
                                        }
                                        Icon(imageVector = icon, contentDescription = null, tint = TextWhite, modifier = Modifier.size(14.dp))
                                    }

                                    Spacer(modifier = Modifier.width(10.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(text = act.title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                                            Text(text = "${act.date} · ${act.time}", fontSize = 10.sp, color = ByceCoolGray)
                                        }
                                        Text(text = act.description, fontSize = 11.sp, color = TextMuted)
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0x35FFFFFF))
                        .border(1.dp, GlassBorderSpecular, RoundedCornerShape(14.dp))
                        .clickable { onDismiss() }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "Close Profile", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                }
            }
        }
    }
}

@Composable
private fun InactiveCustomerCardItem(
    item: com.example.data.models.InactiveCustomerItem,
    onViewClick: () -> Unit
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
                        Text(text = item.customerName.take(2).uppercase(), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(text = item.customerName, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TextWhite)
                        Text(text = "ID: ${item.customerId} · ${item.membershipPlan}", fontSize = 11.sp, color = ByceCoolGray)
                    }
                }
                OwnerStatusBadge(status = item.status)
            }

            Spacer(modifier = Modifier.height(10.dp))
            Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color(0x15FFFFFF)))
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "Last Visit: ${item.lastCheckIn} (${item.daysSinceVisit}d ago)", fontSize = 11.sp, color = StatusPendingAmber)
                    Text(text = "Expires: ${item.membershipExpiry}", fontSize = 11.sp, color = ByceCoolGray)
                }
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
private fun AddCustomerDialog(
    onDismiss: () -> Unit,
    onAdd: (CustomerItem) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var plan by remember { mutableStateOf("Monthly") }

    Dialog(onDismissRequest = onDismiss) {
        LiquidGlassCard(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            shape = RoundedCornerShape(24.dp)
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
                    Text(
                        text = "Add New Customer",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                LiquidGlassTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = "Full Name",
                    placeholder = "e.g. Anand Sharma"
                )

                Spacer(modifier = Modifier.height(10.dp))

                LiquidGlassTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = "Email Address",
                    placeholder = "e.g. anand@gmail.com"
                )

                Spacer(modifier = Modifier.height(10.dp))

                LiquidGlassTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = "Phone Number",
                    placeholder = "+91 98470 00000"
                )

                Spacer(modifier = Modifier.height(18.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color(0xFF6B9330), Color(0xFF4E7320))
                            )
                        )
                        .clickable {
                            if (name.isNotBlank()) {
                                val initials = name.split(" ").mapNotNull { it.firstOrNull()?.toString() }.take(2).joinToString("").uppercase()
                                onAdd(
                                    CustomerItem(
                                        id = "c_${System.currentTimeMillis()}",
                                        name = name,
                                        email = email.ifBlank { "member@byce.in" },
                                        phone = phone.ifBlank { "+91 98470 12345" },
                                        membershipPlan = plan,
                                        startDate = "24 Sep 2026",
                                        endDate = "23 Oct 2026",
                                        status = "Active",
                                        joinedDate = "24 Sep 2026",
                                        avatarInitials = initials.ifBlank { "MB" }
                                    )
                                )
                            }
                        }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Register Customer",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )
                }
            }
        }
    }
}
