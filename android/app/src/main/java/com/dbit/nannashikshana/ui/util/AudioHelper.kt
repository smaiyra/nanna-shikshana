package com.dbit.nannashikshana.ui.util

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.PI
import kotlin.math.sin

@Singleton
class AudioHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private var tts: TextToSpeech? = null
    private var ttsReady = false
    private val scope = CoroutineScope(Dispatchers.Default)

    init {
        try {
            tts = TextToSpeech(context) { status ->
                ttsReady = (status == TextToSpeech.SUCCESS)
            }
        } catch (_: Exception) {
            ttsReady = false
        }
    }

    fun speak(text: String, langCode: String, onFinished: () -> Unit = {}) {
        val engine = tts
        if (engine == null || !ttsReady) {
            onFinished()
            return
        }
        val locale = if (langCode == "kn") Locale("kn", "IN") else Locale("en", "IN")
        val res = engine.setLanguage(locale)
        if (res == TextToSpeech.LANG_MISSING_DATA || res == TextToSpeech.LANG_NOT_SUPPORTED) {
            engine.language = Locale.getDefault()
        }
        engine.setSpeechRate(0.9f)
        engine.setPitch(1.05f)
        engine.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {}
            override fun onDone(utteranceId: String?) {
                onFinished()
            }
            @Deprecated("Deprecated in Java")
            override fun onError(utteranceId: String?) {
                onFinished()
            }
        })
        engine.speak(text, TextToSpeech.QUEUE_FLUSH, null, "nanna_utt_${System.currentTimeMillis()}")
    }

    fun stopSpeaking() {
        try {
            tts?.stop()
        } catch (_: Exception) {
        }
    }

    fun playChime() {
        playTone(freqHz = 784.0, durationMs = 110)
    }

    fun playBuzz() {
        playTone(freqHz = 220.0, durationMs = 180)
    }

    fun playFanfare() {
        scope.launch {
            playToneSync(523.25, 90)
            playToneSync(659.25, 90)
            playToneSync(783.99, 140)
        }
    }

    private fun playTone(freqHz: Double, durationMs: Int) {
        scope.launch {
            playToneSync(freqHz, durationMs)
        }
    }

    private fun playToneSync(freqHz: Double, durationMs: Int) {
        try {
            val sampleRate = 22050
            val count = (sampleRate * durationMs) / 1000
            val samples = ShortArray(count)
            for (i in 0 until count) {
                val env = 1.0 - (i.toDouble() / count)
                val angle = 2.0 * PI * i * freqHz / sampleRate
                samples[i] = (sin(angle) * env * Short.MAX_VALUE * 0.25).toInt().toShort()
            }
            val track = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
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
                .setBufferSizeInBytes(samples.size * 2)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()
            track.write(samples, 0, samples.size)
            track.play()
            Thread.sleep(durationMs.toLong() + 20)
            track.release()
        } catch (_: Exception) {
            // Safe fallback in unit/Robolectric environments
        }
    }

    fun release() {
        try {
            tts?.stop()
            tts?.shutdown()
        } catch (_: Exception) {
        }
    }
}
