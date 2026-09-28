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
import com.example.data.models.StaffMember
import com.example.data.services.GymOwnerRepository
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun OwnerStaffScreen(
    modifier: Modifier = Modifier
) {
    val staffList = remember { mutableStateListOf<StaffMember>().apply { addAll(GymOwnerRepository.getStaff()) } }
    var showAddDialog by remember { mutableStateOf(false) }

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
                            text = "PERSONNEL & ACCESS",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = TextSubtle,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Gym Staff Members",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextWhite
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(18.dp))
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color(0xFF6B9330), Color(0xFF4E7320))
                                )
                            )
                            .clickable { showAddDialog = true }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.PersonAdd, contentDescription = null, tint = TextWhite, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "Add Staff", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Assign roles (Gym Admin or Staff) to manage daily gym operations and check-in assistance.",
                    fontSize = 12.sp,
                    color = ByceCoolGray
                )
                Spacer(modifier = Modifier.height(6.dp))
            }

            items(staffList, key = { it.id }) { staff ->
                StaffRowCard(
                    staff = staff,
                    onToggleStatus = {
                        val index = staffList.indexOfFirst { it.id == staff.id }
                        if (index != -1) {
                            val newStatus = if (staff.status == "Active") "Inactive" else "Active"
                            staffList[index] = staff.copy(status = newStatus)
                            GymOwnerRepository.toggleStaffStatus(staff.id)
                        }
                    }
                )
            }

            item {
                Spacer(modifier = Modifier.height(40.dp))
            }
        }

        if (showAddDialog) {
            AddStaffDialog(
                onDismiss = { showAddDialog = false },
                onAdd = { newStaff ->
                    staffList.add(0, newStaff)
                    GymOwnerRepository.addStaff(newStaff)
                    showAddDialog = false
                }
            )
        }
    }
}

@Composable
private fun StaffRowCard(
    staff: StaffMember,
    onToggleStatus: () -> Unit
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
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(Color(0x20FFFFFF)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (staff.role == "Gym Admin") Icons.Default.AdminPanelSettings else Icons.Default.Badge,
                        contentDescription = null,
                        tint = TextWhite,
                        modifier = Modifier.size(20.dp)
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
                            text = staff.name,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextWhite
                        )
                        OwnerStatusBadge(status = staff.status)
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (staff.role == "Gym Admin") Color(0x3038BDF8) else Color(0x20FFFFFF))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = staff.role,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (staff.role == "Gym Admin") StatusInfoBlue else TextWhite
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Joined ${staff.createdDate}",
                            fontSize = 11.sp,
                            color = ByceCoolGray
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = staff.email, fontSize = 11.sp, color = TextMuted)
                    Text(text = staff.phone, fontSize = 11.sp, color = ByceCoolGray)
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (staff.status == "Active") StatusExpiredRedBg else StatusActiveGreenBg)
                        .border(1.dp, if (staff.status == "Active") StatusExpiredRed.copy(alpha = 0.5f) else StatusActiveGreen.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                        .clickable { onToggleStatus() }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = if (staff.status == "Active") "Deactivate" else "Activate",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (staff.status == "Active") StatusExpiredRed else StatusActiveGreen
                    )
                }
            }
        }
    }
}

@Composable
private fun AddStaffDialog(
    onDismiss: () -> Unit,
    onAdd: (StaffMember) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("Staff") } // "Gym Admin" or "Staff"

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
                Text(text = "Add Staff Member", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            LiquidGlassTextField(value = name, onValueChange = { name = it }, label = "Full Name", placeholder = "e.g. Ramesh Nair")
            Spacer(modifier = Modifier.height(8.dp))
            LiquidGlassTextField(value = email, onValueChange = { email = it }, label = "Email", placeholder = "ramesh@ironhousefitness.com")
            Spacer(modifier = Modifier.height(8.dp))
            LiquidGlassTextField(value = phone, onValueChange = { phone = it }, label = "Phone", placeholder = "+91 98470 55667")
            Spacer(modifier = Modifier.height(12.dp))

            Text(text = "Assign Role", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = TextMuted)
            Spacer(modifier = Modifier.height(6.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                listOf("Staff", "Gym Admin").forEach { r ->
                    val isSelected = role == r
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) Color(0x35FFFFFF) else Color(0x18FFFFFF))
                            .border(1.dp, if (isSelected) GlassBorderSpecular else Color.Transparent, RoundedCornerShape(12.dp))
                            .clickable { role = r }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = r,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextWhite
                        )
                    }
                }
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
                        if (name.isNotBlank()) {
                            onAdd(
                                StaffMember(
                                    id = "st_${System.currentTimeMillis()}",
                                    name = name,
                                    email = email.ifBlank { "staff@ironhousefitness.com" },
                                    phone = phone.ifBlank { "+91 98470 00000" },
                                    role = role,
                                    status = "Active",
                                    createdDate = "24 Sep 2026"
                                )
                            )
                        }
                    }
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "Add Member", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextWhite)
            }
        }
    }
}
