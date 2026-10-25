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
import com.example.model.BiomeType
import com.example.model.CosmicEventType
import com.example.model.FacilityType
import com.example.model.PlanetData
import com.example.model.PlanetFacilityState
import java.util.Random
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun PlanetEnvironmentCanvas(
    planet: PlanetData,
    nodeCurrentHp: Long,
    nodeMaxHp: Long,
    animationTimeMs: Long,
    facilities: List<PlanetFacilityState> = emptyList(),
    activeEvent: CosmicEventType? = null,
    screenShakeOffsetX: Float = 0f,
    screenShakeOffsetY: Float = 0f,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        // 1. Deep Space, Nebula Clouds & Twinkling Starfield
        drawDeepSpaceAndNebulae(planet, w, h, animationTimeMs)

        // 2. 3D Shaded Orbiting Moons & Celestial Satellites
        draw3DMoonsAndSatellites(planet, facilities, w, h, animationTimeMs)

        // 3. Cosmic Events (Meteor showers, magnetic storms)
        if (activeEvent != null) {
            drawCinematicCosmicEvents(activeEvent, w, h, animationTimeMs)
        }

        // 4. Planetary Rings (Back Layer)
        if (planet.hasRings) {
            drawPlanetaryRings(planet, w, h, isFront = false)
        }

        // 5. Curved Alien Planetary Surface & Horizon
        drawCinematicPlanetSurface(planet, w, h, animationTimeMs)

        // 6. Colonized Living Infrastructure on the Horizon
        drawLivingInfrastructure(facilities, w, h, animationTimeMs)

        // 7. Planetary Rings (Front Layer)
        if (planet.hasRings) {
            drawPlanetaryRings(planet, w, h, isFront = true)
        }

        // 8. Ground Machine Anchors (Hydraulic landing legs planted onto the rock surface)
        drawMachineGroundAnchors(w, h, animationTimeMs)

        // 9. Active Mineral Excavation Deposit & Impact Crater (with drill contact, fracture veins & damage)
        val nodeCenterX = (w / 2f) + screenShakeOffsetX
        val nodeCenterY = (h * 0.70f) + screenShakeOffsetY
        drawCinematicMineralDeposit(
            planet = planet,
            cx = nodeCenterX,
            cy = nodeCenterY,
            currentHp = nodeCurrentHp,
            maxHp = nodeMaxHp,
            timeMs = animationTimeMs
        )
    }
}

private fun DrawScope.drawDeepSpaceAndNebulae(planet: PlanetData, w: Float, h: Float, timeMs: Long) {
    // Deep space gradient
    drawRect(
        brush = Brush.verticalGradient(
            listOf(
                Color(0xFF04060C),
                Color(0xFF070B14),
                Color(planet.skyColor).copy(alpha = 0.85f)
            ),
            startY = 0f,
            endY = h * 0.65f
        )
    )

    // Layered Nebula clouds with subtle breathing pulse
    val atmoColor = Color(planet.atmosphereColor)
    val nebPulse1 = (sin((timeMs * 0.0008).toDouble()).toFloat() * 18f + 200f)
    val nebCenter1 = Offset(w * 0.75f, h * 0.22f)
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(atmoColor.copy(alpha = 0.28f), atmoColor.copy(alpha = 0.08f), Color.Transparent),
            center = nebCenter1,
            radius = nebPulse1
        ),
        radius = nebPulse1,
        center = nebCenter1
    )

    val nebPulse2 = (sin((timeMs * 0.0006 + 2.0).toDouble()).toFloat() * 15f + 160f)
    val nebCenter2 = Offset(w * 0.20f, h * 0.15f)
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(Color(0xFFA855F7).copy(alpha = 0.18f), Color(0xFF00F0FF).copy(alpha = 0.05f), Color.Transparent),
            center = nebCenter2,
            radius = nebPulse2
        ),
        radius = nebPulse2,
        center = nebCenter2
    )

    // Seeded multi-spectral starfield
    val rng = Random(planet.number * 31337L + 7L)
    val starCount = 95
    for (i in 0 until starCount) {
        val sx = rng.nextFloat() * w
        val sy = rng.nextFloat() * (h * 0.62f)
        val sSize = rng.nextFloat() * 2.4f + 0.8f
        val twinklePhase = (sin((timeMs * 0.0022 + i).toDouble()).toFloat() * 0.4f + 0.6f).coerceIn(0.15f, 1f)

        val starColor = when (i % 6) {
            0 -> Color(0xFF38BDF8) // Type B blue
            1 -> Color(0xFFFBBF24) // Type G yellow
            2 -> Color(0xFFA855F7) // Violet pulsar
            3 -> Color(0xFF34D399) // Exotic green
            else -> Color.White
        }

        drawCircle(
            color = starColor.copy(alpha = twinklePhase),
            radius = sSize,
            center = Offset(sx, sy)
        )

        // Prominent stars have subtle cross diffraction spikes
        if (i < 8) {
            val spikeLen = sSize * 4f * twinklePhase
            drawLine(
                color = starColor.copy(alpha = twinklePhase * 0.7f),
                start = Offset(sx - spikeLen, sy),
                end = Offset(sx + spikeLen, sy),
                strokeWidth = 1f
            )
            drawLine(
                color = starColor.copy(alpha = twinklePhase * 0.7f),
                start = Offset(sx, sy - spikeLen),
                end = Offset(sx, sy + spikeLen),
                strokeWidth = 1f
            )
        }
    }
}

private fun DrawScope.draw3DMoonsAndSatellites(
    planet: PlanetData,
    facilities: List<PlanetFacilityState>,
    w: Float,
    h: Float,
    timeMs: Long
) {
    // 3D Shaded Moons with spherical shadow & surface craters
    for (m in 0 until planet.moonCount) {
        val speed = 0.00025f * (m + 1)
        val orbitRadius = (w * 0.35f) + (m * 42f)
        val angle = (timeMs * speed) + (m * 2.2f)
        val mx = (w * 0.5f) + cos(angle.toDouble()).toFloat() * orbitRadius
        val my = (h * 0.20f) + sin(angle.toDouble()).toFloat() * (orbitRadius * 0.30f)
        val moonRadius = 11f + (m * 4.5f)

        // Base lit sphere
        val moonColor = when (m) {
            0 -> Color(0xFFE2E8F0)
            1 -> Color(0xFFFED7AA)
            else -> Color(0xFFBAE6FD)
        }
        drawCircle(color = moonColor, radius = moonRadius, center = Offset(mx, my))

        // Spherical shadow crescent (3D lighting from upper left star)
        drawCircle(
            color = Color(0xCC070B14),
            radius = moonRadius * 0.95f,
            center = Offset(mx + (moonRadius * 0.35f), my - (moonRadius * 0.15f))
        )

        // Moon subtle impact craters
        drawCircle(
            color = Color.Black.copy(alpha = 0.35f),
            radius = moonRadius * 0.22f,
            center = Offset(mx - (moonRadius * 0.25f), my + (moonRadius * 0.1f))
        )
    }

    // High Orbit Station
    val hasOrbitalStation = facilities.any { it.type == FacilityType.ORBITAL_STATION && it.isBuilt }
    if (hasOrbitalStation) {
        val satAngle = (timeMs * 0.0005f)
        val satX = (w * 0.5f) + cos(satAngle.toDouble()).toFloat() * (w * 0.44f)
        val satY = (h * 0.14f) + sin(satAngle.toDouble()).toFloat() * 20f

        // Station Core
        drawCircle(color = Color(0xFF00F0FF), radius = 4f, center = Offset(satX, satY))
        drawRect(color = Color(0xFF334155), topLeft = Offset(satX - 8f, satY - 4f), size = Size(16f, 8f))
        // Solar Wing Arrays
        drawLine(color = Color(0xFF38BDF8), start = Offset(satX - 22f, satY), end = Offset(satX + 22f, satY), strokeWidth = 3f)
        // Red guidance blinker
        val beaconFlash = if ((timeMs / 450) % 2 == 0L) Color(0xFFFF3366) else Color.Transparent
        drawCircle(color = beaconFlash, radius = 2.5f, center = Offset(satX, satY - 6f))
    }
}

private fun DrawScope.drawCinematicCosmicEvents(event: CosmicEventType, w: Float, h: Float, timeMs: Long) {
    when (event) {
        CosmicEventType.METEOR_SHOWER -> {
            for (m in 0 until 6) {
                val cycle = ((timeMs + m * 650) % 2600) / 2600f
                val startX = (w * 0.08f) + (m * (w * 0.18f)) + (cycle * 250f)
                val startY = cycle * (h * 0.52f)
                drawLine(
                    brush = Brush.linearGradient(
                        listOf(Color.White, Color(0xFFFF7A00), Color(0xFFFF0055), Color.Transparent)
                    ),
                    start = Offset(startX, startY),
                    end = Offset(startX - 75f, startY - 55f),
                    strokeWidth = 3.5f,
                    cap = StrokeCap.Round
                )
                // Head plasma glow
                drawCircle(color = Color.White, radius = 2f, center = Offset(startX, startY))
            }
        }
        CosmicEventType.MAGNETIC_STORM -> {
            val waveAlpha = (sin((timeMs * 0.0035).toDouble()).toFloat() * 0.25f + 0.40f)
            val horizonY = h * 0.58f
            // Undulating aurora curtains
            val auroraPath = Path().apply {
                moveTo(0f, horizonY - 110f)
                for (step in 0..10) {
                    val x = step * (w / 10f)
                    val waveY = horizonY - 90f + sin((timeMs * 0.002 + step).toDouble()).toFloat() * 25f
                    lineTo(x, waveY)
                }
                lineTo(w, horizonY)
                lineTo(0f, horizonY)
                close()
            }
            drawPath(
                path = auroraPath,
                brush = Brush.verticalGradient(
                    listOf(
                        Color(0xFF00F0FF).copy(alpha = waveAlpha),
                        Color(0xFFA855F7).copy(alpha = waveAlpha * 0.5f),
                        Color.Transparent
                    ),
                    startY = horizonY - 110f,
                    endY = horizonY
                )
            )
        }
        else -> {}
    }
}

private fun DrawScope.drawPlanetaryRings(planet: PlanetData, w: Float, h: Float, isFront: Boolean) {
    val cx = w * 0.5f
    val cy = h * 0.80f
    val rx = w * 0.78f
    val ry = h * 0.22f
    val ringColor = Color(planet.ringColor)

    rotate(degrees = planet.ringTiltAngle, pivot = Offset(cx, cy)) {
        // Main Dense Ring Belt
        drawOval(
            brush = Brush.horizontalGradient(
                listOf(
                    ringColor.copy(alpha = if (isFront) 0.65f else 0.25f),
                    Color.White.copy(alpha = if (isFront) 0.85f else 0.35f),
                    ringColor.copy(alpha = if (isFront) 0.65f else 0.25f)
                )
            ),
            topLeft = Offset(cx - rx, cy - ry),
            size = Size(rx * 2, ry * 2),
            style = Stroke(width = 20f)
        )
        // Outer Faint Ring Belt
        drawOval(
            color = Color.White.copy(alpha = if (isFront) 0.4f else 0.15f),
            topLeft = Offset(cx - (rx * 1.15f), cy - (ry * 1.15f)),
            size = Size(rx * 2.3f, ry * 2.3f),
            style = Stroke(width = 4f)
        )
    }
}

private fun DrawScope.drawCinematicPlanetSurface(planet: PlanetData, w: Float, h: Float, timeMs: Long) {
    val horizonY = h * 0.56f
    val surfaceColor = Color(planet.surfaceColor)
    val darkColor = Color(planet.surfaceDarkColor)
    val atmoColor = Color(planet.atmosphereColor)
    val priMinColor = Color(planet.primaryMineral.primaryColor)

    // 1. Distant Mountain Silhouette along the horizon
    val mountainPath = Path().apply {
        moveTo(0f, horizonY + 12f)
        val mCount = 12
        for (i in 0..mCount) {
            val mx = i * (w / mCount)
            val peakH = if (i % 2 == 1) 28f + (i * 3f % 16f) else 10f
            val my = horizonY - peakH
            lineTo(mx, my)
        }
        lineTo(w, horizonY + 12f)
        lineTo(w, h)
        lineTo(0f, h)
        close()
    }
    drawPath(
        path = mountainPath,
        color = darkColor.copy(alpha = 0.55f)
    )

    // 2. Realistic Curved Alien Planetary Horizon Arc
    // Using a quadratic bezier to give genuine 3D curvature to the planet
    val surfacePath = Path().apply {
        moveTo(0f, horizonY + 28f)
        quadraticTo(w * 0.5f, horizonY - 48f, w, horizonY + 28f)
        lineTo(w, h)
        lineTo(0f, h)
        close()
    }

    // Planetary Strata Gradient (Deep lit horizon to dark core crust)
    drawPath(
        path = surfacePath,
        brush = Brush.verticalGradient(
            colors = listOf(
                surfaceColor,
                surfaceColor.copy(alpha = 0.95f),
                darkColor,
                Color(0xFF04070D)
            ),
            startY = horizonY - 48f,
            endY = h
        )
    )

    // 3. Brilliant Atmospheric Rim / Horizon Limb Glow
    val limbGlowPath = Path().apply {
        moveTo(0f, horizonY + 28f)
        quadraticTo(w * 0.5f, horizonY - 48f, w, horizonY + 28f)
    }
    drawPath(
        path = limbGlowPath,
        brush = Brush.horizontalGradient(
            listOf(
                atmoColor.copy(alpha = 0.2f),
                atmoColor.copy(alpha = 0.95f),
                Color.White.copy(alpha = 0.9f),
                atmoColor.copy(alpha = 0.95f),
                atmoColor.copy(alpha = 0.2f)
            )
        ),
        style = Stroke(width = 5.5f, cap = StrokeCap.Round)
    )
    drawPath(
        path = limbGlowPath,
        color = atmoColor.copy(alpha = 0.45f),
        style = Stroke(width = 16f, cap = StrokeCap.Round)
    )

    // 4. Subterranean Glowing Mineral Veins pulsing through the crust
    val veinPulse = (sin((timeMs * 0.003).toDouble()).toFloat() * 0.3f + 0.7f)
    val veinPath1 = Path().apply {
        moveTo(w * 0.15f, horizonY + 45f)
        lineTo(w * 0.28f, horizonY + 80f)
        lineTo(w * 0.42f, horizonY + 115f)
        lineTo(w * 0.50f, horizonY + 155f)
    }
    drawPath(
        path = veinPath1,
        color = priMinColor.copy(alpha = 0.65f * veinPulse),
        style = Stroke(width = 2.8f, cap = StrokeCap.Round)
    )

    val veinPath2 = Path().apply {
        moveTo(w * 0.85f, horizonY + 40f)
        lineTo(w * 0.72f, horizonY + 85f)
        lineTo(w * 0.58f, horizonY + 120f)
        lineTo(w * 0.50f, horizonY + 155f)
    }
    drawPath(
        path = veinPath2,
        color = priMinColor.copy(alpha = 0.65f * veinPulse),
        style = Stroke(width = 2.8f, cap = StrokeCap.Round)
    )

    // 5. Biome Distinctive Geology & Terrain
    val rng = Random(planet.number * 8191L)
    when (planet.biome) {
        BiomeType.VOLCANIC_MAGMA -> {
            // Molten lava fissures glowing hot
            val lavaPulse = (sin((timeMs * 0.005).toDouble()).toFloat() * 0.2f + 0.8f)
            for (f in 0 until 4) {
                val fx = (w * 0.15f) + (f * (w * 0.25f))
                drawLine(
                    brush = Brush.verticalGradient(listOf(Color(0xFFFFD700), Color(0xFFFF3300), Color(0xFF990000))),
                    start = Offset(fx, horizonY + 15f),
                    end = Offset(fx + 35f, horizonY + 130f),
                    strokeWidth = 6f * lavaPulse,
                    cap = StrokeCap.Round
                )
            }
        }
        BiomeType.CRYSTALLINE_FROST -> {
            // Prismatic crystal spires jutting from the ice
            for (s in 0 until 6) {
                val sx = 20f + (s * (w / 5.2f))
                val sh = 35f + (rng.nextFloat() * 25f)
                val spirePath = Path().apply {
                    moveTo(sx, horizonY + 45f)
                    lineTo(sx + 12f, horizonY + 45f - sh)
                    lineTo(sx + 24f, horizonY + 45f)
                    close()
                }
                drawPath(path = spirePath, color = Color(0xFFBAE6FD).copy(alpha = 0.9f))
                drawPath(path = spirePath, color = Color.White, style = Stroke(1.5f))
            }
        }
        BiomeType.ALIEN_RUINS -> {
            // Ancient Xeno-Monoliths with pulsing neon runes
            for (m in 0 until 3) {
                val mx = (w * 0.20f) + (m * (w * 0.30f))
                drawRect(color = Color(0xFF0F172A), topLeft = Offset(mx - 8f, horizonY - 15f), size = Size(16f, 55f))
                drawRect(color = Color(0xFFA855F7), topLeft = Offset(mx - 8f, horizonY - 15f), size = Size(16f, 55f), style = Stroke(1.5f))
                // Rune glyph
                drawLine(color = Color(0xFF00F0FF), start = Offset(mx, horizonY - 8f), end = Offset(mx, horizonY + 28f), strokeWidth = 2f)
            }
        }
        BiomeType.TOXIC_GAS -> {
            // Corrosive emerald pools
            for (p in 0 until 3) {
                val px = (w * 0.22f) + (p * (w * 0.32f))
                val py = horizonY + 65f + (p * 20f)
                drawOval(
                    color = Color(0xFF84CC16).copy(alpha = 0.65f),
                    topLeft = Offset(px - 28f, py - 8f),
                    size = Size(56f, 16f)
                )
            }
        }
        else -> {
            // Barren / Terrestrial: 3D Impact Craters with shadows
            for (c in 0 until 5) {
                val cx = (c * (w / 4.5f)) + 30f
                val cy = horizonY + 45f + (rng.nextFloat() * 50f)
                val cr = 16f + (rng.nextFloat() * 14f)
                // Outer illuminated lip
                drawOval(
                    color = atmoColor.copy(alpha = 0.4f),
                    topLeft = Offset(cx - cr - 2f, cy - (cr * 0.45f) - 2f),
                    size = Size((cr * 2) + 4f, (cr * 0.9f) + 4f)
                )
                // Inner crater bowl shadow
                drawOval(
                    color = Color(planet.craterColor),
                    topLeft = Offset(cx - cr, cy - (cr * 0.45f)),
                    size = Size(cr * 2, cr * 0.9f)
                )
            }
        }
    }

    // 6. Atmospheric surface haze / dust blowing across ground
    val dustPhase = (timeMs * 0.05f) % w
    for (d in 0 until 4) {
        val dy = horizonY + 30f + (d * 35f)
        val dx = (dustPhase + (d * (w / 3f))) % w
        drawLine(
            brush = Brush.horizontalGradient(
                listOf(Color.Transparent, atmoColor.copy(alpha = 0.25f), Color.Transparent)
            ),
            start = Offset(dx - 60f, dy),
            end = Offset(dx + 60f, dy),
            strokeWidth = 3f,
            cap = StrokeCap.Round
        )
    }
}

private fun DrawScope.drawLivingInfrastructure(
    facilities: List<PlanetFacilityState>,
    w: Float,
    h: Float,
    timeMs: Long
) {
    val horizonY = h * 0.56f

    // 1. Geodesic Mining Outpost on the left horizon
    val outpost = facilities.firstOrNull { it.type == FacilityType.MINING_OUTPOST && it.isBuilt }
    if (outpost != null) {
        val domeX = w * 0.16f
        val domeY = horizonY - 14f
        drawArc(
            color = Color(0xFF0F172A),
            startAngle = 180f,
            sweepAngle = 180f,
            useCenter = true,
            topLeft = Offset(domeX - 22f, domeY - 22f),
            size = Size(44f, 44f)
        )
        drawArc(
            color = Color(0xFF00F0FF),
            startAngle = 180f,
            sweepAngle = 180f,
            useCenter = false,
            topLeft = Offset(domeX - 22f, domeY - 22f),
            size = Size(44f, 44f),
            style = Stroke(2f)
        )
        // Internal warm observation light
        drawCircle(color = Color(0xFF00F0FF).copy(alpha = 0.85f), radius = 5f, center = Offset(domeX, domeY - 8f))
        // Comm Antenna mast
        drawLine(color = Color(0xFF94A3B8), start = Offset(domeX, domeY - 22f), end = Offset(domeX, domeY - 36f), strokeWidth = 2f)
        drawCircle(color = Color.Red, radius = 2f, center = Offset(domeX, domeY - 36f))
    }

    // 2. Automated Derrick Rig on the right horizon
    val boreRig = facilities.firstOrNull { it.type == FacilityType.AUTOMATED_EXTRACTOR && it.isBuilt }
    if (boreRig != null) {
        val rigX = w * 0.84f
        val rigY = horizonY - 24f
        val rigPath = Path().apply {
            moveTo(rigX - 12f, horizonY - 5f)
            lineTo(rigX, rigY)
            lineTo(rigX + 12f, horizonY - 5f)
            close()
        }
        drawPath(path = rigPath, color = Color(0xFF1E293B))
        drawPath(path = rigPath, color = Color(0xFF38BDF8), style = Stroke(1.8f))
        val beaconFlash = if ((timeMs / 400) % 2 == 0L) Color(0xFFFF3366) else Color.Transparent
        drawCircle(color = beaconFlash, radius = 3.5f, center = Offset(rigX, rigY - 2f))
    }

    // 3. Fusion Power Station
    val powerGen = facilities.firstOrNull { it.type == FacilityType.POWER_GENERATOR && it.isBuilt }
    if (powerGen != null) {
        val genX = w * 0.32f
        val genY = horizonY - 12f
        val pulse = (sin((timeMs * 0.007).toDouble()).toFloat() * 0.3f + 0.7f)
        drawCircle(color = Color(0xFFFFD700).copy(alpha = 0.35f * pulse), radius = 16f, center = Offset(genX, genY))
        drawCircle(color = Color(0xFFFFD700), radius = 7f, center = Offset(genX, genY))
    }
}

private fun DrawScope.drawMachineGroundAnchors(w: Float, h: Float, timeMs: Long) {
    val cx = w / 2f
    val surfaceY = h * 0.68f

    // Heavy hydraulic landing outriggers clamping into the rock on left and right
    val legSpread = w * 0.34f

    for (side in listOf(-1f, 1f)) {
        val legTopX = cx + (side * 48f)
        val legTopY = h * 0.44f
        val footX = cx + (side * legSpread)
        val footY = surfaceY

        // Hydraulic strut
        drawLine(
            color = Color(0xFF334155),
            start = Offset(legTopX, legTopY),
            end = Offset(footX, footY),
            strokeWidth = 6f,
            cap = StrokeCap.Round
        )
        drawLine(
            color = Color(0xFF00F0FF).copy(alpha = 0.7f),
            start = Offset(legTopX, legTopY),
            end = Offset(footX, footY),
            strokeWidth = 2f,
            cap = StrokeCap.Round
        )

        // Magnetic Ground Foot Pad
        drawOval(
            color = Color(0xFF0F172A),
            topLeft = Offset(footX - 18f, footY - 6f),
            size = Size(36f, 12f)
        )
        drawOval(
            color = Color(0xFF00F0FF),
            topLeft = Offset(footX - 18f, footY - 6f),
            size = Size(36f, 12f),
            style = Stroke(1.5f)
        )
        // Ground contact dust / friction sparks
        val sparkPhase = (sin((timeMs * 0.02 + side).toDouble()).toFloat() * 0.5f + 0.5f)
        drawCircle(
            color = Color(0xFFFFD700).copy(alpha = sparkPhase * 0.6f),
            radius = 3f,
            center = Offset(footX, footY - 2f)
        )
    }
}

private fun DrawScope.drawCinematicMineralDeposit(
    planet: PlanetData,
    cx: Float,
    cy: Float,
    currentHp: Long,
    maxHp: Long,
    timeMs: Long
) {
    val hpFraction = (currentHp.toFloat() / maxHp.coerceAtLeast(1L)).coerceIn(0f, 1f)
    val res = planet.primaryMineral
    val mineralColor = Color(res.primaryColor)
    val secColor = Color(res.secondaryColor)

    val nodeRadius = 65f

    // 1. Excavation Impact Basin (Crater bowl where drill operates)
    drawOval(
        color = Color(0xFF070B14),
        topLeft = Offset(cx - (nodeRadius * 1.8f), cy - (nodeRadius * 0.5f)),
        size = Size(nodeRadius * 3.6f, nodeRadius * 1.1f)
    )
    drawOval(
        brush = Brush.radialGradient(
            listOf(mineralColor.copy(alpha = 0.5f), Color.Transparent),
            center = Offset(cx, cy),
            radius = nodeRadius * 1.6f
        ),
        topLeft = Offset(cx - (nodeRadius * 1.8f), cy - (nodeRadius * 0.5f)),
        size = Size(nodeRadius * 3.6f, nodeRadius * 1.1f)
    )

    // 2. Glowing Superheated Mineral Core Crystals
    val pulse = (sin((timeMs * 0.004).toDouble()).toFloat() * 0.08f + 0.92f)
    val scaledR = nodeRadius * pulse

    // Central core crystal spire
    val pathCenter = Path().apply {
        moveTo(cx, cy - (scaledR * 1.25f))
        lineTo(cx + (scaledR * 0.55f), cy + (scaledR * 0.25f))
        lineTo(cx - (scaledR * 0.55f), cy + (scaledR * 0.25f))
        close()
    }
    drawPath(
        path = pathCenter,
        brush = Brush.verticalGradient(
            listOf(Color.White, mineralColor, secColor),
            startY = cy - (scaledR * 1.25f),
            endY = cy + (scaledR * 0.25f)
        )
    )
    drawPath(path = pathCenter, color = Color.White.copy(alpha = 0.75f), style = Stroke(width = 2.5f))

    // Flanking crystal clusters
    val pathLeft = Path().apply {
        moveTo(cx - (scaledR * 0.65f), cy - (scaledR * 0.85f))
        lineTo(cx - (scaledR * 0.1f), cy + (scaledR * 0.35f))
        lineTo(cx - (scaledR * 0.95f), cy + (scaledR * 0.30f))
        close()
    }
    drawPath(path = pathLeft, brush = Brush.verticalGradient(listOf(mineralColor, secColor), startY = cy - (scaledR * 0.85f), endY = cy + (scaledR * 0.35f)))

    val pathRight = Path().apply {
        moveTo(cx + (scaledR * 0.65f), cy - (scaledR * 0.85f))
        lineTo(cx + (scaledR * 0.95f), cy + (scaledR * 0.30f))
        lineTo(cx + (scaledR * 0.1f), cy + (scaledR * 0.35f))
        close()
    }
    drawPath(path = pathRight, brush = Brush.verticalGradient(listOf(mineralColor, secColor), startY = cy - (scaledR * 0.85f), endY = cy + (scaledR * 0.35f)))

    // Drill contact impact point: Molten Plasma & Extraction Shockwave
    val shockPulse = ((timeMs % 1200) / 1200f)
    drawCircle(
        color = Color.White.copy(alpha = (1f - shockPulse).coerceIn(0f, 1f)),
        radius = 8f + (shockPulse * 30f),
        center = Offset(cx, cy - (scaledR * 0.7f)),
        style = Stroke(width = 2f)
    )

    // Damage Cracks when HP decreases
    if (hpFraction < 0.92f) {
        val crackAlpha = (1f - hpFraction).coerceIn(0.2f, 0.95f)
        drawLine(
            color = Color.Black.copy(alpha = crackAlpha),
            start = Offset(cx, cy - (scaledR * 0.9f)),
            end = Offset(cx + 10f, cy - (scaledR * 0.2f)),
            strokeWidth = 3f
        )
        drawLine(
            color = Color.Black.copy(alpha = crackAlpha),
            start = Offset(cx + 10f, cy - (scaledR * 0.2f)),
            end = Offset(cx - 14f, cy + (scaledR * 0.2f)),
            strokeWidth = 2.5f
        )
    }

    // Health Bar
    val barWidth = 150f
    val barHeight = 8f
    val barX = cx - (barWidth / 2f)
    val barY = cy + (scaledR * 0.65f)

    drawRect(color = Color.Black.copy(alpha = 0.8f), topLeft = Offset(barX, barY), size = Size(barWidth, barHeight))
    drawRect(
        brush = Brush.horizontalGradient(listOf(mineralColor, Color.White)),
        topLeft = Offset(barX, barY),
        size = Size(barWidth * hpFraction, barHeight)
    )
    drawRect(color = Color.White.copy(alpha = 0.45f), topLeft = Offset(barX, barY), size = Size(barWidth, barHeight), style = Stroke(width = 1.2f))
}
