package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.EfficiencyModule
import com.example.model.EquipmentEra
import com.example.model.EquipmentSystem
import com.example.ui.components.formatLargeNumber
import com.example.ui.engine.EquipmentVisualCanvas
import com.example.viewmodel.GameUiState

@Composable
fun EquipmentScreen(
    uiState: GameUiState,
    onUpgradeEquipment: () -> Unit,
    modifier: Modifier = Modifier
) {
    var previewLevel by remember(uiState.equipmentLevel) { mutableIntStateOf(uiState.equipmentLevel) }
    val isPreviewingCurrent = previewLevel == uiState.equipmentLevel
    val displayConfig = EquipmentSystem.getEquipmentVisualConfig(previewLevel)
    val displayStats = EquipmentSystem.getEquipmentStats(previewLevel)

    val currentStats = EquipmentSystem.getEquipmentStats(uiState.equipmentLevel)
    val nextStats = EquipmentSystem.getEquipmentStats(uiState.equipmentLevel + 1)
    val upgradeCost = uiState.nextUpgradeCost

    val hasCredits = uiState.credits >= upgradeCost.creditCost
    val resCount = uiState.inventory[upgradeCost.requiredResourceType] ?: 0L
    val hasResources = resCount >= upgradeCost.requiredResourceAmount
    val canUpgrade = hasCredits && hasResources && uiState.equipmentLevel < 1000

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF070A12))
            .padding(horizontal = 14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(6.dp))

            // Header Banner
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "EQUIPMENT WORKSHOP",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF00F0FF),
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.2.sp
                    )
                    Text(
                        text = "Level ${uiState.equipmentLevel} / 1000",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(displayConfig.era.eraColor).copy(alpha = 0.2f))
                        .border(1.dp, Color(displayConfig.era.eraColor), RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = displayConfig.era.title.uppercase(),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(displayConfig.era.eraColor)
                    )
                }
            }
        }

        // Live Equipment Rig Viewport Canvas
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0xFF0F172A), Color(0xFF070A12))
                        )
                    )
                    .border(1.dp, Color(displayConfig.era.eraColor).copy(alpha = 0.4f), RoundedCornerShape(16.dp))
            ) {
                EquipmentVisualCanvas(
                    visualConfig = displayConfig,
                    isMiningActive = false,
                    animationTimeMs = uiState.animationTimeMs,
                    overrideEfficiency = if (isPreviewingCurrent) uiState.effectiveEfficiency else displayConfig.baseEfficiencyRating,
                    isOverdrive = isPreviewingCurrent && uiState.isOverdriveActive,
                    modifier = Modifier.fillMaxSize()
                )

                // Inspecting indicator badge
                val previewEff = if (isPreviewingCurrent) uiState.effectiveEfficiency else displayConfig.baseEfficiencyRating
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(10.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xDD070B14))
                        .border(1.dp, Color(0xFF38BDF8).copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "⚙️ EFF: ${kotlin.math.round(previewEff).toInt()}%",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF00F0FF),
                            fontFamily = FontFamily.Monospace
                        )
                        val unlockedMods = EfficiencyModule.getUnlockedModules(previewEff)
                        if (unlockedMods.isNotEmpty()) {
                            Spacer(modifier = Modifier.width(4.dp))
                            unlockedMods.forEach { mod ->
                                Text(text = mod.iconEmoji, fontSize = 9.sp)
                            }
                        }
                    }
                }

                if (!isPreviewingCurrent) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(10.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xDD000000))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "PREVIEWING LVL $previewLevel",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFFD700),
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Button(
                        onClick = { previewLevel = uiState.equipmentLevel },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(10.dp)
                            .height(30.dp),
                        shape = RoundedCornerShape(6.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF334155))
                    ) {
                        Text("Reset to Current", fontSize = 10.sp, color = Color.White)
                    }
                }
            }
        }

        // Interactive 1,001 Level Scrubber / Inspector
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF0F172A))
                    .border(1.dp, Color(0x3300F0FF), RoundedCornerShape(12.dp))
                    .padding(12.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "MACHINE INSPECTOR (0 - 1000)",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF00F0FF),
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "LVL $previewLevel",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Slider(
                        value = previewLevel.toFloat(),
                        onValueChange = { previewLevel = it.toInt() },
                        valueRange = 0f..1000f,
                        colors = SliderDefaults.colors(
                            thumbColor = Color(0xFF00F0FF),
                            activeTrackColor = Color(0xFF00F0FF),
                            inactiveTrackColor = Color(0x33FFFFFF)
                        ),
                        modifier = Modifier.fillMaxWidth().testTag("equipment_level_slider")
                    )

                    // Quick Jump Era Buttons
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        val milestonePresets = listOf(0, 50, 100, 200, 350, 500, 700, 900, 1000)
                        items(milestonePresets) { lvl ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (previewLevel == lvl) Color(0xFF00F0FF) else Color(0xFF1E293B))
                                    .clickable { previewLevel = lvl }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "Lvl $lvl",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (previewLevel == lvl) Color.Black else Color.LightGray
                                )
                            }
                        }
                    }
                }
            }
        }

        // Upgrade Card (Current vs Next Level Delta)
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFF0F172A))
                    .border(1.dp, Color(0xFF00F0FF).copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                    .padding(14.dp)
            ) {
                Column {
                    Text(
                        text = "UPGRADE SPECIFICATIONS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF00F0FF),
                        fontFamily = FontFamily.Monospace
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Stats Comparison Table
                    StatDeltaRow(
                        label = "Mining Tap Power",
                        currentVal = formatLargeNumber(currentStats.miningPower),
                        nextVal = formatLargeNumber(nextStats.miningPower)
                    )
                    StatDeltaRow(
                        label = "Critical Strike Chance",
                        currentVal = "${currentStats.critChance.toInt()}%",
                        nextVal = "${nextStats.critChance.toInt()}%"
                    )
                    StatDeltaRow(
                        label = "Idle Extractor Rate",
                        currentVal = "+${formatLargeNumber(currentStats.autoMiningRate)}/s",
                        nextVal = "+${formatLargeNumber(nextStats.autoMiningRate)}/s"
                    )
                    StatDeltaRow(
                        label = "Drill Efficiency",
                        currentVal = "${currentStats.efficiencyPercent}%",
                        nextVal = "${nextStats.efficiencyPercent}%"
                    )
                    StatDeltaRow(
                        label = "Rare Mineral Discovery",
                        currentVal = "+${currentStats.rareFindBonus}%",
                        nextVal = "+${nextStats.rareFindBonus}%"
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Resource Cost Requirements
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "REQUIREMENTS",
                                fontSize = 9.sp,
                                color = Color.Gray,
                                fontFamily = FontFamily.Monospace
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "₡ ${formatLargeNumber(upgradeCost.creditCost)}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (hasCredits) Color(0xFF38BDF8) else Color(0xFFEF4444),
                                    fontFamily = FontFamily.Monospace
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "${upgradeCost.requiredResourceType.symbol} $resCount / ${upgradeCost.requiredResourceAmount}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (hasResources) Color(0xFF34D399) else Color(0xFFEF4444),
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }

                        Button(
                            onClick = onUpgradeEquipment,
                            enabled = canUpgrade,
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF00F0FF),
                                disabledContainerColor = Color(0xFF1E293B)
                            ),
                            modifier = Modifier.testTag("upgrade_equipment_button")
                        ) {
                            Text(
                                text = if (uiState.equipmentLevel >= 1000) "MAX LEVEL" else "FORGE UPGRADE",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black,
                                color = if (canUpgrade) Color.Black else Color.Gray
                            )
                        }
                    }
                }
            }
        }

        // Technological Eras Progression List
        item {
            Text(
                text = "TECHNOLOGICAL ERAS",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF00F0FF),
                fontFamily = FontFamily.Monospace,
                letterSpacing = 1.2.sp
            )
        }

        items(EquipmentEra.entries) { era ->
            val isUnlocked = uiState.equipmentLevel >= era.minLevel
            val isCurrent = uiState.equipmentLevel in era.minLevel..era.maxLevel

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (isCurrent) Color(0xFF1E293B) else Color(0xFF0B101E))
                    .border(
                        width = 1.dp,
                        color = if (isCurrent) Color(era.eraColor) else Color(0x22FFFFFF),
                        shape = RoundedCornerShape(10.dp)
                    )
                    .clickable { previewLevel = era.minLevel }
                    .padding(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = era.title,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isUnlocked) Color(era.eraColor) else Color.Gray
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Lvl ${era.minLevel} - ${era.maxLevel}",
                                fontSize = 10.sp,
                                color = Color.LightGray,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                        Text(
                            text = era.description,
                            fontSize = 10.sp,
                            color = Color(0xFFA0AEC0),
                            maxLines = 2
                        )
                    }

                    Text(
                        text = if (isCurrent) "ACTIVE" else if (isUnlocked) "UNLOCKED" else "LOCKED",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isCurrent) Color(0xFF00F0FF) else if (isUnlocked) Color(0xFF34D399) else Color.DarkGray,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun StatDeltaRow(label: String, currentVal: String, nextVal: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            color = Color.LightGray
        )
        Row {
            Text(
                text = currentVal,
                fontSize = 11.sp,
                color = Color.White,
                fontFamily = FontFamily.Monospace
            )
            Text(
                text = " -> ",
                fontSize = 11.sp,
                color = Color.Gray
            )
            Text(
                text = nextVal,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF00F0FF),
                fontFamily = FontFamily.Monospace
            )
        }
    }
}
