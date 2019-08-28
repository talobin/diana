package com.haivo.diana.Model;

public class TunerResult {

    public double percentage;
    public double percentageActual;
    public String note;
    private TunerOptions tunerOptions;
    public String statusText = "";

    public enum INDICATOR_TYPE {
        ACTIVE, CORRECT, INACTIVE, INCORRECT
    }
    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public int getOctave() {
        return octave;
    }

    public void setOctave(int octave) {
        this.octave = octave;
    }

    public float getFrequency() {
        return frequency;
    }

    public void setFrequency(float frequency) {
        this.frequency = frequency;
    }

    public String getNoteLabel() {
        String noteName = this.note;
        if (noteName.contains("#") || noteName.contains("b")) {
            noteName = noteName.substring(0, 1);
        }
        return noteName;
    }

    public String getNoteLabelWithAug() {
        String noteName = this.note;
        return noteName;
    }

    public String getNoteLabelWithAugAndOctave() {
        String noteName = this.note + this.octave;
        return noteName;
    }

    public String getFrequencyLabel() {
        return String.valueOf(Math.round(frequency)) + " Hz";
    }

    public String getOctaveLabel() {
        return this.octave != 0 ? String.valueOf(this.octave) : "";
    }

    public Double getPercentage() {
        if (this.type != TunerResult.INDICATOR_TYPE.INACTIVE) {
            return this.percentage;
        } else {
            return -1.00;
        }
    }

    public float getPercentageLabel() {
        return (Math.round(this.percentageActual) - 50f) / 10f;
    }

    public boolean isCorrect() {
        return this.type == TunerResult.INDICATOR_TYPE.CORRECT;
    }

    public boolean isValid() {
        return this.type != TunerResult.INDICATOR_TYPE.INACTIVE;
    }

    public boolean isNull() {
        return this.type == TunerResult.INDICATOR_TYPE.INACTIVE;
    }

    public Double getPercentageActual() {
        return Math.floor(this.percentageActual - 50f) / 10f;
    }

    public TunerResult.INDICATOR_TYPE getType() {
        return this.type;
    }

    public String getNoteAug() {
        if (this.noteObj != null) {
            if (this.noteObj.isAccidental() && this.tunerOptions.naming.equals("english")) {
                return this.tunerOptions.sharps ? "#" : "b";
            }
        }
        return "";
    }

    public int octave;
    public float frequency;
    private float tolerance = .1f;
    public TunerResult.INDICATOR_TYPE type;
    private ExternalNote noteObj;

    public TunerResult(double freq, ExternalNote[] notesMatch, TunerOptions tunerOptions) {
        this.tunerOptions = tunerOptions;
        int index = -1;
        Double dist = java.lang.Double.MAX_VALUE;
        for (int i = 0; i < notesMatch.length; i++) {
            Double d = Math.abs(freq - notesMatch[i].frequency);
            if (d < dist) {
                index = i;
                dist = d;
            }
        }
        ExternalNote mNote = notesMatch[index];
        this.octave = mNote.octave;
        this.note = mNote.getTranslatedNote();

        if (freq != 0) {

            this.percentage = ExternalNote.parse(freq, tunerOptions).offsetFrom(mNote) + 50;
            this.percentageActual = this.percentage;

            if (this.percentage > 50 - 50 * tolerance && this.percentage < 50 + 50 * tolerance) {
                this.percentage = 50;
                this.type = TunerResult.INDICATOR_TYPE.CORRECT;
            } else if (this.percentage < 0) {
                this.percentage = 5;
                this.type = TunerResult.INDICATOR_TYPE.INCORRECT;
            } else if (this.percentage > 100) {
                this.percentage = 95;
                this.type = TunerResult.INDICATOR_TYPE.INCORRECT;
            } else {
                this.type = TunerResult.INDICATOR_TYPE.ACTIVE;
            }

            this.frequency = (float) Math.round(freq * 100.00) / 100.00f;
            this.noteObj = mNote;
        } else {
            this.note = "";
            this.noteObj = null;
            this.octave = 0;
            this.frequency = 0.0f;
            //            this.percentage = 50.0;
            this.type = TunerResult.INDICATOR_TYPE.INACTIVE;
        }

        if (percentageActual > 50f && (type == TunerResult.INDICATOR_TYPE.ACTIVE
            || type == TunerResult.INDICATOR_TYPE.INCORRECT)) {
            this.statusText = "sharp by " + Math.abs(50 - Math.round(getPercentageActual())) + "%";
        } else if (percentageActual < 50f && (type == TunerResult.INDICATOR_TYPE.ACTIVE
            || type == TunerResult.INDICATOR_TYPE.INCORRECT)) {
            this.statusText = "flat by " + Math.abs(50 - Math.round(getPercentageActual())) + "%";
        } else if (type == TunerResult.INDICATOR_TYPE.CORRECT) {
            this.statusText = "in tune.";
        } else if (type == TunerResult.INDICATOR_TYPE.INACTIVE) {
            this.statusText = "aux.";
        }
    }
}
