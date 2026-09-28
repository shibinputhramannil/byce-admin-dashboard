package com.example.ui.owner

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import com.example.data.models.MembershipPlanItem
import com.example.data.services.GymOwnerRepository
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun OwnerPlansScreen(
    modifier: Modifier = Modifier
) {
    val plans = remember { mutableStateListOf<MembershipPlanItem>().apply { addAll(GymOwnerRepository.getMembershipPlans()) } }
    var showCreateDialog by remember { mutableStateOf(false) }
    var planToEdit by remember { mutableStateOf<MembershipPlanItem?>(null) }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "OFFERING TIERS",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = TextSubtle,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Membership Plans",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextWhite
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(18.dp))
                            .background(Color(0x35FFFFFF))
                            .border(1.dp, GlassBorderSpecular, RoundedCornerShape(18.dp))
                            .clickable { showCreateDialog = true }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Add, contentDescription = "Add", tint = TextWhite, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "Create Plan", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Configure pricing, perks, and availability for memberships offered at Iron House Fitness.",
                    fontSize = 12.sp,
                    color = ByceCoolGray
                )
                Spacer(modifier = Modifier.height(6.dp))
            }

            items(plans, key = { it.id }) { plan ->
                PlanCard(
                    plan = plan,
                    onToggleActive = {
                        val index = plans.indexOfFirst { it.id == plan.id }
                        if (index != -1) {
                            val updated = plan.copy(isActive = !plan.isActive)
                            plans[index] = updated
                            GymOwnerRepository.togglePlanStatus(plan.id)
                        }
                    },
                    onEditClick = { planToEdit = plan }
                )
            }

            item {
                Spacer(modifier = Modifier.height(40.dp))
            }
        }

        // Create Dialog
        if (showCreateDialog) {
            CreatePlanDialog(
                onDismiss = { showCreateDialog = false },
                onSave = { newPlan ->
                    plans.add(newPlan)
                    GymOwnerRepository.addPlan(newPlan)
                    showCreateDialog = false
                }
            )
        }

        // Edit Dialog
        planToEdit?.let { plan ->
            CreatePlanDialog(
                initialPlan = plan,
                onDismiss = { planToEdit = null },
                onSave = { updatedPlan ->
                    val index = plans.indexOfFirst { it.id == updatedPlan.id }
                    if (index != -1) {
                        plans[index] = updatedPlan
                    }
                    planToEdit = null
                }
            )
        }
    }
}

@Composable
private fun PlanCard(
    plan: MembershipPlanItem,
    onToggleActive: () -> Unit,
    onEditClick: () -> Unit
) {
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
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = plan.name,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextWhite
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        OwnerStatusBadge(status = if (plan.isActive) "Active" else "Inactive")
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = plan.description,
                        fontSize = 12.sp,
                        color = ByceCoolGray,
                        maxLines = 2
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Price Row
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = "₹${plan.price.toInt()}",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextWhite
                )
                Text(
                    text = " / ${plan.duration} ${plan.durationUnit}",
                    fontSize = 13.sp,
                    color = TextMuted,
                    modifier = Modifier.padding(bottom = 3.dp)
                )
                if (plan.discountPrice != null) {
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "₹${plan.discountPrice.toInt()}",
                        fontSize = 14.sp,
                        color = ByceCoolGray,
                        modifier = Modifier.padding(bottom = 3.dp),
                        style = androidx.compose.ui.text.TextStyle(textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color(0x18FFFFFF)))
            Spacer(modifier = Modifier.height(12.dp))

            // Features Checklist
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                plan.features.forEach { feature ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = StatusActiveGreen,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = feature, fontSize = 12.sp, color = TextWhite)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0x20FFFFFF))
                        .clickable { onEditClick() }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "Edit Plan", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextWhite)
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (plan.isActive) StatusExpiredRedBg else StatusActiveGreenBg)
                        .border(1.dp, if (plan.isActive) StatusExpiredRed.copy(alpha = 0.5f) else StatusActiveGreen.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                        .clickable { onToggleActive() }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (plan.isActive) "Deactivate" else "Activate",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (plan.isActive) StatusExpiredRed else StatusActiveGreen
                    )
                }
            }
        }
    }
}

@Composable
private fun CreatePlanDialog(
    initialPlan: MembershipPlanItem? = null,
    onDismiss: () -> Unit,
    onSave: (MembershipPlanItem) -> Unit
) {
    var name by remember { mutableStateOf(initialPlan?.name ?: "") }
    var description by remember { mutableStateOf(initialPlan?.description ?: "") }
    var durationText by remember { mutableStateOf(initialPlan?.duration?.toString() ?: "1") }
    var durationUnit by remember { mutableStateOf(initialPlan?.durationUnit ?: "Month") }
    var priceText by remember { mutableStateOf(initialPlan?.price?.toInt()?.toString() ?: "1499") }
    var featureInput by remember { mutableStateOf(initialPlan?.features?.joinToString(", ") ?: "Gym Floor Access, Locker, App Check-in") }

    OwnerModalDialog(onDismissRequest = onDismiss) {
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
                Text(
                    text = if (initialPlan != null) "Edit Plan" else "Create Membership Plan",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            LiquidGlassTextField(value = name, onValueChange = { name = it }, label = "Plan Name", placeholder = "e.g. Quarterly Pro")
            Spacer(modifier = Modifier.height(10.dp))
            LiquidGlassTextField(value = description, onValueChange = { description = it }, label = "Description", placeholder = "Plan summary")
            Spacer(modifier = Modifier.height(10.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                LiquidGlassTextField(
                    value = durationText,
                    onValueChange = { durationText = it },
                    label = "Duration",
                    placeholder = "1",
                    modifier = Modifier.weight(1f)
                )
                LiquidGlassTextField(
                    value = durationUnit,
                    onValueChange = { durationUnit = it },
                    label = "Unit",
                    placeholder = "Month(s)",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
            LiquidGlassTextField(value = priceText, onValueChange = { priceText = it }, label = "Price (₹)", placeholder = "e.g. 3999")
            Spacer(modifier = Modifier.height(10.dp))
            LiquidGlassTextField(value = featureInput, onValueChange = { featureInput = it }, label = "Features (comma separated)", placeholder = "Cardio, Steam, Locker")

            Spacer(modifier = Modifier.height(20.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(Color(0xFF6B9330), Color(0xFF4E7320))
                        )
                    )
                    .clickable {
                        val p = initialPlan ?: MembershipPlanItem(
                            id = "plan_${System.currentTimeMillis()}",
                            name = name.ifBlank { "Custom Plan" },
                            description = description,
                            duration = durationText.toIntOrNull() ?: 1,
                            durationUnit = durationUnit,
                            price = priceText.toDoubleOrNull() ?: 1499.0,
                            features = featureInput.split(",").map { it.trim() }.filter { it.isNotBlank() },
                            isActive = true
                        )
                        onSave(
                            p.copy(
                                name = name.ifBlank { "Custom Plan" },
                                description = description,
                                duration = durationText.toIntOrNull() ?: 1,
                                durationUnit = durationUnit,
                                price = priceText.toDoubleOrNull() ?: 1499.0,
                                features = featureInput.split(",").map { it.trim() }.filter { it.isNotBlank() }
                            )
                        )
                    }
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "Save Plan", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextWhite)
            }
        }
    }
}
