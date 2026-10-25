package com.example.model

import kotlin.math.pow
import kotlin.math.roundToLong

enum class EquipmentEra(
    val id: String,
    val title: String,
    val minLevel: Int,
    val maxLevel: Int,
    val description: String,
    val eraColor: Long
) {
    PRIMITIVE_SCRAP(
        id = "era_primitive",
        title = "Primitive Scrap",
        minLevel = 0,
        maxLevel = 49,
        description = "Handcrafted scrap tools, exposed cables, rattling pistons, and small drills.",
        eraColor = 0xFF94A3B8
    ),
    HEAVY_INDUSTRIAL(
        id = "era_industrial",
        title = "Heavy Industrial",
        minLevel = 50,
        maxLevel = 99,
        description = "Reinforced cast-steel frames, hydraulic shock absorbers, and hardened carbide heads.",
        eraColor = 0xFF38BDF8
    ),
    ADVANCED_EXTRACTOR(
        id = "era_advanced",
        title = "Advanced Extractor",
        minLevel = 100,
        maxLevel = 199,
        description = "Articulated robotic arms, high-RPM counter-rotating cutting blades, and luminous sensors.",
        eraColor = 0xFF34D399
    ),
    PLASMA_RIG(
        id = "era_plasma",
        title = "Plasma Drill",
        minLevel = 200,
        maxLevel = 349,
        description = "Superheated plasma arcs, magnetic containment rings, and thermal discharge fins.",
        eraColor = 0xFFF59E0B
    ),
    QUANTUM_HARVESTER(
        id = "era_quantum",
        title = "Quantum Machinery",
        minLevel = 350,
        maxLevel = 499,
        description = "Floating electromagnetic chassis plates, tachyon flux conduits, and holographic optics.",
        eraColor = 0xFFA855F7
    ),
    INTERSTELLAR_COLOSSUS(
        id = "era_colossus",
        title = "Interstellar Colossus",
        minLevel = 500,
        maxLevel = 699,
        description = "Gigantic orbital drill arrays, gyroscopic stabilizer rings, and antimatter injectors.",
        eraColor = 0xFFEC4899
    ),
    XENO_GRAVITATIONAL(
        id = "era_xeno",
        title = "Xeno-Gravitational",
        minLevel = 700,
        maxLevel = 899,
        description = "Alien biomechanical chassis, gravity-warping orbs, and exotic matter siphons.",
        eraColor = 0xFF06B6D4
    ),
    COSMIC_TRANSCENDENT(
        id = "era_cosmic",
        title = "Cosmic Transcendent",
        minLevel = 900,
        maxLevel = 999,
        description = "Forged in dying stars: celestial hyper-dimensional geometry and solar flare auras.",
        eraColor = 0xFFF43F5E
    ),
    OMEGA_ASTRAFORGE(
        id = "era_omega",
        title = "The Omega Astraforge",
        minLevel = 1000,
        maxLevel = 1000,
        description = "The ultimate celestial mining monolith: tearing through space-time horizons.",
        eraColor = 0xFFFFD700
    );

    companion object {
        fun getEraForLevel(level: Int): EquipmentEra {
            return entries.firstOrNull { level in it.minLevel..it.maxLevel } ?: OMEGA_ASTRAFORGE
        }
    }
}

enum class EfficiencyModule(
    val threshold: Float,
    val title: String,
    val badgeLabel: String,
    val iconEmoji: String,
    val description: String
) {
    GLOWING_VENTS(
        150f,
        "Superheated Thermal Vents",
        "Vents (150%)",
        "🔥",
        "Pulsing lateral plasma louvers venting excessive heat from overdrive drilling"
    ),
    ROTATING_GEARS(
        250f,
        "Kinetic Planetary Gear Train",
        "Gears (250%)",
        "⚙️",
        "Dual intermeshed spur cogs actively rotating on the transmission collar"
    ),
    MODULAR_EXTENSIONS(
        400f,
        "Articulated Outrigger Pods",
        "Outriggers (400%)",
        "🛸",
        "Twin auxiliary drilling heads deployed on telescoping hydraulic pylons"
    ),
    RESONANCE_ARCS(
        650f,
        "Harmonic Plasma Arcs",
        "Arcs (650%)",
        "⚡",
        "Electromagnetic plasma filaments arcing between outriggers and drill point"
    ),
    QUAD_MATRIX(
        900f,
        "Quad Monolith Matrix",
        "Quad Pods (900%)",
        "🌀",
        "Quad-pod celestial excavation array with localized warp distortion"
    );

    companion object {
        fun getUnlockedModules(efficiency: Float): List<EfficiencyModule> {
            return entries.filter { efficiency >= it.threshold }
        }

        fun getNextModule(efficiency: Float): EfficiencyModule? {
            return entries.firstOrNull { efficiency < it.threshold }
        }
    }
}

/**
 * Visual configuration computed deterministically for each of the 1,001 equipment levels.
 * Every single level (0 through 1,000) alters one or more geometric or visual parameters.
 */
data class EquipmentVisualConfig(
    val level: Int,
    val era: EquipmentEra,
    val drillConeLengthFactor: Float, // 0.7f to 1.8f
    val drillConeWidthFactor: Float,
    val spiralFluteCount: Int, // 2 to 8
    val bladeCount: Int, // 0 to 6
    val bladeLength: Float,
    val chassisPlateTier: Int, // 0 to 8
    val chassisWidthFactor: Float,
    val pistonCount: Int, // 0 to 4
    val armCount: Int, // 0 to 6
    val energyRings: Int, // 0 to 4
    val coreGlowRadius: Float,
    val primaryColor: Long,
    val secondaryColor: Long,
    val glowColor: Long,
    val exhaustLevel: Int, // 0 to 3
    val hasLaserBeam: Boolean,
    val hasFloatingPlates: Boolean,
    val hasHoloRings: Boolean,
    val hasSingularityVortex: Boolean,
    val microGeometryDetail: Int, // 0..9 fine micro-tweak distinct for every single level
    val baseEfficiencyRating: Float = 100f + (level * 2.5f),
    val hasGlowingVents: Boolean = (100f + (level * 2.5f)) >= 150f,
    val hasRotatingGears: Boolean = (100f + (level * 2.5f)) >= 250f,
    val hasModularExtensions: Boolean = (100f + (level * 2.5f)) >= 400f,
    val hasResonanceArcs: Boolean = (100f + (level * 2.5f)) >= 650f,
    val hasQuadExtensions: Boolean = (100f + (level * 2.5f)) >= 900f
)

data class EquipmentUpgradeCost(
    val creditCost: Long,
    val requiredResourceType: ResourceType,
    val requiredResourceAmount: Long
)

object EquipmentSystem {

    fun getEquipmentVisualConfig(level: Int): EquipmentVisualConfig {
        val clampedLevel = level.coerceIn(0, 1000)
        val era = EquipmentEra.getEraForLevel(clampedLevel)
        val micro = clampedLevel % 10
        val subEraProgress = ((clampedLevel - era.minLevel).toFloat() / (era.maxLevel - era.minLevel + 1).coerceAtLeast(1))

        val drillConeLength = 0.8f + (clampedLevel / 1000f) * 0.7f + (micro * 0.02f)
        val drillConeWidth = 0.85f + (clampedLevel / 1000f) * 0.5f + ((micro % 3) * 0.04f)
        val spiralFlutes = 2 + (clampedLevel / 150) + (micro % 2)
        val blades = when {
            clampedLevel < 50 -> 0
            clampedLevel < 100 -> 2
            clampedLevel < 350 -> 3 + (micro % 2)
            clampedLevel < 700 -> 4 + (micro % 3)
            else -> 6
        }
        val bladeLen = 12f + (clampedLevel / 50f) + (micro * 1.5f)

        val chassisTier = (clampedLevel / 125).coerceIn(0, 8)
        val chassisWidth = 1f + (clampedLevel / 1000f) * 0.4f + ((micro % 4) * 0.03f)

        val pistons = when {
            clampedLevel < 50 -> 1
            clampedLevel < 200 -> 2
            clampedLevel < 500 -> 3
            else -> 4
        }

        val arms = when {
            clampedLevel < 100 -> 0
            clampedLevel < 300 -> 2
            clampedLevel < 600 -> 4
            else -> 6
        }

        val rings = when {
            clampedLevel >= 1000 -> 4
            clampedLevel >= 700 -> 3
            clampedLevel >= 350 -> 2
            clampedLevel >= 200 -> 1
            else -> 0
        }

        val coreRadius = 14f + (clampedLevel / 35f) + (micro * 0.8f)

        // Palette transitions deterministically with level
        val (priColor, secColor, glow) = when (era) {
            EquipmentEra.PRIMITIVE_SCRAP -> {
                val rustTone = 0xFF785135L + (micro * 0x020202L)
                Triple(rustTone, 0xFF523725L, 0xFFD97706L)
            }
            EquipmentEra.HEAVY_INDUSTRIAL -> {
                val steelTone = 0xFF64748BL + (micro * 0x030303L)
                Triple(steelTone, 0xFF334155L, 0xFF38BDF8L)
            }
            EquipmentEra.ADVANCED_EXTRACTOR -> {
                Triple(0xFF1E293BL, 0xFF0F172AL, 0xFF10B981L + (micro * 0x000200L))
            }
            EquipmentEra.PLASMA_RIG -> {
                Triple(0xFF7C2D12L, 0xFF431407L, 0xFFF59E0BL + (micro * 0x000100L))
            }
            EquipmentEra.QUANTUM_HARVESTER -> {
                Triple(0xFF3B0764L, 0xFF1E1B4BL, 0xFFA855F7L + (micro * 0x000100L))
            }
            EquipmentEra.INTERSTELLAR_COLOSSUS -> {
                Triple(0xFF0F172AL, 0xFF1E293BL, 0xFF06B6D4L + (micro * 0x000100L))
            }
            EquipmentEra.XENO_GRAVITATIONAL -> {
                Triple(0xFF042F2EL, 0xFF134E4AL, 0xFF2DD4BFL + (micro * 0x000100L))
            }
            EquipmentEra.COSMIC_TRANSCENDENT -> {
                Triple(0xFF4C0519L, 0xFF1E1B4BL, 0xFFF43F5EL + (micro * 0x010000L))
            }
            EquipmentEra.OMEGA_ASTRAFORGE -> {
                Triple(0xFF0A0A0AL, 0xFF1F1F1FL, 0xFFFFD700L)
            }
        }

        return EquipmentVisualConfig(
            level = clampedLevel,
            era = era,
            drillConeLengthFactor = drillConeLength,
            drillConeWidthFactor = drillConeWidth,
            spiralFluteCount = spiralFlutes,
            bladeCount = blades,
            bladeLength = bladeLen,
            chassisPlateTier = chassisTier,
            chassisWidthFactor = chassisWidth,
            pistonCount = pistons,
            armCount = arms,
            energyRings = rings,
            coreGlowRadius = coreRadius,
            primaryColor = priColor,
            secondaryColor = secColor,
            glowColor = glow,
            exhaustLevel = (clampedLevel / 250).coerceIn(0, 3),
            hasLaserBeam = clampedLevel >= 150,
            hasFloatingPlates = clampedLevel >= 350,
            hasHoloRings = clampedLevel >= 400,
            hasSingularityVortex = clampedLevel >= 700,
            microGeometryDetail = micro
        )
    }

    fun getEquipmentStats(level: Int): EquipmentStats {
        val lvl = level.coerceIn(0, 1000)
        // Base damage scales exponentially yet predictably
        val basePower = (10.0 * 1.018.pow(lvl)).roundToLong()
        val critChance = (5.0 + (lvl / 1000.0) * 45.0).toFloat().coerceIn(5f, 50f)
        val critMultiplier = 1.5f + (lvl / 500f)
        val autoRate = (basePower * 0.25).roundToLong().coerceAtLeast(1L)
        val rareFindBonus = (lvl / 50) * 2 // +2% per 50 levels up to +40%

        return EquipmentStats(
            level = lvl,
            miningPower = basePower,
            critChance = critChance,
            critMultiplier = critMultiplier,
            autoMiningRate = autoRate,
            rareFindBonus = rareFindBonus
        )
    }

    fun getUpgradeCost(currentLevel: Int): EquipmentUpgradeCost {
        val nextLevel = currentLevel + 1
        val creditCost = (25.0 * 1.022.pow(nextLevel) + nextLevel * 10).roundToLong()

        // Resource required changes with level tiers
        val (resType, amount) = when {
            nextLevel < 20 -> ResourceType.STONE to (15L + nextLevel * 8L)
            nextLevel < 50 -> ResourceType.COPPER to (20L + (nextLevel - 20) * 10L)
            nextLevel < 100 -> ResourceType.IRON to (25L + (nextLevel - 50) * 12L)
            nextLevel < 200 -> ResourceType.SILVER to (30L + (nextLevel - 100) * 15L)
            nextLevel < 350 -> ResourceType.GOLD to (40L + (nextLevel - 200) * 18L)
            nextLevel < 500 -> ResourceType.TITANIUM to (50L + (nextLevel - 350) * 20L)
            nextLevel < 700 -> ResourceType.URANIUM to (60L + (nextLevel - 500) * 22L)
            nextLevel < 850 -> ResourceType.QUARTZ to (75L + (nextLevel - 700) * 25L)
            nextLevel < 950 -> ResourceType.DIAMOND to (90L + (nextLevel - 850) * 30L)
            nextLevel < 1000 -> ResourceType.ALIEN_CRYSTAL to (100L + (nextLevel - 950) * 35L)
            else -> ResourceType.OMEGA_CORE to 10L
        }

        return EquipmentUpgradeCost(
            creditCost = creditCost,
            requiredResourceType = resType,
            requiredResourceAmount = amount
        )
    }

    fun calculateEffectiveEfficiency(
        baseLevel: Int,
        rpmResearchLevel: Int = 0,
        isOverdrive: Boolean = false,
        hasSpeedBoost: Boolean = false,
        hasMagneticStorm: Boolean = false
    ): Float {
        val base = 100f + (baseLevel * 2.5f)
        val rpmBonus = rpmResearchLevel * 0.20f
        val speedMult = if (hasSpeedBoost) 1.5f else 1.0f
        val stormMult = if (hasMagneticStorm) 1.5f else 1.0f
        val overdriveMult = if (isOverdrive) 1.8f else 1.0f
        return base * (1f + rpmBonus) * speedMult * stormMult * overdriveMult
    }
}

data class EquipmentStats(
    val level: Int,
    val miningPower: Long,
    val critChance: Float,
    val critMultiplier: Float,
    val autoMiningRate: Long,
    val rareFindBonus: Int,
    val efficiencyRating: Float = 100f + (level * 2.5f)
) {
    val efficiencyPercent: Int get() = kotlin.math.round(efficiencyRating).toInt()
    val hasGlowingVents: Boolean get() = efficiencyRating >= 150f
    val hasRotatingGears: Boolean get() = efficiencyRating >= 250f
    val hasModularExtensions: Boolean get() = efficiencyRating >= 400f
    val hasResonanceArcs: Boolean get() = efficiencyRating >= 650f
    val hasQuadExtensions: Boolean get() = efficiencyRating >= 900f
}

