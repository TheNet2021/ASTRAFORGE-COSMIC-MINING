package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.EquipmentSystem
import com.example.model.RelicType
import com.example.ui.components.formatLargeNumber
import com.example.ui.engine.EquipmentVisualCanvas
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import com.example.ui.components.SciFiButton
import com.example.ui.components.SciFiButtonStyle
import com.example.viewmodel.GameUiState
import com.example.viewmodel.OfflineRewardSummary

@Composable
fun SettingsDialog(
    uiState: GameUiState,
    onDismiss: () -> Unit,
    onSetVolume: (Float) -> Unit,
    onSetMusicVolume: (Float) -> Unit = {},
    onToggleMute: () -> Unit,
    onToggleVibration: () -> Unit = {},
    onSetGraphicsQuality: (String) -> Unit = {},
    onToggleReducedAnimations: () -> Unit = {},
    onToggleShake: () -> Unit,
    onToggleBatterySaver: () -> Unit,
    onResetGame: () -> Unit
) {
    var showConfirmReset by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color(0xFF0A0F1D),
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = (LocalConfiguration.current.screenHeightDp * 0.88f).dp)
                .border(1.dp, Color(0xFF00F0FF).copy(alpha = 0.5f), RoundedCornerShape(16.dp))
        ) {
            Column(
                modifier = Modifier
                    .padding(18.dp)
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "⚙️ SYSTEM CONFIGURATION",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF00F0FF),
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "v2.5 // PROD",
                        fontSize = 10.sp,
                        color = Color(0xFF64748B),
                        fontFamily = FontFamily.Monospace
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Section 1: Audio Systems
                SettingsSubheader("AUDIO SYSTEMS")
                
                // Sound Effects Volume Slider
                Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Sound FX Volume", fontSize = 11.sp, color = Color.White)
                        Text(
                            "${(uiState.soundVolume * 100).toInt()}%",
                            fontSize = 11.sp,
                            color = Color(0xFF38BDF8),
                            fontFamily = FontFamily.Monospace
                        )
                    }
                    Slider(
                        value = uiState.soundVolume,
                        onValueChange = onSetVolume,
                        valueRange = 0f..1f,
                        colors = SliderDefaults.colors(
                            thumbColor = Color(0xFF00F0FF),
                            activeTrackColor = Color(0xFF00F0FF)
                        ),
                        modifier = Modifier.testTag("volume_slider")
                    )
                }

                // Music Volume Slider
                Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Music Volume", fontSize = 11.sp, color = Color.White)
                        Text(
                            "${(uiState.musicVolume * 100).toInt()}%",
                            fontSize = 11.sp,
                            color = Color(0xFFA855F7),
                            fontFamily = FontFamily.Monospace
                        )
                    }
                    Slider(
                        value = uiState.musicVolume,
                        onValueChange = onSetMusicVolume,
                        valueRange = 0f..1f,
                        colors = SliderDefaults.colors(
                            thumbColor = Color(0xFFA855F7),
                            activeTrackColor = Color(0xFFA855F7)
                        ),
                        modifier = Modifier.testTag("music_slider")
                    )
                }

                SettingToggleRow(
                    label = "Mute All Audio",
                    checked = uiState.isMuted,
                    onCheckedChange = { onToggleMute() },
                    testTag = "setting_mute_toggle"
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Section 2: Haptics & Feedback
                SettingsSubheader("FEEDBACK & SENSORS")

                SettingToggleRow(
                    label = "Haptic Vibration",
                    checked = uiState.vibrationEnabled,
                    onCheckedChange = { onToggleVibration() },
                    testTag = "setting_vibration_toggle"
                )

                SettingToggleRow(
                    label = "Screen Shake Impact",
                    checked = uiState.screenShakeEnabled,
                    onCheckedChange = { onToggleShake() },
                    testTag = "setting_shake_toggle"
                )

                SettingToggleRow(
                    label = "Reduced Animations",
                    checked = uiState.reducedAnimations,
                    onCheckedChange = { onToggleReducedAnimations() },
                    testTag = "setting_reduced_anim_toggle"
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Section 3: Graphics & Performance
                SettingsSubheader("GRAPHICS & POWER")

                // Graphics Quality Selector
                Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Text("Graphics Quality Preset", fontSize = 11.sp, color = Color.White)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("ULTRA", "HIGH", "BALANCED", "LOW").forEach { q ->
                            val isSelected = uiState.graphicsQuality == q
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isSelected) Color(0xFF0284C7) else Color(0xFF1E293B))
                                    .border(
                                        1.dp,
                                        if (isSelected) Color(0xFF00F0FF) else Color(0xFF334155),
                                        RoundedCornerShape(6.dp)
                                    )
                                    .clickable { onSetGraphicsQuality(q) }
                                    .padding(vertical = 6.dp)
                                    .testTag("graphics_preset_$q"),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = q,
                                    fontSize = 9.sp,
                                    fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
                                    color = if (isSelected) Color.White else Color.LightGray,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    }
                }

                SettingToggleRow(
                    label = "Battery Saver Mode (20 FPS)",
                    checked = uiState.batterySaverEnabled,
                    onCheckedChange = { onToggleBatterySaver() },
                    testTag = "setting_battery_toggle"
                )

                Spacer(modifier = Modifier.height(10.dp))

                Spacer(modifier = Modifier.height(12.dp))

                // Section 5: Lifetime Statistics
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF070B14))
                        .border(1.dp, Color(0xFF334155), RoundedCornerShape(10.dp))
                        .padding(10.dp)
                ) {
                    Column {
                        Text(
                            text = "EXPEDITION LIFETIME STATS",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFFFFD700),
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 0.8.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Total Taps: ${uiState.statsTotalTaps}", fontSize = 10.sp, color = Color.White)
                            Text("Ores Mined: ${formatLargeNumber(uiState.statsTotalOresMined)}", fontSize = 10.sp, color = Color.White)
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Credits Earned: ₡${formatLargeNumber(uiState.statsTotalCreditsEarned)}", fontSize = 10.sp, color = Color(0xFF38BDF8))
                            Text("Ascensions: ${uiState.ascensionCount}", fontSize = 10.sp, color = Color(0xFFA855F7))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Section 6: Danger Zone (Reset Progress)
                if (!showConfirmReset) {
                    SciFiButton(
                        text = "RESET PROGRESSION DATA",
                        iconEmoji = "⚠️",
                        onClick = { showConfirmReset = true },
                        style = SciFiButtonStyle.DANGER,
                        testTag = "reset_game_prompt_button",
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0x33EF4444))
                            .border(1.dp, Color(0xFFEF4444), RoundedCornerShape(10.dp))
                            .padding(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "WARNING: RESET CANNOT BE UNDONE!",
                            fontSize = 11.sp,
                            color = Color(0xFFEF4444),
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            "This permanently clears equipment, planets, inventory, and drones.",
                            fontSize = 9.sp,
                            color = Color.White,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            SciFiButton(
                                text = "YES, PURGE SAVE",
                                onClick = {
                                    onResetGame()
                                    showConfirmReset = false
                                    onDismiss()
                                },
                                style = SciFiButtonStyle.DANGER,
                                testTag = "confirm_reset_button",
                                modifier = Modifier.weight(1f)
                            )
                            SciFiButton(
                                text = "CANCEL",
                                onClick = { showConfirmReset = false },
                                style = SciFiButtonStyle.SECONDARY,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Close Button
                SciFiButton(
                    text = "SAVE & CLOSE",
                    onClick = onDismiss,
                    style = SciFiButtonStyle.PRIMARY,
                    testTag = "close_settings_button",
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun SettingsSubheader(title: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(width = 3.dp, height = 10.dp)
                .clip(RoundedCornerShape(1.dp))
                .background(Color(0xFF00F0FF))
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = title,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF00F0FF),
            fontFamily = FontFamily.Monospace,
            letterSpacing = 1.sp
        )
    }
}

@Composable
fun SettingToggleRow(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit, testTag: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, fontSize = 11.sp, color = Color.White)
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color(0xFF00F0FF),
                checkedTrackColor = Color(0xFF00F0FF).copy(alpha = 0.5f)
            ),
            modifier = Modifier.testTag(testTag)
        )
    }
}


@Composable
fun UpgradeCelebrationDialog(
    newLevel: Int,
    animationTimeMs: Long,
    onDismiss: () -> Unit
) {
    val config = EquipmentSystem.getEquipmentVisualConfig(newLevel)
    val stats = EquipmentSystem.getEquipmentStats(newLevel)

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(18.dp),
            color = Color(0xFF070A12),
            modifier = Modifier
                .fillMaxWidth()
                .border(2.dp, Color(config.era.eraColor), RoundedCornerShape(18.dp))
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "TRANSFORMATION COMPLETE",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF00F0FF),
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 1.2.sp
                )

                Text(
                    text = "RIG UPGRADED TO LVL $newLevel",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )

                Text(
                    text = config.era.title.uppercase(),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(config.era.eraColor)
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Canvas showing the newly evolved rig
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF0F172A))
                ) {
                    EquipmentVisualCanvas(
                        visualConfig = config,
                        isMiningActive = true,
                        animationTimeMs = animationTimeMs,
                        overrideEfficiency = config.baseEfficiencyRating,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Efficiency Rating: ${stats.efficiencyPercent}% | Auto DPS: +${formatLargeNumber(stats.autoMiningRate)}/s",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF34D399),
                    fontFamily = FontFamily.Monospace,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00F0FF)),
                    modifier = Modifier.fillMaxWidth().height(44.dp).testTag("celebration_continue_button")
                ) {
                    Text("RESUME EXTRACTION", fontSize = 12.sp, fontWeight = FontWeight.Black, color = Color.Black)
                }
            }
        }
    }
}

@Composable
fun OfflineRewardsDialog(
    summary: OfflineRewardSummary,
    onClaim: () -> Unit
) {
    val mins = summary.elapsedSeconds / 60
    val hours = mins / 60
    val displayTime = if (hours > 0) "${hours}h ${mins % 60}m" else "${mins}m"

    Dialog(onDismissRequest = onClaim) {
        Surface(
            shape = RoundedCornerShape(18.dp),
            color = Color(0xFF0F172A),
            modifier = Modifier
                .fillMaxWidth()
                .border(2.dp, Color(0xFF34D399), RoundedCornerShape(18.dp))
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "🛰️ AUTONOMOUS FLEET REPORT",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF34D399),
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 1.sp
                )

                Text(
                    text = "WELCOME BACK, COMMANDER",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )

                Text(
                    text = "Automated drones extracted resources while offline for $displayTime.",
                    fontSize = 11.sp,
                    color = Color.LightGray,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Gains summary
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF070A12))
                        .padding(12.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "OFFLINE YIELD:",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF00F0FF),
                            fontFamily = FontFamily.Monospace
                        )

                        Text(
                            text = "+₡${formatLargeNumber(summary.creditsEarned)} Credits",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF38BDF8),
                            fontFamily = FontFamily.Monospace
                        )

                        summary.resourcesEarned.forEach { (res, amt) ->
                            Text(
                                text = "+${formatLargeNumber(amt)} ${res.symbol} ${res.displayName}",
                                fontSize = 12.sp,
                                color = Color(res.primaryColor),
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onClaim,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF34D399)),
                    modifier = Modifier.fillMaxWidth().height(44.dp).testTag("claim_offline_rewards_button")
                ) {
                    Text("CLAIM BOUNTY", fontSize = 12.sp, fontWeight = FontWeight.Black, color = Color.Black)
                }
            }
        }
    }
}

@Composable
fun AscensionDialog(
    uiState: GameUiState,
    onAscend: () -> Unit,
    onDismiss: () -> Unit
) {
    val shardsToEarn = ((uiState.equipmentLevel * 2L) + uiState.playerLevel + uiState.unlockedPlanets.size).coerceAtLeast(1L)

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(18.dp),
            color = Color(0xFF070A12),
            modifier = Modifier
                .fillMaxWidth()
                .border(2.dp, Color(0xFFA855F7), RoundedCornerShape(18.dp))
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "⚡ COSMIC ASCENSION",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFA855F7),
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 1.sp
                )

                Text(
                    text = "REBIRTH IN THE SUPERNOVA",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Sacrifice your current equipment, planets, and fleet to absorb the primordial energy of the cosmos. Gain permanent Stellar Shards that multiply all future mining output forever!",
                    fontSize = 11.sp,
                    color = Color.LightGray,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(14.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF1E1B4B))
                        .border(1.dp, Color(0xFFA855F7), RoundedCornerShape(10.dp))
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "+$shardsToEarn STELLAR SHARDS",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFFFFD700),
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "Next Permanent Multiplier: +${((uiState.ascensionCount + 1) * 50)}%",
                            fontSize = 11.sp,
                            color = Color(0xFFC084FC)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF334155)),
                        modifier = Modifier.weight(1f).height(42.dp)
                    ) {
                        Text("CANCEL", fontSize = 11.sp, color = Color.White)
                    }

                    Button(
                        onClick = {
                            onAscend()
                            onDismiss()
                        },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFA855F7)),
                        modifier = Modifier.weight(1.3f).height(42.dp).testTag("confirm_ascend_button")
                    ) {
                        Text("ASCEND NOW", fontSize = 11.sp, fontWeight = FontWeight.Black, color = Color.White)
                    }
                }
            }
        }
    }
}
