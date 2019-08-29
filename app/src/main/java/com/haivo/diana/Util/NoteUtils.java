package com.haivo.diana.Util;

import com.haivo.diana.Model.BaseNote;
import com.haivo.diana.Model.DetectedNote;
import com.haivo.diana.Model.TunerOptions;

import static com.haivo.diana.Model.BaseNote.CHROMATIC_NOTES;
import static com.haivo.diana.Model.BaseNote.DEFAULT_NOTES;

public final class NoteUtils {
    public static DetectedNote parseNote(String s, TunerOptions tunerOptions) {
        String note = s.substring(0, s.length() - 1);
        int octave = Integer.valueOf(s.substring(s.length() - 1, s.length()));
        double semitones = (octave - 4) * 12 + indexOfNote(DEFAULT_NOTES, note) - indexOfNote(DEFAULT_NOTES, "A");
        double BASE = tunerOptions.tunerBase;
        double frequency = BASE * Math.pow(Math.pow(2, (1.00 / 12.00)), semitones);

        String translatedNote = tunerOptions.getNotes()[indexOfNote(DEFAULT_NOTES, note)];
        return new DetectedNote(translatedNote, octave, frequency, frequency, 0, tunerOptions);
    }

    public static DetectedNote parseNote(double frequency, TunerOptions tunerOptions) {
        double aboveA = Math.log((frequency / tunerOptions.tunerBase)) / Math.log(Math.pow(2.00, 1 / 12.00));
        double closest = tunerOptions.tunerBase * Math.pow(Math.pow(2, 1.00 / 12.00), Math.round(aboveA));

        // NOTE ( 57 is the number of semitones of A4 )
        int index = (int) ((57 + Math.round(aboveA)) % 12);
        String note = tunerOptions.getNotes()[index];
        String localizedNote = DEFAULT_NOTES[index];

        // OCTAVE
        int octave = (int) (Math.floor((Math.log(frequency) - Math.log(tunerOptions.tunerBase)) / Math.log(2) + 4.0));

        double offset = 100 * (aboveA - Math.round(aboveA));
        return new DetectedNote(note, octave, closest, frequency, offset, tunerOptions);
    }

    public static String[] getNotesByInstrument(BaseNote.Instrument mode) {
        switch (mode) {
            case KALIMBA:
                return BaseNote.KALIMBA_NOTES;
            case FLUTE:
                return BaseNote.VIETNAMESE_FLUTE_NOTES;
            default:
                return CHROMATIC_NOTES;
        }
    }

    private static int indexOfNote(String[] a, String s) {
        int index = -1;
        for (int i = 0; i < a.length; i++) {
            if (a[i].equals(s)) {
                index = i;
                break;
            }
        }
        return index;
    }

    public static String getRealNote(TunerOptions tunerOptions, String translatedNote) {
        return DEFAULT_NOTES[getNoteIndex(tunerOptions, translatedNote)];
    }

    public static int getNoteIndex(TunerOptions tunerOptions, String translatedNote) {
        return indexOfNote(tunerOptions.getNotes(), translatedNote);
    }
}
