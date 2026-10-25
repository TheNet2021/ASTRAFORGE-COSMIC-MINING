package com.example.model

enum class CosmicEventType(
    val id: String,
    val title: String,
    val iconEmoji: String,
    val alertText: String,
    val durationSeconds: Long,
    val colorHex: Long,
    val effectDescription: String
) {
    METEOR_SHOWER(
        id = "event_meteor",
        title = "Astral Meteor Storm",
        iconEmoji = "☄️",
        alertText = "Warning: Dense astral meteor storm passing through planetary orbit!",
        durationSeconds = 60L,
        colorHex = 0xFFF97316,
        effectDescription = "Automatic mining drops x2 bonus mineral yields!"
    ),
    MAGNETIC_STORM(
        id = "event_magnetic",
        title = "Solar Magnetar Surge",
        iconEmoji = "⚡",
        alertText = "Intense electromagnetic radiation detected! Drill capacitors overcharged!",
        durationSeconds = 45L,
        colorHex = 0xFF38BDF8,
        effectDescription = "Drill rotation & automatic mining speed increased by +200%!"
    ),
    ASTEROID_TRANSIT(
        id = "event_asteroid",
        title = "Prismatic Comet Transit",
        iconEmoji = "💎",
        alertText = "A luminous mineral-dense comet is skimming the exosphere!",
        durationSeconds = 60L,
        colorHex = 0xFFA855F7,
        effectDescription = "Rare relic and crystal drop rates increased by +50%!"
    ),
    GRAVITATIONAL_SURGE(
        id = "event_gravity",
        title = "Subspace Gravitational Wave",
        iconEmoji = "🌀",
        alertText = "Space-time fluctuation detected! Planetary crust density destabilized!",
        durationSeconds = 40L,
        colorHex = 0xFF34D399,
        effectDescription = "Mining node resistance reduced by 50%!"
    )
}

data class ActiveCosmicEvent(
    val type: CosmicEventType,
    val expiresAtTimestampMs: Long
) {
    val isExpired: Boolean get() = System.currentTimeMillis() >= expiresAtTimestampMs
    val remainingSeconds: Long get() = ((expiresAtTimestampMs - System.currentTimeMillis()) / 1000).coerceAtLeast(0L)
}
