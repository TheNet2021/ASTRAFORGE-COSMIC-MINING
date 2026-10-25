package com.example.viewmodel

import android.app.Application
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai.AstraAiService
import com.example.ai.ChatMessage
import com.example.audio.HapticManager
import com.example.audio.SoundSynthesizer
import com.example.data.GameDatabase
import com.example.data.GameRepository
import com.example.data.LoadedGameState
import com.example.model.AchievementRegistry
import com.example.model.ActiveBoost
import com.example.model.ActiveCosmicEvent
import com.example.model.CosmicEventType
import com.example.model.CraftingRecipe
import com.example.model.CraftingRegistry
import com.example.model.DroneFleetItem
import com.example.model.DroneType
import com.example.model.EquipmentSystem
import com.example.model.EquipmentUpgradeCost
import com.example.model.EquipmentVisualConfig
import com.example.model.FacilityType
import com.example.model.GalaxyId
import com.example.model.GameAchievement
import com.example.model.PlanetData
import com.example.model.PlanetFacilityState
import com.example.model.PlanetGenerator
import com.example.model.RelicType
import com.example.model.ResourceType
import com.example.model.ShopBoostType
import com.example.model.TechNode
import com.example.model.TechNodeState
import com.example.ui.engine.ParticleManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.pow
import kotlin.math.roundToLong
import kotlin.random.Random

data class OfflineRewardSummary(
    val elapsedSeconds: Long,
    val creditsEarned: Long,
    val resourcesEarned: Map<ResourceType, Long>
)

data class GameUiState(
    val credits: Long = 350L,
    val darkMatter: Long = 0L,
    val energy: Int = 15,
    val playerLevel: Int = 1,
    val playerXp: Long = 0L,
    val xpRequired: Long = 100L,
    val equipmentLevel: Int = 0,
    val currentPlanetNumber: Int = 1,
    val currentPlanet: PlanetData = PlanetGenerator.getPlanet(1),
    val nodeCurrentHp: Long = 50L,
    val nodeMaxHp: Long = 50L,
    val unlockedPlanets: Set<Int> = setOf(1),
    val inventory: Map<ResourceType, Long> = mapOf(ResourceType.STONE to 30L),
    val drones: List<DroneFleetItem> = DroneType.entries.map { DroneFleetItem(it, count = if (it == DroneType.STONE_COLLECTOR) 1 else 0) },
    val facilities: Map<Int, List<PlanetFacilityState>> = emptyMap(),
    val discoveredRelics: Set<RelicType> = emptySet(),
    val craftedComponents: Map<String, Long> = emptyMap(),
    val activeBoosts: List<ActiveBoost> = emptyList(),
    val activeCosmicEvent: ActiveCosmicEvent? = null,
    val researchLevels: Map<TechNode, Int> = emptyMap(),
    val claimedAchievements: Set<String> = emptySet(),
    val statsTotalTaps: Long = 0L,
    val statsTotalOresMined: Long = 0L,
    val statsTotalCreditsEarned: Long = 0L,
    val ascensionCount: Int = 0,
    val ascensionStellarShards: Long = 0L,
    val soundVolume: Float = 0.8f,
    val musicVolume: Float = 0.7f,
    val isMuted: Boolean = false,
    val vibrationEnabled: Boolean = true,
    val graphicsQuality: String = "HIGH",
    val reducedAnimations: Boolean = false,
    val screenShakeEnabled: Boolean = true,
    val batterySaverEnabled: Boolean = false,
    val selectedLanguage: String = "English",
    val activeTab: Int = 0,
    val isMiningActive: Boolean = true, // Continuous auto mining by default!
    val isOverdriveActive: Boolean = false,
    val screenShakeOffsetX: Float = 0f,
    val screenShakeOffsetY: Float = 0f,
    val animationTimeMs: Long = 0L,
    val showUpgradeCelebration: Boolean = false,
    val upgradeCelebrationOldLevel: Int = 0,
    val upgradeCelebrationNewLevel: Int = 0,
    val offlineReward: OfflineRewardSummary? = null,
    val newlyDiscoveredRelic: RelicType? = null,
    val chatMessages: List<ChatMessage> = listOf(
        ChatMessage(
            id = 1L,
            sender = "ASTRA-9",
            text = "Welcome aboard, Commander. Autonomous drilling rigs are active. I am monitoring planetary core resonance and fleet telemetry. How may I direct our cosmic operations?",
            isAi = true
        )
    ),
    val isAiLoading: Boolean = false
) {
    val effectiveEfficiency: Float
        get() = EquipmentSystem.calculateEffectiveEfficiency(
            baseLevel = equipmentLevel,
            rpmResearchLevel = researchLevels[TechNode.DRILL_RPM] ?: 0,
            isOverdrive = isOverdriveActive,
            hasSpeedBoost = activeBoosts.any { it.type == ShopBoostType.HYPER_DRILL && !it.isExpired },
            hasMagneticStorm = activeCosmicEvent?.type == CosmicEventType.MAGNETIC_STORM && !activeCosmicEvent.isExpired
        )

    val equipmentConfig: EquipmentVisualConfig
        get() = EquipmentSystem.getEquipmentVisualConfig(equipmentLevel)

    val nextUpgradeCost: EquipmentUpgradeCost
        get() = EquipmentSystem.getUpgradeCost(equipmentLevel)

    val currentGalaxy: GalaxyId
        get() = GalaxyId.getGalaxyForSector(currentPlanet.sector)

    val currentPlanetFacilities: List<PlanetFacilityState>
        get() = facilities[currentPlanetNumber] ?: FacilityType.entries.map { PlanetFacilityState(it, 0) }

    val achievements: List<GameAchievement>
        get() = AchievementRegistry.getAchievements(
            equipmentLevel = equipmentLevel,
            unlockedCount = unlockedPlanets.size,
            totalDrones = drones.sumOf { it.count },
            relicsCount = discoveredRelics.size,
            ascensionCount = ascensionCount,
            totalMined = statsTotalOresMined,
            claimedIds = claimedAchievements
        )

    val researchNodes: List<TechNodeState>
        get() = TechNode.entries.map { node ->
            TechNodeState(node, researchLevels[node] ?: 0)
        }
}

class GameViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = GameRepository(GameDatabase.getInstance(application).gameSaveDao())
    val soundSynth = SoundSynthesizer(application)
    val hapticManager = HapticManager(application)
    val particleManager = ParticleManager()

    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private var overdriveTimer = 0
    private var cosmicEventCooldown = 75

    init {
        loadSavedGame()
        startAnimationLoop()
        startEconomyLoop()
    }

    private fun loadSavedGame() {
        viewModelScope.launch {
            val loaded = repository.loadGame()
            soundSynth.soundVolume = loaded.soundVolume
            soundSynth.isMuted = loaded.isMuted
            hapticManager.isEnabled = !loaded.isMuted

            val planet = PlanetGenerator.getPlanet(loaded.currentPlanetNumber)
            val xpReq = calculateXpRequired(loaded.playerLevel)

            // Offline progress calculation
            val now = System.currentTimeMillis()
            val elapsedSec = ((now - loaded.lastSavedTimestampMs) / 1000).coerceAtLeast(0L)
            var offlineSummary: OfflineRewardSummary? = null

            val updatedInventory = loaded.inventory.toMutableMap()
            var offlineCredits = 0L

            if (elapsedSec > 15L) {
                val siloBonusHours = (loaded.research[TechNode.STELLAR_SILO] ?: 0) * 2
                val maxOfflineSec = (14400L + siloBonusHours * 3600L)
                val validSec = elapsedSec.coerceAtMost(maxOfflineSec)

                val offlineGains = mutableMapOf<ResourceType, Long>()
                for (drone in loaded.drones) {
                    if (drone.count > 0) {
                        val yield = (drone.totalYieldPerSec * validSec * 0.5).roundToLong()
                        if (yield > 0) {
                            offlineGains[drone.type.targetResource] = (offlineGains[drone.type.targetResource] ?: 0L) + yield
                            updatedInventory[drone.type.targetResource] = (updatedInventory[drone.type.targetResource] ?: 0L) + yield
                        }
                    }
                }
                offlineCredits = (validSec * 3L).coerceAtLeast(25L)

                offlineSummary = OfflineRewardSummary(
                    elapsedSeconds = validSec,
                    creditsEarned = offlineCredits,
                    resourcesEarned = offlineGains
                )
            }

            _uiState.update { current ->
                current.copy(
                    credits = loaded.credits + offlineCredits,
                    darkMatter = loaded.darkMatter,
                    energy = loaded.energy.coerceAtLeast(15),
                    playerLevel = loaded.playerLevel,
                    playerXp = loaded.playerXp,
                    xpRequired = xpReq,
                    equipmentLevel = loaded.equipmentLevel,
                    currentPlanetNumber = loaded.currentPlanetNumber,
                    currentPlanet = planet,
                    nodeCurrentHp = planet.nodeMaxHp,
                    nodeMaxHp = planet.nodeMaxHp,
                    unlockedPlanets = loaded.unlockedPlanets,
                    inventory = updatedInventory,
                    drones = if (loaded.drones.isNotEmpty()) loaded.drones else current.drones,
                    facilities = loaded.facilities,
                    discoveredRelics = loaded.discoveredRelics,
                    craftedComponents = loaded.craftedComponents,
                    activeBoosts = loaded.activeBoosts,
                    researchLevels = loaded.research,
                    claimedAchievements = loaded.claimedAchievements,
                    statsTotalTaps = loaded.statsTotalTaps,
                    statsTotalOresMined = loaded.statsTotalOresMined,
                    statsTotalCreditsEarned = loaded.statsTotalCreditsEarned,
                    ascensionCount = loaded.ascensionCount,
                    ascensionStellarShards = loaded.ascensionStellarShards,
                    soundVolume = loaded.soundVolume,
                    isMuted = loaded.isMuted,
                    screenShakeEnabled = loaded.screenShakeEnabled,
                    batterySaverEnabled = loaded.batterySaverEnabled,
                    offlineReward = offlineSummary
                )
            }
        }
    }

    private fun startAnimationLoop() {
        viewModelScope.launch {
            var lastTime = System.currentTimeMillis()
            var continuousDrillEffectTimer = 0

            while (isActive) {
                val now = System.currentTimeMillis()
                val delta = now - lastTime
                lastTime = now

                particleManager.update()

                // Spawn continuous micro-sparks from the automatic cutting tip
                continuousDrillEffectTimer++
                if (continuousDrillEffectTimer >= 12) {
                    continuousDrillEffectTimer = 0
                    val res = _uiState.value.currentPlanet.primaryMineral
                    particleManager.spawnSparks(
                        origin = Offset(540f, 1080f),
                        count = 2,
                        primaryColor = Color(res.primaryColor),
                        secondaryColor = Color.White
                    )
                }

                _uiState.update { st ->
                    var shakeX = st.screenShakeOffsetX * 0.82f
                    var shakeY = st.screenShakeOffsetY * 0.82f
                    if (kotlin.math.abs(shakeX) < 0.2f) shakeX = 0f
                    if (kotlin.math.abs(shakeY) < 0.2f) shakeY = 0f

                    if (overdriveTimer > 0) overdriveTimer--

                    st.copy(
                        animationTimeMs = st.animationTimeMs + delta,
                        screenShakeOffsetX = shakeX,
                        screenShakeOffsetY = shakeY,
                        isMiningActive = true,
                        isOverdriveActive = overdriveTimer > 0
                    )
                }

                val delayTime = if (_uiState.value.batterySaverEnabled) 33L else 16L
                delay(delayTime)
            }
        }
    }

    private fun startEconomyLoop() {
        viewModelScope.launch {
            var autoSaveTimer = 0
            while (isActive) {
                delay(1000L) // 1 Hz automatic continuous mining tick

                _uiState.update { st ->
                    val activeBoosts = st.activeBoosts.filter { !it.isExpired }
                    val activeCosmicEvent = if (st.activeCosmicEvent != null && !st.activeCosmicEvent.isExpired) st.activeCosmicEvent else null

                    // Multipliers
                    val hasSpeedBoost = activeBoosts.any { it.type == ShopBoostType.HYPER_DRILL }
                    val hasHarvestBoost = activeBoosts.any { it.type == ShopBoostType.MEGA_HARVEST }
                    val isMeteorStorm = activeCosmicEvent?.type == CosmicEventType.METEOR_SHOWER
                    val isMagneticStorm = activeCosmicEvent?.type == CosmicEventType.MAGNETIC_STORM

                    val rpmBonus = (st.researchLevels[TechNode.DRILL_RPM] ?: 0) * 0.20f
                    val droneBonus = (st.researchLevels[TechNode.DRONE_SWARM_LOGISTICS] ?: 0) * 0.25f

                    val globalYieldMult = (if (hasHarvestBoost) 3L else 1L) * (if (isMeteorStorm) 2L else 1L)
                    val drillSpeedMult = (if (hasSpeedBoost) 2f else 1f) * (if (isMagneticStorm) 3f else 1f) * (1f + rpmBonus) * (if (overdriveTimer > 0) 3f else 1f)

                    // 1. Drone Fleet Auto Harvest
                    val newInventory = st.inventory.toMutableMap()
                    var totalOreTick = 0L

                    for (drone in st.drones) {
                        if (drone.count > 0 && drone.assignedPlanet in st.unlockedPlanets) {
                            val droneRate = (drone.totalYieldPerSec * (1f + droneBonus) * globalYieldMult).roundToLong()
                            if (droneRate > 0) {
                                val currentCount = newInventory[drone.type.targetResource] ?: 0L
                                newInventory[drone.type.targetResource] = currentCount + droneRate
                                totalOreTick += droneRate
                            }
                        }
                    }

                    // 2. Fully Automatic Continuous Machine Drilling
                    val baseAutoDmg = EquipmentSystem.getEquipmentStats(st.equipmentLevel).autoMiningRate
                    val continuousDmg = (baseAutoDmg * drillSpeedMult).roundToLong().coerceAtLeast(1L)
                    var hp = st.nodeCurrentHp - continuousDmg
                    var creditsEarned = st.credits
                    var newXp = st.playerXp
                    var pLevel = st.playerLevel
                    var xpReq = st.xpRequired

                    // Passive Dark Matter generation from research
                    val dmResearchLevel = st.researchLevels[TechNode.DARK_MATTER_CONDENSER] ?: 0
                    var currentDm = st.darkMatter
                    if (dmResearchLevel > 0 && (st.animationTimeMs / 1000) % 60 == 0L) {
                        currentDm += dmResearchLevel
                    }

                    if (hp <= 0L) {
                        // Node depleted automatically
                        val dropCount = (18L + st.currentPlanetNumber * 3L) * globalYieldMult
                        val primeRes = st.currentPlanet.primaryMineral
                        newInventory[primeRes] = (newInventory[primeRes] ?: 0L) + dropCount

                        val smeltingBonus = (st.researchLevels[TechNode.QUANTUM_SMELTING] ?: 0) * 0.20f
                        val valuePerUnit = (primeRes.baseValue * (1f + smeltingBonus)).roundToLong()
                        creditsEarned += valuePerUnit * dropCount
                        newXp += 30L
                        hp = st.nodeMaxHp
                        totalOreTick += dropCount

                        // Continuous auto-sound ping
                        soundSynth.playDrillHit(1.2f)
                    }

                    while (newXp >= xpReq) {
                        newXp -= xpReq
                        pLevel++
                        xpReq = calculateXpRequired(pLevel)
                    }

                    // Trigger periodic cosmic events
                    var nextEvent = activeCosmicEvent
                    cosmicEventCooldown--
                    if (cosmicEventCooldown <= 0 && activeCosmicEvent == null) {
                        cosmicEventCooldown = Random.nextInt(75, 120)
                        val randomEvent = CosmicEventType.entries.random()
                        nextEvent = ActiveCosmicEvent(
                            type = randomEvent,
                            expiresAtTimestampMs = System.currentTimeMillis() + (randomEvent.durationSeconds * 1000L)
                        )
                        soundSynth.playRareDiscovery()
                    }

                    st.copy(
                        credits = creditsEarned,
                        darkMatter = currentDm,
                        inventory = newInventory,
                        nodeCurrentHp = hp.coerceAtLeast(1L),
                        playerXp = newXp,
                        playerLevel = pLevel,
                        xpRequired = xpReq,
                        activeBoosts = activeBoosts,
                        activeCosmicEvent = nextEvent,
                        statsTotalOresMined = st.statsTotalOresMined + totalOreTick
                    )
                }

                autoSaveTimer++
                if (autoSaveTimer >= 10) {
                    autoSaveTimer = 0
                    saveCurrentGame()
                }
            }
        }
    }

    // Manual tap acts as an Overdrive Burst
    fun onMineTap(touchOffset: Offset) {
        overdriveTimer = 180 // 3 seconds of overdrive

        val st = _uiState.value
        val stats = EquipmentSystem.getEquipmentStats(st.equipmentLevel)
        val damage = (stats.miningPower * 2.5).roundToLong()

        soundSynth.playDrillHit(1.4f)
        hapticManager.click()

        particleManager.spawnSparks(
            origin = touchOffset,
            count = 14,
            primaryColor = Color(0xFF00F0FF),
            secondaryColor = Color.White
        )
        particleManager.spawnFloatingText(
            origin = touchOffset,
            text = "OVERDRIVE! -$damage",
            color = Color(0xFF00F0FF),
            isCrit = true
        )

        var newHp = st.nodeCurrentHp - damage
        if (newHp <= 0L) {
            val primeRes = st.currentPlanet.primaryMineral
            val newInv = st.inventory.toMutableMap()
            val dropCount = 35L + (st.currentPlanetNumber * 5L)
            newInv[primeRes] = (newInv[primeRes] ?: 0L) + dropCount
            newHp = st.nodeMaxHp
            _uiState.update {
                it.copy(
                    nodeCurrentHp = newHp,
                    inventory = newInv,
                    statsTotalTaps = it.statsTotalTaps + 1
                )
            }
        } else {
            _uiState.update {
                it.copy(
                    nodeCurrentHp = newHp,
                    statsTotalTaps = it.statsTotalTaps + 1
                )
            }
        }
    }

    fun onUpgradeEquipment() {
        val st = _uiState.value
        if (st.equipmentLevel >= 1000) return

        val cost = st.nextUpgradeCost
        if (st.credits < cost.creditCost) return
        val currentResourceCount = st.inventory[cost.requiredResourceType] ?: 0L
        if (currentResourceCount < cost.requiredResourceAmount) return

        val newInv = st.inventory.toMutableMap()
        newInv[cost.requiredResourceType] = currentResourceCount - cost.requiredResourceAmount

        val oldLvl = st.equipmentLevel
        val newLvl = oldLvl + 1

        soundSynth.playUpgradeFanfare()
        hapticManager.heavyImpact()

        _uiState.update {
            it.copy(
                credits = it.credits - cost.creditCost,
                inventory = newInv,
                equipmentLevel = newLvl,
                showUpgradeCelebration = true,
                upgradeCelebrationOldLevel = oldLvl,
                upgradeCelebrationNewLevel = newLvl
            )
        }
        saveCurrentGame()
    }

    fun onUpgradeTech(node: TechNode) {
        val st = _uiState.value
        val currentLvl = st.researchLevels[node] ?: 0
        if (currentLvl >= node.maxLevel) return

        val nodeState = TechNodeState(node, currentLvl)
        if (st.credits < nodeState.nextCreditCost) return
        if (st.darkMatter < nodeState.nextDarkMatterCost) return

        val updatedResearch = st.researchLevels.toMutableMap()
        updatedResearch[node] = currentLvl + 1

        soundSynth.playUpgradeFanfare()
        hapticManager.heavyImpact()

        _uiState.update {
            it.copy(
                credits = it.credits - nodeState.nextCreditCost,
                darkMatter = it.darkMatter - nodeState.nextDarkMatterCost,
                researchLevels = updatedResearch
            )
        }
        saveCurrentGame()
    }

    fun onClaimAchievement(achievementId: String) {
        val st = _uiState.value
        if (achievementId in st.claimedAchievements) return

        val ach = st.achievements.firstOrNull { it.id == achievementId } ?: return
        if (!ach.isCompleted) return

        soundSynth.playRareDiscovery()
        hapticManager.heavyImpact()

        _uiState.update {
            it.copy(
                credits = it.credits + ach.rewardCredits,
                darkMatter = it.darkMatter + ach.rewardDarkMatter,
                claimedAchievements = it.claimedAchievements + achievementId
            )
        }
        saveCurrentGame()
    }

    fun onSendAiMessage(promptText: String) {
        if (promptText.isBlank()) return
        val userMsg = ChatMessage(
            id = System.currentTimeMillis(),
            sender = "Commander",
            text = promptText,
            isAi = false
        )

        _uiState.update {
            it.copy(
                chatMessages = it.chatMessages + userMsg,
                isAiLoading = true
            )
        }

        val st = _uiState.value
        val context = "Planet #${st.currentPlanetNumber} (${st.currentPlanet.name}, Biome: ${st.currentPlanet.biome.title}), Drill Lvl ${st.equipmentLevel} (${st.equipmentConfig.era.title}), Credits: ${st.credits}, Drones: ${st.drones.sumOf { it.count }}, Active Event: ${st.activeCosmicEvent?.type?.title ?: "None"}."

        viewModelScope.launch {
            val aiResponse = AstraAiService.queryAstraAi(promptText, context)
            val aiMsg = ChatMessage(
                id = System.currentTimeMillis() + 1,
                sender = "ASTRA-9",
                text = aiResponse,
                isAi = true
            )
            _uiState.update {
                it.copy(
                    chatMessages = it.chatMessages + aiMsg,
                    isAiLoading = false
                )
            }
        }
    }

    fun onDismissUpgradeCelebration() {
        _uiState.update { it.copy(showUpgradeCelebration = false) }
    }

    fun onDismissOfflineReward() {
        _uiState.update { it.copy(offlineReward = null) }
    }

    fun onDismissRelicDiscovery() {
        _uiState.update { it.copy(newlyDiscoveredRelic = null) }
    }

    fun onSelectTab(tabIndex: Int) {
        _uiState.update { it.copy(activeTab = tabIndex) }
    }

    fun onTravelToPlanet(planetNumber: Int) {
        val st = _uiState.value
        if (planetNumber !in st.unlockedPlanets) return
        val newPlanet = PlanetGenerator.getPlanet(planetNumber)
        soundSynth.playLaserPulse()
        _uiState.update {
            it.copy(
                currentPlanetNumber = planetNumber,
                currentPlanet = newPlanet,
                nodeCurrentHp = newPlanet.nodeMaxHp,
                nodeMaxHp = newPlanet.nodeMaxHp,
                activeTab = 0
            )
        }
        saveCurrentGame()
    }

    fun onUnlockPlanet(planetNumber: Int) {
        val st = _uiState.value
        if (planetNumber in st.unlockedPlanets) return
        val planetToUnlock = PlanetGenerator.getPlanet(planetNumber)

        if (st.credits < planetToUnlock.unlockCreditsCost) return
        val resCount = st.inventory[planetToUnlock.unlockRequiredMineral] ?: 0L
        if (resCount < planetToUnlock.unlockMineralAmount) return

        val newInv = st.inventory.toMutableMap()
        newInv[planetToUnlock.unlockRequiredMineral] = resCount - planetToUnlock.unlockMineralAmount

        soundSynth.playRareDiscovery()
        hapticManager.heavyImpact()

        _uiState.update {
            it.copy(
                credits = it.credits - planetToUnlock.unlockCreditsCost,
                inventory = newInv,
                unlockedPlanets = it.unlockedPlanets + planetNumber,
                currentPlanetNumber = planetNumber,
                currentPlanet = planetToUnlock,
                nodeCurrentHp = planetToUnlock.nodeMaxHp,
                nodeMaxHp = planetToUnlock.nodeMaxHp,
                activeTab = 0
            )
        }
        saveCurrentGame()
    }

    fun onBuyDrone(type: DroneType) {
        val st = _uiState.value
        val existingItem = st.drones.firstOrNull { it.type == type } ?: DroneFleetItem(type)
        val cost = existingItem.nextPurchaseCost
        if (st.credits < cost) return

        val updatedDrones = st.drones.map { d ->
            if (d.type == type) d.copy(count = d.count + 1) else d
        }

        soundSynth.playDrillHit(1.5f)
        _uiState.update {
            it.copy(
                credits = it.credits - cost,
                drones = updatedDrones
            )
        }
        saveCurrentGame()
    }

    fun onUpgradeDrone(type: DroneType) {
        val st = _uiState.value
        val existingItem = st.drones.firstOrNull { it.type == type } ?: return
        val cost = existingItem.upgradeCost
        if (st.credits < cost) return

        val updatedDrones = st.drones.map { d ->
            if (d.type == type) d.copy(upgradeLevel = d.upgradeLevel + 1) else d
        }

        soundSynth.playUpgradeFanfare()
        _uiState.update {
            it.copy(
                credits = it.credits - cost,
                drones = updatedDrones
            )
        }
        saveCurrentGame()
    }

    fun onFuseDrones(type: DroneType) {
        val st = _uiState.value
        val existingItem = st.drones.firstOrNull { it.type == type } ?: return
        if (existingItem.count < 2) return

        val updatedDrones = st.drones.map { d ->
            if (d.type == type) d.copy(count = d.count - 1, upgradeLevel = d.upgradeLevel + 2) else d
        }

        soundSynth.playLaserPulse()
        hapticManager.heavyImpact()
        _uiState.update { it.copy(drones = updatedDrones) }
        saveCurrentGame()
    }

    fun onBuildFacility(planetNumber: Int, type: FacilityType) {
        val st = _uiState.value
        val planetFacilities = st.facilities[planetNumber] ?: FacilityType.entries.map { PlanetFacilityState(it, 0) }
        val currentFac = planetFacilities.firstOrNull { it.type == type } ?: PlanetFacilityState(type, 0)
        val cost = currentFac.upgradeCost

        if (st.credits < cost) return
        if (type.requiredEnergy > st.energy && type != FacilityType.POWER_GENERATOR) return

        val updatedList = planetFacilities.map { f ->
            if (f.type == type) f.copy(level = f.level + 1) else f
        }
        val updatedMap = st.facilities.toMutableMap()
        updatedMap[planetNumber] = updatedList

        var newEnergy = st.energy
        if (type == FacilityType.POWER_GENERATOR) {
            newEnergy += 50
        }

        soundSynth.playUpgradeFanfare()
        _uiState.update {
            it.copy(
                credits = it.credits - cost,
                facilities = updatedMap,
                energy = newEnergy
            )
        }
        saveCurrentGame()
    }

    fun onCraftRecipe(recipe: CraftingRecipe) {
        val st = _uiState.value
        if (st.credits < recipe.creditFee) return

        for ((res, needed) in recipe.inputResources) {
            val currentCount = st.inventory[res] ?: 0L
            if (currentCount < needed) return
        }

        val newInv = st.inventory.toMutableMap()
        for ((res, needed) in recipe.inputResources) {
            newInv[res] = (newInv[res] ?: 0L) - needed
        }

        val currentCrafted = st.craftedComponents[recipe.id] ?: 0L
        val updatedCrafted = st.craftedComponents.toMutableMap()
        updatedCrafted[recipe.id] = currentCrafted + recipe.outputAmount

        soundSynth.playLaserPulse()
        _uiState.update {
            it.copy(
                credits = it.credits - recipe.creditFee,
                inventory = newInv,
                craftedComponents = updatedCrafted
            )
        }
        saveCurrentGame()
    }

    fun onBuyBoost(boost: ShopBoostType) {
        val st = _uiState.value
        if (boost.costCredits > 0L && st.credits < boost.costCredits) return
        if (boost.costDarkMatter > 0L && st.darkMatter < boost.costDarkMatter) return

        if (boost == ShopBoostType.INSTANT_OVERCHARGE) {
            soundSynth.playCritStrike()
            hapticManager.heavyImpact()
            val primeRes = st.currentPlanet.primaryMineral
            val newInv = st.inventory.toMutableMap()
            val dropCount = (50L + st.currentPlanetNumber * 10L) * 5L
            newInv[primeRes] = (newInv[primeRes] ?: 0L) + dropCount
            _uiState.update {
                it.copy(
                    credits = it.credits - boost.costCredits,
                    darkMatter = it.darkMatter - boost.costDarkMatter,
                    nodeCurrentHp = it.nodeMaxHp,
                    inventory = newInv
                )
            }
            saveCurrentGame()
            return
        }

        val expiresAt = System.currentTimeMillis() + (boost.durationSeconds * 1000L)
        val newBoost = ActiveBoost(boost, expiresAt)
        soundSynth.playRareDiscovery()

        _uiState.update {
            it.copy(
                credits = it.credits - boost.costCredits,
                darkMatter = it.darkMatter - boost.costDarkMatter,
                activeBoosts = it.activeBoosts.filter { b -> b.type != boost } + newBoost
            )
        }
        saveCurrentGame()
    }

    fun onAscendCosmic() {
        val st = _uiState.value
        val earnedShards = ((st.equipmentLevel * 2L) + st.playerLevel + st.unlockedPlanets.size).coerceAtLeast(1L)

        soundSynth.playUpgradeFanfare()
        hapticManager.heavyImpact()

        val resetPlanet = PlanetGenerator.getPlanet(1)

        _uiState.update {
            it.copy(
                credits = 500L,
                equipmentLevel = 0,
                currentPlanetNumber = 1,
                currentPlanet = resetPlanet,
                nodeCurrentHp = resetPlanet.nodeMaxHp,
                nodeMaxHp = resetPlanet.nodeMaxHp,
                unlockedPlanets = setOf(1),
                inventory = mapOf(ResourceType.STONE to 50L),
                playerLevel = 1,
                playerXp = 0L,
                xpRequired = 100L,
                drones = DroneType.entries.map { d -> DroneFleetItem(d, count = if (d == DroneType.STONE_COLLECTOR) 1 else 0) },
                facilities = emptyMap(),
                activeBoosts = emptyList(),
                ascensionCount = it.ascensionCount + 1,
                ascensionStellarShards = it.ascensionStellarShards + earnedShards,
                activeTab = 0
            )
        }
        saveCurrentGame()
    }

    fun onToggleMute() {
        _uiState.update {
            val newMute = !it.isMuted
            soundSynth.isMuted = newMute
            hapticManager.isEnabled = !newMute
            it.copy(isMuted = newMute)
        }
        saveCurrentGame()
    }

    fun onSetSoundVolume(vol: Float) {
        val clamped = vol.coerceIn(0f, 1f)
        soundSynth.soundVolume = clamped
        _uiState.update { it.copy(soundVolume = clamped) }
        saveCurrentGame()
    }

    fun onSetMusicVolume(vol: Float) {
        val clamped = vol.coerceIn(0f, 1f)
        _uiState.update { it.copy(musicVolume = clamped) }
        saveCurrentGame()
    }

    fun onToggleVibration() {
        _uiState.update {
            val next = !it.vibrationEnabled
            hapticManager.isEnabled = next && !it.isMuted
            if (next) hapticManager.click()
            it.copy(vibrationEnabled = next)
        }
        saveCurrentGame()
    }

    fun onSetGraphicsQuality(quality: String) {
        _uiState.update { it.copy(graphicsQuality = quality) }
        saveCurrentGame()
    }

    fun onToggleReducedAnimations() {
        _uiState.update { it.copy(reducedAnimations = !it.reducedAnimations) }
        saveCurrentGame()
    }

    fun onSetLanguage(language: String) {
        _uiState.update { it.copy(selectedLanguage = language) }
        saveCurrentGame()
    }

    fun onToggleScreenShake() {
        _uiState.update { it.copy(screenShakeEnabled = !it.screenShakeEnabled) }
        saveCurrentGame()
    }

    fun onToggleBatterySaver() {
        _uiState.update { it.copy(batterySaverEnabled = !it.batterySaverEnabled) }
        saveCurrentGame()
    }

    fun onResetAllProgress() {
        viewModelScope.launch {
            repository.resetGame()
            val resetPlanet = PlanetGenerator.getPlanet(1)
            _uiState.value = GameUiState(
                currentPlanet = resetPlanet,
                nodeCurrentHp = resetPlanet.nodeMaxHp,
                nodeMaxHp = resetPlanet.nodeMaxHp
            )
        }
    }

    private fun saveCurrentGame() {
        viewModelScope.launch {
            val st = _uiState.value
            val loaded = LoadedGameState(
                credits = st.credits,
                darkMatter = st.darkMatter,
                energy = st.energy,
                playerLevel = st.playerLevel,
                playerXp = st.playerXp,
                equipmentLevel = st.equipmentLevel,
                currentPlanetNumber = st.currentPlanetNumber,
                unlockedPlanets = st.unlockedPlanets,
                inventory = st.inventory,
                drones = st.drones,
                facilities = st.facilities,
                discoveredRelics = st.discoveredRelics,
                craftedComponents = st.craftedComponents,
                activeBoosts = st.activeBoosts,
                statsTotalTaps = st.statsTotalTaps,
                statsTotalOresMined = st.statsTotalOresMined,
                statsTotalCreditsEarned = st.statsTotalCreditsEarned,
                ascensionCount = st.ascensionCount,
                ascensionStellarShards = st.ascensionStellarShards,
                soundVolume = st.soundVolume,
                isMuted = st.isMuted,
                screenShakeEnabled = st.screenShakeEnabled,
                batterySaverEnabled = st.batterySaverEnabled,
                research = st.researchLevels,
                claimedAchievements = st.claimedAchievements,
                lastSavedTimestampMs = System.currentTimeMillis()
            )
            repository.saveGame(loaded)
        }
    }

    private fun calculateXpRequired(level: Int): Long {
        return (100.0 * 1.15.pow(level.toDouble())).roundToLong()
    }
}
