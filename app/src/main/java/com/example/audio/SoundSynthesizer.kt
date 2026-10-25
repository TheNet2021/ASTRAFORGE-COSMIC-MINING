package com.example.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.sin

class SoundSynthesizer(private val context: Context) {
    private val scope = CoroutineScope(Dispatchers.Default)
    var isMuted: Boolean = false
    var soundVolume: Float = 0.8f

    fun playDrillHit(pitchMultiplier: Float = 1.0f) {
        if (isMuted || soundVolume <= 0.05f) return
        scope.launch {
            val sampleRate = 22050
            val durationMs = 60
            val numSamples = (durationMs * sampleRate) / 1000
            val buffer = ShortArray(numSamples)

            val baseFreq = (220.0 * pitchMultiplier).coerceIn(100.0, 880.0)
            for (i in 0 until numSamples) {
                val t = i.toDouble() / sampleRate
                val envelope = 1.0 - (i.toDouble() / numSamples) // linear decay
                // Combine square tone with slight metallic harmonic
                val wave = sin(2.0 * PI * baseFreq * t) + 0.3 * sin(2.0 * PI * (baseFreq * 2.8) * t)
                val sampleVal = (wave * envelope * Short.MAX_VALUE * 0.45 * soundVolume).toInt()
                buffer[i] = sampleVal.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
            }
            playPcmBuffer(buffer, sampleRate)
        }
    }

    fun playCritStrike() {
        if (isMuted || soundVolume <= 0.05f) return
        scope.launch {
            val sampleRate = 22050
            val durationMs = 120
            val numSamples = (durationMs * sampleRate) / 1000
            val buffer = ShortArray(numSamples)

            for (i in 0 until numSamples) {
                val t = i.toDouble() / sampleRate
                val envelope = (1.0 - (i.toDouble() / numSamples))
                // Heavy punch chord
                val wave = 0.5 * sin(2.0 * PI * 180.0 * t) + 0.4 * sin(2.0 * PI * 360.0 * t) + 0.3 * sin(2.0 * PI * 540.0 * t)
                val sampleVal = (wave * envelope * Short.MAX_VALUE * 0.6 * soundVolume).toInt()
                buffer[i] = sampleVal.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
            }
            playPcmBuffer(buffer, sampleRate)
        }
    }

    fun playLaserPulse() {
        if (isMuted || soundVolume <= 0.05f) return
        scope.launch {
            val sampleRate = 22050
            val durationMs = 140
            val numSamples = (durationMs * sampleRate) / 1000
            val buffer = ShortArray(numSamples)

            for (i in 0 until numSamples) {
                val progress = i.toDouble() / numSamples
                val currentFreq = 1200.0 - (progress * 800.0) // downwards pitch sweep
                val t = i.toDouble() / sampleRate
                val envelope = (1.0 - progress)
                val wave = sin(2.0 * PI * currentFreq * t)
                val sampleVal = (wave * envelope * Short.MAX_VALUE * 0.5 * soundVolume).toInt()
                buffer[i] = sampleVal.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
            }
            playPcmBuffer(buffer, sampleRate)
        }
    }

    fun playUpgradeFanfare() {
        if (isMuted || soundVolume <= 0.05f) return
        scope.launch {
            val sampleRate = 22050
            val notes = listOf(261.63, 329.63, 392.00, 523.25, 659.25) // C4, E4, G4, C5, E5
            val noteDurationMs = 70
            val totalDurationMs = notes.size * noteDurationMs
            val numSamples = (totalDurationMs * sampleRate) / 1000
            val buffer = ShortArray(numSamples)

            val samplesPerNote = (noteDurationMs * sampleRate) / 1000

            for (nIdx in notes.indices) {
                val freq = notes[nIdx]
                val offset = nIdx * samplesPerNote
                for (i in 0 until samplesPerNote) {
                    val globalIdx = offset + i
                    if (globalIdx < numSamples) {
                        val t = i.toDouble() / sampleRate
                        val env = (1.0 - (i.toDouble() / samplesPerNote))
                        val wave = sin(2.0 * PI * freq * t) + 0.25 * sin(2.0 * PI * (freq * 2) * t)
                        val sampleVal = (wave * env * Short.MAX_VALUE * 0.5 * soundVolume).toInt()
                        buffer[globalIdx] = sampleVal.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
                    }
                }
            }
            playPcmBuffer(buffer, sampleRate)
        }
    }

    fun playRareDiscovery() {
        if (isMuted || soundVolume <= 0.05f) return
        scope.launch {
            val sampleRate = 22050
            val notes = listOf(440.0, 554.37, 659.25, 880.0) // A4, C#5, E5, A5
            val noteDurationMs = 90
            val numSamples = (notes.size * noteDurationMs * sampleRate) / 1000
            val buffer = ShortArray(numSamples)
            val samplesPerNote = (noteDurationMs * sampleRate) / 1000

            for (nIdx in notes.indices) {
                val freq = notes[nIdx]
                val offset = nIdx * samplesPerNote
                for (i in 0 until samplesPerNote) {
                    val globalIdx = offset + i
                    if (globalIdx < numSamples) {
                        val t = i.toDouble() / sampleRate
                        val env = (1.0 - (i.toDouble() / samplesPerNote))
                        val wave = sin(2.0 * PI * freq * t)
                        val sampleVal = (wave * env * Short.MAX_VALUE * 0.45 * soundVolume).toInt()
                        buffer[globalIdx] = sampleVal.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
                    }
                }
            }
            playPcmBuffer(buffer, sampleRate)
        }
    }

    private fun playPcmBuffer(buffer: ShortArray, sampleRate: Int) {
        try {
            val minBufferSize = AudioTrack.getMinBufferSize(
                sampleRate,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT
            )
            val trackSize = buffer.size * 2
            val finalBufferSize = if (trackSize > minBufferSize) trackSize else minBufferSize

            val audioTrack = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_GAME)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(finalBufferSize)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            audioTrack.write(buffer, 0, buffer.size)
            audioTrack.play()
            scope.launch {
                kotlinx.coroutines.delay(1000)
                try {
                    audioTrack.stop()
                    audioTrack.release()
                } catch (_: Exception) {}
            }
        } catch (_: Exception) {}
    }
}

class HapticManager(private val context: Context) {
    private val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val manager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        manager?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }

    var isEnabled: Boolean = true

    fun click() {
        if (!isEnabled || vibrator == null || !vibrator.hasVibrator()) return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(18, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(18)
            }
        } catch (_: Exception) {}
    }

    fun heavyImpact() {
        if (!isEnabled || vibrator == null || !vibrator.hasVibrator()) return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(45, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(45)
            }
        } catch (_: Exception) {}
    }
}
