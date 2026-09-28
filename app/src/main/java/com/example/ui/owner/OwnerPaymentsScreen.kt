package com.example.ui.owner

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.models.PaymentItem
import com.example.data.services.GymOwnerRepository
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun OwnerPaymentsScreen(
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedStatusFilter by remember { mutableStateOf("All") }
    var refreshTrigger by remember { mutableIntStateOf(0) }
    var showExportDialog by remember { mutableStateOf(false) }
    var exportFormat by remember { mutableStateOf("CSV") }
    var paymentToRecordPaid by remember { mutableStateOf<com.example.data.models.PendingPaymentItem?>(null) }
    var reminderToastMessage by remember { mutableStateOf<String?>(null) }

    val allPayments = remember { GymOwnerRepository.getPayments() }
    val allPendingPayments = remember(refreshTrigger) { GymOwnerRepository.getPendingPayments() }
    var selectedPaymentForDetail by remember { mutableStateOf<PaymentItem?>(null) }

    val filteredPayments = remember(searchQuery, selectedStatusFilter, allPayments) {
        allPayments.filter { payment ->
            val matchesQuery = payment.customerName.contains(searchQuery, ignoreCase = true) ||
                    payment.transactionId.contains(searchQuery, ignoreCase = true) ||
                    payment.provider.contains(searchQuery, ignoreCase = true)
            val matchesStatus = if (selectedStatusFilter == "All") true else payment.status.equals(selectedStatusFilter, ignoreCase = true)
            matchesQuery && matchesStatus
        }
    }

    val filteredPendingPayments = remember(searchQuery, selectedStatusFilter, allPendingPayments) {
        allPendingPayments.filter { pending ->
            pending.customerName.contains(searchQuery, ignoreCase = true) ||
                    pending.membershipPlan.contains(searchQuery, ignoreCase = true) ||
                    pending.phoneNumber.contains(searchQuery, ignoreCase = true)
        }
    }

    val totalPendingAmount = remember(allPendingPayments) {
        allPendingPayments.sumOf { it.amount }
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
                            text = "FINANCIAL LEDGER",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = TextSubtle,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Payments & Transactions",
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

                // Top summary cards (2x2)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MiniPaymentKpi("Total Revenue", "₹4,82,500", TextWhite, Modifier.weight(1f))
                    MiniPaymentKpi("Pending Dues", "₹${totalPendingAmount.toInt()}", StatusPendingAmber, Modifier.weight(1f))
                }
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MiniPaymentKpi("Successful", "942", TextWhite, Modifier.weight(1f))
                    MiniPaymentKpi("Failed", "6", StatusExpiredRed, Modifier.weight(1f))
                }

                Spacer(modifier = Modifier.height(16.dp))

                reminderToastMessage?.let { msg ->
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
                            IconButton(onClick = { reminderToastMessage = null }, modifier = Modifier.size(20.dp)) {
                                Icon(Icons.Default.Close, contentDescription = "Dismiss", tint = StatusActiveGreen, modifier = Modifier.size(14.dp))
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }

                OwnerSearchBar(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    placeholder = "Search transaction, member, phone...",
                    filterOptions = listOf("All", "Pending / Dues", "Successful", "Failed"),
                    selectedFilter = selectedStatusFilter,
                    onFilterSelected = { selectedStatusFilter = it }
                )

                Spacer(modifier = Modifier.height(12.dp))

                val countText = if (selectedStatusFilter == "Pending / Dues") {
                    "Showing ${filteredPendingPayments.size} pending dues"
                } else {
                    "Showing ${filteredPayments.size} transactions"
                }
                Text(
                    text = countText,
                    fontSize = 12.sp,
                    color = ByceCoolGray
                )

                Spacer(modifier = Modifier.height(4.dp))
            }

            if (selectedStatusFilter == "Pending / Dues") {
                if (filteredPendingPayments.isEmpty()) {
                    item {
                        OwnerEmptyState(
                            title = "No Pending Dues",
                            description = "All membership and subscription payments are up to date."
                        )
                    }
                } else {
                    items(filteredPendingPayments, key = { it.id }) { pending ->
                        PendingPaymentCardRow(
                            item = pending,
                            onSendReminder = {
                                GymOwnerRepository.sendPaymentReminder(pending.id)
                                reminderToastMessage = "✓ Payment reminder dispatched to ${pending.customerName} via SMS & WhatsApp."
                            },
                            onRecordPayment = {
                                paymentToRecordPaid = pending
                            }
                        )
                    }
                }
            } else {
                if (filteredPayments.isEmpty()) {
                    item {
                        OwnerEmptyState(
                            title = "No Payments Found",
                            description = "No transaction matches filter '$selectedStatusFilter'."
                        )
                    }
                } else {
                    items(filteredPayments, key = { it.transactionId }) { payment ->
                        PaymentRowCard(
                            payment = payment,
                            onViewClick = { selectedPaymentForDetail = payment }
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(40.dp))
            }
        }

        selectedPaymentForDetail?.let { payment ->
            PaymentDetailModal(
                payment = payment,
                onDismiss = { selectedPaymentForDetail = null }
            )
        }

        paymentToRecordPaid?.let { item ->
            RecordPaymentConfirmDialog(
                item = item,
                onConfirm = {
                    GymOwnerRepository.recordPendingPaymentPaid(item.id)
                    paymentToRecordPaid = null
                    refreshTrigger++
                    reminderToastMessage = "✓ Recorded manual payment of ₹${item.amount.toInt()} for ${item.customerName}."
                },
                onDismiss = { paymentToRecordPaid = null }
            )
        }

        if (showExportDialog) {
            ExportModalDialog(
                moduleName = "Payments & Transactions",
                selectedFormat = exportFormat,
                onFormatChange = { exportFormat = it },
                onDismiss = { showExportDialog = false }
            )
        }
    }
}

@Composable
private fun PendingPaymentCardRow(
    item: com.example.data.models.PendingPaymentItem,
    onSendReminder: () -> Unit,
    onRecordPayment: () -> Unit
) {
    val isOverdue = item.status.contains("Overdue", ignoreCase = true) || item.status.contains("Failed", ignoreCase = true)
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
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.customerName,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${item.phoneNumber} · ${item.membershipPlan}",
                        fontSize = 12.sp,
                        color = ByceCoolGray
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "₹${item.amount.toInt()}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (isOverdue) StatusExpiredRed else StatusPendingAmber
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (isOverdue) StatusExpiredRedBg else StatusPendingAmberBg)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = item.status,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isOverdue) StatusExpiredRed else StatusPendingAmber
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = null,
                        tint = if (isOverdue) StatusExpiredRed else ByceCoolGray,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = item.dueDate,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (isOverdue) StatusExpiredRed else ByceCoolGray
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Send Reminder Button
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0x20FFFFFF))
                            .clickable { onSendReminder() }
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Send,
                                contentDescription = "Remind",
                                tint = TextWhite,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "Remind", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                        }
                    }

                    // Record Payment Button
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(StatusActiveGreen.copy(alpha = 0.3f), StatusActiveGreen.copy(alpha = 0.15f))
                                )
                            )
                            .border(1.dp, StatusActiveGreen.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                            .clickable { onRecordPayment() }
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Record Paid",
                                tint = StatusActiveGreen,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "Record Paid", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = StatusActiveGreen)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RecordPaymentConfirmDialog(
    item: com.example.data.models.PendingPaymentItem,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        LiquidGlassCard(
            modifier = Modifier.fillMaxWidth().wrapContentHeight(),
            shape = RoundedCornerShape(22.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Record Payment", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Confirm manual payment collection for this member:",
                    fontSize = 12.sp,
                    color = ByceCoolGray
                )

                Spacer(modifier = Modifier.height(14.dp))

                LiquidGlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Column(modifier = Modifier.fillMaxWidth().padding(14.dp)) {
                        ReceiptRow("Member", item.customerName)
                        ReceiptRow("Phone", item.phoneNumber)
                        ReceiptRow("Plan", item.membershipPlan)
                        ReceiptRow("Amount", "₹${item.amount.toInt()}")
                        ReceiptRow("Status", item.dueDate)
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0x20FFFFFF))
                            .clickable { onDismiss() }
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "Cancel", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextWhite)
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(Color(0xFF6B9330), Color(0xFF4E7320))
                                )
                            )
                            .clickable { onConfirm() }
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "Mark Paid", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                    }
                }
            }
        }
    }
}

@Composable
private fun MiniPaymentKpi(
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
                .padding(14.dp)
        ) {
            Text(text = label, fontSize = 11.sp, color = ByceCoolGray)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = value, fontSize = 17.sp, fontWeight = FontWeight.Bold, color = accentColor)
        }
    }
}

@Composable
private fun PaymentRowCard(
    payment: PaymentItem,
    onViewClick: () -> Unit
) {
    LiquidGlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onViewClick() },
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0x20FFFFFF)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Receipt,
                    contentDescription = null,
                    tint = TextWhite,
                    modifier = Modifier.size(18.dp)
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
                        text = payment.customerName,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )
                    Text(
                        text = "₹${payment.amount.toInt()}",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${payment.transactionId} · ${payment.provider}",
                        fontSize = 11.sp,
                        color = ByceCoolGray
                    )
                    OwnerStatusBadge(status = payment.status)
                }

                Text(
                    text = "${payment.planName} · ${payment.date}",
                    fontSize = 11.sp,
                    color = TextSubtle
                )
            }
        }
    }
}

@Composable
private fun PaymentDetailModal(
    payment: PaymentItem,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        LiquidGlassCard(
            modifier = Modifier.fillMaxWidth().wrapContentHeight(),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Payment Receipt", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "₹${payment.amount.toInt()}",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextWhite
                )
                Text(text = "Transaction ${payment.transactionId}", fontSize = 12.sp, color = ByceCoolGray)

                Spacer(modifier = Modifier.height(14.dp))
                Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color(0x20FFFFFF)))
                Spacer(modifier = Modifier.height(12.dp))

                ReceiptRow("Customer", payment.customerName)
                ReceiptRow("Plan", payment.planName)
                ReceiptRow("Provider", payment.provider)
                ReceiptRow("Order ID", payment.orderId)
                ReceiptRow("Payment ID", payment.paymentId)
                ReceiptRow("Date", payment.date)
                ReceiptRow("Paid At", payment.paidAt)

                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Status", fontSize = 13.sp, color = TextMuted)
                    OwnerStatusBadge(status = payment.status)
                }

                Spacer(modifier = Modifier.height(20.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color(0x35FFFFFF), Color(0x20FFFFFF))
                            )
                        )
                        .border(1.dp, GlassBorderSpecular, RoundedCornerShape(16.dp))
                        .clickable { onDismiss() }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "Close Receipt", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                }
            }
        }
    }
}

@Composable
private fun ReceiptRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 13.sp, color = TextMuted)
        Text(text = value, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextWhite)
    }
}
