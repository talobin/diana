package com.haivo.diana.model

import android.content.Context
import com.haivo.diana.util.NoteUtils

class TunerMode(val tunerOptions: TunerOptions) {

    var name: String = ""
    var group: String = ""
    var notesObjects: Array<BaseNote.DetectedNote?> = emptyArray()
    var notes: String = ""
        set(value) {
            field = value
            val notesArr = notes.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val notesMatch =
                    notesArr.map { note ->
                        if (note.trim { it <= ' ' } != "" && note.isNotEmpty()) {
                            NoteUtils.parseNote(note, tunerOptions)
                        } else {
                            null
                        }
                    }.toTypedArray()

            this.notesObjects = notesMatch // TODO
        }

    val notesObjectsForGroup: Array<BaseNote.DetectedNote?>
        get() = if (isChromatic) {
            emptyArray()
        } else {
            notesObjects
        }

    val isChromatic: Boolean
        get() = this.name == "Chromatic"

    val nameLabel: String
        get() = if (name == "Chromatic") {
            "Automatic"
        } else {
            name
        }

    val notesLabel: String
        get() = if (name == "Chromatic") {
            "Any Notes"
        } else {
            BaseNote.DetectedNote.getNotes(notes, tunerOptions)
        }

//    fun getNotes(): String {
//        return notes
//    }
//
//    fun setNotes(notes: String) {
//        this.notes = notes
//        val notesArr = notes.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
//        val notesMatch = arrayOfNulls<BaseNote.DetectedNote>(notesArr.size)
//        for (i in notesArr.indices) {
//            if (notesArr[i].trim { it <= ' ' } != "" && !notesArr[i].isEmpty()) {
//                notesMatch[i] = NoteUtils.parseNote(notesArr[i], tunerOptions)
//            }
//        }
//        this.notesObjects = notesMatch
//    }

    fun setInTune(noteName: String) {
        for (n in notesObjects) {
            if (n.toString() == noteName) {
                n?.getIsInTune()?.set(true)
            }
        }
    }

    override fun toString(): String {
        return if (name == "Chromatic") {
            "ChromaticMode"
        } else {
            "$group,$name,$notes"
        }
    }

    companion object {

        fun getAllTuningModes(c: Context) {

        }

        val chromaticMode: TunerMode
            get() {
                val tunerOptions = TunerOptions()
                val tunerMode = TunerMode(tunerOptions)
                tunerMode.name = "Chromatic"
                tunerMode.group = "All Notes"
                tunerMode.notes =
                        "A1 A#1 B1 C1 C#1 D1 D#1 E1 F1 F#1 G1 G#1 " +
                                "A2 A#2 B2 C2 C#2 D2 D#2 E2 F2 F#2 G2 G#2 " +
                                "A3 A#3 B3 C3 C#3 D3 D#3 E3 F3 F#3 G3 G#3 " +
                                "A4 A#4 B4 C4 C#4 D4 D#4 E4 F4 F#4 G4 G#4 " +
                                "A5 A#5 B5 C5 C#5 D5 D#5 E5 F5 F#5 G5 G#5 " +
                                "A6 A#6 B6 C6 C#6 D6 D#6 E6 F6 F#6 G6 G#6 " +
                                "A7 A#7 B7 C7 C#7 D7 D#7 E7 F7 F#7 G7 G#7 " +
                                "A8 A#8 B8 C8 C#8 D8 D#8 E8 F8 F#8 G8 G#8 "
                return tunerMode
            }

        fun valueOf(s: String): TunerMode {
            return if (s == "ChromaticMode") {
                chromaticMode
            } else {
                val tunerMode = TunerMode(TunerOptions())
                tunerMode.name = s.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1]
                tunerMode.group = s.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]
                tunerMode.notes = (s.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[2])
                tunerMode
            }
        }
    }
}
