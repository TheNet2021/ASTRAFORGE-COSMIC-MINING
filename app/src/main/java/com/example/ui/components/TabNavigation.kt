package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class NavigationTabItem(
    val index: Int,
    val label: String,
    val iconEmoji: String,
    val testTag: String
)

val NAV_TABS = listOf(
    NavigationTabItem(0, "Mine", "⛏️", "tab_mine"),
    NavigationTabItem(1, "Equipment", "⚙️", "tab_equipment"),
    NavigationTabItem(2, "Planets", "🪐", "tab_planets"),
    NavigationTabItem(3, "Inventory", "🎒", "tab_inventory"),
    NavigationTabItem(4, "Empire", "🛰️", "tab_empire")
)

@Composable
fun TabNavigation(
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit,
    canUpgradeEquipment: Boolean,
    modifier: Modifier = Modifier
) {
    Surface(
        color = Color(0xF0070B14),
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(listOf(Color(0xFF00F0FF).copy(alpha = 0.35f), Color.Transparent)),
                shape = androidx.compose.ui.graphics.RectangleShape
            )
            .navigationBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 6.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NAV_TABS.forEach { tab ->
                val isSelected = tab.index == selectedTabIndex
                val activeColor = Color(0xFF00F0FF)

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(54.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (isSelected) activeColor.copy(alpha = 0.16f) else Color.Transparent
                        )
                        .clickable { onTabSelected(tab.index) }
                        .padding(horizontal = 2.dp, vertical = 4.dp)
                        .testTag(tab.testTag),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box {
                            Text(
                                text = tab.iconEmoji,
                                fontSize = 18.sp
                            )
                            // Badge notification for affordable equipment upgrade
                            if (tab.index == 1 && canUpgradeEquipment) {
                                Box(
                                    modifier = Modifier
                                        .size(7.dp)
                                        .align(Alignment.TopEnd)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(Color(0xFFFFD700))
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(2.dp))

                        Text(
                            text = tab.label,
                            fontSize = 10.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) activeColor else Color.LightGray
                        )
                    }
                }
            }
        }
    }
}
