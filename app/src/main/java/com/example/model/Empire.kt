package com.example.model

import kotlin.math.pow
import kotlin.math.roundToLong

enum class FacilityType(
    val id: String,
    val displayName: String,
    val iconEmoji: String,
    val baseCreditCost: Long,
    val requiredEnergy: Int,
    val description: String,
    val benefitLabel: String
) {
    MINING_OUTPOST(
        id = "fac_outpost",
        displayName = "Command Outpost",
        iconEmoji = "🏛️",
        baseCreditCost = 500L,
        requiredEnergy = 0,
        description = "Establishes permanent planetary colony presence and increases offline storage time.",
        benefitLabel = "+2h Offline Storage"
    ),
    AUTOMATED_EXTRACTOR(
        id = "fac_extractor",
        displayName = "Deep Bore Rig",
        iconEmoji = "🏗️",
        baseCreditCost = 1200L,
        requiredEnergy = 5,
        description = "Industrial stationary rig generating constant passive resources from the world.",
        benefitLabel = "+50/s Passive Extraction"
    ),
    ORE_REFINERY(
        id = "fac_refinery",
        displayName = "Smelting Foundry",
        iconEmoji = "🏭",
        baseCreditCost = 3500L,
        requiredEnergy = 10,
        description = "Refines raw minerals into high-purity ingots, increasing sell values across the empire.",
        benefitLabel = "+15% Global Mineral Value"
    ),
    POWER_GENERATOR(
        id = "fac_power",
        displayName = "Fusion Generator",
        iconEmoji = "⚡",
        baseCreditCost = 8000L,
        requiredEnergy = 0,
        description = "Generates clean plasma power (Watts) required to fuel high-tier empire infrastructure.",
        benefitLabel = "+50 Energy Grid (⚡)"
    ),
    RESEARCH_LAB(
        id = "fac_lab",
        displayName = "Xeno-Science Lab",
        iconEmoji = "🔬",
        baseCreditCost = 25000L,
        requiredEnergy = 20,
        description = "Decodes ancient celestial relics and accelerates tech tree breakthroughs.",
        benefitLabel = "+5 Tech Points/sec"
    ),
    ORBITAL_STATION(
        id = "fac_orbital",
        displayName = "Orbital Relay Hub",
        iconEmoji = "🛰️",
        baseCreditCost = 80000L,
        requiredEnergy = 35,
        description = "Coordinates drone swarms across the entire sector, boosting drone speeds.",
        benefitLabel = "+25% Sector Drone Output"
    )
}

data class PlanetFacilityState(
    val type: FacilityType,
    val level: Int = 0
) {
    val upgradeCost: Long
        get() = (type.baseCreditCost * 1.35.pow(level.toDouble())).roundToLong()

    val isBuilt: Boolean get() = level > 0
}
