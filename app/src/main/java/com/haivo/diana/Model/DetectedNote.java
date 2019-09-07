package com.haivo.diana.Model;

import androidx.databinding.ObservableBoolean;
import com.haivo.diana.Util.NoteUtils;

public class DetectedNote extends BaseNote {

    String realNote;
    double frequency;
    double offset;
    double actualFrequency;
    public ObservableBoolean isInTune = new ObservableBoolean(false);

    private TunerOptions tunerOptions;

    public DetectedNote(String translatedNote,
                        int octave,
                        double frequency,
                        double actualFrequency,
                        double offset,
                        TunerOptions tunerOptions) {
        super(translatedNote, octave);
        this.translatedNote = translatedNote;
        this.realNote = NoteUtils.getRealNote(tunerOptions, translatedNote);
        this.octave = octave;
        this.frequency = frequency;
        this.offset = offset;
        this.actualFrequency = actualFrequency;
        this.tunerOptions = tunerOptions;
    }

    public double offsetFrom(DetectedNote n) {
        double nAboveA = Math.log((n.actualFrequency / tunerOptions.tunerBase)) / Math.log(Math.pow(2, 1.00 / 12.00));

        double thisAboveA =
            Math.log((this.actualFrequency / tunerOptions.tunerBase)) / Math.log(Math.pow(2, 1.00 / 12.00));
        ;
        return 100.00 * (thisAboveA - nAboveA);
    }

    public boolean isAccidental() {
        // Accidental positions: 1 3 6 8 10

        int index = NoteUtils.getNoteIndex(tunerOptions, getTranslatedNote());
        return (index == 1 || index == 3 || index == 6 || index == 8 || index == 10);
    }

    public String getRealNote() {
        return realNote;
    }

    public void setRealNote(String realNote) {
        this.realNote = realNote;
    }

    public double getFrequency() {
        return frequency;
    }

    public void setFrequency(double frequency) {
        this.frequency = frequency;
    }

    public int semiTonesFromBase() {
        return (int) Math.round(Math.log(this.frequency / tunerOptions.tunerBase) / Math.log(Math.pow(2, 2.00 / 12)));
    }

    public boolean getIsInTune() {
        return isInTune.get();
    }

    public static String getNotes(String s, TunerOptions tunerOptions) {
        String[] notes = s.split(" ");
        StringBuilder notesString = new StringBuilder();
        for (int i = 0; i < notes.length; i++) {
            notesString.append(NoteUtils.parseNote(notes[i], tunerOptions).getTranslatedNote());
            notesString.append(NoteUtils.parseNote(notes[i], tunerOptions).getOctave());
            notesString.append(" ");
        }
        return notesString.toString();
    }

    public String toString() {
        return getTranslatedNote() + octave;
    }
}