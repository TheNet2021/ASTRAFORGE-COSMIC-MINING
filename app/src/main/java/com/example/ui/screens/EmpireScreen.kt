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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.DroneType
import com.example.model.FacilityType
import com.example.model.PlanetFacilityState
import com.example.model.ShopBoostType
import com.example.model.TechNode
import com.example.model.TechNodeState
import com.example.ui.components.formatLargeNumber
import com.example.viewmodel.GameUiState

@Composable
fun EmpireScreen(
    uiState: GameUiState,
    onBuyDrone: (DroneType) -> Unit,
    onUpgradeDrone: (DroneType) -> Unit,
    onFuseDrones: (DroneType) -> Unit,
    onBuildFacility: (Int, FacilityType) -> Unit,
    onUpgradeTech: (TechNode) -> Unit,
    onBuyBoost: (ShopBoostType) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedSubTab by remember { mutableIntStateOf(0) } // 0: Drones, 1: Colony, 2: Research, 3: Shop

    val totalDronesCount = uiState.drones.sumOf { it.count }
    val totalDroneYield = uiState.drones.sumOf { it.totalYieldPerSec }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF070A12))
            .padding(horizontal = 14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
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
                        text = "FLEET & EMPIRE INFRASTRUCTURE",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF00F0FF),
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.2.sp
                    )
                    Text(
                        text = "$totalDronesCount Drones Active // +${formatLargeNumber(totalDroneYield)}/s",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF34D399)
                    )
                }

                // Sub-tabs switcher
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF0F172A))
                        .padding(2.dp)
                ) {
                    val tabs = listOf("Drones", "Colony", "Research", "Shop")
                    tabs.forEachIndexed { index, title ->
                        val isSel = selectedSubTab == index
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isSel) Color(0xFF00F0FF) else Color.Transparent)
                                .clickable { selectedSubTab = index }
                                .padding(horizontal = 7.dp, vertical = 5.dp)
                                .testTag("empire_subtab_$index")
                        ) {
                            Text(
                                text = title,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSel) Color.Black else Color.LightGray
                            )
                        }
                    }
                }
            }
        }

        when (selectedSubTab) {
            0 -> {
                // 1. MINING DRONES FLEET
                items(uiState.drones) { drone ->
                    val buyCost = drone.nextPurchaseCost
                    val upgradeCost = drone.upgradeCost
                    val canBuy = uiState.credits >= buyCost
                    val canUpgrade = uiState.credits >= upgradeCost && drone.count > 0
                    val canFuse = drone.count >= 2

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF0F172A))
                            .border(1.dp, Color(drone.type.colorHex).copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = drone.type.iconEmoji, fontSize = 22.sp)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = drone.type.displayName,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                        Text(
                                            text = "Target: ${drone.type.targetResource.symbol} ${drone.type.targetResource.displayName} (+${formatLargeNumber(drone.totalYieldPerSec)}/s)",
                                            fontSize = 10.sp,
                                            color = Color(drone.type.colorHex),
                                            fontFamily = FontFamily.Monospace
                                        )
                                    }
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = "Qty: ${drone.count}",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color.White,
                                        fontFamily = FontFamily.Monospace
                                    )
                                    Text(
                                        text = "Lvl ${drone.upgradeLevel}",
                                        fontSize = 10.sp,
                                        color = Color(0xFFFFD700),
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Action buttons: Buy +1 / Upgrade / Fuse
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Button(
                                    onClick = { onBuyDrone(drone.type) },
                                    enabled = canBuy,
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFF00F0FF),
                                        disabledContainerColor = Color(0xFF1E293B)
                                    ),
                                    modifier = Modifier.weight(1f).height(34.dp).testTag("buy_drone_${drone.type.id}")
                                ) {
                                    Text(
                                        text = "+1 (₡${formatLargeNumber(buyCost)})",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (canBuy) Color.Black else Color.Gray
                                    )
                                }

                                Button(
                                    onClick = { onUpgradeDrone(drone.type) },
                                    enabled = canUpgrade,
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFF38BDF8),
                                        disabledContainerColor = Color(0xFF1E293B)
                                    ),
                                    modifier = Modifier.weight(1f).height(34.dp).testTag("upgrade_drone_${drone.type.id}")
                                ) {
                                    Text(
                                        text = "UPG (₡${formatLargeNumber(upgradeCost)})",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (canUpgrade) Color.Black else Color.Gray
                                    )
                                }

                                Button(
                                    onClick = { onFuseDrones(drone.type) },
                                    enabled = canFuse,
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFFA855F7),
                                        disabledContainerColor = Color(0xFF1E293B)
                                    ),
                                    modifier = Modifier.weight(0.9f).height(34.dp).testTag("fuse_drone_${drone.type.id}")
                                ) {
                                    Text(
                                        text = "FUSE 2x",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Black,
                                        color = if (canFuse) Color.White else Color.Gray
                                    )
                                }
                            }
                        }
                    }
                }
            }

            1 -> {
                // 2. PLANETARY INFRASTRUCTURE ON CURRENT PLANET
                item {
                    Text(
                        text = "FACILITIES ON PLANET ${uiState.currentPlanetNumber}: ${uiState.currentPlanet.name.uppercase()}",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF00F0FF),
                        fontFamily = FontFamily.Monospace
                    )
                }

                val currentPlanetFacilities = uiState.facilities[uiState.currentPlanetNumber]
                    ?: FacilityType.entries.map { PlanetFacilityState(it, 0) }

                items(currentPlanetFacilities) { fac ->
                    val cost = fac.upgradeCost
                    val canAfford = uiState.credits >= cost

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF0F172A))
                            .border(1.dp, Color(0x3300F0FF), RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                Text(text = fac.type.iconEmoji, fontSize = 24.sp)
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = fac.type.displayName,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Text(
                                        text = fac.type.benefitLabel,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF34D399),
                                        fontFamily = FontFamily.Monospace
                                    )
                                    Text(
                                        text = fac.type.description,
                                        fontSize = 9.sp,
                                        color = Color.LightGray,
                                        maxLines = 2
                                    )
                                }
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "Lvl ${fac.level}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFFFFD700),
                                    fontFamily = FontFamily.Monospace
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Button(
                                    onClick = { onBuildFacility(uiState.currentPlanetNumber, fac.type) },
                                    enabled = canAfford,
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFF00F0FF),
                                        disabledContainerColor = Color(0xFF1E293B)
                                    ),
                                    modifier = Modifier.height(30.dp).testTag("build_facility_${fac.type.id}")
                                ) {
                                    Text(
                                        text = "₡${formatLargeNumber(cost)}",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (canAfford) Color.Black else Color.Gray
                                    )
                                }
                            }
                        }
                    }
                }
            }

            2 -> {
                // 3. RESEARCH TECH TREE
                items(uiState.researchNodes) { nodeState ->
                    val node = nodeState.node
                    val isMax = nodeState.isMaxLevel
                    val creditCost = nodeState.nextCreditCost
                    val dmCost = nodeState.nextDarkMatterCost
                    val canAffordCredits = uiState.credits >= creditCost
                    val canAffordDm = dmCost == 0L || uiState.darkMatter >= dmCost
                    val canUpgrade = !isMax && canAffordCredits && canAffordDm

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
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                    Text(text = node.iconEmoji, fontSize = 22.sp)
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = node.title,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                        Text(
                                            text = node.perkFormulaLabel(nodeState.level),
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF34D399),
                                            fontFamily = FontFamily.Monospace
                                        )
                                    }
                                }

                                Text(
                                    text = if (isMax) "MAX" else "Lvl ${nodeState.level}/${node.maxLevel}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black,
                                    color = if (isMax) Color(0xFF34D399) else Color(0xFFFFD700),
                                    fontFamily = FontFamily.Monospace
                                )
                            }

                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = node.description,
                                fontSize = 9.sp,
                                color = Color.LightGray
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val costText = if (isMax) "MAXED"
                                else if (dmCost > 0L) "₡${formatLargeNumber(creditCost)} + $dmCost Ω"
                                else "₡${formatLargeNumber(creditCost)}"

                                Text(
                                    text = "COST: $costText",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (canUpgrade) Color(0xFF00F0FF) else Color.Gray,
                                    fontFamily = FontFamily.Monospace
                                )

                                Button(
                                    onClick = { onUpgradeTech(node) },
                                    enabled = canUpgrade,
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFF00F0FF),
                                        disabledContainerColor = Color(0xFF1E293B)
                                    ),
                                    modifier = Modifier.height(32.dp).testTag("upgrade_tech_${node.id}")
                                ) {
                                    Text(
                                        text = if (isMax) "COMPLETED" else "RESEARCH",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Black,
                                        color = if (canUpgrade) Color.Black else Color.Gray
                                    )
                                }
                            }
                        }
                    }
                }
            }

            3 -> {
                // 4. SHOP & TEMPORARY BOOST CANISTERS
                items(ShopBoostType.entries) { boost ->
                    val canAffordCredits = boost.costCredits == 0L || uiState.credits >= boost.costCredits
                    val canAffordDarkMatter = boost.costDarkMatter == 0L || uiState.darkMatter >= boost.costDarkMatter
                    val canBuy = canAffordCredits && canAffordDarkMatter
                    val isAlreadyActive = uiState.activeBoosts.any { it.type == boost }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF0F172A))
                            .border(1.dp, Color(0x3300F0FF), RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                Text(text = boost.iconEmoji, fontSize = 24.sp)
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = boost.title,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Text(
                                        text = boost.description,
                                        fontSize = 10.sp,
                                        color = Color.LightGray
                                    )
                                }
                            }

                            Button(
                                onClick = { onBuyBoost(boost) },
                                enabled = canBuy && !isAlreadyActive,
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFFFD700),
                                    disabledContainerColor = Color(0xFF1E293B)
                                ),
                                modifier = Modifier.height(34.dp).testTag("shop_boost_${boost.id}")
                            ) {
                                Text(
                                    text = if (isAlreadyActive) "ACTIVE"
                                    else if (boost.costDarkMatter > 0) "${boost.costDarkMatter} Ω"
                                    else "₡${formatLargeNumber(boost.costCredits)}",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black,
                                    color = if (canBuy && !isAlreadyActive) Color.Black else Color.Gray
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
