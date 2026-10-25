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
import com.example.model.CraftingRecipe
import com.example.model.CraftingRegistry
import com.example.model.RelicType
import com.example.model.ResourceType
import com.example.ui.components.formatLargeNumber
import com.example.viewmodel.GameUiState

@Composable
fun InventoryScreen(
    uiState: GameUiState,
    onCraftRecipe: (CraftingRecipe) -> Unit,
    onClaimAchievement: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedSubTab by remember { mutableIntStateOf(0) } // 0: Vault, 1: Workshop, 2: Relics, 3: Trophies

    val totalNetWorth = uiState.inventory.entries.sumOf { (res, count) -> res.baseValue * count }

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
                        text = "CARGO HOLD & FORGE",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF00F0FF),
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.2.sp
                    )
                    Text(
                        text = "Net Value: ₡${formatLargeNumber(totalNetWorth)}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFFFFD700)
                    )
                }

                // Sub-tabs switcher
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF0F172A))
                        .padding(2.dp)
                ) {
                    val tabs = listOf("Vault", "Craft", "Relics", "Trophies")
                    tabs.forEachIndexed { index, title ->
                        val isSel = selectedSubTab == index
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isSel) Color(0xFF00F0FF) else Color.Transparent)
                                .clickable { selectedSubTab = index }
                                .padding(horizontal = 7.dp, vertical = 5.dp)
                                .testTag("inventory_subtab_$index")
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
                // VAULT: 16 MINERALS LIST
                items(ResourceType.entries) { mineral ->
                    val count = uiState.inventory[mineral] ?: 0L
                    val isDiscovered = mineral.unlockPlanet <= uiState.unlockedPlanets.maxOrNull() ?: 1

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF0F172A))
                            .border(
                                width = 1.dp,
                                color = if (isDiscovered) Color(mineral.primaryColor).copy(alpha = 0.4f) else Color(0x22FFFFFF),
                                shape = RoundedCornerShape(10.dp)
                            )
                            .padding(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(mineral.primaryColor).copy(alpha = 0.2f))
                                        .border(1.dp, Color(mineral.primaryColor), RoundedCornerShape(8.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = if (isDiscovered) mineral.symbol else "?",
                                        fontSize = 16.sp
                                    )
                                }

                                Spacer(modifier = Modifier.width(10.dp))

                                Column {
                                    Text(
                                        text = if (isDiscovered) mineral.displayName else "Undiscovered Mineral",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isDiscovered) Color.White else Color.Gray
                                    )
                                    Text(
                                        text = "${mineral.rarity.label} // Base ₡${mineral.baseValue}",
                                        fontSize = 10.sp,
                                        color = Color(mineral.rarity.colorHex),
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = formatLargeNumber(count),
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Black,
                                    color = if (count > 0) Color(0xFF00F0FF) else Color.DarkGray,
                                    fontFamily = FontFamily.Monospace
                                )
                                Text(
                                    text = "₡${formatLargeNumber(count * mineral.baseValue)}",
                                    fontSize = 9.sp,
                                    color = Color.LightGray
                                )
                            }
                        }
                    }
                }
            }

            1 -> {
                // WORKSHOP: CRAFTING COMPONENT RECIPES
                items(CraftingRegistry.RECIPES) { recipe ->
                    var canCraft = uiState.credits >= recipe.creditFee
                    recipe.inputResources.forEach { (res, req) ->
                        if ((uiState.inventory[res] ?: 0L) < req) canCraft = false
                    }
                    val currentCraftedCount = uiState.craftedComponents[recipe.id] ?: 0L

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
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = recipe.iconEmoji, fontSize = 20.sp)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = recipe.name,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                        Text(
                                            text = recipe.description,
                                            fontSize = 10.sp,
                                            color = Color.LightGray
                                        )
                                    }
                                }

                                Text(
                                    text = "Owned: $currentCraftedCount",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF38BDF8),
                                    fontFamily = FontFamily.Monospace
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Requirements
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "INPUT MATERIALS:",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Gray,
                                        fontFamily = FontFamily.Monospace
                                    )
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        recipe.inputResources.forEach { (res, req) ->
                                            val have = uiState.inventory[res] ?: 0L
                                            Text(
                                                text = "${res.symbol} $have/$req",
                                                fontSize = 10.sp,
                                                color = if (have >= req) Color(0xFF34D399) else Color(0xFFEF4444),
                                                fontFamily = FontFamily.Monospace
                                            )
                                        }
                                        Text(
                                            text = "₡${recipe.creditFee}",
                                            fontSize = 10.sp,
                                            color = if (uiState.credits >= recipe.creditFee) Color(0xFF38BDF8) else Color(0xFFEF4444)
                                        )
                                    }
                                }

                                Button(
                                    onClick = { onCraftRecipe(recipe) },
                                    enabled = canCraft,
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFF00F0FF),
                                        disabledContainerColor = Color(0xFF1E293B)
                                    ),
                                    modifier = Modifier.height(34.dp).testTag("craft_button_${recipe.id}")
                                ) {
                                    Text(
                                        text = "FORGE",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Black,
                                        color = if (canCraft) Color.Black else Color.Gray
                                    )
                                }
                            }
                        }
                    }
                }
            }

            2 -> {
                // RELICS JOURNAL: DISCOVERED COSMIC ARTIFACTS
                items(RelicType.entries) { relic ->
                    val isFound = relic in uiState.discoveredRelics

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isFound) Color(0xFF1E1B4B) else Color(0xFF0F172A))
                            .border(
                                width = 1.dp,
                                color = if (isFound) Color(0xFFA855F7) else Color(0x22FFFFFF),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .padding(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (isFound) relic.iconEmoji else "❓",
                                fontSize = 24.sp
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = if (isFound) relic.title else "Unidentified Relic",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isFound) Color.White else Color.Gray
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = relic.rarity.label,
                                        fontSize = 9.sp,
                                        color = Color(relic.rarity.colorHex),
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                                Text(
                                    text = if (isFound) relic.description else "Unearthed by deep-core drilling on high-tier planets.",
                                    fontSize = 10.sp,
                                    color = Color.LightGray
                                )
                                if (isFound) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "BONUS: ${relic.permanentBonus}",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF34D399),
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }
                        }
                    }
                }
            }

            3 -> {
                // TROPHIES & ACHIEVEMENTS
                items(uiState.achievements) { ach ->
                    val canClaim = ach.isCompleted && !ach.isClaimed

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (ach.isCompleted) Color(0xFF0F242A) else Color(0xFF0F172A))
                            .border(
                                width = 1.dp,
                                color = if (ach.isClaimed) Color(0x33FFFFFF) else if (canClaim) Color(0xFF34D399) else Color(0x22FFFFFF),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .padding(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                Text(text = ach.iconEmoji, fontSize = 24.sp)
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = ach.title,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (ach.isCompleted) Color.White else Color.Gray
                                    )
                                    Text(
                                        text = ach.description,
                                        fontSize = 10.sp,
                                        color = Color.LightGray
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    val dmRewardText = if (ach.rewardDarkMatter > 0L) " + ${ach.rewardDarkMatter} Ω" else ""
                                    Text(
                                        text = "REWARD: +₡${formatLargeNumber(ach.rewardCredits)}$dmRewardText",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFFFD700),
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }

                            Button(
                                onClick = { onClaimAchievement(ach.id) },
                                enabled = canClaim,
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF34D399),
                                    disabledContainerColor = Color(0xFF1E293B)
                                ),
                                modifier = Modifier.height(32.dp).testTag("claim_ach_${ach.id}")
                            ) {
                                Text(
                                    text = if (ach.isClaimed) "CLAIMED" else if (ach.isCompleted) "CLAIM!" else "LOCKED",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black,
                                    color = if (canClaim) Color.Black else Color.Gray
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
