package com.example.model

import kotlin.math.pow
import kotlin.math.roundToLong

enum class TechNode(
    val id: String,
    val title: String,
    val iconEmoji: String,
    val maxLevel: Int,
    val baseCreditCost: Long,
    val baseDarkMatterCost: Long,
    val description: String,
    val perkFormulaLabel: (Int) -> String
) {
    DRILL_RPM(
        id = "tech_rpm",
        title = "Tachyon Rotary Motors",
        iconEmoji = "⚙️",
        maxLevel = 10,
        baseCreditCost = 500L,
        baseDarkMatterCost = 0L,
        description = "Upgrades magnetic motor windings for faster continuous automatic drill cycles.",
        perkFormulaLabel = { lvl -> "+${lvl * 20}% Auto Mining Speed" }
    ),
    DRONE_SWARM_LOGISTICS(
        id = "tech_drones",
        title = "Swarm Neural Mesh",
        iconEmoji = "🛸",
        maxLevel = 10,
        baseCreditCost = 1200L,
        baseDarkMatterCost = 0L,
        description = "Coordinates automated fleet harvesting paths across colonized sectors.",
        perkFormulaLabel = { lvl -> "+${lvl * 25}% Drone Fleet Output" }
    ),
    DEEP_CORE_SONAR(
        id = "tech_sonar",
        title = "Seismic Resonance Sonar",
        iconEmoji = "📡",
        maxLevel = 8,
        baseCreditCost = 3000L,
        baseDarkMatterCost = 2L,
        description = "Penetrates tectonic layers to locate hidden mineral pockets and ancient relics.",
        perkFormulaLabel = { lvl -> "+${lvl * 15}% Rare Mineral / Relic Chance" }
    ),
    QUANTUM_SMELTING(
        id = "tech_smelting",
        title = "Plasma Refining Arc",
        iconEmoji = "🔥",
        maxLevel = 10,
        baseCreditCost = 8000L,
        baseDarkMatterCost = 5L,
        description = "Refines raw minerals into higher purity composites with enhanced sell value.",
        perkFormulaLabel = { lvl -> "+${lvl * 20}% Resource Credit Value" }
    ),
    DARK_MATTER_CONDENSER(
        id = "tech_dark_matter",
        title = "Dark Matter Condenser",
        iconEmoji = "🌀",
        maxLevel = 5,
        baseCreditCost = 25000L,
        baseDarkMatterCost = 10L,
        description = "Traps exotic non-baryonic particles directly from cosmic voids.",
        perkFormulaLabel = { lvl -> "+$lvl Ω Dark Matter / 60s" }
    ),
    STELLAR_SILO(
        id = "tech_silo",
        title = "Subspace Compression Silo",
        iconEmoji = "🏛️",
        maxLevel = 6,
        baseCreditCost = 15000L,
        baseDarkMatterCost = 5L,
        description = "Warps internal storage space to accumulate greater offline mining bounty.",
        perkFormulaLabel = { lvl -> "+${lvl * 2} Hours Offline Cap" }
    )
}

data class TechNodeState(
    val node: TechNode,
    val level: Int = 0
) {
    val nextCreditCost: Long
        get() = (node.baseCreditCost * 1.45.pow(level.toDouble())).roundToLong()

    val nextDarkMatterCost: Long
        get() = node.baseDarkMatterCost * (level + 1)

    val isMaxLevel: Boolean get() = level >= node.maxLevel
}

data class GameAchievement(
    val id: String,
    val title: String,
    val description: String,
    val iconEmoji: String,
    val rewardCredits: Long,
    val rewardDarkMatter: Long,
    val isCompleted: Boolean = false,
    val isClaimed: Boolean = false
)

object AchievementRegistry {
    fun getAchievements(
        equipmentLevel: Int,
        unlockedCount: Int,
        totalDrones: Int,
        relicsCount: Int,
        ascensionCount: Int,
        totalMined: Long,
        claimedIds: Set<String>
    ): List<GameAchievement> {
        val list = listOf(
            GameAchievement(
                id = "ach_first_drill",
                title = "Ignite the Core",
                description = "Upgrade your mining drill to Level 5.",
                iconEmoji = "⛏️",
                rewardCredits = 500L,
                rewardDarkMatter = 0L,
                isCompleted = equipmentLevel >= 5,
                isClaimed = "ach_first_drill" in claimedIds
            ),
            GameAchievement(
                id = "ach_industrial",
                title = "Industrial Revolution",
                description = "Reach Equipment Level 50 (Heavy Industrial Era).",
                iconEmoji = "⚙️",
                rewardCredits = 5000L,
                rewardDarkMatter = 5L,
                isCompleted = equipmentLevel >= 50,
                isClaimed = "ach_industrial" in claimedIds
            ),
            GameAchievement(
                id = "ach_advanced",
                title = "Cybernetic Harvester",
                description = "Reach Equipment Level 100 (Advanced Extractor Era).",
                iconEmoji = "🦾",
                rewardCredits = 25000L,
                rewardDarkMatter = 10L,
                isCompleted = equipmentLevel >= 100,
                isClaimed = "ach_advanced" in claimedIds
            ),
            GameAchievement(
                id = "ach_colossus",
                title = "Cosmic Titan",
                description = "Reach Equipment Level 500 (Interstellar Colossus Era).",
                iconEmoji = "🌌",
                rewardCredits = 200000L,
                rewardDarkMatter = 30L,
                isCompleted = equipmentLevel >= 500,
                isClaimed = "ach_colossus" in claimedIds
            ),
            GameAchievement(
                id = "ach_omega",
                title = "The Omega Astraforge",
                description = "Achieve the ultimate Equipment Level 1000!",
                iconEmoji = "👑",
                rewardCredits = 1000000L,
                rewardDarkMatter = 100L,
                isCompleted = equipmentLevel >= 1000,
                isClaimed = "ach_omega" in claimedIds
            ),
            GameAchievement(
                id = "ach_explorer_10",
                title = "System Cartographer",
                description = "Discover and unlock 10 celestial worlds.",
                iconEmoji = "🪐",
                rewardCredits = 10000L,
                rewardDarkMatter = 5L,
                isCompleted = unlockedCount >= 10,
                isClaimed = "ach_explorer_10" in claimedIds
            ),
            GameAchievement(
                id = "ach_explorer_50",
                title = "Cluster Sovereign",
                description = "Discover and unlock 50 celestial worlds.",
                iconEmoji = "🗺️",
                rewardCredits = 80000L,
                rewardDarkMatter = 20L,
                isCompleted = unlockedCount >= 50,
                isClaimed = "ach_explorer_50" in claimedIds
            ),
            GameAchievement(
                id = "ach_fleet_10",
                title = "Swarm Commander",
                description = "Assemble a fleet of 10 or more automated drones.",
                iconEmoji = "🛸",
                rewardCredits = 8000L,
                rewardDarkMatter = 5L,
                isCompleted = totalDrones >= 10,
                isClaimed = "ach_fleet_10" in claimedIds
            ),
            GameAchievement(
                id = "ach_relic_hunter",
                title = "Xeno-Archaeologist",
                description = "Unearth your first ancient alien relic.",
                iconEmoji = "📜",
                rewardCredits = 15000L,
                rewardDarkMatter = 10L,
                isCompleted = relicsCount >= 1,
                isClaimed = "ach_relic_hunter" in claimedIds
            ),
            GameAchievement(
                id = "ach_ascension",
                title = "Reborn in Supernova",
                description = "Perform your first Cosmic Ascension.",
                iconEmoji = "⚡",
                rewardCredits = 50000L,
                rewardDarkMatter = 25L,
                isCompleted = ascensionCount >= 1,
                isClaimed = "ach_ascension" in claimedIds
            ),
            GameAchievement(
                id = "ach_millionaire",
                title = "Galactic Tycoon",
                description = "Extract over 10,000 raw mineral units across worlds.",
                iconEmoji = "💎",
                rewardCredits = 30000L,
                rewardDarkMatter = 15L,
                isCompleted = totalMined >= 10000L,
                isClaimed = "ach_millionaire" in claimedIds
            )
        )
        return list
    }
}
