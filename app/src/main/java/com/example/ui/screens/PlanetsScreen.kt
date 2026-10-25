package com.example.ui.screens

import androidx.compose.foundation.Canvas
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.GalaxyId
import com.example.model.PlanetData
import com.example.model.PlanetGenerator
import com.example.ui.components.formatLargeNumber
import com.example.viewmodel.GameUiState

@Composable
fun PlanetsScreen(
    uiState: GameUiState,
    onTravelToPlanet: (Int) -> Unit,
    onUnlockPlanet: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedGalaxy by remember { mutableStateOf(uiState.currentGalaxy) }
    var selectedSector by remember(selectedGalaxy) { mutableIntStateOf(selectedGalaxy.sectorStart) }

    val sectorStartPlanet = (selectedSector - 1) * 100 + 1
    val sectorEndPlanet = selectedSector * 100
    val planetsInSector = remember(selectedSector) {
        (sectorStartPlanet..sectorEndPlanet).map { PlanetGenerator.getPlanet(it) }
    }

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
                        text = "INTERSTELLAR PORTAL NETWORK",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF00F0FF),
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.2.sp
                    )
                    Text(
                        text = "10,000 Worlds // 5 Galaxies",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0x3300F0FF))
                        .border(1.dp, Color(0xFF00F0FF), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "${uiState.unlockedPlanets.size} / 10,000 DISCOVERED",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF00F0FF),
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }

        // 1. Galaxy Selection Tabs (5 Galaxies)
        item {
            Column {
                Text(
                    text = "SELECT GALAXY",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.LightGray,
                    fontFamily = FontFamily.Monospace
                )
                Spacer(modifier = Modifier.height(4.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(GalaxyId.entries) { gal ->
                        val isSel = gal == selectedGalaxy
                        val isAvailable = uiState.equipmentLevel >= gal.requiredEquipmentLevel

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSel) Color(gal.themeColorHex) else Color(0xFF0F172A))
                                .border(
                                    width = 1.dp,
                                    color = if (isSel) Color(gal.themeColorHex) else Color(0x33FFFFFF),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable {
                                    if (isAvailable) {
                                        selectedGalaxy = gal
                                        selectedSector = gal.sectorStart
                                    }
                                }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Column {
                                Text(
                                    text = gal.title,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSel) Color.Black else if (isAvailable) Color.White else Color.Gray
                                )
                                Text(
                                    text = if (isAvailable) "Sectors ${gal.sectorStart}-${gal.sectorEnd}" else "Requires Lvl ${gal.requiredEquipmentLevel}",
                                    fontSize = 8.sp,
                                    color = if (isSel) Color.Black.copy(alpha = 0.7f) else Color.Gray,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    }
                }
            }
        }

        // 2. Sector Selector within Galaxy
        item {
            Column {
                Text(
                    text = "${selectedGalaxy.title.uppercase()} // SECTORS",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(selectedGalaxy.themeColorHex),
                    fontFamily = FontFamily.Monospace
                )
                Spacer(modifier = Modifier.height(4.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val sectorsInGalaxy = (selectedGalaxy.sectorStart..selectedGalaxy.sectorEnd).toList()
                    items(sectorsInGalaxy) { sec ->
                        val isSel = sec == selectedSector
                        val hasUnlocked = (sec - 1) * 100 + 1 in uiState.unlockedPlanets

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isSel) Color(0xFF00F0FF) else if (hasUnlocked) Color(0xFF1E293B) else Color(0xFF0F172A))
                                .border(1.dp, if (isSel) Color(0xFF00F0FF) else Color(0x22FFFFFF), RoundedCornerShape(6.dp))
                                .clickable { selectedSector = sec }
                                .padding(horizontal = 8.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = "Sec ${sec.toString().padStart(2, '0')}",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSel) Color.Black else if (hasUnlocked) Color.White else Color.Gray,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }
        }

        // 3. Planets in Sector List
        items(planetsInSector) { planet ->
            val isCurrent = planet.number == uiState.currentPlanetNumber
            val isUnlocked = planet.number in uiState.unlockedPlanets
            val isLegendary = planet.number % 50 == 0 || planet.sector >= 95
            val hasCredits = uiState.credits >= planet.unlockCreditsCost
            val hasMineral = (uiState.inventory[planet.unlockRequiredMineral] ?: 0L) >= planet.unlockMineralAmount
            val canUnlock = hasCredits && hasMineral && !isUnlocked

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isCurrent) Color(0xFF1E293B) else Color(0xFF0F172A))
                    .border(
                        width = if (isLegendary) 1.5.dp else 1.dp,
                        color = if (isCurrent) Color(0xFF00F0FF) else if (isLegendary) Color(0xFFFFD700) else Color(0x22FFFFFF),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(12.dp)
                    .testTag("planet_card_${planet.number}")
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    PlanetMiniPreview(planet = planet, modifier = Modifier.size(54.dp))

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "#${planet.number} ${planet.name}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                            if (isLegendary) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(Color(0xFFFFD700).copy(alpha = 0.2f))
                                        .padding(horizontal = 4.dp, vertical = 1.dp)
                                ) {
                                    Text("LEGENDARY", fontSize = 8.sp, fontWeight = FontWeight.Black, color = Color(0xFFFFD700))
                                }
                            }
                        }

                        Text(
                            text = planet.biome.title,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(planet.surfaceColor)
                        )

                        Spacer(modifier = Modifier.height(2.dp))

                        Text(
                            text = "Deposit: ${planet.primaryMineral.symbol} ${planet.primaryMineral.displayName}",
                            fontSize = 10.sp,
                            color = Color.LightGray
                        )
                    }

                    if (isCurrent) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF00F0FF).copy(alpha = 0.2f))
                                .border(1.dp, Color(0xFF00F0FF), RoundedCornerShape(8.dp))
                                .padding(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "ACTIVE",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF00F0FF),
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    } else if (isUnlocked) {
                        Button(
                            onClick = { onTravelToPlanet(planet.number) },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00F0FF)),
                            modifier = Modifier.height(34.dp).testTag("travel_button_${planet.number}")
                        ) {
                            Text("WARP", fontSize = 10.sp, fontWeight = FontWeight.Black, color = Color.Black)
                        }
                    } else {
                        Button(
                            onClick = { onUnlockPlanet(planet.number) },
                            enabled = canUnlock,
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isLegendary) Color(0xFFFFD700) else Color(0xFF10B981),
                                disabledContainerColor = Color(0xFF1E293B)
                            ),
                            modifier = Modifier.height(34.dp).testTag("unlock_planet_button_${planet.number}")
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "UNLOCK",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black,
                                    color = if (canUnlock) Color.Black else Color.Gray
                                )
                                Text(
                                    text = "₡${formatLargeNumber(planet.unlockCreditsCost)} + ${planet.unlockMineralAmount}${planet.unlockRequiredMineral.symbol}",
                                    fontSize = 8.sp,
                                    color = if (canUnlock) Color.Black else Color.DarkGray
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

@Composable
fun PlanetMiniPreview(planet: PlanetData, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val cx = size.width / 2f
        val cy = size.height / 2f
        val r = size.width * 0.38f

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(planet.surfaceColor), Color(planet.surfaceDarkColor)),
                center = Offset(cx - (r * 0.3f), cy - (r * 0.3f)),
                radius = r * 1.2f
            ),
            radius = r,
            center = Offset(cx, cy)
        )

        drawCircle(
            color = Color(planet.atmosphereColor).copy(alpha = 0.5f),
            radius = r * 1.08f,
            center = Offset(cx, cy),
            style = Stroke(width = 2.5f)
        )

        if (planet.hasRings) {
            rotate(degrees = planet.ringTiltAngle, pivot = Offset(cx, cy)) {
                drawOval(
                    color = Color(planet.ringColor).copy(alpha = 0.7f),
                    topLeft = Offset(cx - (r * 1.5f), cy - (r * 0.45f)),
                    size = Size(r * 3f, r * 0.9f),
                    style = Stroke(width = 3.5f)
                )
            }
        }
    }
}
