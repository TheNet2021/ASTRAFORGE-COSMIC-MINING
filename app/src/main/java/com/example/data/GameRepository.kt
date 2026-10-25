package com.example.data

import com.example.model.ActiveBoost
import com.example.model.DroneFleetItem
import com.example.model.DroneType
import com.example.model.FacilityType
import com.example.model.PlanetFacilityState
import com.example.model.RelicType
import com.example.model.ResourceType
import com.example.model.ShopBoostType
import com.example.model.TechNode
import org.json.JSONArray
import org.json.JSONObject

data class LoadedGameState(
    val credits: Long,
    val darkMatter: Long,
    val energy: Int,
    val playerLevel: Int,
    val playerXp: Long,
    val equipmentLevel: Int,
    val currentPlanetNumber: Int,
    val unlockedPlanets: Set<Int>,
    val inventory: Map<ResourceType, Long>,
    val drones: List<DroneFleetItem>,
    val facilities: Map<Int, List<PlanetFacilityState>>,
    val discoveredRelics: Set<RelicType>,
    val craftedComponents: Map<String, Long>,
    val activeBoosts: List<ActiveBoost>,
    val statsTotalTaps: Long,
    val statsTotalOresMined: Long,
    val statsTotalCreditsEarned: Long,
    val ascensionCount: Int,
    val ascensionStellarShards: Long,
    val soundVolume: Float,
    val isMuted: Boolean,
    val screenShakeEnabled: Boolean,
    val batterySaverEnabled: Boolean,
    val research: Map<TechNode, Int>,
    val claimedAchievements: Set<String>,
    val lastSavedTimestampMs: Long
)

class GameRepository(private val dao: GameSaveDao) {

    suspend fun loadGame(): LoadedGameState {
        val entity = dao.getSaveDirect() ?: GameSaveEntity()

        val unlocked = entity.unlockedPlanetsCsv
            .split(",")
            .mapNotNull { it.trim().toIntOrNull() }
            .toSet()
            .ifEmpty { setOf(1) }

        val inv = mutableMapOf<ResourceType, Long>()
        try {
            val json = JSONObject(entity.inventoryJson)
            val keys = json.keys()
            while (keys.hasNext()) {
                val key = keys.next()
                val type = ResourceType.fromId(key)
                inv[type] = json.optLong(key, 0L)
            }
        } catch (_: Exception) {}

        val droneList = mutableListOf<DroneFleetItem>()
        try {
            val arr = JSONArray(entity.dronesJson)
            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)
                val type = DroneType.fromId(obj.getString("type"))
                val count = obj.getInt("count")
                val lvl = obj.getInt("level")
                val p = obj.getInt("planet")
                droneList.add(DroneFleetItem(type, count, lvl, p))
            }
        } catch (_: Exception) {}

        val facMap = mutableMapOf<Int, List<PlanetFacilityState>>()
        try {
            val json = JSONObject(entity.facilitiesJson)
            val keys = json.keys()
            while (keys.hasNext()) {
                val pStr = keys.next()
                val pNum = pStr.toIntOrNull() ?: continue
                val arr = json.getJSONArray(pStr)
                val list = mutableListOf<PlanetFacilityState>()
                for (j in 0 until arr.length()) {
                    val obj = arr.getJSONObject(j)
                    val fType = FacilityType.entries.firstOrNull { it.id == obj.getString("type") } ?: continue
                    val lvl = obj.getInt("level")
                    list.add(PlanetFacilityState(fType, lvl))
                }
                facMap[pNum] = list
            }
        } catch (_: Exception) {}

        val relics = entity.discoveredRelicsCsv
            .split(",")
            .mapNotNull { id -> RelicType.entries.firstOrNull { it.id == id.trim() } }
            .toSet()

        val crafted = mutableMapOf<String, Long>()
        try {
            val json = JSONObject(entity.craftedComponentsJson)
            val keys = json.keys()
            while (keys.hasNext()) {
                val k = keys.next()
                crafted[k] = json.optLong(k, 0L)
            }
        } catch (_: Exception) {}

        val boosts = mutableListOf<ActiveBoost>()
        try {
            val arr = JSONArray(entity.activeBoostsJson)
            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)
                val bType = ShopBoostType.entries.firstOrNull { it.id == obj.getString("type") } ?: continue
                val exp = obj.getLong("expiresAt")
                if (System.currentTimeMillis() < exp) {
                    boosts.add(ActiveBoost(bType, exp))
                }
            }
        } catch (_: Exception) {}

        val resMap = mutableMapOf<TechNode, Int>()
        try {
            val rJson = JSONObject(entity.researchJson)
            val keys = rJson.keys()
            while (keys.hasNext()) {
                val k = keys.next()
                val node = TechNode.entries.firstOrNull { it.id == k } ?: continue
                resMap[node] = rJson.optInt(k, 0)
            }
        } catch (_: Exception) {}

        val claimedAch = entity.claimedAchievementsCsv
            .split(",")
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .toSet()

        return LoadedGameState(
            credits = entity.credits,
            darkMatter = entity.darkMatter,
            energy = entity.energy,
            playerLevel = entity.playerLevel,
            playerXp = entity.playerXp,
            equipmentLevel = entity.equipmentLevel,
            currentPlanetNumber = entity.currentPlanetNumber,
            unlockedPlanets = unlocked,
            inventory = inv,
            drones = droneList,
            facilities = facMap,
            discoveredRelics = relics,
            craftedComponents = crafted,
            activeBoosts = boosts,
            statsTotalTaps = entity.statsTotalTaps,
            statsTotalOresMined = entity.statsTotalOresMined,
            statsTotalCreditsEarned = entity.statsTotalCreditsEarned,
            ascensionCount = entity.ascensionCount,
            ascensionStellarShards = entity.ascensionStellarShards,
            soundVolume = entity.soundVolume,
            isMuted = entity.isMuted,
            screenShakeEnabled = entity.screenShakeEnabled,
            batterySaverEnabled = entity.batterySaverEnabled,
            research = resMap,
            claimedAchievements = claimedAch,
            lastSavedTimestampMs = entity.lastSavedTimestampMs
        )
    }

    suspend fun saveGame(state: LoadedGameState) {
        val invObj = JSONObject()
        state.inventory.forEach { (res, count) -> invObj.put(res.id, count) }

        val dronesArr = JSONArray()
        state.drones.forEach { d ->
            val obj = JSONObject()
            obj.put("type", d.type.id)
            obj.put("count", d.count)
            obj.put("level", d.upgradeLevel)
            obj.put("planet", d.assignedPlanet)
            dronesArr.put(obj)
        }

        val facObj = JSONObject()
        state.facilities.forEach { (planetNum, facList) ->
            val arr = JSONArray()
            facList.forEach { f ->
                val o = JSONObject()
                o.put("type", f.type.id)
                o.put("level", f.level)
                arr.put(o)
            }
            facObj.put(planetNum.toString(), arr)
        }

        val relicsCsv = state.discoveredRelics.joinToString(",") { it.id }

        val craftedObj = JSONObject()
        state.craftedComponents.forEach { (recipeId, count) -> craftedObj.put(recipeId, count) }

        val boostsArr = JSONArray()
        state.activeBoosts.filter { !it.isExpired }.forEach { b ->
            val o = JSONObject()
            o.put("type", b.type.id)
            o.put("expiresAt", b.expiresAtTimestampMs)
            boostsArr.put(o)
        }

        val researchObj = JSONObject()
        state.research.forEach { (node, lvl) -> researchObj.put(node.id, lvl) }

        val entity = GameSaveEntity(
            id = 1,
            credits = state.credits,
            darkMatter = state.darkMatter,
            energy = state.energy,
            playerLevel = state.playerLevel,
            playerXp = state.playerXp,
            equipmentLevel = state.equipmentLevel,
            currentPlanetNumber = state.currentPlanetNumber,
            unlockedPlanetsCsv = state.unlockedPlanets.joinToString(","),
            inventoryJson = invObj.toString(),
            dronesJson = dronesArr.toString(),
            facilitiesJson = facObj.toString(),
            discoveredRelicsCsv = relicsCsv,
            craftedComponentsJson = craftedObj.toString(),
            activeBoostsJson = boostsArr.toString(),
            statsTotalTaps = state.statsTotalTaps,
            statsTotalOresMined = state.statsTotalOresMined,
            statsTotalCreditsEarned = state.statsTotalCreditsEarned,
            ascensionCount = state.ascensionCount,
            ascensionStellarShards = state.ascensionStellarShards,
            soundVolume = state.soundVolume,
            isMuted = state.isMuted,
            screenShakeEnabled = state.screenShakeEnabled,
            batterySaverEnabled = state.batterySaverEnabled,
            researchJson = researchObj.toString(),
            claimedAchievementsCsv = state.claimedAchievements.joinToString(","),
            lastSavedTimestampMs = System.currentTimeMillis()
        )

        dao.insertOrUpdate(entity)
    }

    suspend fun resetGame() {
        dao.clearSave()
    }
}
