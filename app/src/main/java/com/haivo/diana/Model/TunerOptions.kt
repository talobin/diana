package com.haivo.diana.Model

class TunerOptions {
    var tunerBase: Int = 0
    var sharps: Boolean = false
    var naming: String
    var sfx: Boolean = false
    var suppressor: Boolean = false
    private val NOTES_ENGLISH_SHARPS = arrayOf("C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B")
    private val NOTES_ENGLISH_FLATS = arrayOf("C", "Db", "D", "Eb", "E", "F", "Gb", "G", "Ab", "A", "Bb", "B")
    private val NOTES_SOLFEGE_SHARPS = arrayOf("Do", "Di", "Re", "Ri", "Mi", "Fa", "Fi", "Sol", "Si", "La", "Li", "Si")
    private val NOTES_SOLFEGE_FLATS = arrayOf("Do", "Ra", "Re", "Me", "Mi", "Fa", "Se", "Sol", "Le", "La", "Se", "Si")

    val notes: Array<String>
        get() = if (this.naming == "english") {
            if (this.sharps) {
                NOTES_ENGLISH_SHARPS
            } else {
                NOTES_ENGLISH_FLATS
            }
        } else if (this.naming == "solfege") {
            if (this.sharps) {
                NOTES_SOLFEGE_SHARPS
            } else {
                NOTES_SOLFEGE_FLATS
            }
        } else {
            NOTES_SOLFEGE_SHARPS
        }

    val chromaticNotes: String
        get() {
            val notes = notes
            val stringBuilder = StringBuilder()
            for (i in 1..8) {
                for (j in notes.indices) {
                    stringBuilder.append(notes[j])
                    stringBuilder.append(i)
                    stringBuilder.append(" ")
                }
            }
            return stringBuilder.toString()
        }

    init {
        tunerBase = DEFAULT_CALIBRATION
        sharps = DEFAULT_SHARP
        naming = DEFAULT_NAMING
        sfx = DEFAULT_SFX
        suppressor = DEFAULT_SUPPRESSOR
    }

    companion object {
        private val DEFAULT_SFX = true
        private val DEFAULT_SUPPRESSOR = false
        private val DEFAULT_SHARP = true
        private val DEFAULT_CALIBRATION = 440
        private val DEFAULT_NAMING = "english"
        private val CALIBRATION_STEP = 1
        private val CALIBRATION_MIN = 415
        private val CALIBRATION_MAX = 466
    }
}
