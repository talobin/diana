package com.haivo.diana.Util;

import androidx.databinding.ObservableBoolean;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.media.audiofx.NoiseSuppressor;
import android.os.Handler;
import android.os.Looper;
import com.haivo.diana.Model.TunerMode;
import com.haivo.diana.Model.TunerOptions;
import com.haivo.diana.Model.TunerResult;
import java.util.HashMap;
import java.util.Map;

public class Tuner {
    private int sampleRate = 0;
    private int bufferSize = 0;
    private int readSize = 0;
    private float[] buffer = null;
    private short[] intermediaryBuffer = null;
    private short[] statusBuffer = new short[20];
    private String[] noteBuffer = new String[30];
    public boolean isFake = false;

    /* Preference Options */
    public TunerOptions options;
    /* // */

    public long ptr = 0;
    public long input = 0;
    public long pitch = 0;

    private boolean isRecording = false;
    private Handler handler = null;
    private AudioRecord audioRecord = null;

    private TunerResult currentNoteResult;
    private OnNoteFoundListener onNoteFoundListener;
    Thread audioThread;
    private NoiseSuppressor suppressor;

    private boolean isMuted = false;

    public TunerMode tunerMode;

    private boolean playedSfx = false;

    public ObservableBoolean hasValidResult = new ObservableBoolean(false);
    public ObservableBoolean hasCorrectResult = new ObservableBoolean(false);

    static {
        System.loadLibrary("aubio");
        System.loadLibrary("pitch");
    }

    //    public Tuner(Context context) {
    //        init("E2 A2 D3 G3 B3 E4");
    //    }

    public Tuner(TunerMode mode) {
        options = new TunerOptions();
        init(mode);
    }

    private void init(TunerMode mode) {
        tunerMode = mode;
        sampleRate = 44100;
        bufferSize = 4096;
        readSize = bufferSize / 4;
        buffer = new float[readSize];
        intermediaryBuffer = new short[readSize];
        handler = new Handler(Looper.getMainLooper());
    }

    public void start() {
        if (!isRecording&& !isFake) {
            isRecording = true;
            initPitch(sampleRate, bufferSize);
            audioRecord = new AudioRecord(MediaRecorder.AudioSource.DEFAULT,
                                          sampleRate,
                                          AudioFormat.CHANNEL_IN_DEFAULT,
                                          AudioFormat.ENCODING_PCM_16BIT,
                                          bufferSize);

            if (NoiseSuppressor.isAvailable() && options.suppressor) {
                suppressor = NoiseSuppressor.create(audioRecord.getAudioSessionId());
            }

            audioRecord.startRecording();
            audioThread = new Thread(new Runnable() {
                //Runs off the UI thread
                @Override
                public void run() {
                    findNote();
                }
            }, "Tuner Thread");
            audioThread.start();
        }
    }

    public void stop() {
        // stops the recording activity
        isRecording = false;
        sendNullResult();
        if (audioRecord != null) {
            audioRecord.stop();
            audioRecord.release();
            audioRecord = null;
            audioThread = null;
            onNoteFoundListener = null;
            if (suppressor != null) {
                suppressor.release();
                suppressor = null;
            }
        }
    }

    private void findNote() {
        while (isRecording) {
            if (!isMuted) {
                audioRecord.read(intermediaryBuffer, 0, readSize);
                buffer = shortArrayToFloatArray(intermediaryBuffer);
                final TunerResult result = new TunerResult(getPitch(buffer), tunerMode.getNotesObjects(), options);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        pushType(result.type);
                        TunerResult.INDICATOR_TYPE tempType = result.type;
                        pushNote(result.note + result.octave);
                        result.type = getType();

                        hasValidResult.set(result.frequency > -1);
                        hasCorrectResult.set(getType() == TunerResult.INDICATOR_TYPE.CORRECT);

                        if (getType() == TunerResult.INDICATOR_TYPE.CORRECT && !tunerMode.isChromatic()) {
                            tunerMode.setInTune(result.getNoteLabelWithAugAndOctave());
                        }

                        if (!(tempType == TunerResult.INDICATOR_TYPE.INACTIVE
                            && result.type != TunerResult.INDICATOR_TYPE.INACTIVE) && onNoteFoundListener != null && (
                            result.note
                                + result.octave).equals(getNote())) {
                            currentNoteResult = result;
                            onNoteFoundListener.onEvent(result);
                            if (getType() == TunerResult.INDICATOR_TYPE.CORRECT) {
                                if (!playedSfx) {
                                    playedSfx = true;
                                    new android.os.Handler().postDelayed(new Runnable() {
                                        public void run() {
                                            playedSfx = false;
                                        }
                                    }, 3000);
                                }
                            }
                        }
                        //                        if(onNoteFoundListener != null)
                        //                            onNoteFoundListener.onEvent(result);
                    }
                });
            }
        }
    }

    private void pushType(TunerResult.INDICATOR_TYPE v) {
        short s = 0;
        switch (v) {
            case ACTIVE:
                s = 1;
                break;
            case CORRECT:
                s = 2;
                break;
            case INCORRECT:
                s = 3;
        }
        for (int i = 0; i < statusBuffer.length; i++) {
            if (i != statusBuffer.length - 1) {
                statusBuffer[i] = statusBuffer[i + 1];
            } else {
                statusBuffer[i] = s;
            }
        }
    }

    private void pushNote(String v) {
        for (int i = 0; i < noteBuffer.length; i++) {
            if (i != noteBuffer.length - 1) {
                noteBuffer[i] = noteBuffer[i + 1];
            } else {
                noteBuffer[i] = v;
            }
        }
    }

    private TunerResult.INDICATOR_TYPE getType() {
        short type = statusBuffer[0];
        if (type == 0) {
            for (int i = 0; i < statusBuffer.length; i++) {
                if (type != statusBuffer[i]) {
                    type = 1;
                    break;
                }
            }
        } else {
            for (int i = 0; i < statusBuffer.length; i++) {
                if (type != statusBuffer[i] && statusBuffer[i] != 0) {
                    type = 1;
                    break;
                }
            }
        }
        TunerResult.INDICATOR_TYPE i = null;
        switch (type) {
            case 0:
                i = TunerResult.INDICATOR_TYPE.INACTIVE;
                break;
            case 1:
                i = TunerResult.INDICATOR_TYPE.ACTIVE;
                break;
            case 2:
                i = TunerResult.INDICATOR_TYPE.CORRECT;
                break;
            case 3:
                i = TunerResult.INDICATOR_TYPE.INCORRECT;
                break;
        }
        return i;
    }

    String getNote() {
        String[] ary = noteBuffer;
        Map<String, Integer> m = new HashMap<>();

        for (String a : ary) {
            Integer occ = m.get(a);
            m.put(a, (occ == null) ? 1 : occ + 1);
        }

        int max = -1;
        String mostFrequent = null;

        for (Map.Entry<String, Integer> e : m.entrySet()) {
            if (e.getValue() > max) {
                mostFrequent = e.getKey();
                max = e.getValue();
            }
        }

        return mostFrequent;
    }

    public String getCurrentNoteResultLabel() {
        return currentNoteResult.getNoteLabelWithAug();
    }

    private float[] shortArrayToFloatArray(short[] array) {
        float[] fArray = new float[array.length];
        for (int i = 0; i < array.length; i++) {
            fArray[i] = (float) array[i];
        }
        return fArray;
    }

    public void sendNullResult() {
        if (onNoteFoundListener != null) {
            TunerResult nullTunerResult = new TunerResult(0, tunerMode.getNotesObjects(), options);
            nullTunerResult.type = TunerResult.INDICATOR_TYPE.INACTIVE;
            nullTunerResult.percentage = 50f;
            onNoteFoundListener.onEvent(nullTunerResult);
        }
    }

    public boolean isMuted() {
        return isMuted;
    }

    public boolean isRecording() {
        return isRecording;
    }
    public void setMuted(boolean muted) {
        isMuted = muted;
    }


    public void setOnNoteFoundListener(Tuner.OnNoteFoundListener eventListener) {
        onNoteFoundListener = eventListener;
    }

    public void removeOnNoteFoundListener() {
        onNoteFoundListener = null;
    }

    public void destroy() {
        cleanupPitch();
    }

    public interface OnNoteFoundListener {
        void onEvent(TunerResult note);
    }

    private native float getPitch(float[] input);
    private native void initPitch(int sampleRate, int B);
    private native void cleanupPitch();
}
