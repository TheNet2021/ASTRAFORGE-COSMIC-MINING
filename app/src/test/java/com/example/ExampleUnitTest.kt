package com.example

import com.example.model.EquipmentEra
import com.example.model.EquipmentSystem
import com.example.model.PlanetGenerator
import com.example.model.ResourceType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun testEquipmentLevels_0_to_1000_areDeterministicAndDistinct() {
        // Compare milestones requested in user prompt: levels 0, 1, 10, 50, 100, 500, 999, and 1000
        val l0 = EquipmentSystem.getEquipmentVisualConfig(0)
        val l1 = EquipmentSystem.getEquipmentVisualConfig(1)
        val l10 = EquipmentSystem.getEquipmentVisualConfig(10)
        val l50 = EquipmentSystem.getEquipmentVisualConfig(50)
        val l100 = EquipmentSystem.getEquipmentVisualConfig(100)
        val l500 = EquipmentSystem.getEquipmentVisualConfig(500)
        val l999 = EquipmentSystem.getEquipmentVisualConfig(999)
        val l1000 = EquipmentSystem.getEquipmentVisualConfig(1000)

        // Verify Era classification
        assertEquals(EquipmentEra.PRIMITIVE_SCRAP, l0.era)
        assertEquals(EquipmentEra.HEAVY_INDUSTRIAL, l50.era)
        assertEquals(EquipmentEra.ADVANCED_EXTRACTOR, l100.era)
        assertEquals(EquipmentEra.INTERSTELLAR_COLOSSUS, l500.era)
        assertEquals(EquipmentEra.COSMIC_TRANSCENDENT, l999.era)
        assertEquals(EquipmentEra.OMEGA_ASTRAFORGE, l1000.era)

        // Verify consecutive levels 0 and 1 have distinct geometry
        assertNotEquals(l0.drillConeLengthFactor, l1.drillConeLengthFactor)
        assertNotEquals(l0.microGeometryDetail, l1.microGeometryDetail)

        // Verify Level 1000 has singularity vortex and max rings
        assertTrue(l1000.hasSingularityVortex)
        assertEquals(4, l1000.energyRings)

        // Verify determinism: evaluating level 500 twice yields exact same config
        val l500Retry = EquipmentSystem.getEquipmentVisualConfig(500)
        assertEquals(l500.drillConeLengthFactor, l500Retry.drillConeLengthFactor, 0.0001f)
        assertEquals(l500.primaryColor, l500Retry.primaryColor)
    }

    @Test
    fun testPlanetGeneration_10000Planets_DeterministicAndValid() {
        val p1 = PlanetGenerator.getPlanet(1)
        val p2 = PlanetGenerator.getPlanet(2)
        val p100 = PlanetGenerator.getPlanet(100)
        val p5000 = PlanetGenerator.getPlanet(5000)
        val p10000 = PlanetGenerator.getPlanet(10000)

        assertEquals(1, p1.sector)
        assertEquals(1, p100.sector)
        assertEquals(50, p5000.sector)
        assertEquals(100, p10000.sector)

        // Verify seed reproducibility
        val p42_first = PlanetGenerator.getPlanet(42)
        val p42_second = PlanetGenerator.getPlanet(42)
        assertEquals(p42_first.name, p42_second.name)
        assertEquals(p42_first.biome, p42_second.biome)
        assertEquals(p42_first.surfaceColor, p42_second.surfaceColor)

        // Verify distinct names across worlds
        assertNotEquals(p1.name, p2.name)
        assertNotNull(p1.primaryMineral)
        assertNotNull(p10000.primaryMineral)
    }

    @Test
    fun testResourceSystem_16MineralsAvailable() {
        val minerals = ResourceType.entries
        assertEquals(16, minerals.size)
        assertEquals(ResourceType.STONE, minerals.first())
        assertEquals(ResourceType.OMEGA_CORE, minerals.last())
    }

    @Test
    fun testEfficiencyStatAndThresholdModuleUnlocks() {
        val stats0 = EquipmentSystem.getEquipmentStats(0)
        assertEquals(100f, stats0.efficiencyRating, 0.01f)
        assertEquals(100, stats0.efficiencyPercent)
        // At level 0, base efficiency (100%) has not reached 150% threshold
        org.junit.Assert.assertFalse(stats0.hasGlowingVents)
        org.junit.Assert.assertFalse(stats0.hasRotatingGears)
        org.junit.Assert.assertFalse(stats0.hasModularExtensions)

        // Threshold 1: Glowing Vents (>= 150%) - reached at level 20
        val stats20 = EquipmentSystem.getEquipmentStats(20)
        assertEquals(150f, stats20.efficiencyRating, 0.01f)
        assertTrue(stats20.hasGlowingVents)
        org.junit.Assert.assertFalse(stats20.hasRotatingGears)

        // Threshold 2: Rotating Gears (>= 250%) - reached at level 60
        val stats60 = EquipmentSystem.getEquipmentStats(60)
        assertEquals(250f, stats60.efficiencyRating, 0.01f)
        assertTrue(stats60.hasGlowingVents)
        assertTrue(stats60.hasRotatingGears)
        org.junit.Assert.assertFalse(stats60.hasModularExtensions)

        // Threshold 3: Modular Outrigger Extensions (>= 400%) - reached at level 120
        val stats120 = EquipmentSystem.getEquipmentStats(120)
        assertEquals(400f, stats120.efficiencyRating, 0.01f)
        assertTrue(stats120.hasModularExtensions)
        org.junit.Assert.assertFalse(stats120.hasResonanceArcs)

        // Threshold 4: Harmonic Plasma Resonance Arcs (>= 650%) - reached at level 220
        val stats220 = EquipmentSystem.getEquipmentStats(220)
        assertEquals(650f, stats220.efficiencyRating, 0.01f)
        assertTrue(stats220.hasResonanceArcs)
        org.junit.Assert.assertFalse(stats220.hasQuadExtensions)

        // Threshold 5: Quad Monolith Matrix (>= 900%) - reached at level 320
        val stats320 = EquipmentSystem.getEquipmentStats(320)
        assertEquals(900f, stats320.efficiencyRating, 0.01f)
        assertTrue(stats320.hasQuadExtensions)

        // Verify dynamic effective efficiency calculation with Overdrive
        val effectiveOverdrive = EquipmentSystem.calculateEffectiveEfficiency(
            baseLevel = 0,
            rpmResearchLevel = 2,
            isOverdrive = true
        )
        // Base 100 * (1 + 0.40) * 1.8 = 252% -> Overdrive dynamically unlocks gears!
        assertTrue(effectiveOverdrive >= 250f)
        val unlockedWithOverdrive = com.example.model.EfficiencyModule.getUnlockedModules(effectiveOverdrive)
        assertTrue(unlockedWithOverdrive.contains(com.example.model.EfficiencyModule.GLOWING_VENTS))
        assertTrue(unlockedWithOverdrive.contains(com.example.model.EfficiencyModule.ROTATING_GEARS))
    }
}

