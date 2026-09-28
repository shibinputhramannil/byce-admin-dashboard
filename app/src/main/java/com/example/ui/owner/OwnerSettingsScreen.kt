package com.example.ui.owner

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.services.GymOwnerRepository
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun OwnerSettingsScreen(
    onLogoutClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val owner = remember { GymOwnerRepository.getOwnerProfile() }
    var ownerName by remember { mutableStateOf(owner.name) }
    var ownerEmail by remember { mutableStateOf(owner.email) }
    var ownerPhone by remember { mutableStateOf(owner.phone) }

    var selectedSection by remember { mutableStateOf("Account") } // Account, Security, Notifications, Gym Settings

    var showPasswordDialog by remember { mutableStateOf(false) }
    var notificationsEnabled by remember { mutableStateOf(true) }
    var emailAlertsEnabled by remember { mutableStateOf(true) }
    var autoApproveCheckin by remember { mutableStateOf(true) }

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Text(
            text = "PREFERENCES & SECURITY",
            fontSize = 11.sp,
            fontWeight = FontWeight.ExtraBold,
            color = TextSubtle,
            letterSpacing = 1.sp
        )
        Text(
            text = "Owner Settings",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = TextWhite
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Navigation Tabs (Account, Security, Notifications, Gym Settings)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            listOf("Account", "Security", "Alerts", "Gym").forEach { sec ->
                val isSelected = selectedSection == sec
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) Color(0x35FFFFFF) else Color(0x15FFFFFF))
                        .border(1.dp, if (isSelected) GlassBorderSpecular else Color.Transparent, RoundedCornerShape(12.dp))
                        .clickable { selectedSection = sec }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = sec,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) TextWhite else TextMuted
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        when (selectedSection) {
            "Account" -> {
                LiquidGlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(CircleShape)
                                    .background(Color(0x28FFFFFF))
                                    .border(1.dp, GlassBorderSpecular, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "KK",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextWhite
                                )
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column {
                                Text(
                                    text = ownerName,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextWhite
                                )
                                Text(
                                    text = "Gym Owner · Iron House Fitness",
                                    fontSize = 12.sp,
                                    color = ByceCoolGray
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))
                        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color(0x18FFFFFF)))
                        Spacer(modifier = Modifier.height(16.dp))

                        LiquidGlassTextField(
                            value = ownerName,
                            onValueChange = { ownerName = it },
                            label = "Full Name"
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        LiquidGlassTextField(
                            value = ownerEmail,
                            onValueChange = { ownerEmail = it },
                            label = "Email Address"
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        LiquidGlassTextField(
                            value = ownerPhone,
                            onValueChange = { ownerPhone = it },
                            label = "Phone Number"
                        )

                        Spacer(modifier = Modifier.height(20.dp))

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
                                    GymOwnerRepository.updateOwnerProfile(ownerName, ownerEmail, ownerPhone)
                                }
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Save Profile",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextWhite
                            )
                        }
                    }
                }
            }
            "Security" -> {
                LiquidGlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Text(
                            text = "SECURITY CREDENTIALS",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextSubtle,
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Manage your authentication credentials and password protection.",
                            fontSize = 12.sp,
                            color = ByceCoolGray
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color(0x18FFFFFF))
                                .clickable { showPasswordDialog = true }
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Lock, contentDescription = null, tint = TextWhite, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(text = "Change Password", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TextWhite)
                                    Text(text = "Last updated 30 days ago", fontSize = 11.sp, color = ByceCoolGray)
                                }
                            }
                            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = TextMuted)
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color(0x18FFFFFF))
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Security, contentDescription = null, tint = TextWhite, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(text = "Two-Factor Authentication", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TextWhite)
                                    Text(text = "SMS OTP verification active", fontSize = 11.sp, color = ByceCoolGray)
                                }
                            }
                            OwnerStatusBadge(status = "Active")
                        }
                    }
                }
            }
            "Alerts" -> {
                LiquidGlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Text(
                            text = "COMMUNICATION CHANNELS",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextSubtle,
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.height(14.dp))

                        TogglePreferenceRow("Push Notifications", "Instant alerts for payments and check-ins", notificationsEnabled) {
                            notificationsEnabled = !notificationsEnabled
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        TogglePreferenceRow("Email Financial Receipts", "Receive daily revenue settlements", emailAlertsEnabled) {
                            emailAlertsEnabled = !emailAlertsEnabled
                        }
                    }
                }
            }
            "Gym" -> {
                LiquidGlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Text(
                            text = "GYM OPERATIONAL RULES",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextSubtle,
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.height(14.dp))

                        TogglePreferenceRow("Auto-Approve Check-ins", "Allow members instant entry via QR scan", autoApproveCheckin) {
                            autoApproveCheckin = !autoApproveCheckin
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        TogglePreferenceRow("Allow Guest Passes", "Permit members to book guest passes", true) {}
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Logout Button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(StatusExpiredRedBg)
                .border(1.dp, StatusExpiredRed.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                .clickable { onLogoutClick() }
                .padding(vertical = 14.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Logout, contentDescription = "Logout", tint = StatusExpiredRed, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Log Out from Gym Owner", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = StatusExpiredRed)
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        if (showPasswordDialog) {
            ChangePasswordDialog(
                onDismiss = { showPasswordDialog = false }
            )
        }
    }
}

@Composable
private fun TogglePreferenceRow(
    title: String,
    subtitle: String,
    isEnabled: Boolean,
    onToggle: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0x18FFFFFF))
            .clickable { onToggle() }
            .padding(14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextWhite)
            Text(text = subtitle, fontSize = 11.sp, color = ByceCoolGray)
        }
        Spacer(modifier = Modifier.width(8.dp))
        Switch(
            checked = isEnabled,
            onCheckedChange = { onToggle() },
            colors = SwitchDefaults.colors(
                checkedThumbColor = DarkNavy,
                checkedTrackColor = StatusActiveGreen,
                uncheckedThumbColor = TextMuted,
                uncheckedTrackColor = Color(0x30FFFFFF)
            )
        )
    }
}

@Composable
private fun ChangePasswordDialog(
    onDismiss: () -> Unit
) {
    var oldPass by remember { mutableStateOf("") }
    var newPass by remember { mutableStateOf("") }
    var confirmPass by remember { mutableStateOf("") }

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
                Text(text = "Change Password", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            LiquidGlassTextField(value = oldPass, onValueChange = { oldPass = it }, label = "Current Password", isPassword = true)
            Spacer(modifier = Modifier.height(8.dp))
            LiquidGlassTextField(value = newPass, onValueChange = { newPass = it }, label = "New Password", isPassword = true)
            Spacer(modifier = Modifier.height(8.dp))
            LiquidGlassTextField(value = confirmPass, onValueChange = { confirmPass = it }, label = "Confirm New Password", isPassword = true)

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
                    .clickable { onDismiss() }
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "Update Password", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextWhite)
            }
        }
    }
}
