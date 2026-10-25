# ASTRAFORGE: COSMIC MINING — Master Architecture & Implementation Plan

ASTRAFORGE: COSMIC MINING is a premium sci-fi mining progression game designed specifically for portrait mobile Android devices. Players harness evolving drill machinery through 1,001 distinct visual equipment states, explore 10,000 procedurally generated planets across 100 cosmic sectors, deploy automated drone fleets, construct planetary industrial empires, and research advanced technologies.

---

## User Review & Critical Decisions

> [!IMPORTANT]
> The following architectural and design decisions incorporate the user's clarified preferences from Phase 1:

- **Visual Presentation Engine**: Procedural dynamic 2D canvas with layered particle FX. Every equipment level (0 to 1,000) and planet (1 to 10,000) is rendered dynamically using custom Jetpack Compose `Canvas` drawing scopes, multi-layered geometry calculations, procedural atmospheric lighting, and high-performance particle emitters.
- **Economy Pacing & Balance**: Deep strategic economy with deliberate, satisfying industrial expansion. Early milestones reward tactical resource allocation; mid-game unlocks automated drone networks and empire outposts; end-game features Cosmic Ascension and legendary planet expeditions.
- **Sensory Feedback**: Full procedural sci-fi audio effects (synthesized via Android `AudioTrack` and tone generation), dynamic tactile haptics, screen shake on heavy extraction hits, and mineral deposit particle bursts enabled by default (with user toggles in Settings).
- **Data Persistence**: Android Jetpack Room Database with reactive Kotlin Coroutines/Flow, persisting complete player state, unlocked worlds, drone fleets, infrastructure levels, inventory, achievements, and offline timestamps.

---

## 1. Overview & Core Concept

- **Tagline**: *MINE WORLDS. FORGE YOUR DESTINY.*
- **Platform & Form Factor**: Native Android (Jetpack Compose), optimized for portrait 9:16 mobile displays with edge-to-edge system bar insets and safe area padding.
- **Core Loop**:
  1. **Mine**: Tap-to-drill active mining and automated extractor harvesting on the active planet. Reactive deposits show damage, sparks, and mineral depletion.
  2. **Collect & Craft**: Amass ores, refine raw materials into advanced composites, craft precision components, and research planetary technologies.
  3. **Evolve Machinery**: Upgrade mining rig through 1,001 distinct, deterministic visual models spanning 9 technological eras.
  4. **Expand Across the Cosmos**: Travel through 100 sectors to discover and exploit 10,000 unique procedural planets.
  5. **Automate & Colonize**: Deploy specialized mining drone fleets and construct surface infrastructure (outposts, refineries, power plants, research labs).
  6. **Cosmic Ascension**: Prestige system resetting local operations in exchange for Stellar Relics and permanent cosmic perks.

---

## 2. User Experience & Visual Design

### Navigation & Top HUD Layout
- **Fixed Top HUD**:
  - Current Planet & Sector indicator (`Sector 01 // Planet 0004: Zephyria-IV`)
  - Player Level & Experience bar
  - Primary Currencies: Cosmic Credits (₡), Dark Matter (Ω), Energy/Power (⚡)
  - Settings & Sound toggles
- **Main 5-Tab Navigation Bar**:
  1. ⛏️ **Mine**: Live planetary surface, active drill rig, clickable mineral nodes, drone activity, real-time particle feedback, and active mining timer.
  2. ⚙️ **Equipment**: 1,001-level drill inspection viewport, current vs. next level visual preview, attribute delta, and upgrade transformation triggers.
  3. 🪐 **Planets**: Sector browser (100 sectors x 100 planets), planet preview card, atmospheric scanner, colonization stats, and warp engine.
  4. 🎒 **Inventory & Crafting**: Categorized resource vaults (15+ mineral species), artifact relics journal, component workshop, and tech tree research lab.
  5. 🛰️ **Empire & Shop**: Drone Fleet hangar (7 drone archetypes & fusion), planetary infrastructure manager, temporary boost canisters, and cosmic store.

### Visual Themes & Aesthetic Tokens
- **Backgrounds**: Deep cosmic space `#070A12`, obsidian navy `#0B101E`, stellar nebula gradients.
- **Primary Accents**: Neon cyan `#00F0FF`, electric violet `#8A2BE2`, hyperdrive turquoise `#05D5AA`.
- **Rarity Palette**:
  - *Common* (Stone, Iron, Copper, Coal): Muted slate `#8892B0`, rust `#C86D51`, copper bronze `#D9825B`
  - *Precious* (Gold, Silver, Platinum, Titanium): Brilliant auric `#FFD700`, lustrous silver `#E2E8F0`, titanium blue `#70A5D6`
  - *Exotic* (Uranium, Quartz, Diamonds, Emeralds): Toxic emerald `#10B981`, prismatic cyan `#38BDF8`, radiant diamond `#E0F2FE`
  - *Cosmic & Alien* (Alien Crystals, Cosmic Minerals, Planetary Cores): Violet plasma `#C084FC`, cosmic magenta `#F43F5E`, singularity gold `#FBBF24`
- **Typography**: Clean, high-tech mono-spaced headers with legible body hierarchy.

---

## 3. Modular Equipment Visual Evolution (Levels 0 – 1000)

Every individual level from 0 to 1,000 features a mathematically deterministic, visible architectural evolution generated on the Compose Canvas:

```
┌────────────────────────────────────────────────────────────────────────┐
│                   EQUIPMENT VISUAL ERAS (LEVELS 0-1000)                │
├───────────────┬────────────────────────────────────────────────────────┤
│ Level Range   │ Visual Paradigm & Mechanical Anatomy                   │
├───────────────┼────────────────────────────────────────────────────────┤
│ Levels 0–49   │ Primitive Scrap: Rusty steel bits, exposed copper      │
│               │ wiring, hand-crank pistons, rattling exhaust pipe.     │
├───────────────┼────────────────────────────────────────────────────────┤
│ Levels 50–99  │ Heavy Industrial: Reinforced dual chassis, hydraulic   │
│               │ shock absorbers, diamond carbide triple-drill head.    │
├───────────────┼────────────────────────────────────────────────────────┤
│ Levels 100–199│ Advanced Extractor: Articulated quad-piston arms,      │
│               │ high-RPM counter-rotating blades, luminous core.       │
├───────────────┼────────────────────────────────────────────────────────┤
│ Levels 200–349│ Plasma Rig: Magnetic stabilization rings, ionization   │
│               │ chambers, twin thermal heatsinks, plasma cutter tip.   │
├───────────────┼────────────────────────────────────────────────────────┤
│ Levels 350–499│ Quantum Harvester: Levitating chassis segments, flux   │
│               │ conduits, crystalline emitters, holographic HUD rings. │
├───────────────┼────────────────────────────────────────────────────────┤
│ Levels 500–699│ Interstellar Colossus: Multi-ring gyro-rotors, heavy   │
│               │ orbital chassis, dual laser arrays, antimatter injector│
├───────────────┼────────────────────────────────────────────────────────┤
│ Levels 700–899│ Xeno-Gravitational: Alien biomechanical plating,      │
│               │ gravity distortion orbs, singularity siphon beams.     │
├───────────────┼────────────────────────────────────────────────────────┤
│ Levels 900–999│ Cosmic Transcendent: Celestial star-forged mantle,     │
│               │ hyper-dimensional geometry, solar corona particle aura │
├───────────────┼────────────────────────────────────────────────────────┤
│ Level 1000    │ THE OMEGA ASTRAFORGE: Ultimate celestial monolith,     │
│               │ triple counter-rotating planetary rings, event-horizon │
│               │ drill vortex, radiant pulsar light beam.               │
└───────────────┴────────────────────────────────────────────────────────┘
```

### Deterministic Procedural Model Synthesis
A deterministic formula derives parameters from level $L$:
- `headType = (L % 8)`: Chisel, Dual-Cone, Diamond-Spiral, Tri-Cutter, Plasma-Emitter, Laser-Array, Singularity-Vortex, Omega-Spire.
- `plateTier = L / 25`: Changes plating materials (Rust -> Iron -> Titanium -> Carbon -> Gold -> Obsidian -> Alien Alloy -> Stellarite).
- `armCount = 2 + (L / 125)`: Scales from 2 to 10 articulated stabilizer arms.
- `energyRings = (L >= 200) ? min(5, (L - 200) / 160 + 1) : 0`: Gyroscopic spinning rings with level-dependent speeds.
- `coreGlowIntensity = (L % 50) / 50f`: Pulse dynamics and sub-level geometry refinements.

---

## 4. Deterministic Planetary Generation (10,000 Worlds)

Planets are deterministically evaluated on demand using a 64-bit seed generated from their index (1 to 10,000):
- **Sectors (1 to 100)**: Each sector has 100 planets.
  - *Sectors 1–10*: Terrestrial & Barren Rocky Worlds (Stone, Coal, Iron, Copper)
  - *Sectors 11–25*: Molten & Volcanic Worlds (Sulfur, Titanium, Obsidian, Magma Cores)
  - *Sectors 26–50*: Crystalline & Frozen Moons (Quartz, Diamonds, Emeralds, Liquid Methane)
  - *Sectors 51–75*: Toxic Gas Harvesters & Radioactive Enclaves (Uranium, Heavy Isotopes, Plasma)
  - *Sectors 76–95*: Ancient Alien Necro-Spheres (Xeno-Artifacts, Chrono-Gems, Biometal)
  - *Sectors 96–100*: Legendary Cosmic Singularity Worlds (Stellarite, Singularity Shards, Omega Cores)
- **Procedural Canvas Rendering**:
  - Procedural atmosphere gradient and sun flare lighting angle.
  - Surface curvature with seed-generated crater clusters, tectonic fissures, and mountain ridges.
  - Planetary rings (tilt, multi-band color, transparency).
  - Ambient orbiting moons and celestial dust belts.
  - Mineral outcrop positions reflecting current node integrity.

---

## 5. Technical Architecture & Data Strategy

```
┌────────────────────────────────────────────────────────────────────────┐
│                        ASTRAFORGE SYSTEM DIAGRAM                       │
├────────────────────────────────────────────────────────────────────────┤
│                                                                        │
│   ┌──────────────────────────────────────────────────────────────┐     │
│   │                         UI LAYER                             │     │
│   │  [TopHUD]  [MiningCanvas]  [EquipmentViewer]  [EmpireGrid]   │     │
│   │  [PlanetSelector]  [CraftingWorkshop]  [ShopDialogs]         │     │
│   └──────────────────────────────┬───────────────────────────────┘     │
│                                  │ (StateFlow / Events)                │
│   ┌──────────────────────────────▼───────────────────────────────┐     │
│   │                    GAME VIEW MODEL & ENGINE                  │     │
│   │  - GameLoop (Tick Coroutine: 60Hz FX / 1Hz Economy)          │     │
│   │  - MiningEngine (Damage Calc, Critical Hits, Drops)          │     │
│   │  - DroneManager (Automated Harvesters Tick)                  │     │
│   │  - EmpireManager (Infrastructure Passive Rate Calc)          │     │
│   │  - BoostManager (Temporary Buff Expiry Timers)               │     │
│   │  - AudioSynth (Procedural Laser, Drill, Clink AudioTrack)   │     │
│   └──────────────┬───────────────────────────────┬───────────────┘     │
│                  │                               │                     │
│   ┌──────────────▼─────────────┐   ┌─────────────▼────────────────┐    │
│   │ PROCEDURAL GENERATORS      │   │ REPOSITORY & ROOM PERSISTENCE│    │
│   │ - EquipmentVisualGenerator │   │ - GameSaveEntity & Dao       │    │
│   │ - PlanetVisualGenerator    │   │ - OfflineTimeEvaluator       │    │
│   │ - MineralDefinitionCatalog │   │ - Auto-Save & Manual Export  │    │
│   └────────────────────────────┘   └──────────────────────────────┘    │
│                                                                        │
└────────────────────────────────────────────────────────────────────────┘
```

### Key Modules:
1. **Procedural Generators**:
   - `EquipmentVisualGenerator`: Pure functional model evaluating level $0..1000$ into geometry primitives (drawPaths, polygons, rotation vectors, colors).
   - `PlanetVisualGenerator`: Seed-based generator creating unique atmospheres, terrain bands, ring geometries, and deposit layout.
2. **Audio & Sensory Feedback Engine**:
   - `AudioSynth`: Synthesizes authentic retro-sci-fi audio via Android's `AudioTrack` without external bloated MP3 assets. Generates drill hums, laser pulses, metal strike clicks, and level-up arpeggios.
   - `HapticFeedback`: Contextual vibration pulses for mining impacts and upgrade fanfares.
3. **Economy & Progression Engine**:
   - `MiningEngine`: Calculates tap damage = `(BaseEquipmentDamage * LevelMultiplier * BoostMultiplier)`.
   - `OfflineRewards`: Computes elapsed seconds between saves, capped at player's current silo capacity, presenting a welcome-back dialog.
4. **Persistence Layer**:
   - Room Database storing active state, unlocked planet bitmap/set, drone inventories, building levels, and achievement progress.

---

## 6. Implementation Plan & Execution Sequence

### Phase 1: Foundation, Data Models & Architecture Setup
- Update `metadata.json` and `strings.xml` to **Astraforge: Cosmic Mining**.
- Define data classes for Resources (15 types), MineralNodes, EquipmentLevels (0..1000 specs), Planets (1..10000 generator specs), Drones (7 classes), and Facilities.
- Setup Room Database, GameSaveDao, and GameSaveRepository with JSON serialization for complex collections.

### Phase 2: Procedural Visual Engines (Canvas Renderers)
- Implement `EquipmentVisualCanvas`: Procedural drawing logic for all 9 technological eras, rendering drill bits, pistons, rotating energy rings, chassis, and laser beams.
- Implement `PlanetVisualCanvas`: Seed-based dynamic planet drawing with atmospheric glow, craters, rings, and tectonic bands.
- Implement `ParticleSystem`: Lightweight, performant particle pool for sparks, rock debris, and floating resource indicators.

### Phase 3: Core Mining & Planetary Gameplay
- Connect active mining tap gestures with screen shake, particle spray, node health depletion, and mineral drop math.
- Implement automated mining ticks (player drill idle extraction + drone fleet extraction).
- Implement Planet travel and unlocking requirements based on resource thresholds.

### Phase 4: Equipment Upgrade & Transformation Experience
- Equipment screen with side-by-side level preview ($L$ vs $L+1$).
- Animated upgrade transformation sequence with milestone fanfare.
- Equipment stat scaling (Drill Power, Critical Rate, Heat Dissipation, Rare Find %).

### Phase 5: Empire, Drones, Crafting & Technology
- Drone Fleet screen: Purchase, upgrade, assign to discovered planets, and drone fusion.
- Planetary Infrastructure: Build and upgrade Outposts, Refineries, Power Stations, and Science Labs.
- Crafting & Tech Tree: Research permanent mining and economy multipliers.

### Phase 6: Shop, Boosts, Offline Progression & Settings
- Shop with Cosmic Credits and Dark Matter purchases (2x mining speed, 3x mineral drops, instant recharge).
- Offline progression calculator with welcome-back dialog showing elapsed time and collected resources.
- Settings menu with Audio Volume, Screen Shake toggle, Particle FX intensity, Battery Saver mode, and Game Reset with safety confirmation.

### Phase 7: Verification & Build
- Verify compilation with `compile_applet`.
- Ensure flawless portrait layout, zero memory leaks in Canvas particle loops, and strict adherence to Google Play policies.
