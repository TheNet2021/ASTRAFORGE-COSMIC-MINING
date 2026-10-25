package com.example.model

enum class GalaxyId(
    val title: String,
    val sectorStart: Int,
    val sectorEnd: Int,
    val requiredEquipmentLevel: Int,
    val warpEnergyCost: Int,
    val description: String,
    val themeColorHex: Long
) {
    MILKY_WAY_SPUR(
        title = "Milky Way Spur",
        sectorStart = 1,
        sectorEnd = 25,
        requiredEquipmentLevel = 0,
        warpEnergyCost = 0,
        description = "The familiar home arm of the galaxy. Rich in silicates, iron, copper, and fossil fuels.",
        themeColorHex = 0xFF38BDF8
    ),
    ANDROMEDA_CLUSTER(
        title = "Andromeda Cluster",
        sectorStart = 26,
        sectorEnd = 50,
        requiredEquipmentLevel = 50,
        warpEnergyCost = 25,
        description = "A glittering expanse of frozen crystal moons, titanium belts, and dense quartz formations.",
        themeColorHex = 0xFF34D399
    ),
    CENTAURUS_VOID(
        title = "Centaurus Void",
        sectorStart = 51,
        sectorEnd = 75,
        requiredEquipmentLevel = 150,
        warpEnergyCost = 50,
        description = "A radioactive gas expanse harboring fissile uranium and volatile exotic isotopes.",
        themeColorHex = 0xFFF59E0B
    ),
    CYGNUS_CORE(
        title = "Cygnus Core",
        sectorStart = 76,
        sectorEnd = 90,
        requiredEquipmentLevel = 350,
        warpEnergyCost = 80,
        description = "Ancient home of the Precursors, filled with hyper-advanced alien ruins and chrono-crystals.",
        themeColorHex = 0xFFA855F7
    ),
    ASTRAFORGE_SINGULARITY(
        title = "Astraforge Singularity",
        sectorStart = 91,
        sectorEnd = 100,
        requiredEquipmentLevel = 700,
        warpEnergyCost = 120,
        description = "The heart of reality where space-time curves into legendary cosmic singularity cores.",
        themeColorHex = 0xFFFFD700
    );

    companion object {
        fun getGalaxyForSector(sector: Int): GalaxyId {
            return entries.firstOrNull { sector in it.sectorStart..it.sectorEnd } ?: MILKY_WAY_SPUR
        }
    }
}
