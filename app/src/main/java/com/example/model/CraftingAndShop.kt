package com.example.model

enum class RelicType(
    val id: String,
    val title: String,
    val iconEmoji: String,
    val rarity: ResourceRarity,
    val description: String,
    val permanentBonus: String
) {
    ANCIENT_STAR_CHART(
        id = "relic_chart",
        title = "Ancient Star Chart",
        iconEmoji = "📜",
        rarity = ResourceRarity.PRECIOUS,
        description = "Decoded celestial coordinates left behind by a forgotten spacefaring civilization.",
        permanentBonus = "+15% Planet Travel Efficiency"
    ),
    CHRONO_CRYSTAL(
        id = "relic_chrono",
        title = "Chrono-Crystal Fragment",
        iconEmoji = "⏳",
        rarity = ResourceRarity.EXOTIC,
        description = "A crystal that vibrates outside normal time flow, speeding up planetary extraction.",
        permanentBonus = "+20% Offline Extraction Rate"
    ),
    HYPERDRIVE_CORE(
        id = "relic_hyperdrive",
        title = "Precursor Hyperdrive Core",
        iconEmoji = "🔋",
        rarity = ResourceRarity.EXOTIC,
        description = "An intact alien propulsion engine radiating clean antimatter currents.",
        permanentBonus = "+15% Drill Mining Speed"
    ),
    DARK_MATTER_CANISTER(
        id = "relic_dark_matter",
        title = "Compressed Dark Matter",
        iconEmoji = "🌀",
        rarity = ResourceRarity.COSMIC,
        description = "A magnetic flask holding exotic non-baryonic particles.",
        permanentBonus = "+50 Dark Matter Currency (Ω)"
    ),
    PRECURSOR_CIRCUIT(
        id = "relic_circuit",
        title = "Subspace Neural Matrix",
        iconEmoji = "💠",
        rarity = ResourceRarity.COSMIC,
        description = "Crystalline neural circuitry capable of calculating planetary crust weakpoints.",
        permanentBonus = "+10% Critical Hit Chance"
    ),
    VOID_COMPASS(
        id = "relic_compass",
        title = "Singularity Compass",
        iconEmoji = "🧭",
        rarity = ResourceRarity.LEGENDARY,
        description = "Always points directly toward the nearest ultra-rare mineral vein.",
        permanentBonus = "+25% Rare Mineral Find Chance"
    )
}

data class CraftingRecipe(
    val id: String,
    val name: String,
    val iconEmoji: String,
    val outputAmount: Int,
    val inputResources: Map<ResourceType, Long>,
    val creditFee: Long,
    val sellValueCredits: Long,
    val description: String
)

object CraftingRegistry {
    val RECIPES = listOf(
        CraftingRecipe(
            id = "craft_iron_bar",
            name = "Reinforced Steel Ingot",
            iconEmoji = "🔩",
            outputAmount = 1,
            inputResources = mapOf(ResourceType.IRON to 5L, ResourceType.COAL to 2L),
            creditFee = 50L,
            sellValueCredits = 150L,
            description = "Smelted high-tensile structural steel."
        ),
        CraftingRecipe(
            id = "craft_magnetic_coil",
            name = "Magnetic Induction Coil",
            iconEmoji = "🧲",
            outputAmount = 1,
            inputResources = mapOf(ResourceType.COPPER to 8L, ResourceType.SILVER to 2L),
            creditFee = 120L,
            sellValueCredits = 350L,
            description = "High-conductivity field coil for drill motors."
        ),
        CraftingRecipe(
            id = "craft_plasma_cell",
            name = "Plasma Containment Cell",
            iconEmoji = "⚡",
            outputAmount = 1,
            inputResources = mapOf(ResourceType.TITANIUM to 5L, ResourceType.URANIUM to 2L),
            creditFee = 400L,
            sellValueCredits = 1200L,
            description = "Magnetic containment cylinder charged with ionized isotopes."
        ),
        CraftingRecipe(
            id = "craft_quantum_chip",
            name = "Tachyon Quantum Processor",
            iconEmoji = "💎",
            outputAmount = 1,
            inputResources = mapOf(ResourceType.QUARTZ to 4L, ResourceType.DIAMOND to 2L),
            creditFee = 1500L,
            sellValueCredits = 4500L,
            description = "Optical nanotech processor governing drone swarms."
        ),
        CraftingRecipe(
            id = "craft_singularity_matrix",
            name = "Singularity Siphon Matrix",
            iconEmoji = "🌌",
            outputAmount = 1,
            inputResources = mapOf(ResourceType.ALIEN_CRYSTAL to 3L, ResourceType.COSMIC_MINERAL to 1L),
            creditFee = 8000L,
            sellValueCredits = 25000L,
            description = "Extra-dimensional focusing ring for cosmic tier drills."
        )
    )
}

enum class ShopBoostType(
    val id: String,
    val title: String,
    val iconEmoji: String,
    val durationSeconds: Long,
    val costCredits: Long,
    val costDarkMatter: Long,
    val description: String
) {
    HYPER_DRILL(
        id = "boost_hyper_drill",
        title = "Hyper-Overdrive",
        iconEmoji = "🔥",
        durationSeconds = 300L, // 5 min
        costCredits = 2000L,
        costDarkMatter = 0L,
        description = "Doubles active tap drill power for 5 minutes."
    ),
    MEGA_HARVEST(
        id = "boost_mega_harvest",
        title = "Subsurface Seeker",
        iconEmoji = "✨",
        durationSeconds = 600L, // 10 min
        costCredits = 6000L,
        costDarkMatter = 0L,
        description = "Triples mineral drops and increases critical chance for 10 minutes."
    ),
    LUCKY_VEIN(
        id = "boost_lucky_vein",
        title = "Resonance Scanner",
        iconEmoji = "💎",
        durationSeconds = 900L, // 15 min
        costCredits = 0L,
        costDarkMatter = 10L,
        description = "+35% chance to discover rare crystals and ancient relics for 15 minutes."
    ),
    INSTANT_OVERCHARGE(
        id = "boost_instant_overcharge",
        title = "Orbital Laser Strike",
        iconEmoji = "💥",
        durationSeconds = 0L,
        costCredits = 0L,
        costDarkMatter = 15L,
        description = "Instantly vaporizes current planetary node, showering you in 5x mineral bounty!"
    )
}

data class ActiveBoost(
    val type: ShopBoostType,
    val expiresAtTimestampMs: Long
) {
    val isExpired: Boolean get() = System.currentTimeMillis() >= expiresAtTimestampMs
    val remainingSeconds: Long get() = ((expiresAtTimestampMs - System.currentTimeMillis()) / 1000).coerceAtLeast(0L)
}
