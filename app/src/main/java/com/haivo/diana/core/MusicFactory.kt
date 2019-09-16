package com.haivo.diana.core

import com.haivo.diana.model.BaseNote
import com.haivo.diana.util.NoteUtils
import java.util.*

object MusicFactory {
    /**
     * Get all the available songs in the factory.
     */

    val availableSongs: Array<String> = emptyArray()
    /**
     * Get the next note
     *
     * @return a Note if there is more left. Null otherwise
     */
    val nextNote: BaseNote.GeneratedNote?
        @Synchronized get() = if (isRandom) {
            nextRandomNote
        } else {
            nextSongNote
        }

    private var timeStamp: Long = 0
    private var isRandom = true
    private val noteBank = Stack<BaseNote.GeneratedNote>()
    private var currentInstrument: BaseNote.Instrument = BaseNote.Instrument.ALL
    private const val DEFAULT_NOTE_BANK_SIZE = 100
    /**
     * Get the next random note. If bank is empty, fill it up
     *
     * @return A random note
     */
    private val nextRandomNote: BaseNote.GeneratedNote
        get() {
            if (noteBank.empty()) {
                generateRandomNotes()
            }
            return noteBank.pop()
        }
    /**
     * Get the next note in the song.
     *
     * @return Next note in song if that at the end. Null otherwise.
     */
    private val nextSongNote: BaseNote.GeneratedNote?
        get() =
            if (noteBank.empty()) {
                null
            } else noteBank.pop()

    fun setInstrument(mode: BaseNote.Instrument) {
        currentInstrument = mode
    }

    /**
     * Starts the factory to produce random Notes
     */
    fun startRandom() {
        isRandom = true
        start()
    }

    /**
     * Starts the factory to produce Notes from selected song index.
     * To get all available songs, call {[.getAvailableSongs]}
     */
    fun startSong(songIndex: Int) {
        isRandom = false
        start()
    }

    /**
     * Get the next batch of note
     *
     * @param howMany quantity of notes in a batch
     * @return a nonempty List of Notes if there is more left. Empty List otherwise
     */
    @Synchronized
    fun getNextNotes(howMany: Int): List<BaseNote.GeneratedNote>? {
        val noteArray = ArrayList<BaseNote.GeneratedNote>()
        for (i in 0 until howMany) {
            val note = nextNote
            if (note != null) {
                noteArray.add(note)
            } else {
                break
            }
        }
        return noteArray
    }

    /**
     * Fill out {[.noteBank]} with random notes;
     */
    private fun generateRandomNotes() {
        noteBank.clear()
        val allAppropreateNotes = NoteUtils.getNotesByInstrument(currentInstrument)
        for (i in 0 until DEFAULT_NOTE_BANK_SIZE) {
            val translatedNote = allAppropreateNotes[generateRandomNumber(0, allAppropreateNotes.size)]
            val note = translatedNote.substring(0, translatedNote.length - 1)
            val octave = Integer.parseInt(translatedNote.substring(translatedNote.length - 1))
            //int octave = generateRandomNumber(1, 8);
            noteBank.push(BaseNote.GeneratedNote(timeStamp, note, octave))
            timeStamp++
        }
    }

    private fun start() {
        timeStamp = 0
    }

    /**
     * Generate a truly random number within the range of min and max
     */
    @Synchronized
    private fun generateRandomNumber(min: Int, max: Int): Int {
        val rand = Random(System.nanoTime())
//Log.d("Hai", "Min " + min + "Max" + max + "|" + result);
        return min + rand.nextInt(max)
    }


}
