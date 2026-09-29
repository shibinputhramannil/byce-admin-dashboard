package com.example.ui.owner

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
import com.example.data.models.NotificationItem
import com.example.data.services.GymOwnerRepository
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun OwnerNotificationsScreen(
    modifier: Modifier = Modifier
) {
    var selectedTypeFilter by remember { mutableStateOf("All") }
    val notifications = remember { mutableStateListOf<NotificationItem>().apply { addAll(GymOwnerRepository.getNotifications()) } }

    val filteredNotifications = remember(selectedTypeFilter, notifications.toList()) {
        notifications.filter { item ->
            if (selectedTypeFilter == "All") true else item.type.equals(selectedTypeFilter, ignoreCase = true)
        }
    }

    LazyColumn(
        modifier = modifier
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
                        text = "ALERTS & ACTIVITY",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextSubtle,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Notifications Center",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0x20FFFFFF))
                        .clickable {
                            for (i in notifications.indices) {
                                notifications[i] = notifications[i].copy(isRead = true)
                            }
                            GymOwnerRepository.markAllNotificationsAsRead()
                        }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "Mark all read",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextWhite
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Type Filter Pills
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf("All", "Reminder", "Payment", "Membership", "Booking", "System").forEach { type ->
                    val isSelected = selectedTypeFilter == type
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) Color(0x35FFFFFF) else Color(0x15FFFFFF))
                            .border(1.dp, if (isSelected) GlassBorderSpecular else Color.Transparent, RoundedCornerShape(12.dp))
                            .clickable { selectedTypeFilter = type }
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = type,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) TextWhite else TextMuted
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
        }

        if (filteredNotifications.isEmpty()) {
            item {
                OwnerEmptyState(
                    title = "No Notifications",
                    description = "No alerts found under '$selectedTypeFilter'."
                )
            }
        } else {
            items(filteredNotifications, key = { it.id }) { item ->
                NotificationRowCard(
                    item = item,
                    onMarkRead = {
                        val index = notifications.indexOfFirst { it.id == item.id }
                        if (index != -1) {
                            notifications[index] = item.copy(isRead = true)
                            GymOwnerRepository.markNotificationAsRead(item.id)
                        }
                    }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
private fun NotificationRowCard(
    item: NotificationItem,
    onMarkRead: () -> Unit
) {
    val icon = when (item.type) {
        "Reminder" -> Icons.Default.Email
        "Payment" -> Icons.Default.CurrencyRupee
        "Membership" -> Icons.Default.CardMembership
        "Booking" -> Icons.Default.EventAvailable
        else -> Icons.Default.Info
    }

    LiquidGlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { if (!item.isRead) onMarkRead() },
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color(0x22FFFFFF)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
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
                        text = item.title,
                        fontSize = 14.sp,
                        fontWeight = if (!item.isRead) FontWeight.Bold else FontWeight.Medium,
                        color = TextWhite
                    )
                    if (!item.isRead) {
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .background(StatusActiveGreen, CircleShape)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = item.message,
                    fontSize = 12.sp,
                    color = if (!item.isRead) TextWhite else TextMuted,
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${item.type} · ${item.timestamp}",
                        fontSize = 11.sp,
                        color = ByceCoolGray
                    )
                }
            }
        }
    }
}
