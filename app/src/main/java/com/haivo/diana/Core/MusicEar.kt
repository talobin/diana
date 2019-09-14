package com.haivo.diana.Core

import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import com.haivo.diana.Model.TunerMode
import com.haivo.diana.Util.Tuner

object MusicEar {
    /* For Android 6.0 */
    val isMicrophoneAvailable: Boolean
        get() {
            var audio: AudioRecord? = null
            var ready = true
            try {
                val baseSampleRate = 44100
                val channel = AudioFormat.CHANNEL_IN_MONO
                val format = AudioFormat.ENCODING_PCM_16BIT
                val buffSize = AudioRecord.getMinBufferSize(baseSampleRate, channel, format)
                audio = AudioRecord(MediaRecorder.AudioSource.MIC, baseSampleRate, channel, format, buffSize)
                audio.startRecording()
                val buffer = ShortArray(buffSize)
                val audioStatus = audio.read(buffer, 0, buffSize)

                if (audioStatus == AudioRecord.ERROR_INVALID_OPERATION || audioStatus == AudioRecord.STATE_UNINITIALIZED) {
                    ready = false
                }
            } catch (e: Exception) {
                ready = false
            } finally {
                try {
                    audio?.release()
                } catch (e: Exception) {
                }

            }

            return ready
        }
    val isRunning: Boolean
        get() = tuner.isRecording

    private val tuner: Tuner by lazy { Tuner(TunerMode.chromaticMode) }

    /**
     * Initialize and start
     */
    fun start(listener: Tuner.OnNoteFoundListener) {
        tuner.setOnNoteFoundListener(listener)
        tuner.start()
    }

    /**
     * Stop and clean up
     */
    fun stop() {
        if (tuner.isRecording) {
            tuner.stop()
        }
        tuner.destroy()
    }

    fun setNoteListener(listener: Tuner.OnNoteFoundListener) {
        tuner.setOnNoteFoundListener(listener)
    }
}
