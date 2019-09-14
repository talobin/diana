package com.haivo.diana.Model

import com.haivo.diana.Util.NoteUtils
import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.roundToInt

class TunerResult(
        freq: Double,
        notesMatch: Array<BaseNote.DetectedNote>,
        private val tunerOptions: TunerOptions) {

    var percentage: Double = 0.toDouble()
    var percentageActualValue: Double = 0.toDouble()
    var note: String
    var statusText = ""
    var octave: Int = 0
    var frequency: Float = 0.toFloat()
    var type: INDICATOR_TYPE
    private val tolerance = .1f
    private var noteObj: BaseNote.DetectedNote? = null

    val noteLabel: String
        get() {
            var noteName = this.note
            if (noteName.contains("#") || noteName.contains("b")) {
                noteName = noteName.substring(0, 1)
            }
            return noteName
        }

    val noteLabelWithAug: String
        get() = note

    val noteLabelWithAugAndOctave: String
        get() = note + octave

    val frequencyLabel: String
        get() = Math.round(frequency).toString() + " Hz"

    val octaveLabel: String
        get() = if (this.octave != 0) this.octave.toString() else ""

    val percentageLabel: Float
        get() = (Math.round(this.percentageActualValue) - 50f) / 10f

    val isCorrect: Boolean
        get() = this.type == INDICATOR_TYPE.CORRECT

    val isValid: Boolean
        get() = this.type != INDICATOR_TYPE.INACTIVE

    val isNull: Boolean
        get() = this.type == INDICATOR_TYPE.INACTIVE

    val noteAug: String
        get() {
            return noteObj?.let{
                if (it.isAccidental && this.tunerOptions.naming == "english") {
                    return if (this.tunerOptions.sharps) "#" else "b"
                } else {
                    ""
                }
            } ?: ""
        }



    enum class INDICATOR_TYPE {
        ACTIVE, CORRECT, INACTIVE, INCORRECT
    }

    fun getPercentage(): Double? {
        return if (this.type != INDICATOR_TYPE.INACTIVE) {
            this.percentage
        } else {
            -1.00
        }
    }

    fun getPercentageActual(): Double {
        return floor(this.percentageActualValue - 50f) / 10f
    }

    init {
        var index = -1
        var dist: Double = java.lang.Double.MAX_VALUE
        for (i in notesMatch.indices) {
            val d = abs(freq - notesMatch[i].frequency)
            if (d < dist) {
                index = i
                dist = d
            }
        }
        val mNote = notesMatch[index]
        this.octave = mNote.octave
        this.note = mNote.translatedNote

        if (freq != 0.0) {

            this.percentage = NoteUtils.parseNote(freq, tunerOptions).offsetFrom(mNote) + 50
            this.percentageActualValue = this.percentage

            if (this.percentage > 50 - 50 * tolerance && this.percentage < 50 + 50 * tolerance) {
                this.percentage = 50.0
                this.type = INDICATOR_TYPE.CORRECT
            } else if (this.percentage < 0) {
                this.percentage = 5.0
                this.type = INDICATOR_TYPE.INCORRECT
            } else if (this.percentage > 100) {
                this.percentage = 95.0
                this.type = INDICATOR_TYPE.INCORRECT
            } else {
                this.type = INDICATOR_TYPE.ACTIVE
            }

            this.frequency = Math.round(freq * 100.00).toFloat() / 100.00f
            this.noteObj = mNote
        } else {
            this.note = ""
            this.noteObj = null
            this.octave = 0
            this.frequency = 0.0f
            //            this.percentage = 50.0;
            this.type = INDICATOR_TYPE.INACTIVE
        }

        if (percentageActualValue > 50f && (type == INDICATOR_TYPE.ACTIVE || type == INDICATOR_TYPE.INCORRECT)) {
            this.statusText = "sharp by " + Math.abs(50 - (getPercentageActual()).roundToInt()) + "%"
        } else if (percentageActualValue < 50f && (type == INDICATOR_TYPE.ACTIVE || type == INDICATOR_TYPE.INCORRECT)) {
            this.statusText = "flat by " + Math.abs(50 - (getPercentageActual()).roundToInt()) + "%"
        } else if (type == INDICATOR_TYPE.CORRECT) {
            this.statusText = "in tune."
        } else if (type == INDICATOR_TYPE.INACTIVE) {
            this.statusText = "aux."
        }
    }
}
