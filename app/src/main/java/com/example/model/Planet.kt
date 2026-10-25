package com.example.model

import java.util.Random
import kotlin.math.pow
import kotlin.math.roundToLong

enum class BiomeType(
    val title: String,
    val description: String
) {
    BARREN_ROCK("Barren Silicate", "A dusty, crater-scarred world stripped of atmosphere by stellar winds."),
    TERRESTRIAL("Verdant Crust", "Rich in organic moss, tectonic faults, and dense mineral deposits."),
    VOLCANIC_MAGMA("Molten Core", "Rivers of liquid basalt, geothermal vents, and superheated precious ores."),
    CRYSTALLINE_FROST("Prismatic Glaze", "Vast glaciers embedded with towering quartz and subterranean diamond veins."),
    TOXIC_GAS("Acidic Exosphere", "Swirling ammonia storms condensing into rare radioactive isotopes."),
    ALIEN_RUINS("Xeno-Monolith", "Ancient abandoned extraterrestrial structures built from exotic hyper-alloys."),
    STELLAR_NEBULA("Corona Belt", "Planetary fragments bathed in the radiant plasma of an energetic stellar nebula."),
    COSMIC_SINGULARITY("Event Horizon", "A reality-warping world perched precariously on the brink of a supermassive black hole.")
}

data class PlanetData(
    val number: Int,
    val sector: Int,
    val name: String,
    val biome: BiomeType,
    val skyColor: Long,
    val surfaceColor: Long,
    val surfaceDarkColor: Long,
    val craterColor: Long,
    val atmosphereColor: Long,
    val hasRings: Boolean,
    val ringColor: Long,
    val ringTiltAngle: Float,
    val moonCount: Int,
    val primaryMineral: ResourceType,
    val secondaryMinerals: List<ResourceType>,
    val nodeMaxHp: Long,
    val hardness: Float,
    val unlockCreditsCost: Long,
    val unlockRequiredMineral: ResourceType,
    val unlockMineralAmount: Long,
    val flavorDescription: String
)

data class PlanetColors(
    val sky: Long,
    val surface: Long,
    val surfaceDark: Long,
    val crater: Long,
    val atmo: Long,
    val ringCol: Long
)

object PlanetGenerator {

    private val PREFIXES = arrayOf(
        "Astra", "Zephyr", "Vortic", "Kryp", "Obsid", "Chron", "Tachy", "Giga",
        "Apex", "Nova", "Solar", "Xeno", "Hyper", "Stell", "Nebul", "Terra",
        "Vesper", "Eos", "Acheron", "Elys", "Thal", "Phaet", "Orion", "Cygnus"
    )

    private val ROOTS = arrayOf(
        "on", "ius", "aris", "ox", "ar", "ia", "ium", "yx",
        "ora", "eth", "an", "ex", "is", "os", "ara", "eon"
    )

    private val SUFFIXES = arrayOf(
        "Prime", "Secundus", "Major", "Minor", "IV", "VII", "IX", "Omega", "Beta", "Gamma", "X"
    )

    fun getPlanet(number: Int): PlanetData {
        val clampedNumber = number.coerceIn(1, 10000)
        val sector = (clampedNumber - 1) / 100 + 1

        // Seeded deterministic generator based on planet number
        val seed = clampedNumber.toLong() * 2654435761L xor 0x5DEECE66DL
        val rng = Random(seed)

        // Name Generation
        val prefix = PREFIXES[rng.nextInt(PREFIXES.size)]
        val root = ROOTS[rng.nextInt(ROOTS.size)]
        val suffix = SUFFIXES[rng.nextInt(SUFFIXES.size)]
        val name = "$prefix$root $suffix"

        // Biome determination: progresses as sectors increase
        val biome = when {
            sector <= 10 -> if (rng.nextBoolean()) BiomeType.BARREN_ROCK else BiomeType.TERRESTRIAL
            sector <= 25 -> if (rng.nextFloat() < 0.6f) BiomeType.VOLCANIC_MAGMA else BiomeType.BARREN_ROCK
            sector <= 50 -> if (rng.nextFloat() < 0.7f) BiomeType.CRYSTALLINE_FROST else BiomeType.VOLCANIC_MAGMA
            sector <= 70 -> if (rng.nextFloat() < 0.65f) BiomeType.TOXIC_GAS else BiomeType.CRYSTALLINE_FROST
            sector <= 85 -> if (rng.nextFloat() < 0.7f) BiomeType.ALIEN_RUINS else BiomeType.TOXIC_GAS
            sector <= 98 -> if (rng.nextFloat() < 0.8f) BiomeType.STELLAR_NEBULA else BiomeType.ALIEN_RUINS
            else -> BiomeType.COSMIC_SINGULARITY
        }

        // Colors per biome with seeded variation
        val colors = when (biome) {
            BiomeType.BARREN_ROCK -> {
                val shift = rng.nextInt(15)
                PlanetColors(
                    sky = 0xFF070B14L,
                    surface = 0xFF78716CL + (shift * 0x010101L),
                    surfaceDark = 0xFF44403CL,
                    crater = 0xFF292524L,
                    atmo = 0x6694A3B8L,
                    ringCol = 0x99A8A29EL
                )
            }
            BiomeType.TERRESTRIAL -> {
                PlanetColors(
                    sky = 0xFF061A14L,
                    surface = 0xFF059669L,
                    surfaceDark = 0xFF047857L,
                    crater = 0xFF064E3BL,
                    atmo = 0x6610B981L,
                    ringCol = 0x8834D399L
                )
            }
            BiomeType.VOLCANIC_MAGMA -> {
                PlanetColors(
                    sky = 0xFF1A0808L,
                    surface = 0xFFDC2626L,
                    surfaceDark = 0xFF991B1BL,
                    crater = 0xFF7F1D1DL,
                    atmo = 0x77EF4444L,
                    ringCol = 0x99F97316L
                )
            }
            BiomeType.CRYSTALLINE_FROST -> {
                PlanetColors(
                    sky = 0xFF061826L,
                    surface = 0xFF0284C7L,
                    surfaceDark = 0xFF0369A1L,
                    crater = 0xFF075985L,
                    atmo = 0x7738BDF8L,
                    ringCol = 0x99BAE6FDL
                )
            }
            BiomeType.TOXIC_GAS -> {
                PlanetColors(
                    sky = 0xFF0C1A06L,
                    surface = 0xFF84CC16L,
                    surfaceDark = 0xFF65A30DL,
                    crater = 0xFF4D7C0FL,
                    atmo = 0x77A3E635L,
                    ringCol = 0x99BEF264L
                )
            }
            BiomeType.ALIEN_RUINS -> {
                PlanetColors(
                    sky = 0xFF140826L,
                    surface = 0xFF7C3AEDL,
                    surfaceDark = 0xFF6D28D9L,
                    crater = 0xFF5B21B6L,
                    atmo = 0x77A78BFAL,
                    ringCol = 0x99C4B5FDL
                )
            }
            BiomeType.STELLAR_NEBULA -> {
                PlanetColors(
                    sky = 0xFF1C051AL,
                    surface = 0xFFDB2777L,
                    surfaceDark = 0xFFBE185DL,
                    crater = 0xFF9D174DL,
                    atmo = 0x77F472B6L,
                    ringCol = 0x99FBCFE8L
                )
            }
            BiomeType.COSMIC_SINGULARITY -> {
                PlanetColors(
                    sky = 0xFF030303L,
                    surface = 0xFF18181BL,
                    surfaceDark = 0xFF09090BL,
                    crater = 0xFF27272AL,
                    atmo = 0x99F43F5EL,
                    ringCol = 0xCCFFD700L
                )
            }
        }

        val hasRings = (clampedNumber % 3 == 0) || biome == BiomeType.COSMIC_SINGULARITY
        val ringTilt = (rng.nextFloat() * 40f) - 20f
        val moons = rng.nextInt(4)

        // Resources available in this world
        val availableMinerals = ResourceType.getAvailableForPlanet(clampedNumber)
        val primaryMineral = availableMinerals.last()
        val secondaryMinerals = availableMinerals.takeLast(3).dropLast(1).ifEmpty { listOf(ResourceType.STONE) }

        // Progressive difficulty & unlock costs
        val nodeHp = (50.0 * 1.025.pow(clampedNumber.coerceAtMost(500))).roundToLong() + (clampedNumber * 20L)
        val hardness = 1.0f + (clampedNumber / 500f)

        val unlockCredits = if (clampedNumber == 1) 0L else (100.0 * 1.03.pow((clampedNumber - 1).coerceAtMost(300))).roundToLong()
        val prevPlanetMineral = if (clampedNumber <= 1) ResourceType.STONE else ResourceType.getAvailableForPlanet(clampedNumber - 1).lastOrNull() ?: ResourceType.STONE
        val unlockMineralAmount = if (clampedNumber == 1) 0L else (20L + (clampedNumber * 5L))

        return PlanetData(
            number = clampedNumber,
            sector = sector,
            name = name,
            biome = biome,
            skyColor = colors.sky,
            surfaceColor = colors.surface,
            surfaceDarkColor = colors.surfaceDark,
            craterColor = colors.crater,
            atmosphereColor = colors.atmo,
            hasRings = hasRings,
            ringColor = colors.ringCol,
            ringTiltAngle = ringTilt,
            moonCount = moons,
            primaryMineral = primaryMineral,
            secondaryMinerals = secondaryMinerals,
            nodeMaxHp = nodeHp,
            hardness = hardness,
            unlockCreditsCost = unlockCredits,
            unlockRequiredMineral = prevPlanetMineral,
            unlockMineralAmount = unlockMineralAmount,
            flavorDescription = "${biome.description} Contains high concentrations of ${primaryMineral.displayName}."
        )
    }
}

val PlanetData.atmosphereType: String
    get() = when (biome) {
        BiomeType.BARREN_ROCK -> "Trace Argon (0.01 atm)"
        BiomeType.TERRESTRIAL -> "Nitrogen-Oxygen (1.02 atm)"
        BiomeType.VOLCANIC_MAGMA -> "Sulfur Dioxide (4.8 atm)"
        BiomeType.CRYSTALLINE_FROST -> "Methane-Helium (0.75 atm)"
        BiomeType.TOXIC_GAS -> "Ammonia-Chlorine (8.2 atm)"
        BiomeType.ALIEN_RUINS -> "Ionized Xenon Matrix (0.3 atm)"
        BiomeType.STELLAR_NEBULA -> "Radiant Solar Plasma"
        BiomeType.COSMIC_SINGULARITY -> "Graviton Distortion Field"
    }

data class MineralRatio(
    val resource: ResourceType,
    val percentage: Int
)

val PlanetData.mineralRatios: List<MineralRatio>
    get() {
        val totalSec = secondaryMinerals.size
        return when {
            totalSec == 0 -> listOf(MineralRatio(primaryMineral, 100))
            totalSec == 1 -> listOf(
                MineralRatio(primaryMineral, 68),
                MineralRatio(secondaryMinerals[0], 32)
            )
            totalSec == 2 -> listOf(
                MineralRatio(primaryMineral, 58),
                MineralRatio(secondaryMinerals[0], 26),
                MineralRatio(secondaryMinerals[1], 16)
            )
            else -> listOf(
                MineralRatio(primaryMineral, 52),
                MineralRatio(secondaryMinerals[0], 24),
                MineralRatio(secondaryMinerals[1], 14),
                MineralRatio(secondaryMinerals[2], 10)
            )
        }
    }
