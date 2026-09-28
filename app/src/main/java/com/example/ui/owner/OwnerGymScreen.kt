package com.example.ui.owner

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.R
import com.example.data.models.GymProfile
import com.example.data.services.GymOwnerRepository
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun OwnerGymScreen(
    initialTab: String = "Profile",
    modifier: Modifier = Modifier
) {
    var activeSubTab by remember { mutableStateOf(initialTab) }
    var gymProfile by remember { mutableStateOf(GymOwnerRepository.getGymProfile()) }
    var operatingStatus by remember { mutableStateOf(GymOwnerRepository.getGymOperatingStatus()) }
    val completeness = remember { GymOwnerRepository.getGymProfileCompleteness() }
    var qrPayload by remember { mutableStateOf(GymOwnerRepository.getGymQrPayload()) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showStatusDialog by remember { mutableStateOf(false) }
    var showRegenerateQrDialog by remember { mutableStateOf(false) }
    var qrActionNotice by remember { mutableStateOf<String?>(null) }

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "FACILITY & GEO-LOCATION",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextSubtle,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Gym Management",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color(0x20FFFFFF))
                    .clickable { showEditDialog = true }
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = TextWhite, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Edit Gym", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Subtabs: Profile | Location | Check-in QR
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("Profile", "Location", "Check-in QR").forEach { tab ->
                val isSelected = activeSubTab == tab
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (isSelected) Color(0x35FFFFFF) else Color(0x15FFFFFF))
                        .border(1.dp, if (isSelected) GlassBorderSpecular else Color.Transparent, RoundedCornerShape(14.dp))
                        .clickable { activeSubTab = tab }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = tab,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) TextWhite else TextMuted
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        if (activeSubTab == "Profile") {
            // ----------------------------------------------------
            // FEATURE 12 & 15: GYM OPERATING STATUS & VISIBILITY
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
                                text = "OPERATING STATUS",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextSubtle,
                                letterSpacing = 0.5.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .background(if (operatingStatus.status == "OPEN") StatusActiveGreen else StatusExpiredRed, CircleShape)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = operatingStatus.status,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextWhite
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0x28FFFFFF))
                                .border(1.dp, GlassBorderSpecular, RoundedCornerShape(12.dp))
                                .clickable { showStatusDialog = true }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(text = "Change Status", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color(0x15FFFFFF)))
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(text = "Operating Hours", fontSize = 11.sp, color = ByceCoolGray)
                            Text(text = "${operatingStatus.openingTime} - ${operatingStatus.closingTime}", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextWhite)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(text = "Platform Visibility", fontSize = 11.sp, color = ByceCoolGray)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                OwnerStatusBadge(status = operatingStatus.visibilityStatus)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Listing visibility requires Super Admin approval to modify.",
                        fontSize = 10.sp,
                        color = TextMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ----------------------------------------------------
            // FEATURE 13: GYM PROFILE COMPLETENESS
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
                                text = "PROFILE COMPLETENESS",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextSubtle,
                                letterSpacing = 0.5.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "${completeness.percentage}% Complete",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextWhite
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0x28FFFFFF))
                                .border(1.dp, GlassBorderSpecular, RoundedCornerShape(12.dp))
                                .clickable { showEditDialog = true }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(text = "Complete Profile", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Progress bar
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(Color(0x20FFFFFF))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(completeness.percentage / 100f)
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(3.dp))
                                .background(StatusActiveGreen)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Missing: ${completeness.missingFields.joinToString(", ")}",
                        fontSize = 11.sp,
                        color = ByceCoolGray
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            // Gym Profile Information
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
                        Image(
                            painter = painterResource(id = R.drawable.byce_logo),
                            contentDescription = "Gym Logo",
                            modifier = Modifier.height(38.dp)
                        )
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text(
                                text = gymProfile.name,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextWhite
                            )
                            Text(
                                text = "${gymProfile.city}, ${gymProfile.state}",
                                fontSize = 12.sp,
                                color = ByceCoolGray
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Text(text = gymProfile.description, fontSize = 13.sp, color = TextMuted, lineHeight = 18.sp)

                    Spacer(modifier = Modifier.height(16.dp))
                    Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color(0x18FFFFFF)))
                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "OPERATING HOURS",
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
                        Text(text = "Daily Schedule", fontSize = 13.sp, color = TextMuted)
                        Text(
                            text = "${gymProfile.openingTime} - ${gymProfile.closingTime}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextWhite
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color(0x18FFFFFF)))
                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "CONTACT DETAILS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSubtle,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    ContactRow(Icons.Default.Phone, gymProfile.phone)
                    ContactRow(Icons.Default.Email, gymProfile.email)
                    ContactRow(Icons.Default.Language, gymProfile.website)

                    Spacer(modifier = Modifier.height(16.dp))
                    Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color(0x18FFFFFF)))
                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "ADDRESS & PINCODE",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSubtle,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = "${gymProfile.address}\n${gymProfile.city}, ${gymProfile.district}\n${gymProfile.state} - ${gymProfile.pincode}", fontSize = 13.sp, color = TextWhite, lineHeight = 19.sp)
                }
            }
        } else if (activeSubTab == "Location") {
            // Gym Location & Map View
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
                        text = "BYCE DISCOVERY GEO-LOCATION",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSubtle,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Nearby Gym Positioning",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Map View Container
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color(0xFF14241E), Color(0xFF0F1A15))
                                )
                            )
                            .border(1.dp, GlassBorderLight, RoundedCornerShape(18.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        // Simulated Dark Map Grid Lines
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.SpaceEvenly
                        ) {
                            repeat(5) {
                                Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color(0x10FFFFFF)))
                            }
                        }

                        // Center Map Pin
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(Color(0x30FFFFFF))
                                    .border(1.dp, GlassBorderSpecular, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = "Pin",
                                    tint = TextWhite,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(DarkNavyDepth)
                                    .border(0.5.dp, GlassBorderLight, RoundedCornerShape(10.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "Iron House Fitness",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextWhite
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Coordinates Display
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(text = "Latitude", fontSize = 11.sp, color = ByceCoolGray)
                            Text(text = "${gymProfile.latitude}° N", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                        }
                        Column {
                            Text(text = "Longitude", fontSize = 11.sp, color = ByceCoolGray)
                            Text(text = "${gymProfile.longitude}° E", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                        }
                        Column {
                            Text(text = "Discovery Status", fontSize = 11.sp, color = ByceCoolGray)
                            OwnerStatusBadge(status = "Active")
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color(0x18FFFFFF)))
                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Full Address",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSubtle
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${gymProfile.address}, ${gymProfile.city}, ${gymProfile.district}, ${gymProfile.state} - ${gymProfile.pincode}",
                        fontSize = 13.sp,
                        color = TextMuted,
                        lineHeight = 18.sp
                    )
                }
            }
        } else {
            // ----------------------------------------------------
            // FEATURE 3: GYM QR CHECK-IN MANAGEMENT
            // ----------------------------------------------------
            LiquidGlassCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(22.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "GYM CHECK-IN QR PASS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextSubtle,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Member Arrival Scanner",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Members scan this QR code using their BYCE mobile app to automatically check in at your front desk.",
                        fontSize = 12.sp,
                        color = ByceCoolGray,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // QR Card Display (Frosted Liquid Glass Frame)
                    Box(
                        modifier = Modifier
                            .size(230.dp)
                            .clip(RoundedCornerShape(22.dp))
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
                                contentDescription = "Gym QR",
                                tint = DarkNavy,
                                modifier = Modifier.size(160.dp)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "BYCE · ${gymProfile.name}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = DarkNavy
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // QR Status Badge
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(StatusActiveGreen, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Status: ACTIVE & AUTHENTICATED",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = StatusActiveGreen
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Payload: $qrPayload",
                        fontSize = 10.sp,
                        color = TextMuted,
                        maxLines = 1
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    if (qrActionNotice != null) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0x2534D399))
                                .border(1.dp, StatusActiveGreen.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                                .padding(10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = qrActionNotice ?: "", fontSize = 12.sp, color = TextWhite)
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                    }

                    // QR Action Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color(0x28FFFFFF))
                                .border(1.dp, GlassBorderSpecular, RoundedCornerShape(14.dp))
                                .clickable {
                                    qrActionNotice = "✓ Printable QR PDF generated for front desk standee."
                                    GymOwnerRepository.logAdminAction("Kishore Kumar", "Generated printable QR display", "Gym QR")
                                }
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Print, contentDescription = null, tint = TextWhite, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = "Print Standee", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                            }
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color(0x28FFFFFF))
                                .border(1.dp, GlassBorderSpecular, RoundedCornerShape(14.dp))
                                .clickable {
                                    qrActionNotice = "✓ High-res QR graphic downloaded to device storage."
                                    GymOwnerRepository.logAdminAction("Kishore Kumar", "Downloaded QR UI graphic", "Gym QR")
                                }
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Download, contentDescription = null, tint = TextWhite, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = "Download UI", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0x18FFFFFF))
                            .border(1.dp, GlassBorderLight, RoundedCornerShape(14.dp))
                            .clickable { showRegenerateQrDialog = true }
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Refresh, contentDescription = null, tint = TextWhite, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "Regenerate QR Token", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextWhite)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        // Edit Gym Dialog
        if (showEditDialog) {
            EditGymDialog(
                current = gymProfile,
                onDismiss = { showEditDialog = false },
                onSave = { updated ->
                    gymProfile = updated
                    GymOwnerRepository.updateGymProfile(updated)
                    showEditDialog = false
                }
            )
        }

        // Change Status Dialog
        if (showStatusDialog) {
            ChangeOperatingStatusDialog(
                currentStatus = operatingStatus.status,
                onDismiss = { showStatusDialog = false },
                onConfirm = { newStatus ->
                    GymOwnerRepository.updateGymOperatingStatus(newStatus)
                    operatingStatus = operatingStatus.copy(status = newStatus)
                    showStatusDialog = false
                }
            )
        }

        // Regenerate QR Dialog
        if (showRegenerateQrDialog) {
            RegenerateQrConfirmDialog(
                onDismiss = { showRegenerateQrDialog = false },
                onConfirm = {
                    qrPayload = GymOwnerRepository.regenerateGymQrPayload()
                    qrActionNotice = "✓ Gym QR token regenerated successfully. Older QR passes expired."
                    showRegenerateQrDialog = false
                }
            )
        }
    }
}

@Composable
private fun ChangeOperatingStatusDialog(
    currentStatus: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var selected by remember { mutableStateOf(currentStatus) }

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
                    Text(text = "Update Operating Status", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                listOf("OPEN", "CLOSED", "TEMPORARILY CLOSED").forEach { statusOption ->
                    val isSelected = selected == statusOption
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) Color(0x35FFFFFF) else Color(0x15FFFFFF))
                            .border(1.dp, if (isSelected) GlassBorderSpecular else Color.Transparent, RoundedCornerShape(12.dp))
                            .clickable { selected = statusOption }
                            .padding(14.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(if (statusOption == "OPEN") StatusActiveGreen else StatusExpiredRed, CircleShape)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(text = statusOption, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0x35FFFFFF))
                        .border(1.dp, GlassBorderSpecular, RoundedCornerShape(14.dp))
                        .clickable { onConfirm(selected) }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "Save Operating Status", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                }
            }
        }
    }
}

@Composable
private fun RegenerateQrConfirmDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
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
                Text(text = "Regenerate QR Code?", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Regenerating the QR code will invalidate any previously printed front-desk standees or cached QR tokens. Customers will need to scan the new code to check in.",
                    fontSize = 12.sp,
                    color = ByceCoolGray,
                    lineHeight = 17.sp
                )
                Spacer(modifier = Modifier.height(20.dp))

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
                            .background(StatusExpiredRedBg)
                            .border(1.dp, StatusExpiredRed.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                            .clickable { onConfirm() }
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "Regenerate", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = StatusExpiredRed)
                    }
                }
            }
        }
    }
}

@Composable
private fun ContactRow(icon: androidx.compose.ui.graphics.vector.ImageVector, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = TextWhite, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(10.dp))
        Text(text = value, fontSize = 13.sp, color = TextWhite)
    }
}

@Composable
private fun EditGymDialog(
    current: GymProfile,
    onDismiss: () -> Unit,
    onSave: (GymProfile) -> Unit
) {
    var name by remember { mutableStateOf(current.name) }
    var description by remember { mutableStateOf(current.description) }
    var phone by remember { mutableStateOf(current.phone) }
    var email by remember { mutableStateOf(current.email) }
    var website by remember { mutableStateOf(current.website) }
    var address by remember { mutableStateOf(current.address) }
    var city by remember { mutableStateOf(current.city) }
    var state by remember { mutableStateOf(current.state) }
    var pincode by remember { mutableStateOf(current.pincode) }
    var openTime by remember { mutableStateOf(current.openingTime) }
    var closeTime by remember { mutableStateOf(current.closingTime) }

    Dialog(onDismissRequest = onDismiss) {
        LiquidGlassCard(
            modifier = Modifier.fillMaxWidth().wrapContentHeight(),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(androidx.compose.foundation.rememberScrollState())
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Edit Gym Information", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                LiquidGlassTextField(value = name, onValueChange = { name = it }, label = "Gym Name")
                Spacer(modifier = Modifier.height(8.dp))
                LiquidGlassTextField(value = description, onValueChange = { description = it }, label = "Description")
                Spacer(modifier = Modifier.height(8.dp))
                LiquidGlassTextField(value = phone, onValueChange = { phone = it }, label = "Phone")
                Spacer(modifier = Modifier.height(8.dp))
                LiquidGlassTextField(value = email, onValueChange = { email = it }, label = "Email")
                Spacer(modifier = Modifier.height(8.dp))
                LiquidGlassTextField(value = website, onValueChange = { website = it }, label = "Website")
                Spacer(modifier = Modifier.height(8.dp))
                LiquidGlassTextField(value = address, onValueChange = { address = it }, label = "Address")
                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    LiquidGlassTextField(value = city, onValueChange = { city = it }, label = "City", modifier = Modifier.weight(1f))
                    LiquidGlassTextField(value = pincode, onValueChange = { pincode = it }, label = "Pincode", modifier = Modifier.weight(1f))
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    LiquidGlassTextField(value = openTime, onValueChange = { openTime = it }, label = "Opening Time", modifier = Modifier.weight(1f))
                    LiquidGlassTextField(value = closeTime, onValueChange = { closeTime = it }, label = "Closing Time", modifier = Modifier.weight(1f))
                }

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
                            onSave(
                                current.copy(
                                    name = name,
                                    description = description,
                                    phone = phone,
                                    email = email,
                                    website = website,
                                    address = address,
                                    city = city,
                                    state = state,
                                    pincode = pincode,
                                    openingTime = openTime,
                                    closingTime = closeTime
                                )
                            )
                        }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "Save Changes", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                }
            }
        }
    }
}
