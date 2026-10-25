package com.example.ui.engine

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import com.example.model.EquipmentEra
import com.example.model.EquipmentVisualConfig
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun EquipmentVisualCanvas(
    visualConfig: EquipmentVisualConfig,
    isMiningActive: Boolean,
    animationTimeMs: Long,
    modifier: Modifier = Modifier,
    overrideEfficiency: Float? = null,
    isOverdrive: Boolean = false
) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val centerX = size.width / 2f
        val centerY = size.height * 0.40f
        val baseScale = (size.width.coerceAtMost(size.height) / 320f).coerceIn(0.7f, 1.4f)

        val currentEfficiency = overrideEfficiency ?: visualConfig.baseEfficiencyRating
        val overdriveFactor = if (isOverdrive) 1.8f else 1.0f

        // Continuous automatic rotation speed accelerated by efficiency and overdrive
        val speedFactor = (if (isMiningActive) 0.007f else 0.0035f) * (currentEfficiency / 100f).coerceIn(0.8f, 2.5f) * overdriveFactor
        val rotationAngle = (animationTimeMs * speedFactor * 360f) % 360f
        val pulse = (sin((animationTimeMs * 0.004).toDouble()).toFloat() * 0.15f + 0.85f)

        drawEquipmentRig(
            config = visualConfig,
            cx = centerX,
            cy = centerY,
            scale = baseScale,
            rotationAngle = rotationAngle,
            pulse = pulse,
            isMining = isMiningActive,
            timeMs = animationTimeMs,
            efficiency = currentEfficiency,
            isOverdrive = isOverdrive
        )
    }
}

private fun DrawScope.drawEquipmentRig(
    config: EquipmentVisualConfig,
    cx: Float,
    cy: Float,
    scale: Float,
    rotationAngle: Float,
    pulse: Float,
    isMining: Boolean,
    timeMs: Long,
    efficiency: Float,
    isOverdrive: Boolean
) {
    val priColor = Color(config.primaryColor)
    val secColor = Color(config.secondaryColor)
    val glowColor = Color(config.glowColor)

    // 0. Articulated Modular Outriggers (Deployed when efficiency >= 400% or config flag)
    if (efficiency >= 400f || config.hasModularExtensions) {
        val hasQuad = efficiency >= 900f || config.hasQuadExtensions
        drawModularOutriggers(
            cx = cx,
            cy = cy,
            scale = scale,
            rotationAngle = rotationAngle,
            efficiency = efficiency,
            isMining = isMining,
            glowColor = glowColor,
            secondaryColor = secColor,
            timeMs = timeMs,
            hasQuad = hasQuad
        )
    }

    // 1. Floating Chassis Plates (Quantum and higher)
    if (config.hasFloatingPlates) {
        val floatOffset = sin((timeMs * 0.003f).toDouble()).toFloat() * 6f * scale
        drawFloatingPlates(cx, cy + floatOffset, scale, glowColor, config.microGeometryDetail)
    }

    // 2. Articulated Hydraulic Stabilizer Arms
    if (config.armCount > 0) {
        drawStabilizerArms(cx, cy, scale, config.armCount, secColor, glowColor, timeMs)
    }

    // 3. Side Pistons
    drawPistons(cx, cy, scale, config.pistonCount, priColor, secColor, timeMs, isMining)

    // 4. Main Upper Chassis Body
    drawChassisBody(cx, cy, scale, config, priColor, secColor, glowColor, timeMs)

    // 5. Procedural Glowing Heat Vents (Activated when efficiency >= 150%)
    if (efficiency >= 150f || config.hasGlowingVents) {
        drawProceduralGlowingVents(
            cx = cx,
            cy = cy,
            scale = scale,
            config = config,
            glowColor = glowColor,
            timeMs = timeMs,
            efficiency = efficiency,
            isOverdrive = isOverdrive
        )
    }

    // 6. Kinetic Planetary Gear Train (Activated when efficiency >= 250%)
    val drillY = cy + (44f * scale)
    if (efficiency >= 250f || config.hasRotatingGears) {
        val gearboxY = cy + (24f * scale)
        drawKineticGearAssembly(
            cx = cx,
            gearboxY = gearboxY,
            scale = scale,
            rotationAngle = rotationAngle,
            efficiency = efficiency,
            isMining = isMining,
            primaryColor = priColor,
            secondaryColor = secColor,
            glowColor = glowColor
        )
    }

    // 7. Central Energy Core
    drawEnergyCore(cx, cy, scale, config, glowColor, pulse)

    // 8. Rotating Gyroscopic Energy Rings (Levels 200+)
    if (config.energyRings > 0) {
        drawEnergyRings(cx, cy, scale, config.energyRings, glowColor, rotationAngle, pulse)
    }

    // 9. Rotating Multi-Stage Cutting Head & Diamond Flutes
    drawDrillAssembly(
        cx = cx,
        topY = drillY,
        scale = scale,
        config = config,
        rotationAngle = rotationAngle,
        priColor = priColor,
        secColor = secColor,
        glowColor = glowColor,
        isMining = isMining,
        pulse = pulse
    )

    // 10. Harmonic Resonance Arcs (Activated when efficiency >= 650%)
    val tipY = drillY + (68f * config.drillConeLengthFactor * scale)
    if (efficiency >= 650f || config.hasResonanceArcs) {
        drawResonanceArcs(
            cx = cx,
            drillTipY = tipY,
            scale = scale,
            glowColor = glowColor,
            timeMs = timeMs
        )
    }

    // 11. Laser Beam or Singularity Vortex
    if (config.hasSingularityVortex) {
        drawSingularityVortex(cx, tipY, scale, glowColor, timeMs, pulse)
    } else if (config.hasLaserBeam) {
        drawLaserBeam(cx, tipY, scale, glowColor, pulse, isMining)
    }
}

private fun DrawScope.drawChassisBody(
    cx: Float,
    cy: Float,
    scale: Float,
    config: EquipmentVisualConfig,
    priColor: Color,
    secColor: Color,
    glowColor: Color,
    timeMs: Long
) {
    val w = 56f * config.chassisWidthFactor * scale
    val h = 42f * scale

    // Chassis polygon
    val path = Path().apply {
        moveTo(cx - w, cy - h)
        lineTo(cx + w, cy - h)
        lineTo(cx + (w * 0.78f), cy + h)
        lineTo(cx - (w * 0.78f), cy + h)
        close()
    }

    drawPath(
        path = path,
        brush = Brush.verticalGradient(
            colors = listOf(priColor, secColor),
            startY = cy - h,
            endY = cy + h
        )
    )

    drawPath(
        path = path,
        color = glowColor.copy(alpha = 0.65f),
        style = Stroke(width = 2.5f * scale)
    )

    // Micro-detail: Heat vent fins
    val ventCount = 3 + config.microGeometryDetail
    val ventSpacing = (w * 1.4f) / (ventCount + 1)
    val ventStartY = cy - (h * 0.65f)
    for (i in 1..ventCount) {
        val vx = (cx - (w * 0.7f)) + (i * ventSpacing)
        drawLine(
            color = Color.Black.copy(alpha = 0.75f),
            start = Offset(vx, ventStartY),
            end = Offset(vx, ventStartY + (12f * scale)),
            strokeWidth = 2.2f * scale,
            cap = StrokeCap.Round
        )
    }

    // Pressure Dial Gauge Indicator
    val dialR = 8f * scale
    val dialCx = cx - (w * 0.55f)
    val dialCy = cy - (h * 0.15f)
    drawCircle(color = Color.Black, radius = dialR, center = Offset(dialCx, dialCy))
    drawCircle(color = glowColor, radius = dialR, center = Offset(dialCx, dialCy), style = Stroke(1.2f))
    val dialAngle = (sin((timeMs * 0.005).toDouble()).toFloat() * 45f) - 45f
    val needleRad = dialAngle * (PI / 180f)
    drawLine(
        color = Color(0xFFFF4500),
        start = Offset(dialCx, dialCy),
        end = Offset(dialCx + cos(needleRad).toFloat() * dialR * 0.8f, dialCy + sin(needleRad).toFloat() * dialR * 0.8f),
        strokeWidth = 1.5f * scale
    )

    // Neon Plasma Conduit Tube
    val wireAlpha = if (config.era == EquipmentEra.PRIMITIVE_SCRAP) 0.9f else 0.5f
    val wirePath = Path().apply {
        moveTo(cx - (w * 0.75f), cy)
        quadraticTo(cx - (w * 0.35f), cy + (h * 0.75f) + (config.microGeometryDetail * 1.2f), cx, cy + (h * 0.45f))
        quadraticTo(cx + (w * 0.35f), cy + (h * 0.75f) - (config.microGeometryDetail * 1.2f), cx + (w * 0.75f), cy)
    }
    drawPath(
        path = wirePath,
        color = glowColor.copy(alpha = wireAlpha),
        style = Stroke(width = 2.2f * scale, cap = StrokeCap.Round)
    )
}

private fun DrawScope.drawPistons(
    cx: Float,
    cy: Float,
    scale: Float,
    count: Int,
    priColor: Color,
    secColor: Color,
    timeMs: Long,
    isMining: Boolean
) {
    if (count <= 0) return
    val speed = if (isMining) 0.015f else 0.006f

    for (i in 0 until count) {
        val side = if (i % 2 == 0) -1f else 1f
        val verticalOffset = (i / 2) * 16f * scale
        val stroke = sin((timeMs * speed + i).toDouble()).toFloat() * 6f * scale

        val startX = cx + (side * 42f * scale)
        val startY = cy - (10f * scale) + verticalOffset
        val endX = cx + (side * (60f + stroke) * scale)

        // Cylinder base
        drawRect(
            color = secColor,
            topLeft = Offset(if (side < 0) endX else startX, startY - (4f * scale)),
            size = Size(18f * scale, 8f * scale)
        )

        // Piston shaft
        drawLine(
            color = Color.LightGray,
            start = Offset(startX, startY),
            end = Offset(endX, startY),
            strokeWidth = 3f * scale,
            cap = StrokeCap.Square
        )
    }
}

private fun DrawScope.drawStabilizerArms(
    cx: Float,
    cy: Float,
    scale: Float,
    armCount: Int,
    color: Color,
    glow: Color,
    timeMs: Long
) {
    val armsToDraw = armCount.coerceIn(1, 6)
    for (i in 0 until armsToDraw) {
        val side = if (i % 2 == 0) -1f else 1f
        val layer = i / 2
        val jointX = cx + (side * (52f + layer * 12f) * scale)
        val jointY = cy + (layer * 14f * scale)

        val extendAngle = sin((timeMs * 0.003f + i).toDouble()).toFloat() * 0.12f
        val armEndX = jointX + (side * 30f * scale * cos(extendAngle))
        val armEndY = jointY + 24f * scale

        // Segment 1
        drawLine(
            color = color,
            start = Offset(cx + (side * 32f * scale), cy),
            end = Offset(jointX, jointY),
            strokeWidth = 4f * scale,
            cap = StrokeCap.Round
        )

        // Segment 2
        drawLine(
            color = color,
            start = Offset(jointX, jointY),
            end = Offset(armEndX, armEndY),
            strokeWidth = 3f * scale,
            cap = StrokeCap.Round
        )

        // Joint knuckle
        drawCircle(
            color = glow,
            radius = 3.5f * scale,
            center = Offset(jointX, jointY)
        )
    }
}

private fun DrawScope.drawEnergyCore(
    cx: Float,
    cy: Float,
    scale: Float,
    config: EquipmentVisualConfig,
    glowColor: Color,
    pulse: Float
) {
    val coreR = config.coreGlowRadius * scale * pulse

    // Glow aura
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(glowColor.copy(alpha = 0.85f), glowColor.copy(alpha = 0.2f), Color.Transparent),
            center = Offset(cx, cy),
            radius = coreR * 1.8f
        ),
        radius = coreR * 1.8f,
        center = Offset(cx, cy)
    )

    // Inner bright core
    drawCircle(
        color = Color.White,
        radius = coreR * 0.45f,
        center = Offset(cx, cy)
    )
}

private fun DrawScope.drawEnergyRings(
    cx: Float,
    cy: Float,
    scale: Float,
    ringCount: Int,
    glowColor: Color,
    rotationAngle: Float,
    pulse: Float
) {
    for (r in 1..ringCount) {
        val rx = (40f + r * 14f) * scale
        val ry = (14f + r * 5f) * scale
        val ringAngle = if (r % 2 == 0) rotationAngle else -rotationAngle * 1.2f

        rotate(degrees = ringAngle + (r * 35f), pivot = Offset(cx, cy)) {
            drawOval(
                color = glowColor.copy(alpha = (0.5f + (pulse * 0.3f)).coerceIn(0.2f, 1f)),
                topLeft = Offset(cx - rx, cy - ry),
                size = Size(rx * 2, ry * 2),
                style = Stroke(width = (2.2f + r * 0.6f) * scale)
            )

            // Orbiting energy bead
            val beadX = cx + rx * cos(rotationAngle * (PI / 180f)).toFloat()
            val beadY = cy + ry * sin(rotationAngle * (PI / 180f)).toFloat()
            drawCircle(
                color = Color.White,
                radius = 3.8f * scale,
                center = Offset(beadX, beadY)
            )
        }
    }
}

private fun DrawScope.drawDrillAssembly(
    cx: Float,
    topY: Float,
    scale: Float,
    config: EquipmentVisualConfig,
    rotationAngle: Float,
    priColor: Color,
    secColor: Color,
    glowColor: Color,
    isMining: Boolean,
    pulse: Float
) {
    val coneWidth = 42f * config.drillConeWidthFactor * scale
    val coneLength = 68f * config.drillConeLengthFactor * scale
    val tipY = topY + coneLength

    // Multi-stage Drill Cone Outer Casing
    val conePath = Path().apply {
        moveTo(cx - (coneWidth / 2f), topY)
        lineTo(cx + (coneWidth / 2f), topY)
        lineTo(cx, tipY)
        close()
    }

    drawPath(
        path = conePath,
        brush = Brush.verticalGradient(
            colors = listOf(priColor, secColor, Color.DarkGray),
            startY = topY,
            endY = tipY
        )
    )

    drawPath(
        path = conePath,
        color = glowColor.copy(alpha = 0.75f),
        style = Stroke(width = 2.4f * scale)
    )

    // Intermediate Titanium Cutting Ribs (Horizontal rings)
    val ribCount = 3 + (config.level / 200)
    for (rib in 1..ribCount) {
        val ribY = topY + (coneLength * (rib.toFloat() / (ribCount + 1)))
        val ribWidthFraction = 1f - (rib.toFloat() / (ribCount + 1))
        val rw = (coneWidth / 2f) * ribWidthFraction
        drawLine(
            color = Color.White.copy(alpha = 0.45f),
            start = Offset(cx - rw, ribY),
            end = Offset(cx + rw, ribY),
            strokeWidth = 2f * scale
        )
    }

    // Spiral Cutting Flutes with High RPM dynamic phase
    val flutes = config.spiralFluteCount
    val rotRad = rotationAngle * (PI / 180f)
    for (f in 0 until flutes) {
        val fluteOffset = (f.toDouble() / flutes) * 2 * PI + rotRad
        val flutePhase = (sin(fluteOffset) * (coneWidth * 0.44f)).toFloat()
        val fluteY = topY + (coneLength * 0.5f) + (cos(fluteOffset) * (coneLength * 0.32f)).toFloat()

        drawLine(
            color = Color.White.copy(alpha = 0.85f),
            start = Offset(cx + flutePhase, topY + (10f * scale)),
            end = Offset(cx + (flutePhase * 0.25f), tipY - (6f * scale)),
            strokeWidth = 2.8f * scale,
            cap = StrokeCap.Round
        )
    }

    // Articulated Cutting Blades
    if (config.bladeCount > 0) {
        val bladeLen = config.bladeLength * scale
        for (b in 0 until config.bladeCount) {
            val side = if (b % 2 == 0) -1f else 1f
            val bladeY = topY + ((b + 1) * (coneLength / (config.bladeCount + 2)))
            val bladeX = cx + (side * (coneWidth * 0.38f))

            drawLine(
                color = glowColor,
                start = Offset(bladeX, bladeY),
                end = Offset(bladeX + (side * bladeLen), bladeY + (8f * scale)),
                strokeWidth = 3.2f * scale,
                cap = StrokeCap.Round
            )
        }
    }

    // Rotating Diamond Tip Point
    drawCircle(
        color = Color.White,
        radius = 3.5f * scale * pulse,
        center = Offset(cx, tipY)
    )
}

private fun DrawScope.drawLaserBeam(
    cx: Float,
    tipY: Float,
    scale: Float,
    glowColor: Color,
    pulse: Float,
    isMining: Boolean
) {
    val beamLength = (if (isMining) 140f else 100f) * scale
    // Outer glow beam
    drawLine(
        color = glowColor.copy(alpha = 0.45f * pulse),
        start = Offset(cx, tipY),
        end = Offset(cx, tipY + beamLength),
        strokeWidth = 11f * scale,
        cap = StrokeCap.Round
    )
    // Core intense white laser
    drawLine(
        color = Color.White,
        start = Offset(cx, tipY),
        end = Offset(cx, tipY + beamLength),
        strokeWidth = 3.8f * scale,
        cap = StrokeCap.Round
    )
}

private fun DrawScope.drawSingularityVortex(
    cx: Float,
    tipY: Float,
    scale: Float,
    glowColor: Color,
    timeMs: Long,
    pulse: Float
) {
    val rot = (timeMs * 0.01f * 360f) % 360f
    rotate(degrees = rot, pivot = Offset(cx, tipY)) {
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color.Black, glowColor, Color.Transparent),
                center = Offset(cx, tipY),
                radius = 32f * scale * pulse
            ),
            radius = 32f * scale * pulse,
            center = Offset(cx, tipY)
        )
    }
    // Event horizon core
    drawCircle(
        color = Color.Black,
        radius = 9f * scale,
        center = Offset(cx, tipY)
    )
    drawCircle(
        color = Color(0xFFFFD700),
        radius = 10.5f * scale,
        center = Offset(cx, tipY),
        style = Stroke(width = 1.8f * scale)
    )
}

private fun DrawScope.drawFloatingPlates(
    cx: Float,
    cy: Float,
    scale: Float,
    glowColor: Color,
    microDetail: Int
) {
    val plateWidth = 20f * scale
    val plateHeight = 26f * scale
    val offset = 68f * scale + (microDetail * 1.5f)

    // Left floating shard
    drawRect(
        brush = Brush.linearGradient(listOf(glowColor.copy(alpha = 0.85f), Color.Black)),
        topLeft = Offset(cx - offset - plateWidth, cy - (plateHeight / 2f)),
        size = Size(plateWidth, plateHeight)
    )
    // Right floating shard
    drawRect(
        brush = Brush.linearGradient(listOf(glowColor.copy(alpha = 0.85f), Color.Black)),
        topLeft = Offset(cx + offset, cy - (plateHeight / 2f)),
        size = Size(plateWidth, plateHeight)
    )
}
