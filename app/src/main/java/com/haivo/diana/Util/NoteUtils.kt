package com.haivo.diana.Util

import com.haivo.diana.Model.BaseNote
import com.haivo.diana.Model.TunerOptions
import kotlin.math.floor
import kotlin.math.ln
import kotlin.math.pow
import kotlin.math.roundToInt

object NoteUtils {
    fun parseNote(s: String, tunerOptions: TunerOptions): BaseNote.DetectedNote {
        val note = s.substring(0, s.length - 1)
        val octave = Integer.valueOf(s.substring(s.length - 1, s.length))
        val semitones = ((octave - 4) * 12 + indexOfNote(BaseNote.DEFAULT_NOTES, note)
                - indexOfNote(BaseNote.DEFAULT_NOTES, "A")).toDouble()
        val base = tunerOptions.tunerBase.toDouble()
        val frequency = base * 2.0.pow(1.00 / 12.00).pow(semitones)

        val translatedNote = tunerOptions.notes[indexOfNote(BaseNote.DEFAULT_NOTES, note)]
        return BaseNote.DetectedNote(translatedNote, octave, frequency, frequency, 0.0, tunerOptions)
    }

    fun parseNote(frequency: Double, tunerOptions: TunerOptions): BaseNote.DetectedNote {
        val aboveA = ln(frequency / tunerOptions.tunerBase) / ln(2.00.pow(1 / 12.00))
        val closest = tunerOptions.tunerBase * 2.0.pow(1.00 / 12.00).pow(aboveA.roundToInt().toDouble())

        // NOTE ( 57 is the number of semitones of A4 )
        val index = ((57 + aboveA.roundToInt()) % 12).toInt()
        val note = tunerOptions.notes[index]
        val localizedNote = BaseNote.DEFAULT_NOTES[index]

        // OCTAVE
        val octave = floor((ln(frequency) - ln(tunerOptions.tunerBase.toDouble())) / ln(2.0) + 4.0).toInt()

        val offset = 100 * (aboveA - aboveA.roundToInt())
        return BaseNote.DetectedNote(note, octave, closest, frequency, offset, tunerOptions)
    }

    fun getNotesByInstrument(mode: BaseNote.Instrument): Array<String> {
        return when (mode) {
            BaseNote.Instrument.KALIMBA -> BaseNote.KALIMBA_NOTES
            BaseNote.Instrument.FLUTE -> BaseNote.VIETNAMESE_FLUTE_NOTES
            else -> BaseNote.CHROMATIC_NOTES
        }
    }

    private fun indexOfNote(a: Array<String>, s: String): Int {
        return a.indexOf(s)
    }

    fun getRealNote(tunerOptions: TunerOptions, translatedNote: String): String {
        return BaseNote.DEFAULT_NOTES[getNoteIndex(tunerOptions, translatedNote)]
    }

    fun getNoteIndex(tunerOptions: TunerOptions, translatedNote: String): Int {
        return indexOfNote(tunerOptions.notes, translatedNote)
    }
}
