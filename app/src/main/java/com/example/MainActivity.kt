package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.TabNavigation
import com.example.ui.components.TopHud
import com.example.ui.screens.AscensionDialog
import com.example.ui.screens.AstraAiDialog
import com.example.ui.screens.EmpireScreen
import com.example.ui.screens.EquipmentScreen
import com.example.ui.screens.InventoryScreen
import com.example.ui.screens.MiningScreen
import com.example.ui.screens.OfflineRewardsDialog
import com.example.ui.screens.PlanetsScreen
import com.example.ui.screens.SettingsDialog
import com.example.ui.screens.UpgradeCelebrationDialog
import com.example.ui.theme.AstraforgeTheme
import com.example.viewmodel.GameViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AstraforgeTheme {
                AstraforgeApp()
            }
        }
    }
}

@Composable
fun AstraforgeApp(
    viewModel: GameViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var showSettingsDialog by remember { mutableStateOf(false) }
    var showAscensionDialog by remember { mutableStateOf(false) }
    var showAstraAiDialog by remember { mutableStateOf(false) }

    // System Back Handler
    BackHandler(enabled = uiState.activeTab != 0 || showSettingsDialog || showAscensionDialog || showAstraAiDialog) {
        when {
            showAstraAiDialog -> showAstraAiDialog = false
            showSettingsDialog -> showSettingsDialog = false
            showAscensionDialog -> showAscensionDialog = false
            uiState.activeTab != 0 -> viewModel.onSelectTab(0)
        }
    }

    val upgradeCost = uiState.nextUpgradeCost
    val hasCredits = uiState.credits >= upgradeCost.creditCost
    val resCount = uiState.inventory[upgradeCost.requiredResourceType] ?: 0L
    val canUpgradeEquipment = hasCredits && resCount >= upgradeCost.requiredResourceAmount && uiState.equipmentLevel < 1000

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color(0xFF070A12),
        topBar = {
            TopHud(
                currentPlanet = uiState.currentPlanet,
                galaxy = uiState.currentGalaxy,
                credits = uiState.credits,
                darkMatter = uiState.darkMatter,
                energy = uiState.energy,
                playerLevel = uiState.playerLevel,
                playerXp = uiState.playerXp,
                xpRequired = uiState.xpRequired,
                activeBoosts = uiState.activeBoosts,
                activeCosmicEvent = uiState.activeCosmicEvent,
                isMuted = uiState.isMuted,
                onToggleMute = { viewModel.onToggleMute() },
                onOpenSettings = { showSettingsDialog = true },
                onOpenAscension = { showAscensionDialog = true },
                onOpenAstraAi = { showAstraAiDialog = true }
            )
        },
        bottomBar = {
            TabNavigation(
                selectedTabIndex = uiState.activeTab,
                onTabSelected = { viewModel.onSelectTab(it) },
                canUpgradeEquipment = canUpgradeEquipment
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFF070A12))
        ) {
            when (uiState.activeTab) {
                0 -> MiningScreen(
                    uiState = uiState,
                    particleManager = viewModel.particleManager,
                    onMineTap = { viewModel.onMineTap(it) },
                    onNavigateToEquipment = { viewModel.onSelectTab(1) },
                    onNavigateToPlanets = { viewModel.onSelectTab(2) },
                    onOpenAiChat = { showAstraAiDialog = true }
                )
                1 -> EquipmentScreen(
                    uiState = uiState,
                    onUpgradeEquipment = { viewModel.onUpgradeEquipment() }
                )
                2 -> PlanetsScreen(
                    uiState = uiState,
                    onTravelToPlanet = { viewModel.onTravelToPlanet(it) },
                    onUnlockPlanet = { viewModel.onUnlockPlanet(it) }
                )
                3 -> InventoryScreen(
                    uiState = uiState,
                    onCraftRecipe = { viewModel.onCraftRecipe(it) },
                    onClaimAchievement = { viewModel.onClaimAchievement(it) }
                )
                4 -> EmpireScreen(
                    uiState = uiState,
                    onBuyDrone = { viewModel.onBuyDrone(it) },
                    onUpgradeDrone = { viewModel.onUpgradeDrone(it) },
                    onFuseDrones = { viewModel.onFuseDrones(it) },
                    onBuildFacility = { p, f -> viewModel.onBuildFacility(p, f) },
                    onUpgradeTech = { viewModel.onUpgradeTech(it) },
                    onBuyBoost = { viewModel.onBuyBoost(it) }
                )
            }
        }
    }

    // Modal Dialogs
    if (showSettingsDialog) {
        SettingsDialog(
            uiState = uiState,
            onDismiss = { showSettingsDialog = false },
            onSetVolume = { viewModel.onSetSoundVolume(it) },
            onToggleMute = { viewModel.onToggleMute() },
            onToggleShake = { viewModel.onToggleScreenShake() },
            onToggleBatterySaver = { viewModel.onToggleBatterySaver() },
            onResetGame = { viewModel.onResetAllProgress() }
        )
    }

    if (showAscensionDialog) {
        AscensionDialog(
            uiState = uiState,
            onAscend = { viewModel.onAscendCosmic() },
            onDismiss = { showAscensionDialog = false }
        )
    }

    if (uiState.showUpgradeCelebration) {
        UpgradeCelebrationDialog(
            newLevel = uiState.upgradeCelebrationNewLevel,
            animationTimeMs = uiState.animationTimeMs,
            onDismiss = { viewModel.onDismissUpgradeCelebration() }
        )
    }

    uiState.offlineReward?.let { reward ->
        OfflineRewardsDialog(
            summary = reward,
            onClaim = { viewModel.onDismissOfflineReward() }
        )
    }

    if (showAstraAiDialog) {
        AstraAiDialog(
            messages = uiState.chatMessages,
            isLoading = uiState.isAiLoading,
            onSendMessage = { viewModel.onSendAiMessage(it) },
            onDismiss = { showAstraAiDialog = false }
        )
    }
}
