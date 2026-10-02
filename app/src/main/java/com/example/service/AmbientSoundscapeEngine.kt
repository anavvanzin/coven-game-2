package com.example.service

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import com.example.data.model.SoundscapeType
import kotlinx.coroutines.*
import kotlin.math.*
import kotlin.random.Random

/**
 * Procedural ambient audio synthesis engine using Android's native AudioTrack.
 * Runs 100% offline, requires zero external downloads or network bandwidth,
 * and creates seamless, infinite study audio loops.
 */
class AmbientSoundscapeEngine {
    private var audioTrack: AudioTrack? = null
    private var playbackJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    @Volatile
    private var isRunning = false

    @Volatile
    private var currentType: SoundscapeType = SoundscapeType.RAINY_FOREST

    @Volatile
    private var targetVolume: Float = 0.75f

    private val sampleRate = 44100
    private val bufferSize = AudioTrack.getMinBufferSize(
        sampleRate,
        AudioFormat.CHANNEL_OUT_STEREO,
        AudioFormat.ENCODING_PCM_16BIT
    ).coerceAtLeast(sampleRate / 4)

    fun start(type: SoundscapeType, volume: Float = targetVolume) {
        currentType = type
        targetVolume = volume.coerceIn(0f, 1f)

        if (isRunning && audioTrack != null) {
            // Already running, just changing the soundscape type smoothly
            return
        }

        stop()

        try {
            audioTrack = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_STEREO)
                        .build()
                )
                .setBufferSizeInBytes(bufferSize * 2)
                .setTransferMode(AudioTrack.MODE_STREAM)
                .build()

            audioTrack?.setVolume(targetVolume)
            audioTrack?.play()
            isRunning = true

            playbackJob = scope.launch {
                generateAudioStream()
            }
        } catch (e: Exception) {
            isRunning = false
            e.printStackTrace()
        }
    }

    fun setSoundscape(type: SoundscapeType) {
        currentType = type
        if (!isRunning) {
            start(type, targetVolume)
        }
    }

    fun setVolume(volume: Float) {
        targetVolume = volume.coerceIn(0f, 1f)
        audioTrack?.setVolume(targetVolume)
    }

    fun stop() {
        isRunning = false
        playbackJob?.cancel()
        playbackJob = null
        try {
            audioTrack?.stop()
            audioTrack?.release()
        } catch (e: Exception) {
            // Ignored on cleanup
        } finally {
            audioTrack = null
        }
    }

    private suspend fun generateAudioStream() {
        val chunkSize = 2048
        val buffer = ShortArray(chunkSize * 2) // Stereo: left, right interleaved

        // Filter state variables
        var pinkB0 = 0.0
        var pinkB1 = 0.0
        var pinkB2 = 0.0
        var brownian = 0.0
        var phaseL = 0.0
        var phaseR = 0.0
        var clockTimer = 0
        var crackleCountdown = Random.nextInt(200, 2000)
        var dropCountdown = Random.nextInt(100, 800)
        var bubbleCountdown = Random.nextInt(300, 1500)
        var dropPhase = 0.0
        var dropAmp = 0.0
        var dropFreq = 1200.0

        var bubblePhase = 0.0
        var bubbleAmp = 0.0
        var bubbleFreq = 300.0

        val twoPi = 2.0 * Math.PI

        while (isRunning && (playbackJob?.isActive != false)) {
            val type = currentType

            for (i in 0 until chunkSize) {
                var outL = 0.0
                var outR = 0.0

                // Generate white noise baseline (-1.0 to 1.0)
                val whiteL = Random.nextDouble(-1.0, 1.0)
                val whiteR = Random.nextDouble(-1.0, 1.0)

                // 3-pole Pink Noise generator (Paul Kellet's method)
                pinkB0 = 0.99765 * pinkB0 + whiteL * 0.0990460
                pinkB1 = 0.96300 * pinkB1 + whiteL * 0.2965164
                pinkB2 = 0.57000 * pinkB2 + whiteL * 1.0526913
                val pinkNoiseL = pinkB0 + pinkB1 + pinkB2 + whiteL * 0.1848
                val pinkNoiseR = (pinkNoiseL * 0.6 + whiteR * 0.4)

                // Brownian / Red Noise (integration with leak)
                brownian = (brownian + (0.02 * whiteL)) / 1.02

                when (type) {
                    SoundscapeType.RAINY_FOREST -> {
                        // Continuous rainfall (pink noise softened)
                        val rainBedL = pinkNoiseL * 0.22
                        val rainBedR = pinkNoiseR * 0.22

                        // Stochastic raindrops: Sine pulse with steep exponential decay
                        if (--dropCountdown <= 0) {
                            dropCountdown = Random.nextInt(150, 900)
                            dropAmp = Random.nextDouble(0.15, 0.45)
                            dropFreq = Random.nextDouble(900.0, 2200.0)
                            dropPhase = 0.0
                        }

                        var drop = 0.0
                        if (dropAmp > 0.001) {
                            drop = sin(dropPhase) * dropAmp
                            dropPhase += (twoPi * dropFreq) / sampleRate
                            dropAmp *= 0.996 // exponential decay
                        }

                        // Distant gentle breeze modulation
                        phaseL += (twoPi * 0.15) / sampleRate
                        val breeze = (sin(phaseL) * 0.08)

                        outL = (rainBedL * (1.0 + breeze)) + (drop * 0.8)
                        outR = (rainBedR * (1.0 + breeze)) + (drop * 0.4)
                    }

                    SoundscapeType.CRACKLING_FIREPLACE -> {
                        // Deep low-frequency combustion rumble
                        val flameBody = brownian * 0.45

                        // Embers crackle pops: Poisson random burst impulses
                        var crackle = 0.0
                        if (--crackleCountdown <= 0) {
                            crackleCountdown = Random.nextInt(250, 4500)
                            // Sharp wood pop impulse
                            crackle = if (Random.nextBoolean()) Random.nextDouble(0.4, 0.85) else -Random.nextDouble(0.4, 0.85)
                        }

                        // Gentle hearth hiss
                        val hiss = whiteL * 0.06

                        outL = flameBody + crackle + hiss
                        outR = flameBody + (crackle * 0.7) + (whiteR * 0.06)
                    }

                    SoundscapeType.ANCIENT_LIBRARY -> {
                        // Vast gothic room presence (velvet brown noise)
                        val roomToneL = brownian * 0.25
                        val roomToneR = (brownian * 0.2) + (whiteR * 0.015)

                        // Soft wooden pendulum tick every ~1 second (44100 samples)
                        var tick = 0.0
                        clockTimer = (clockTimer + 1) % sampleRate
                        if (clockTimer in 0..120) {
                            val tickProgress = clockTimer / 120.0
                            val tickSine = sin(tickProgress * Math.PI * 4)
                            tick = tickSine * (1.0 - tickProgress) * 0.18
                        }

                        // Calming 432 Hz warm harmonic tone (very quiet background)
                        phaseL += (twoPi * 432.0) / sampleRate
                        val warmTone = sin(phaseL) * 0.035

                        outL = roomToneL + tick + warmTone
                        outR = roomToneR + (tick * 0.3) + warmTone
                    }

                    SoundscapeType.MIDNIGHT_SANCTUM -> {
                        // Binaural Theta Wave: Left 136.1 Hz (Om), Right 142.1 Hz (Diff = 6 Hz theta)
                        phaseL += (twoPi * 136.1) / sampleRate
                        phaseR += (twoPi * 142.1) / sampleRate
                        val binauralL = sin(phaseL) * 0.16
                        val binauralR = sin(phaseR) * 0.16

                        // Wind chime harmonics (chime trigger)
                        if (--bubbleCountdown <= 0) {
                            bubbleCountdown = Random.nextInt(12000, 35000)
                            bubbleFreq = listOf(528.0, 660.0, 792.0, 1056.0).random()
                            bubbleAmp = 0.25
                            bubblePhase = 0.0
                        }

                        var chime = 0.0
                        if (bubbleAmp > 0.001) {
                            chime = sin(bubblePhase) * bubbleAmp
                            bubblePhase += (twoPi * bubbleFreq) / sampleRate
                            bubbleAmp *= 0.9997 // very long smooth shimmer decay
                        }

                        outL = binauralL + (pinkNoiseL * 0.05) + (chime * 0.8)
                        outR = binauralR + (pinkNoiseR * 0.05) + (chime * 0.5)
                    }

                    SoundscapeType.BUBBLING_CAULDRON -> {
                        // Gentle simmering liquid bed
                        val simmerL = pinkNoiseL * 0.15
                        val simmerR = pinkNoiseR * 0.15

                        // Frequency swept bubble pop: 280 Hz sweeps up to 600 Hz
                        if (--bubbleCountdown <= 0) {
                            bubbleCountdown = Random.nextInt(300, 1800)
                            bubbleFreq = Random.nextDouble(260.0, 420.0)
                            bubbleAmp = Random.nextDouble(0.2, 0.45)
                            bubblePhase = 0.0
                        }

                        var bubble = 0.0
                        if (bubbleAmp > 0.001) {
                            bubble = sin(bubblePhase) * bubbleAmp
                            bubblePhase += (twoPi * bubbleFreq) / sampleRate
                            bubbleFreq += 18.0 // pitch sweeps up
                            bubbleAmp *= 0.985 // quick bubble pop decay
                        }

                        outL = simmerL + (bubble * 0.9)
                        outR = simmerR + (bubble * 0.6)
                    }
                }

                // Clamp to [-1.0, 1.0] and convert to 16-bit PCM short
                val clampedL = outL.coerceIn(-1.0, 1.0)
                val clampedR = outR.coerceIn(-1.0, 1.0)

                buffer[i * 2] = (clampedL * 32767.0).toInt().toShort()
                buffer[i * 2 + 1] = (clampedR * 32767.0).toInt().toShort()
            }

            // Write to Android AudioTrack
            audioTrack?.write(buffer, 0, buffer.size)
        }
    }
}
