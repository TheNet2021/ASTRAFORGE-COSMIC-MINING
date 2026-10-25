package com.example.model

enum class ResourceRarity(val label: String, val colorHex: Long) {
    COMMON("Common", 0xFF94A3B8),
    INDUSTRIAL("Industrial", 0xFF38BDF8),
    PRECIOUS("Precious", 0xFFFBBF24),
    EXOTIC("Exotic", 0xFF34D399),
    COSMIC("Cosmic", 0xFFA855F7),
    LEGENDARY("Legendary", 0xFFF43F5E)
}

enum class ResourceType(
    val id: String,
    val displayName: String,
    val rarity: ResourceRarity,
    val baseValue: Long, // in Credits
    val primaryColor: Long,
    val secondaryColor: Long,
    val symbol: String,
    val description: String,
    val unlockPlanet: Int
) {
    STONE(
        id = "stone",
        displayName = "Cosmic Stone",
        rarity = ResourceRarity.COMMON,
        baseValue = 1L,
        primaryColor = 0xFF64748B,
        secondaryColor = 0xFF475569,
        symbol = "🪨",
        description = "Basic planetary silicate. Found on nearly every celestial body.",
        unlockPlanet = 1
    ),
    BIO_MATTER(
        id = "bio_matter",
        displayName = "Organic Slag",
        rarity = ResourceRarity.COMMON,
        baseValue = 2L,
        primaryColor = 0xFF10B981,
        secondaryColor = 0xFF047857,
        symbol = "🌱",
        description = "Hydrocarbon deposits and fossilized alien moss.",
        unlockPlanet = 1
    ),
    COAL(
        id = "coal",
        displayName = "Astral Coal",
        rarity = ResourceRarity.COMMON,
        baseValue = 4L,
        primaryColor = 0xFF334155,
        secondaryColor = 0xFF1E293B,
        symbol = "⬛",
        description = "Compressed carbonaceous fuel for early kinetic furnaces.",
        unlockPlanet = 2
    ),
    COPPER(
        id = "copper",
        displayName = "Conductive Copper",
        rarity = ResourceRarity.INDUSTRIAL,
        baseValue = 8L,
        primaryColor = 0xFFD97706,
        secondaryColor = 0xFFB45309,
        symbol = "🟤",
        description = "Essential thermal and electrical conductor for machinery wiring.",
        unlockPlanet = 2
    ),
    IRON(
        id = "iron",
        displayName = "Reinforced Iron",
        rarity = ResourceRarity.INDUSTRIAL,
        baseValue = 15L,
        primaryColor = 0xFF9CA3AF,
        secondaryColor = 0xFF6B7280,
        symbol = "⚙️",
        description = "Structural ferrous alloy necessary for reinforced chassis.",
        unlockPlanet = 3
    ),
    SILVER(
        id = "silver",
        displayName = "Lustrous Silver",
        rarity = ResourceRarity.PRECIOUS,
        baseValue = 35L,
        primaryColor = 0xFFE2E8F0,
        secondaryColor = 0xFFCBD5E1,
        symbol = "🥈",
        description = "Reflective noble metal used in optical laser cavities.",
        unlockPlanet = 5
    ),
    GOLD(
        id = "gold",
        displayName = "Auric Gold",
        rarity = ResourceRarity.PRECIOUS,
        baseValue = 80L,
        primaryColor = 0xFFF59E0B,
        secondaryColor = 0xFFD97706,
        symbol = "🥇",
        description = "Corrosion-resistant precious metal prized across the galaxy.",
        unlockPlanet = 8
    ),
    PLATINUM(
        id = "platinum",
        displayName = "Dense Platinum",
        rarity = ResourceRarity.PRECIOUS,
        baseValue = 180L,
        primaryColor = 0xFFE0E7FF,
        secondaryColor = 0xFFA5B4FC,
        symbol = "⚪",
        description = "Catalytic element for high-stress plasma chamber containment.",
        unlockPlanet = 15
    ),
    TITANIUM(
        id = "titanium",
        displayName = "Aerospace Titanium",
        rarity = ResourceRarity.INDUSTRIAL,
        baseValue = 350L,
        primaryColor = 0xFF60A5FA,
        secondaryColor = 0xFF3B82F6,
        symbol = "🛡️",
        description = "Super-tough lightweight metal forged in dying star explosions.",
        unlockPlanet = 25
    ),
    URANIUM(
        id = "uranium",
        displayName = "Fissile Uranium",
        rarity = ResourceRarity.EXOTIC,
        baseValue = 800L,
        primaryColor = 0xFF22C55E,
        secondaryColor = 0xFF15803D,
        symbol = "☢️",
        description = "Radioactive power fuel feeding planetary reactors.",
        unlockPlanet = 50
    ),
    QUARTZ(
        id = "quartz",
        displayName = "Prismatic Quartz",
        rarity = ResourceRarity.EXOTIC,
        baseValue = 1800L,
        primaryColor = 0xFF38BDF8,
        secondaryColor = 0xFF0284C7,
        symbol = "💠",
        description = "Piezoelectric crystals capable of focusing tachyon laser pulses.",
        unlockPlanet = 100
    ),
    DIAMOND(
        id = "diamond",
        displayName = "Hyper-Diamond",
        rarity = ResourceRarity.EXOTIC,
        baseValue = 4200L,
        primaryColor = 0xFFBAE6FD,
        secondaryColor = 0xFF7DD3FC,
        symbol = "💎",
        description = "Ultra-dense tetrahedral carbon capable of shredding planetary crusts.",
        unlockPlanet = 250
    ),
    EMERALD(
        id = "emerald",
        displayName = "Ion Emerald",
        rarity = ResourceRarity.EXOTIC,
        baseValue = 10000L,
        primaryColor = 0xFF10B981,
        secondaryColor = 0xFF059669,
        symbol = "❇️",
        description = "Charged beryllium crystal resonant with subspace frequencies.",
        unlockPlanet = 500
    ),
    ALIEN_CRYSTAL(
        id = "alien_crystal",
        displayName = "Void Crystal",
        rarity = ResourceRarity.COSMIC,
        baseValue = 28000L,
        primaryColor = 0xFFA855F7,
        secondaryColor = 0xFF7E22CE,
        symbol = "🔮",
        description = "Extra-dimensional mineral that bends local gravitational fields.",
        unlockPlanet = 1000
    ),
    COSMIC_MINERAL(
        id = "cosmic_mineral",
        displayName = "Stellarite Ore",
        rarity = ResourceRarity.COSMIC,
        baseValue = 75000L,
        primaryColor = 0xFFEC4899,
        secondaryColor = 0xFFBE185D,
        symbol = "✨",
        description = "Congealed stellar heart matter holding microscopic wormholes.",
        unlockPlanet = 2500
    ),
    OMEGA_CORE(
        id = "omega_core",
        displayName = "Singularity Core",
        rarity = ResourceRarity.LEGENDARY,
        baseValue = 250000L,
        primaryColor = 0xFFF43F5E,
        secondaryColor = 0xFF881337,
        symbol = "🌌",
        description = "The ultimate prize of ASTRAFORGE: an encapsulated miniature black hole.",
        unlockPlanet = 5000
    );

    companion object {
        fun fromId(id: String): ResourceType {
            return entries.firstOrNull { it.id == id } ?: STONE
        }

        fun getAvailableForPlanet(planetNumber: Int): List<ResourceType> {
            return entries.filter { it.unlockPlanet <= planetNumber }
        }
    }
}
