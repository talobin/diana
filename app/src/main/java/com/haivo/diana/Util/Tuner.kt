package com.haivo.diana.Util

import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.media.audiofx.NoiseSuppressor
import android.os.Handler
import android.os.Looper
import androidx.databinding.ObservableBoolean
import com.haivo.diana.Model.TunerMode
import com.haivo.diana.Model.TunerOptions
import com.haivo.diana.Model.TunerResult
import java.util.*

//    public Tuner(Context context) {
//        init("E2 A2 D3 G3 B3 E4");
//    }
class Tuner(val tunerMode: TunerMode) {
    private var sampleRate = 0
    private var bufferSize = 0
    private var readSize = 0
    private var buffer: FloatArray
    private var intermediaryBuffer: ShortArray
    private val statusBuffer = ShortArray(20)
    private val noteBuffer = arrayOfNulls<String>(30)
    var isFake = false

    /* Preference Options */
    var options: TunerOptions = TunerOptions()
    /* // */

    var ptr: Long = 0
    var input: Long = 0
    var pitch: Long = 0

    var isRecording = false
        private set
    private var handler: Handler
    private var audioRecord: AudioRecord? = null
    private var currentNoteResult: TunerResult? = null
    private var onNoteFoundListener: OnNoteFoundListener? = null
    internal var audioThread: Thread? = null
    private var suppressor: NoiseSuppressor? = null

    var isMuted = false

    private var playedSfx = false

    var hasValidResult = ObservableBoolean(false)
    var hasCorrectResult = ObservableBoolean(false)

    private val type: TunerResult.IndicatorType
        get() {
            var type = statusBuffer[0]
            if (type.toInt() == 0) {
                for (i in statusBuffer.indices) {
                    if (type != statusBuffer[i]) {
                        type = 1
                        break
                    }
                }
            } else {
                for (i in statusBuffer.indices) {
                    if (type != statusBuffer[i] && statusBuffer[i].toInt() != 0) {
                        type = 1
                        break
                    }
                }
            }
            return when (type) {
                1.toShort() -> TunerResult.IndicatorType.ACTIVE
                2.toShort() -> TunerResult.IndicatorType.CORRECT
                3.toShort() -> TunerResult.IndicatorType.INCORRECT
                else -> TunerResult.IndicatorType.INACTIVE
            }
        }

    internal val note: String?
        get() {
            val ary = noteBuffer
            val m = HashMap<String?, Int>()

            for (a in ary) {
                val occ = m[a]
                m[a] = if (occ == null) 1 else occ + 1
            }

            var max = -1
            var mostFrequent: String? = null

            for ((key, value) in m) {
                if (value > max) {
                    mostFrequent = key
                    max = value
                }
            }

            return mostFrequent
        }

    val currentNoteResultLabel: String
        get() = currentNoteResult?.noteLabelWithAug ?: ""

    init {
        sampleRate = 44100
        bufferSize = 4096
        readSize = bufferSize / 4
        buffer = FloatArray(readSize)
        intermediaryBuffer = ShortArray(readSize)
        handler = Handler(Looper.getMainLooper())
    }

    fun start() {
        if (!isRecording && !isFake) {
            isRecording = true
            initPitch(sampleRate, bufferSize)
            audioRecord = AudioRecord(MediaRecorder.AudioSource.DEFAULT,
                    sampleRate,
                    AudioFormat.CHANNEL_IN_DEFAULT,
                    AudioFormat.ENCODING_PCM_16BIT,
                    bufferSize)

            if (NoiseSuppressor.isAvailable() && options.suppressor) {
                audioRecord?.let { suppressor = NoiseSuppressor.create(it.audioSessionId) }
            }

            audioRecord?.startRecording()
            audioThread = Thread(Runnable //Runs off the UI thread
            {
                findNote()
            }, "Tuner Thread")
            audioThread?.start()
        }
    }

    fun stop() {
        // stops the recording activity
        isRecording = false
        sendNullResult()
        audioRecord?.let {
            it.stop()
            it.release()
            audioRecord = null
            audioThread = null
            onNoteFoundListener = null
            suppressor?.release()
            suppressor = null
        }
    }

    private fun findNote() {
        while (isRecording) {
            if (!isMuted) {
                audioRecord?.read(intermediaryBuffer, 0, readSize)
                buffer = shortArrayToFloatArray(intermediaryBuffer)
                val result = TunerResult(getPitch(buffer).toDouble(), tunerMode.notesObjects, options)
                handler.post {
                    pushType(result.type)
                    val tempType = result.type
                    pushNote(result.note + result.octave)
                    result.type = type

                    hasValidResult.set(result.frequency > -1)
                    hasCorrectResult.set(type === TunerResult.IndicatorType.CORRECT)

                    if (type === TunerResult.IndicatorType.CORRECT) {
                        tunerMode?.let {
                            if (!it.isChromatic) {
                                it.setInTune(result.noteLabelWithAugAndOctave)
                            }
                        }
                    }

                    if (!(tempType === TunerResult.IndicatorType.INACTIVE
                                    && result.type !== TunerResult.IndicatorType.INACTIVE)
                            && onNoteFoundListener != null && result.note + result.octave == note) {
                        currentNoteResult = result
                        onNoteFoundListener?.onEvent(result)
                        if (type === TunerResult.IndicatorType.CORRECT) {
                            if (!playedSfx) {
                                playedSfx = true
                                Handler().postDelayed({ playedSfx = false }, 3000)
                            }
                        }
                    }
                    //                        if(onNoteFoundListener != null)
                    //                            onNoteFoundListener.onEvent(result);
                }
            }
        }
    }

    private fun pushType(v: TunerResult.IndicatorType) {
        val s: Short =
                when (v) {
                    TunerResult.IndicatorType.ACTIVE -> 1
                    TunerResult.IndicatorType.CORRECT -> 2
                    TunerResult.IndicatorType.INCORRECT -> 3
                    else -> 0
                }
        for (i in statusBuffer.indices) {
            if (i != statusBuffer.size - 1) {
                statusBuffer[i] = statusBuffer[i + 1]
            } else {
                statusBuffer[i] = s
            }
        }
    }

    private fun pushNote(v: String) {
        for (i in noteBuffer.indices) {
            if (i != noteBuffer.size - 1) {
                noteBuffer[i] = noteBuffer[i + 1]
            } else {
                noteBuffer[i] = v
            }
        }
    }

    private fun shortArrayToFloatArray(array: ShortArray): FloatArray {
        val fArray = FloatArray(array.size)
        for (i in array.indices) {
            fArray[i] = array[i].toFloat()
        }
        return fArray
    }

    fun sendNullResult() {
        if (onNoteFoundListener != null) {
            tunerMode?.notesObjects?.let {
                val nullTunerResult = TunerResult(0.0, it, options)
                nullTunerResult.type = TunerResult.IndicatorType.INACTIVE
                nullTunerResult.percentage = 50.0
                onNoteFoundListener?.onEvent(nullTunerResult)
            }
        }
    }


    fun setOnNoteFoundListener(eventListener: OnNoteFoundListener?) {
        onNoteFoundListener = eventListener
    }

    fun destroy() {
        cleanupPitch()
    }

    interface OnNoteFoundListener {
        fun onEvent(note: TunerResult)
    }

    private external fun getPitch(input: FloatArray?): Float
    private external fun initPitch(sampleRate: Int, B: Int)
    private external fun cleanupPitch()

    companion object {

        init {
            System.loadLibrary("aubio")
            System.loadLibrary("pitch")
        }
    }
}
