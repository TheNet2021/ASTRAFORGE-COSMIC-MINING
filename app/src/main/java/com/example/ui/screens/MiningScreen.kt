package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.EfficiencyModule
import com.example.model.EquipmentSystem
import com.example.ui.components.PlanetHudOverlay
import com.example.ui.components.SciFiButton
import com.example.ui.components.SciFiButtonStyle
import com.example.ui.components.formatLargeNumber
import com.example.ui.engine.EquipmentVisualCanvas
import com.example.ui.engine.ParticleManager
import com.example.ui.engine.PlanetEnvironmentCanvas
import com.example.viewmodel.GameUiState

@Composable
fun MiningScreen(
    uiState: GameUiState,
    particleManager: ParticleManager,
    onMineTap: (Offset) -> Unit,
    onNavigateToEquipment: () -> Unit,
    onNavigateToPlanets: () -> Unit,
    onOpenAiChat: () -> Unit,
    modifier: Modifier = Modifier
) {
    val stats = EquipmentSystem.getEquipmentStats(uiState.equipmentLevel)
    val totalDroneDps = uiState.drones.sumOf { it.totalYieldPerSec }
    val primeRes = uiState.currentPlanet.primaryMineral

    Box(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { offset ->
                        onMineTap(offset)
                    }
                )
            }
            .testTag("mining_surface_viewport")
    ) {
        // 1. Cinematic 3D Alien Planetary Environment Canvas
        PlanetEnvironmentCanvas(
            planet = uiState.currentPlanet,
            nodeCurrentHp = uiState.nodeCurrentHp,
            nodeMaxHp = uiState.nodeMaxHp,
            animationTimeMs = uiState.animationTimeMs,
            facilities = uiState.currentPlanetFacilities,
            activeEvent = uiState.activeCosmicEvent?.type,
            screenShakeOffsetX = uiState.screenShakeOffsetX,
            screenShakeOffsetY = uiState.screenShakeOffsetY,
            modifier = Modifier.fillMaxSize()
        )

        // 2. Active Mechanically Complex Mining Equipment Rig Canvas
        EquipmentVisualCanvas(
            visualConfig = uiState.equipmentConfig,
            isMiningActive = true,
            animationTimeMs = uiState.animationTimeMs,
            overrideEfficiency = uiState.effectiveEfficiency,
            isOverdrive = uiState.isOverdriveActive,
            modifier = Modifier.fillMaxSize()
        )

        // 3. Dynamic Particle Overlay Canvas (Sparks, floating text, mineral shards)
        val floatingTextPaint = remember {
            android.graphics.Paint().apply {
                isAntiAlias = true
                typeface = android.graphics.Typeface.MONOSPACE
            }
        }
        Canvas(modifier = Modifier.fillMaxSize()) {
            particleManager.particles.forEach { p ->
                drawCircle(
                    color = p.color.copy(alpha = p.life.coerceIn(0f, 1f)),
                    radius = p.size,
                    center = Offset(p.x, p.y)
                )
            }

            particleManager.floatingTexts.forEach { ft ->
                floatingTextPaint.textSize = if (ft.isCritical) 44f else 32f
                floatingTextPaint.color = ft.color.copy(alpha = ft.alpha).toArgb()
                floatingTextPaint.isFakeBoldText = ft.isCritical
                drawContext.canvas.nativeCanvas.drawText(
                    ft.text,
                    ft.x,
                    ft.y,
                    floatingTextPaint
                )
            }
        }

        // 4. Planet HUD Overlay (Real-time Mineral Composition, Extraction Rate & Atmosphere)
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 8.dp)
        ) {
            PlanetHudOverlay(
                planet = uiState.currentPlanet,
                drillDps = stats.autoMiningRate,
                droneDps = totalDroneDps
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Efficiency Stat & Dynamic Threshold Modules Pill
            val eff = uiState.effectiveEfficiency
            val unlockedModules = EfficiencyModule.getUnlockedModules(eff)
            val nextModule = EfficiencyModule.getNextModule(eff)

            Row(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xD0070B14))
                    .border(
                        1.dp,
                        if (uiState.isOverdriveActive) Color(0xFFFF5722) else Color(0xFF00F0FF).copy(alpha = 0.5f),
                        RoundedCornerShape(12.dp)
                    )
                    .padding(horizontal = 10.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (uiState.isOverdriveActive) "⚡ OVERDRIVE BURST" else "⚡ DRILL EFFICIENCY: ${eff.toInt()}%",
                    color = if (uiState.isOverdriveActive) Color(0xFFFF5722) else Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = FontFamily.Monospace
                )
                if (unlockedModules.isNotEmpty()) {
                    Spacer(modifier = Modifier.width(6.dp))
                    unlockedModules.forEach { mod ->
                        Text(
                            text = mod.iconEmoji,
                            fontSize = 10.sp,
                            modifier = Modifier.padding(horizontal = 1.dp)
                        )
                    }
                }
                if (nextModule != null) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "• Next: ${nextModule.badgeLabel}",
                        color = Color(0xFF94A3B8),
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }

        // 5. Bottom HUD Information, Deposit Health & Controls
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 8.dp)
        ) {
            // Active Node Health Bar Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xE6080E1A))
                    .border(
                        1.dp,
                        Brush.horizontalGradient(
                            listOf(Color(0xFF00F0FF).copy(alpha = 0.4f), Color(primeRes.primaryColor).copy(alpha = 0.6f))
                        ),
                        RoundedCornerShape(14.dp)
                    )
                    .padding(12.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = primeRes.symbol,
                                fontSize = 16.sp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "${primeRes.displayName} Core",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                        }

                        Text(
                            text = "${formatLargeNumber(uiState.nodeCurrentHp)} / ${formatLargeNumber(uiState.nodeMaxHp)} HP",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(primeRes.primaryColor),
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    val hpRatio = (uiState.nodeCurrentHp.toFloat() / uiState.nodeMaxHp.coerceAtLeast(1L)).coerceIn(0f, 1f)
                    LinearProgressIndicator(
                        progress = { hpRatio },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = Color(primeRes.primaryColor),
                        trackColor = Color(0x33FFFFFF)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Tactical Status Badges
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        StatPill(
                            label = "AUTO EXTRACTION",
                            value = "+${formatLargeNumber(stats.autoMiningRate)}/s",
                            accentColor = Color(0xFF38BDF8)
                        )
                        StatPill(
                            label = "TAP OVERDRIVE",
                            value = formatLargeNumber(stats.miningPower * 2),
                            accentColor = Color(0xFFFFD700)
                        )
                        StatPill(
                            label = "DRONE FLEET",
                            value = "+${formatLargeNumber(totalDroneDps)}/s",
                            accentColor = Color(0xFF34D399)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Action Row: Upgrade Drill / Galaxy Map / ASTRA-AI with SciFiButtons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SciFiButton(
                    text = "Lvl ${uiState.equipmentLevel} Drill",
                    iconEmoji = "⚙️",
                    onClick = onNavigateToEquipment,
                    style = SciFiButtonStyle.PRIMARY,
                    testTag = "quick_upgrade_button",
                    modifier = Modifier.weight(1.1f)
                )

                SciFiButton(
                    text = "Galaxies",
                    iconEmoji = "🪐",
                    onClick = onNavigateToPlanets,
                    style = SciFiButtonStyle.SECONDARY,
                    testTag = "quick_planets_button",
                    modifier = Modifier.weight(1.0f)
                )

                SciFiButton(
                    text = "ASTRA-AI",
                    iconEmoji = "🤖",
                    onClick = onOpenAiChat,
                    style = SciFiButtonStyle.GHOST,
                    testTag = "quick_ai_button",
                    modifier = Modifier.weight(1.0f)
                )
            }
        }
    }
}

@Composable
fun StatPill(label: String, value: String, accentColor: Color) {
    Column {
        Text(
            text = label,
            fontSize = 9.sp,
            color = Color(0xFF94A3B8),
            fontWeight = FontWeight.Medium,
            fontFamily = FontFamily.Monospace
        )
        Text(
            text = value,
            fontSize = 13.sp,
            fontWeight = FontWeight.Black,
            color = accentColor,
            fontFamily = FontFamily.Monospace
        )
    }
}
