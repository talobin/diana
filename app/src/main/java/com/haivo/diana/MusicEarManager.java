package com.haivo.diana;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import com.haivo.diana.Model.TunerMode;
import com.haivo.diana.Util.Tuner;

public class MusicEarManager {
    private static final MusicEarManager ourInstance = new MusicEarManager();
    private Tuner tuner;

    public static MusicEarManager getInstance() {
        return ourInstance;
    }

    private MusicEarManager() {
    }

    public final boolean isRunning() {
        return tuner.isRecording();
    }

    /**
     * Initialize and start
     */
    public final void start(Tuner.OnNoteFoundListener listener) {
        if (tuner == null) {
            tuner = new Tuner(TunerMode.getChromaticMode());
        }
        tuner.setOnNoteFoundListener(listener);
        tuner.start();
    }

    /**
     * Stop and clean up
     */
    public final void stop() {
        if (tuner.isRecording()) {
            tuner.stop();
        }
        tuner.destroy();
    }

    public final void setNoteListener(Tuner.OnNoteFoundListener listener) {
        tuner.setOnNoteFoundListener(listener);
    }

    public static boolean isMicrophoneAvailable() {
        AudioRecord audio = null;
        boolean ready = true;
        try {
            int baseSampleRate = 44100;
            int channel = AudioFormat.CHANNEL_IN_MONO;
            int format = AudioFormat.ENCODING_PCM_16BIT;
            int buffSize = AudioRecord.getMinBufferSize(baseSampleRate, channel, format);
            audio = new AudioRecord(MediaRecorder.AudioSource.MIC, baseSampleRate, channel, format, buffSize);
            audio.startRecording();
            short buffer[] = new short[buffSize];
            int audioStatus = audio.read(buffer, 0, buffSize);

            if (audioStatus == AudioRecord.ERROR_INVALID_OPERATION
                || audioStatus == AudioRecord.STATE_UNINITIALIZED /* For Android 6.0 */) {
                ready = false;
            }
        } catch (Exception e) {
            ready = false;
        } finally {
            try {
                audio.release();
            } catch (Exception e) {
            }
        }

        return ready;
    }
}
