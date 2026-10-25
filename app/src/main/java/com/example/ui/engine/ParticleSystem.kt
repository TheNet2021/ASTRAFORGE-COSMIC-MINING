package com.example.ui.engine

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.random.Random

data class Particle(
    var x: Float,
    var y: Float,
    var vx: Float,
    var vy: Float,
    val color: Color,
    val size: Float,
    var life: Float = 1.0f, // 1.0 down to 0.0
    val decayRate: Float = 0.035f
)

data class FloatingText(
    val id: Long,
    var x: Float,
    var y: Float,
    val text: String,
    val color: Color,
    val isCritical: Boolean = false,
    var alpha: Float = 1.0f
)

class ParticleManager {
    val particles = CopyOnWriteArrayList<Particle>()
    val floatingTexts = CopyOnWriteArrayList<FloatingText>()
    private var textIdCounter = 0L

    fun spawnSparks(origin: Offset, count: Int, primaryColor: Color, secondaryColor: Color) {
        val rng = Random
        for (i in 0 until count) {
            val angle = rng.nextFloat() * 2f * Math.PI.toFloat()
            val speed = rng.nextFloat() * 8f + 2f
            val color = if (rng.nextBoolean()) primaryColor else secondaryColor
            val p = Particle(
                x = origin.x,
                y = origin.y,
                vx = kotlin.math.cos(angle) * speed,
                vy = kotlin.math.sin(angle) * speed - rng.nextFloat() * 3f, // upward bias
                color = color,
                size = rng.nextFloat() * 6f + 3f,
                life = 1.0f,
                decayRate = rng.nextFloat() * 0.04f + 0.025f
            )
            particles.add(p)
        }
        // Limit total particle cap to keep 60fps solid on mobile
        if (particles.size > 120) {
            val excess = particles.size - 120
            particles.removeAll(particles.take(excess).toSet())
        }
    }

    fun spawnFloatingText(origin: Offset, text: String, color: Color, isCrit: Boolean = false) {
        val rng = Random
        val jitterX = (rng.nextFloat() * 40f) - 20f
        val ft = FloatingText(
            id = ++textIdCounter,
            x = origin.x + jitterX,
            y = origin.y - 20f,
            text = text,
            color = color,
            isCritical = isCrit,
            alpha = 1.0f
        )
        floatingTexts.add(ft)
        if (floatingTexts.size > 20) {
            floatingTexts.removeAt(0)
        }
    }

    fun update() {
        // Mutate the lightweight particle objects in place and batch removals. Removing each
        // expired item from a CopyOnWriteArrayList separately clones its backing array each time.
        val expiredParticles = mutableListOf<Particle>()
        particles.forEach { p ->
            p.x += p.vx
            p.y += p.vy
            p.vy += 0.25f
            p.life -= p.decayRate
            if (p.life <= 0f) expiredParticles.add(p)
        }
        if (expiredParticles.isNotEmpty()) particles.removeAll(expiredParticles.toSet())

        val expiredTexts = mutableListOf<FloatingText>()
        floatingTexts.forEach { text ->
            text.y -= 2.2f
            text.alpha -= 0.03f
            if (text.alpha <= 0f) expiredTexts.add(text)
        }
        if (expiredTexts.isNotEmpty()) floatingTexts.removeAll(expiredTexts.toSet())
    }

    fun clear() {
        particles.clear()
        floatingTexts.clear()
    }
}
