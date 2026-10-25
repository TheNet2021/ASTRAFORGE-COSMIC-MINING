package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class SciFiButtonStyle {
    PRIMARY,
    SECONDARY,
    DANGER,
    ACCENT_GOLD,
    GHOST
}

@Composable
fun SciFiButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    style: SciFiButtonStyle = SciFiButtonStyle.PRIMARY,
    enabled: Boolean = true,
    iconEmoji: String? = null,
    testTag: String = "",
    minHeight: Dp = 48.dp
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(targetValue = if (isPressed) 0.96f else 1f, label = "btn_scale")

    val bgBrush = when {
        !enabled -> Brush.horizontalGradient(listOf(Color(0xFF1E293B), Color(0xFF1E293B)))
        style == SciFiButtonStyle.PRIMARY -> Brush.horizontalGradient(
            listOf(Color(0xFF0284C7), Color(0xFF0369A1))
        )
        style == SciFiButtonStyle.ACCENT_GOLD -> Brush.horizontalGradient(
            listOf(Color(0xFFD97706), Color(0xFFB45309))
        )
        style == SciFiButtonStyle.DANGER -> Brush.horizontalGradient(
            listOf(Color(0xFFDC2626), Color(0xFF991B1B))
        )
        style == SciFiButtonStyle.SECONDARY -> Brush.horizontalGradient(
            listOf(Color(0xFF0F172A), Color(0xFF1E293B))
        )
        style == SciFiButtonStyle.GHOST -> Brush.horizontalGradient(
            listOf(Color.Transparent, Color.Transparent)
        )
        else -> Brush.horizontalGradient(listOf(Color(0xFF0F172A), Color(0xFF1E293B)))
    }

    val borderColor = when {
        !enabled -> Color(0xFF334155)
        style == SciFiButtonStyle.PRIMARY -> Color(0xFF00F0FF)
        style == SciFiButtonStyle.ACCENT_GOLD -> Color(0xFFFFD700)
        style == SciFiButtonStyle.DANGER -> Color(0xFFF43F5E)
        style == SciFiButtonStyle.SECONDARY -> Color(0xFF38BDF8).copy(alpha = 0.5f)
        style == SciFiButtonStyle.GHOST -> Color(0xFF64748B).copy(alpha = 0.4f)
        else -> Color(0xFF00F0FF)
    }

    val textColor = when {
        !enabled -> Color(0xFF64748B)
        style == SciFiButtonStyle.ACCENT_GOLD -> Color(0xFFFFFAEB)
        style == SciFiButtonStyle.DANGER -> Color(0xFFFFE4E6)
        style == SciFiButtonStyle.PRIMARY -> Color.White
        else -> Color(0xFFE2E8F0)
    }

    Box(
        modifier = modifier
            .scale(scale)
            .defaultMinSize(minHeight = minHeight)
            .clip(RoundedCornerShape(10.dp))
            .background(bgBrush)
            .border(1.dp, borderColor, RoundedCornerShape(10.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                onClick = onClick
            )
            .padding(horizontal = 14.dp, vertical = 8.dp)
            .then(if (testTag.isNotEmpty()) Modifier.testTag(testTag) else Modifier),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (iconEmoji != null) {
                Text(text = iconEmoji, fontSize = 14.sp)
                Spacer(modifier = Modifier.width(6.dp))
            }
            Text(
                text = text,
                color = textColor,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 0.8.sp
            )
        }
    }
}

@Composable
fun SciFiCard(
    modifier: Modifier = Modifier,
    borderColor: Color = Color(0xFF00F0FF).copy(alpha = 0.35f),
    backgroundColor: Color = Color(0xDD0B1220),
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .border(1.dp, borderColor, RoundedCornerShape(12.dp))
            .padding(12.dp),
        content = content
    )
}

@Composable
fun SciFiSectionHeader(
    title: String,
    subtitle: String? = null,
    accentColor: Color = Color(0xFF00F0FF),
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(width = 4.dp, height = 14.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(accentColor)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = title.uppercase(),
                fontSize = 12.sp,
                fontWeight = FontWeight.Black,
                color = accentColor,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 1.2.sp
            )
        }
        if (subtitle != null) {
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                fontSize = 10.sp,
                color = Color(0xFF94A3B8),
                modifier = Modifier.padding(start = 12.dp)
            )
        }
    }
}
