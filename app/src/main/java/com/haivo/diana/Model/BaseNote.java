package com.haivo.diana.Model;

public abstract class BaseNote {
    String translatedNote;
    int octave;
    public static String[] DEFAULT_NOTES =
        new String[] { "C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B" };
    BaseNote(String translatedNote, int octave) {
        this.translatedNote = translatedNote;
        this.octave = octave;
    }

    public String getTranslatedNote() {
        return translatedNote;
    }
    public int getOctave() {
        return octave;
    }

    public void setOctave(int octave) {
        this.octave = octave;
    }

    public void setTranslatedNote(String translatedNote) {
        this.translatedNote = translatedNote;
    }
}
