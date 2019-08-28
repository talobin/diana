package com.haivo.diana.Model;

public class GeneratedNote extends BaseNote {
    private long timeStamp;

    public GeneratedNote(long timeStamp, String note, int octave) {
        super(note, octave);
        this.timeStamp = timeStamp;
    }
    public long getTimeStamp() {
        return timeStamp;
    }
}
