package com.example.ui.engine

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.lerp
import com.example.model.EquipmentVisualConfig
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Procedural animation subsystems that transform the appearance of the main drilling machine
 * as its efficiency stat crosses milestone thresholds:
 *
 * 1. Superheated Thermal Vents (Efficiency >= 150%)
 * 2. High-RPM Kinetic Gear Assembly (Efficiency >= 250%)
 * 3. Articulated Modular Outrigger Pods (Efficiency >= 400%)
 * 4. Harmonic Plasma Resonance Arcs (Efficiency >= 650%)
 * 5. Quad Monolith Matrix Overdrive (Efficiency >= 900%)
 */

fun DrawScope.drawProceduralGlowingVents(
    cx: Float,
    cy: Float,
    scale: Float,
    config: EquipmentVisualConfig,
    glowColor: Color,
    timeMs: Long,
    efficiency: Float,
    isOverdrive: Boolean
) {
    val w = 56f * config.chassisWidthFactor * scale
    val h = 42f * scale
    val heatRatio = ((efficiency - 150f) / 350f).coerceIn(0f, 1f)
    val baseHeatColor = lerp(Color(0xFFFF5722), Color(0xFF00F0FF), heatRatio)
    val overdriveBoost = if (isOverdrive) 1.5f else 1.0f

    val sides = listOf(-1f, 1f)
    for (side in sides) {
        val ventCenterX = cx + (side * (w * 0.82f))
        val ventCenterY = cy - (h * 0.15f)
        val ventW = 16f * scale
        val ventH = 28f * scale

        // 1. Glowing internal radiator chamber
        val pulse = (sin((timeMs * 0.008f + (if (side < 0) 0f else 1.5f)).toDouble()).toFloat() * 0.25f + 0.75f) * overdriveBoost
        val auraRadius = 22f * scale * pulse.coerceIn(0.8f, 1.6f)

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    baseHeatColor.copy(alpha = (0.7f * pulse).coerceIn(0.2f, 1f)),
                    baseHeatColor.copy(alpha = 0.2f),
                    Color.Transparent
                ),
                center = Offset(ventCenterX, ventCenterY),
                radius = auraRadius
            ),
            radius = auraRadius,
            center = Offset(ventCenterX, ventCenterY)
        )

        // 2. Armored louver intake/exhaust housing
        val housingTopLeft = Offset(ventCenterX - (ventW / 2f), ventCenterY - (ventH / 2f))
        drawRoundRect(
            color = Color(0xFF090D16),
            topLeft = housingTopLeft,
            size = Size(ventW, ventH),
            cornerRadius = CornerRadius(4f * scale, 4f * scale)
        )
        drawRoundRect(
            color = baseHeatColor.copy(alpha = 0.8f),
            topLeft = housingTopLeft,
            size = Size(ventW, ventH),
            cornerRadius = CornerRadius(4f * scale, 4f * scale),
            style = Stroke(width = 1.6f * scale)
        )

        // 3. Slotted angled heat louvers
        val louverCount = 4
        val louverSpacing = ventH / (louverCount + 1)
        for (i in 1..louverCount) {
            val ly = (ventCenterY - (ventH / 2f)) + (i * louverSpacing)
            val louverStartX = ventCenterX - (ventW * 0.38f)
            val louverEndX = ventCenterX + (ventW * 0.38f)

            // Inner glowing heat slot
            drawLine(
                color = Color.White.copy(alpha = (0.85f * pulse).coerceIn(0.3f, 1f)),
                start = Offset(louverStartX, ly),
                end = Offset(louverEndX, ly - (2f * scale)),
                strokeWidth = 2.4f * scale,
                cap = StrokeCap.Round
            )

            // Metallic louver fin
            drawLine(
                color = Color(0xFF1E293B),
                start = Offset(louverStartX, ly + (1.2f * scale)),
                end = Offset(louverEndX, ly - (0.8f * scale)),
                strokeWidth = 1.4f * scale,
                cap = StrokeCap.Round
            )

            // 4. Exhaust heat plumes shooting outward into space
            val plumeLength = (10f + (efficiency / 60f)) * scale * (0.75f + 0.25f * sin((timeMs * 0.015f + i).toDouble()).toFloat()) * overdriveBoost
            val plumeColor = if (isOverdrive) Color.White else baseHeatColor
            drawLine(
                color = plumeColor.copy(alpha = (0.55f * pulse).coerceIn(0.1f, 0.9f)),
                start = Offset(if (side < 0) louverStartX else louverEndX, ly),
                end = Offset(if (side < 0) louverStartX - plumeLength else louverEndX + plumeLength, ly - (4f * scale)),
                strokeWidth = 2f * scale,
                cap = StrokeCap.Round
            )
        }
    }
}

fun DrawScope.drawKineticGearAssembly(
    cx: Float,
    gearboxY: Float,
    scale: Float,
    rotationAngle: Float,
    efficiency: Float,
    isMining: Boolean,
    primaryColor: Color,
    secondaryColor: Color,
    glowColor: Color
) {
    // Gearbox mounting chassis bracket
    val bracketW = 68f * scale
    val bracketH = 18f * scale
    drawRoundRect(
        color = Color(0xFF0D131F),
        topLeft = Offset(cx - (bracketW / 2f), gearboxY - (bracketH / 2f)),
        size = Size(bracketW, bracketH),
        cornerRadius = CornerRadius(6f * scale, 6f * scale)
    )
    drawRoundRect(
        color = glowColor.copy(alpha = 0.5f),
        topLeft = Offset(cx - (bracketW / 2f), gearboxY - (bracketH / 2f)),
        size = Size(bracketW, bracketH),
        cornerRadius = CornerRadius(6f * scale, 6f * scale),
        style = Stroke(width = 1.5f * scale)
    )

    // Dynamic gear spin speed proportional to efficiency and mining activity
    val speedMult = ((efficiency / 120f).coerceIn(1f, 4f)) * (if (isMining) 1.5f else 0.8f)
    val gearAngle = (rotationAngle * speedMult) % 360f

    // Center driving gear (10 teeth, radius = 15f * scale)
    val centerR = 15f * scale
    drawSpurGear(
        centerX = cx,
        centerY = gearboxY,
        radius = centerR,
        toothCount = 10,
        rotationDegrees = gearAngle,
        gearBodyColor = primaryColor,
        gearRimColor = Color(0xFF94A3B8),
        accentColor = glowColor,
        scale = scale
    )

    // Left counter-rotating spur gear (7 teeth, radius = 11f * scale)
    val leftCx = cx - (23.5f * scale)
    val leftR = 11f * scale
    val leftAngle = -(gearAngle * (10f / 7f) + 18f) % 360f
    drawSpurGear(
        centerX = leftCx,
        centerY = gearboxY,
        radius = leftR,
        toothCount = 7,
        rotationDegrees = leftAngle,
        gearBodyColor = secondaryColor,
        gearRimColor = Color(0xFFCBD5E1),
        accentColor = glowColor,
        scale = scale
    )

    // Right counter-rotating spur gear (7 teeth, radius = 11f * scale)
    val rightCx = cx + (23.5f * scale)
    val rightR = 11f * scale
    val rightAngle = -(gearAngle * (10f / 7f) - 18f) % 360f
    drawSpurGear(
        centerX = rightCx,
        centerY = gearboxY,
        radius = rightR,
        toothCount = 7,
        rotationDegrees = rightAngle,
        gearBodyColor = secondaryColor,
        gearRimColor = Color(0xFFCBD5E1),
        accentColor = glowColor,
        scale = scale
    )
}

fun DrawScope.drawSpurGear(
    centerX: Float,
    centerY: Float,
    radius: Float,
    toothCount: Int,
    rotationDegrees: Float,
    gearBodyColor: Color,
    gearRimColor: Color,
    accentColor: Color,
    scale: Float
) {
    rotate(degrees = rotationDegrees, pivot = Offset(centerX, centerY)) {
        val toothLength = radius * 0.35f
        val toothWidth = (2f * PI.toFloat() * radius / toothCount) * 0.5f

        // Cut gear teeth around circumference
        for (i in 0 until toothCount) {
            val angleRad = (i.toFloat() / toothCount) * 2f * PI.toFloat()
            val cosA = cos(angleRad)
            val sinA = sin(angleRad)

            val rootX = centerX + (radius * 0.82f) * cosA
            val rootY = centerY + (radius * 0.82f) * sinA
            val tipX = centerX + (radius + toothLength) * cosA
            val tipY = centerY + (radius + toothLength) * sinA

            drawLine(
                color = gearRimColor,
                start = Offset(rootX, rootY),
                end = Offset(tipX, tipY),
                strokeWidth = toothWidth,
                cap = StrokeCap.Square
            )
        }

        // Solid gear body disk
        drawCircle(
            color = gearBodyColor,
            radius = radius,
            center = Offset(centerX, centerY)
        )

        // Outer gear rim ring
        drawCircle(
            color = gearRimColor,
            radius = radius,
            center = Offset(centerX, centerY),
            style = Stroke(width = 1.6f * scale)
        )

        // 3 Weight-reduction holes
        val cutoutR = radius * 0.20f
        val cutoutDist = radius * 0.52f
        for (h in 0 until 3) {
            val holeAngle = (h * 120f) * (PI.toFloat() / 180f)
            val hx = centerX + cutoutDist * cos(holeAngle)
            val hy = centerY + cutoutDist * sin(holeAngle)
            drawCircle(
                color = Color(0xFF080D1A),
                radius = cutoutR,
                center = Offset(hx, hy)
            )
        }

        // Heavy center hub
        drawCircle(
            color = Color(0xFF1E293B),
            radius = radius * 0.38f,
            center = Offset(centerX, centerY)
        )
        drawCircle(
            color = accentColor,
            radius = radius * 0.18f,
            center = Offset(centerX, centerY)
        )
        drawCircle(
            color = Color.White,
            radius = radius * 0.08f,
            center = Offset(centerX, centerY)
        )
    }
}

fun DrawScope.drawModularOutriggers(
    cx: Float,
    cy: Float,
    scale: Float,
    rotationAngle: Float,
    efficiency: Float,
    isMining: Boolean,
    glowColor: Color,
    secondaryColor: Color,
    timeMs: Long,
    hasQuad: Boolean
) {
    val sides = listOf(-1f, 1f)
    for (side in sides) {
        // Articulation vibration & telescoping
        val vibe = if (isMining) (sin((timeMs * 0.025f + side).toDouble()).toFloat() * 1.8f * scale) else 0f
        val deployProgress = ((efficiency - 400f) / 200f).coerceIn(0.5f, 1.0f)

        val mountX = cx + (side * 44f * scale)
        val mountY = cy + (12f * scale)
        val elbowX = cx + (side * (66f * deployProgress) * scale)
        val elbowY = cy + (34f * scale) + vibe
        val podX = cx + (side * (86f * deployProgress) * scale)
        val podY = cy + (64f * scale) + vibe

        // 1. Telescoping heavy titanium support boom
        drawLine(
            color = Color(0xFF334155),
            start = Offset(mountX, mountY),
            end = Offset(elbowX, elbowY),
            strokeWidth = 4.5f * scale,
            cap = StrokeCap.Round
        )
        drawLine(
            color = secondaryColor,
            start = Offset(elbowX, elbowY),
            end = Offset(podX, podY),
            strokeWidth = 3.8f * scale,
            cap = StrokeCap.Round
        )

        // Articulated hydraulic piston cylinder
        val pistonStartX = mountX + (side * 6f * scale)
        val pistonStartY = mountY + (10f * scale)
        drawLine(
            color = Color.LightGray,
            start = Offset(pistonStartX, pistonStartY),
            end = Offset(elbowX, elbowY + (4f * scale)),
            strokeWidth = 2.2f * scale,
            cap = StrokeCap.Round
        )

        // Joint knuckle bearings
        drawCircle(color = glowColor, radius = 3.6f * scale, center = Offset(elbowX, elbowY))
        drawCircle(color = Color.White, radius = 1.6f * scale, center = Offset(elbowX, elbowY))

        // 2. Auxiliary Cutting Pod
        drawAuxiliaryCuttingPod(
            podX = podX,
            podY = podY,
            scale = scale,
            rotationAngle = rotationAngle,
            glowColor = glowColor,
            isMining = isMining
        )

        // If Quad Monolith Matrix (>= 900%), draw upper orbital outriggers
        if (hasQuad) {
            val upperElbowX = cx + (side * 72f * scale)
            val upperElbowY = cy - (18f * scale) + vibe
            val upperPodX = cx + (side * 94f * scale)
            val upperPodY = cy - (36f * scale) + vibe

            drawLine(
                color = Color(0xFF475569),
                start = Offset(mountX, mountY - (14f * scale)),
                end = Offset(upperElbowX, upperElbowY),
                strokeWidth = 3.8f * scale,
                cap = StrokeCap.Round
            )
            drawLine(
                color = secondaryColor,
                start = Offset(upperElbowX, upperElbowY),
                end = Offset(upperPodX, upperPodY),
                strokeWidth = 3.2f * scale,
                cap = StrokeCap.Round
            )
            drawCircle(color = glowColor, radius = 3.2f * scale, center = Offset(upperElbowX, upperElbowY))

            // Upper tachyon emitter node
            drawUpperTachyonNode(
                podX = upperPodX,
                podY = upperPodY,
                scale = scale,
                glowColor = glowColor,
                timeMs = timeMs
            )
        }
    }
}

private fun DrawScope.drawAuxiliaryCuttingPod(
    podX: Float,
    podY: Float,
    scale: Float,
    rotationAngle: Float,
    glowColor: Color,
    isMining: Boolean
) {
    val podW = 16f * scale
    val podH = 26f * scale

    // Pod body housing
    drawRoundRect(
        color = Color(0xFF0F172A),
        topLeft = Offset(podX - (podW / 2f), podY),
        size = Size(podW, podH),
        cornerRadius = CornerRadius(4f * scale, 4f * scale)
    )
    drawRoundRect(
        color = glowColor.copy(alpha = 0.7f),
        topLeft = Offset(podX - (podW / 2f), podY),
        size = Size(podW, podH),
        cornerRadius = CornerRadius(4f * scale, 4f * scale),
        style = Stroke(width = 1.4f * scale)
    )

    // Pod core indicator
    drawCircle(
        color = glowColor,
        radius = 2.5f * scale,
        center = Offset(podX, podY + (8f * scale))
    )

    // Spinning auxiliary micro-drill bit
    val auxTipY = podY + podH + (14f * scale)
    val auxRot = (-rotationAngle * 2.2f) * (PI.toFloat() / 180f)
    val fluteSpread = sin(auxRot) * (podW * 0.45f)

    // Auxiliary drill cone
    val microConePath = Path().apply {
        moveTo(podX - (podW * 0.45f), podY + podH)
        lineTo(podX + (podW * 0.45f), podY + podH)
        lineTo(podX, auxTipY)
        close()
    }
    drawPath(path = microConePath, color = Color(0xFF64748B))
    drawPath(path = microConePath, color = glowColor.copy(alpha = 0.6f), style = Stroke(width = 1.2f * scale))

    // Auxiliary cutting flute
    drawLine(
        color = Color.White,
        start = Offset(podX + fluteSpread, podY + podH + (2f * scale)),
        end = Offset(podX + (fluteSpread * 0.2f), auxTipY),
        strokeWidth = 1.6f * scale,
        cap = StrokeCap.Round
    )

    // Auxiliary diamond tip point
    drawCircle(color = Color.White, radius = 2f * scale, center = Offset(podX, auxTipY))

    // If mining, auxiliary sparks & micro-beam
    if (isMining) {
        val beamLen = 22f * scale
        drawLine(
            color = glowColor.copy(alpha = 0.4f),
            start = Offset(podX, auxTipY),
            end = Offset(podX, auxTipY + beamLen),
            strokeWidth = 3.5f * scale,
            cap = StrokeCap.Round
        )
        drawLine(
            color = Color.White,
            start = Offset(podX, auxTipY),
            end = Offset(podX, auxTipY + beamLen),
            strokeWidth = 1.2f * scale,
            cap = StrokeCap.Round
        )
    }
}

private fun DrawScope.drawUpperTachyonNode(
    podX: Float,
    podY: Float,
    scale: Float,
    glowColor: Color,
    timeMs: Long
) {
    val nodeR = 10f * scale
    val pulse = sin((timeMs * 0.007f).toDouble()).toFloat() * 0.2f + 0.8f

    drawCircle(
        color = Color(0xFF0F172A),
        radius = nodeR,
        center = Offset(podX, podY)
    )
    drawCircle(
        color = glowColor.copy(alpha = 0.8f * pulse),
        radius = nodeR,
        center = Offset(podX, podY),
        style = Stroke(width = 1.6f * scale)
    )
    drawCircle(
        color = Color.White,
        radius = 3f * scale * pulse,
        center = Offset(podX, podY)
    )
}

fun DrawScope.drawResonanceArcs(
    cx: Float,
    drillTipY: Float,
    scale: Float,
    glowColor: Color,
    timeMs: Long
) {
    val leftPodTip = Offset(cx - (86f * scale), drillTipY - (10f * scale))
    val rightPodTip = Offset(cx + (86f * scale), drillTipY - (10f * scale))
    val centerTip = Offset(cx, drillTipY)

    // Draw lightning arc from left outrigger to center drill tip
    drawLightningBranch(
        start = leftPodTip,
        end = centerTip,
        glowColor = glowColor,
        scale = scale,
        timeMs = timeMs,
        seed = 17
    )

    // Draw lightning arc from right outrigger to center drill tip
    drawLightningBranch(
        start = rightPodTip,
        end = centerTip,
        glowColor = glowColor,
        scale = scale,
        timeMs = timeMs,
        seed = 43
    )
}

private fun DrawScope.drawLightningBranch(
    start: Offset,
    end: Offset,
    glowColor: Color,
    scale: Float,
    timeMs: Long,
    seed: Int
) {
    val steps = 4
    var current = start
    val dx = (end.x - start.x) / steps
    val dy = (end.y - start.y) / steps

    for (s in 1..steps) {
        val targetX = if (s == steps) end.x else start.x + (dx * s) + (sin(((timeMs / 45) + seed * s).toDouble()).toFloat() * 7f * scale)
        val targetY = if (s == steps) end.y else start.y + (dy * s) + (cos(((timeMs / 45) + seed * s).toDouble()).toFloat() * 5f * scale)
        val next = Offset(targetX, targetY)

        // Outer glow
        drawLine(
            color = glowColor.copy(alpha = 0.45f),
            start = current,
            end = next,
            strokeWidth = 4.5f * scale,
            cap = StrokeCap.Round
        )
        // Intense electric core
        drawLine(
            color = Color.White,
            start = current,
            end = next,
            strokeWidth = 1.6f * scale,
            cap = StrokeCap.Round
        )
        current = next
    }
}
