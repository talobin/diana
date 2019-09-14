package com.haivo.diana.Util;

import com.haivo.diana.Model.BaseNote;
import com.haivo.diana.Model.TunerOptions;

public final class NoteUtils {
    public static BaseNote.DetectedNote parseNote(String s, TunerOptions tunerOptions) {
        String note = s.substring(0, s.length() - 1);
        int octave = Integer.valueOf(s.substring(s.length() - 1, s.length()));
        double semitones = (octave - 4) * 12 + indexOfNote(BaseNote.Companion.getDEFAULT_NOTES(), note) - indexOfNote(BaseNote.Companion.getDEFAULT_NOTES(), "A");
        double BASE = tunerOptions.getTunerBase();
        double frequency = BASE * Math.pow(Math.pow(2, (1.00 / 12.00)), semitones);

        String translatedNote = tunerOptions.getNotes()[indexOfNote(BaseNote.Companion.getDEFAULT_NOTES(), note)];
        return new BaseNote.DetectedNote(translatedNote, octave, frequency, frequency, 0, tunerOptions);
    }

    public static BaseNote.DetectedNote parseNote(double frequency, TunerOptions tunerOptions) {
        double aboveA = Math.log((frequency / tunerOptions.getTunerBase())) / Math.log(Math.pow(2.00, 1 / 12.00));
        double closest = tunerOptions.getTunerBase() * Math.pow(Math.pow(2, 1.00 / 12.00), Math.round(aboveA));

        // NOTE ( 57 is the number of semitones of A4 )
        int index = (int) ((57 + Math.round(aboveA)) % 12);
        String note = tunerOptions.getNotes()[index];
        String localizedNote = BaseNote.Companion.getDEFAULT_NOTES()[index];

        // OCTAVE
        int octave = (int) (Math.floor((Math.log(frequency) - Math.log(tunerOptions.getTunerBase())) / Math.log(2) + 4.0));

        double offset = 100 * (aboveA - Math.round(aboveA));
        return new BaseNote.DetectedNote(note, octave, closest, frequency, offset, tunerOptions);
    }

    public static String[] getNotesByInstrument(BaseNote.Instrument mode) {
        switch (mode) {
            case KALIMBA:
                return BaseNote.Companion.getKALIMBA_NOTES();
            case FLUTE:
                return BaseNote.Companion.getVIETNAMESE_FLUTE_NOTES();
            default:
                return BaseNote.Companion.getCHROMATIC_NOTES();
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
        return BaseNote.Companion.getDEFAULT_NOTES()[getNoteIndex(tunerOptions, translatedNote)];
    }

    public static int getNoteIndex(TunerOptions tunerOptions, String translatedNote) {
        return indexOfNote(tunerOptions.getNotes(), translatedNote);
    }
}
