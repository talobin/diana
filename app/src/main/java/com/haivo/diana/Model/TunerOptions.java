package com.haivo.diana.Model;

public class TunerOptions {
    public int tunerBase;
    public boolean sharps;
    public String naming;
    public boolean sfx;
    public boolean suppressor;
    private static final boolean DEFAULT_SFX = true;
    private static final boolean DEFAULT_SUPPRESSOR = false;
    private static final boolean DEFAULT_SHARP = true;
    private static final int DEFAULT_CALIBRATION = 440;
    private static final String DEFAULT_NAMING = "english";
    private static final int CALIBRATION_STEP = 1;
    private static final int CALIBRATION_MIN = 415;
    private static final int CALIBRATION_MAX = 466;
    private String[] NOTES_ENGLISH_SHARPS =
        new String[] { "C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B" };
    private String[] NOTES_ENGLISH_FLATS =
        new String[] { "C", "Db", "D", "Eb", "E", "F", "Gb", "G", "Ab", "A", "Bb", "B" };
    private String[] NOTES_SOLFEGE_SHARPS =
        new String[] { "Do", "Di", "Re", "Ri", "Mi", "Fa", "Fi", "Sol", "Si", "La", "Li", "Si" };
    private String[] NOTES_SOLFEGE_FLATS =
        new String[] { "Do", "Ra", "Re", "Me", "Mi", "Fa", "Se", "Sol", "Le", "La", "Se", "Si" };

    public TunerOptions() {
        tunerBase = DEFAULT_CALIBRATION;
        sharps = DEFAULT_SHARP;
        naming = DEFAULT_NAMING;
        sfx = DEFAULT_SFX;
        suppressor = DEFAULT_SUPPRESSOR;
    }

    public String[] getNotes() {
        if (this.naming.equals("english")) {
            if (this.sharps) {
                return NOTES_ENGLISH_SHARPS;
            } else {
                return NOTES_ENGLISH_FLATS;
            }
        } else if (this.naming.equals("solfege")) {
            if (this.sharps) {
                return NOTES_SOLFEGE_SHARPS;
            } else {
                return NOTES_SOLFEGE_FLATS;
            }
        } else {
            return NOTES_SOLFEGE_SHARPS;
        }
    }

    public String getChromaticNotes() {
        String[] notes = getNotes();
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 1; i < 9; i++) {
            for (int j = 0; j < notes.length; j++) {
                stringBuilder.append(notes[j]);
                stringBuilder.append(i);
                stringBuilder.append(" ");
            }
        }
        return stringBuilder.toString();
    }
}
