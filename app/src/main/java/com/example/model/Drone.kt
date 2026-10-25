package com.example.model

import kotlin.math.pow
import kotlin.math.roundToLong

enum class DroneType(
    val id: String,
    val displayName: String,
    val targetResource: ResourceType,
    val baseYieldPerSec: Long,
    val baseCreditCost: Long,
    val iconEmoji: String,
    val colorHex: Long,
    val description: String
) {
    STONE_COLLECTOR(
        id = "drone_stone",
        displayName = "Scrap Collector",
        targetResource = ResourceType.STONE,
        baseYieldPerSec = 2L,
        baseCreditCost = 150L,
        iconEmoji = "🛸",
        colorHex = 0xFF94A3B8,
        description = "Basic automated vacuum drone collecting surface silicates."
    ),
    IRON_EXTRACTOR(
        id = "drone_iron",
        displayName = "Ferrous Extractor",
        targetResource = ResourceType.IRON,
        baseYieldPerSec = 3L,
        baseCreditCost = 600L,
        iconEmoji = "⚙️",
        colorHex = 0xFF38BDF8,
        description = "Magnetic induction drone extracting pure iron veins."
    ),
    GOLD_HUNTER(
        id = "drone_gold",
        displayName = "Auric Hunter",
        targetResource = ResourceType.GOLD,
        baseYieldPerSec = 4L,
        baseCreditCost = 2500L,
        iconEmoji = "🥇",
        colorHex = 0xFFF59E0B,
        description = "Equipped with spectrographic sensors to pinpoint gold nodes."
    ),
    CRYSTAL_SCANNER(
        id = "drone_crystal",
        displayName = "Crystal Scanner",
        targetResource = ResourceType.QUARTZ,
        baseYieldPerSec = 5L,
        baseCreditCost = 10000L,
        iconEmoji = "💠",
        colorHex = 0xFF34D399,
        description = "Uses acoustic resonance to unearth delicate quartz geometries."
    ),
    HEAVY_INDUSTRIAL(
        id = "drone_industrial",
        displayName = "Heavy Bore Drone",
        targetResource = ResourceType.TITANIUM,
        baseYieldPerSec = 8L,
        baseCreditCost = 45000L,
        iconEmoji = "🚜",
        colorHex = 0xFF60A5FA,
        description = "Dual-drill armored automaton capable of deep core drilling."
    ),
    QUANTUM_DRONE(
        id = "drone_quantum",
        displayName = "Quantum Siphon",
        targetResource = ResourceType.ALIEN_CRYSTAL,
        baseYieldPerSec = 12L,
        baseCreditCost = 200000L,
        iconEmoji = "🔮",
        colorHex = 0xFFA855F7,
        description = "Tunnels through subspace to siphon exotic matter directly."
    ),
    COSMIC_HARVESTER(
        id = "drone_cosmic",
        displayName = "Cosmic Harvester",
        targetResource = ResourceType.OMEGA_CORE,
        baseYieldPerSec = 20L,
        baseCreditCost = 1000000L,
        iconEmoji = "🌌",
        colorHex = 0xFFFFD700,
        description = "Legendary autonomous mothership harvesting singularities."
    );

    companion object {
        fun fromId(id: String): DroneType {
            return entries.firstOrNull { it.id == id } ?: STONE_COLLECTOR
        }
    }
}

data class DroneFleetItem(
    val type: DroneType,
    val count: Int = 0,
    val upgradeLevel: Int = 1,
    val assignedPlanet: Int = 1
) {
    val totalYieldPerSec: Long
        get() = (type.baseYieldPerSec * count * (1.0 + (upgradeLevel - 1) * 0.25)).roundToLong()

    val nextPurchaseCost: Long
        get() = (type.baseCreditCost * 1.15.pow(count.toDouble())).roundToLong()

    val upgradeCost: Long
        get() = (type.baseCreditCost * 1.5 * 1.25.pow(upgradeLevel.toDouble())).roundToLong()
}
