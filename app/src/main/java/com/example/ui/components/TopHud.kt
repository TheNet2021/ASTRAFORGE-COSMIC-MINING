package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeMute
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.model.ActiveBoost
import com.example.model.ActiveCosmicEvent
import com.example.model.GalaxyId
import com.example.model.PlanetData

@Composable
fun TopHud(
    currentPlanet: PlanetData,
    galaxy: GalaxyId,
    credits: Long,
    darkMatter: Long,
    energy: Int,
    playerLevel: Int,
    playerXp: Long,
    xpRequired: Long,
    activeBoosts: List<ActiveBoost>,
    activeCosmicEvent: ActiveCosmicEvent?,
    isMuted: Boolean,
    onToggleMute: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenAscension: () -> Unit,
    onOpenAstraAi: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = Color(0xDD070B14),
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(listOf(Color(0xFF00F0FF).copy(alpha = 0.3f), Color.Transparent)),
                shape = androidx.compose.ui.graphics.RectangleShape
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 6.dp)
        ) {
            // Row 1: Planet, Galaxy & System Controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(Color(currentPlanet.atmosphereColor))
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "${galaxy.title.uppercase()} // SEC ${currentPlanet.sector.toString().padStart(2, '0')}",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF00F0FF),
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 1.2.sp
                        )
                    }
                    Text(
                        text = "Planet ${currentPlanet.number}: ${currentPlanet.name}",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        maxLines = 1
                    )
                }

                // AI Chatbot + Ascension + Mute + Settings
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // ASTRA-9 Gemini AI Operator Button
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0x3300F0FF))
                            .border(1.dp, Color(0xFF00F0FF), RoundedCornerShape(8.dp))
                            .clickable { onOpenAstraAi() }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                            .testTag("open_ai_chat_button")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "🤖 ASTRA-AI",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF00F0FF),
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // Ascension button
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0x33A855F7))
                            .border(1.dp, Color(0xFFA855F7), RoundedCornerShape(8.dp))
                            .clickable { onOpenAscension() }
                            .padding(horizontal = 7.dp, vertical = 4.dp)
                            .testTag("ascension_button")
                    ) {
                        Text(
                            text = "⚡ ASCEND",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFFC084FC)
                        )
                    }

                    IconButton(
                        onClick = onToggleMute,
                        modifier = Modifier.size(34.dp).testTag("mute_toggle_button")
                    ) {
                        Icon(
                            imageVector = if (isMuted) Icons.AutoMirrored.Filled.VolumeMute else Icons.AutoMirrored.Filled.VolumeUp,
                            contentDescription = "Sound Toggle",
                            tint = if (isMuted) Color.Gray else Color(0xFF00F0FF),
                            modifier = Modifier.size(17.dp)
                        )
                    }

                    IconButton(
                        onClick = onOpenSettings,
                        modifier = Modifier.size(34.dp).testTag("settings_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = Color.White,
                            modifier = Modifier.size(17.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Row 2: Player Level + XP Bar + Currencies
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Player Level & XP Bar
                Column(modifier = Modifier.width(110.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "LVL $playerLevel",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFFD700),
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "$playerXp/$xpRequired XP",
                            fontSize = 9.sp,
                            color = Color.LightGray
                        )
                    }
                    val xpProgress = (playerXp.toFloat() / xpRequired.coerceAtLeast(1L)).coerceIn(0f, 1f)
                    LinearProgressIndicator(
                        progress = { xpProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp)),
                        color = Color(0xFFFFD700),
                        trackColor = Color(0x33FFFFFF)
                    )
                }

                // Currency Badges
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    CurrencyBadge(symbol = "₡", amount = formatLargeNumber(credits), color = Color(0xFF38BDF8))
                    if (darkMatter > 0L) {
                        CurrencyBadge(symbol = "Ω", amount = darkMatter.toString(), color = Color(0xFFA855F7))
                    }
                    CurrencyBadge(symbol = "⚡", amount = "$energy W", color = Color(0xFFFFD700))
                }
            }

            // Row 3: Cosmic Event Banner (if active)
            activeCosmicEvent?.let { event ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(event.type.colorHex).copy(alpha = 0.2f))
                        .border(1.dp, Color(event.type.colorHex), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${event.type.iconEmoji} ${event.type.title.uppercase()}: ${event.type.effectDescription}",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            maxLines = 1
                        )
                        Text(
                            text = "${event.remainingSeconds}s",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(event.type.colorHex),
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }

            // Row 4: Active Boosts Ticker (if any)
            AnimatedVisibility(visible = activeBoosts.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    activeBoosts.forEach { boost ->
                        val mins = boost.remainingSeconds / 60
                        val secs = boost.remainingSeconds % 60
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0x33FF9900))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "${boost.type.iconEmoji} ${boost.type.title}: ${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFFB74D)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CurrencyBadge(symbol: String, amount: String, color: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(color.copy(alpha = 0.15f))
            .border(1.dp, color.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
            .padding(horizontal = 7.dp, vertical = 2.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = symbol,
                fontWeight = FontWeight.Black,
                fontSize = 11.sp,
                color = color
            )
            Spacer(modifier = Modifier.width(3.dp))
            Text(
                text = amount,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                color = Color.White,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}

fun formatLargeNumber(number: Long): String {
    return when {
        number >= 1_000_000_000_000L -> String.format("%.2fT", number / 1_000_000_000_000.0)
        number >= 1_000_000_000L -> String.format("%.2fB", number / 1_000_000_000.0)
        number >= 1_000_000L -> String.format("%.2fM", number / 1_000_000.0)
        number >= 1_000L -> String.format("%.1fK", number / 1_000.0)
        else -> number.toString()
    }
}
