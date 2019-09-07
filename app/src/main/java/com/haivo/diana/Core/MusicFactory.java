package com.haivo.diana.Core;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.haivo.diana.Model.BaseNote;
import com.haivo.diana.Model.GeneratedNote;
import com.haivo.diana.Util.NoteUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Stack;

public class MusicFactory {
    private long timeStamp = 0;
    private boolean isRandom = true;
    private Stack<GeneratedNote> noteBank = new Stack<>();
    private static final MusicFactory ourInstance = new MusicFactory();
    private static final int DEFAULT_NOTE_BANK_SIZE = 100;
    private BaseNote.Instrument currentInstrument = BaseNote.Instrument.ALL;

    public static MusicFactory getInstance() {
        return ourInstance;
    }

    private MusicFactory() {
    }

    public final void setInstrument(BaseNote.Instrument mode) {
        currentInstrument = mode;
    }

    /**
     * Starts the factory to produce random Notes
     */
    public final void startRandom() {
        isRandom = true;
        start();
    }

    /**
     * Starts the factory to produce Notes from selected song index.
     * To get all available songs, call {{@link #getAvailableSongs()}}
     */
    public final void startSong(int songIndex) {
        isRandom = false;
        start();
    }

    /**
     * Get all the available songs in the factory.
     */
    public final String[] getAvailableSongs() {
        return new String[0];
    }

    /**
     * Get the next note
     *
     * @return a Note if there is more left. Null otherwise
     */
    public synchronized @Nullable
    GeneratedNote getNextNote() {
        if (isRandom) {
            return getNextRandomNote();
        } else {
            return getNextSongNote();
        }
    }

    /**
     * Get the next batch of note
     *
     * @param howMany quantity of notes in a batch
     * @return a nonempty List of Notes if there is more left. Empty List otherwise
     */
    public synchronized @Nullable
    List<GeneratedNote> getNextNotes(int howMany) {
        List<GeneratedNote> noteArray = new ArrayList<GeneratedNote>();
        for (int i = 0; i < howMany; i++) {
            GeneratedNote note = getNextNote();
            if (note != null) {
                noteArray.add(note);
            } else {
                break;
            }
        }
        return noteArray;
    }

    /**
     * Get the next random note. If bank is empty, fill it up
     *
     * @return A random note
     */
    private @NonNull
    GeneratedNote getNextRandomNote() {
        // If empty, generate more notes first
        if (noteBank.empty()) {
            generateRandomNotes();
        }
        return noteBank.pop();
    }
    /**
     * Get the next note in the song.
     *
     * @return Next note in song if that at the end. Null otherwise.
     */
    private @Nullable
    GeneratedNote getNextSongNote() {
        // If empty, return null
        if (noteBank.empty()) {
            return null;
        }
        return noteBank.pop();
    }

    /**
     * Fill out {{@link #noteBank}} with random notes;
     */
    private void generateRandomNotes() {
        noteBank.clear();
        String[] allAppropreateNotes = NoteUtils.getNotesByInstrument(currentInstrument);
        for (int i = 0; i < DEFAULT_NOTE_BANK_SIZE; i++) {
            String translatedNote = allAppropreateNotes[generateRandomNumber(0, allAppropreateNotes.length)];
            String note = translatedNote.substring(0, translatedNote.length() - 1);
            int octave = Integer.parseInt(translatedNote.substring(translatedNote.length() - 1));
            //int octave = generateRandomNumber(1, 8);
            noteBank.push(new GeneratedNote(timeStamp, note, octave));
            timeStamp++;
        }
    }

    private void start() {
        timeStamp = 0;
    }

    /**
     * Generate a truly random number within the range of min and max
     */
    private synchronized int generateRandomNumber(int min, int max) {
        Random rand = new Random(System.nanoTime());
        int result = min + rand.nextInt(max);
        //Log.d("Hai", "Min " + min + "Max" + max + "|" + result);
        return result;
    }
}
