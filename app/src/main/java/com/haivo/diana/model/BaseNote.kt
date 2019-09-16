package com.haivo.diana.model

import androidx.databinding.ObservableBoolean
import com.haivo.diana.util.NoteUtils
import kotlin.math.ln
import kotlin.math.pow
import kotlin.math.roundToInt

sealed class BaseNote constructor(var translatedNote: String, var octave: Int) {

    class GeneratedNote(
            val timeStamp: Long,
            note: String,
            octave: Int
    ) : BaseNote(note, octave) {
        val noteLabel: String
            get() = this.translatedNote + this.octave
    }

    class DetectedNote(translatedNote: String,
                       octave: Int,
                       var frequency: Double,
                       private var actualFrequency: Double,
                       private var offset: Double,
                       private val tunerOptions: TunerOptions) : BaseNote(translatedNote, octave) {

        private var realNote: String
        private var isInTune = ObservableBoolean(false)

        // Accidental positions: 1 3 6 8 10
        val isAccidental: Boolean
            get() {

                val index = NoteUtils.getNoteIndex(tunerOptions, translatedNote)
                return index == 1 || index == 3 || index == 6 || index == 8 || index == 10
            }

        init {
            this.translatedNote = translatedNote
            this.realNote = NoteUtils.getRealNote(tunerOptions, translatedNote)
            this.octave = octave
        }

        fun offsetFrom(n: DetectedNote): Double {
            val nAboveA = ln(n.actualFrequency / tunerOptions.tunerBase) / ln(2.0.pow(1.00 / 12.00))

            val thisAboveA = ln(actualFrequency / tunerOptions.tunerBase) / ln(2.0.pow(1.00 / 12.00))
            return 100.00 * (thisAboveA - nAboveA)
        }

        fun semiTonesFromBase(): Int =
                (ln(frequency / tunerOptions.tunerBase) / ln(2.0.pow(2.00 / 12))).roundToInt()


        fun getIsInTune(): ObservableBoolean = isInTune


        override fun toString(): String = translatedNote + octave


        companion object {

            fun getNotes(s: String, tunerOptions: TunerOptions): String {
                val notes = s.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val notesString = StringBuilder()
                for (i in notes.indices) {
                    notesString.append(NoteUtils.parseNote(notes[i], tunerOptions).translatedNote)
                    notesString.append(NoteUtils.parseNote(notes[i], tunerOptions).octave)
                    notesString.append(" ")
                }
                return notesString.toString()
            }
        }
    }

    enum class Instrument {
        KALIMBA, FLUTE, ALL
    }

    companion object {
        var DEFAULT_NOTES = arrayOf(
                "C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B")

        var CHROMATIC_NOTES = arrayOf(
                "A1", "A#1", "B1", "C1", "C#1", "D1", "D#1", "E1", "F1", "F#1", "G1", "G#1",
                "A2", "A#2", "B2", "C2", "C#2", "D2", "D#2", "E2", "F2", "F#2", "G2", "G#2",
                "A3", "A#3", "B3", "C3", "C#3", "D3", "D#3", "E3", "F3", "F#3", "G3", "G#3",
                "A4", "A#4", "B4", "C4", "C#4", "D4", "D#4", "E4", "F4", "F#4", "G4", "G#4",
                "A5", "A#5", "B5", "C5", "C#5", "D5", "D#5", "E5", "F5", "F#5", "G5", "G#5",
                "A6", "A#6", "B6", "C6", "C#6", "D6", "D#6", "E6", "F6", "F#6", "G6", "G#6",
                "A7", "A#7", "B7", "C7", "C#7", "D7", "D#7", "E7", "F7", "F#7", "G7", "G#7",
                "A8", "A#8", "B8", "C8", "C#8", "D8", "D#8", "E8", "F8", "F#8", "G8", "G#8 ")

        var VIETNAMESE_FLUTE_NOTES = arrayOf(
                "A5", "B5", "C5", "D5", "E5", "F5", "G5",
                "A6", "B6", "C6", "D6", "E6", "F6", "G6")

        var KALIMBA_NOTES = arrayOf(
                "C4", "D4", "E4", "F4", "G4", "A4", "B4",
                "C5", "D5", "E5", "F5", "G5", "A5", "B5", "C6", "D6", "E6")
    }
}
