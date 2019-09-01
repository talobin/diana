package com.haivo.diana.View;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.haivo.diana.MainActivity;
import com.haivo.diana.Model.BaseNote;
import com.haivo.diana.Model.TunerResult;
import com.haivo.diana.Core.MusicEar;
import com.haivo.diana.Core.MusicFactory;
import com.haivo.diana.R;
import com.haivo.diana.Util.Tuner;

public class MainFragment extends Fragment {

    private MainActivity activity;
    private MusicPresenter presenter;
    private String currentNote;
    private KalimbaView instrument;

    public MainFragment() {
        MusicFactory.getInstance().setInstrument(BaseNote.Instrument.KALIMBA);
        MusicFactory.getInstance().startRandom();
        currentNote = MusicFactory.getInstance().getNextNote().getNoteLabel();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void start() {
        if (MusicEar.isMicrophoneAvailable()) {
            MusicEar.getInstance().start(new Tuner.OnNoteFoundListener() {
                @Override
                public void onEvent(TunerResult note) {
                    Log.d("Hai",
                          note.getType() + "|" + note.getFrequencyLabel() + "|" + note.getPercentageLabel() + "|" + note
                              .getNoteLabelWithAugAndOctave() + "|" + note.statusText);

                    checkNote(note.getNoteLabelWithAugAndOctave());
                    //Log.d("Hai", "Generated Note: " + MusicFactory.getInstance().getNextNote().getNoteLabel());
                }
            });
        } else if ((!MusicEar.getInstance().isRunning())) {
            Toast.makeText(this.getContext(), "Cannot access Mic", Toast.LENGTH_SHORT).show();
        }
    }

    private void checkNote(String detectedNote) {
        presenter.setDetectedNote(detectedNote);
        instrument.setDetectedNote(detectedNote);
        if (detectedNote != null && currentNote != null && detectedNote.equals(currentNote)) {
            currentNote = MusicFactory.getInstance().getNextNote().getNoteLabel();
            presenter.setTargetNote(currentNote);
            instrument.setTargetNote(currentNote);
        }
    }

    public void stop() {
        MusicEar.getInstance().stop();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        presenter =  rootView.findViewById(R.id.presenter);
        instrument = rootView.findViewById(R.id.kalimba);
        presenter.setTargetNote(currentNote);
        if(instrument==null){
            instrument = (KalimbaView) ((ViewGroup)rootView).getChildAt(1);
        }
        instrument.setTargetNote(currentNote);
        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof AppCompatActivity) {
            this.activity = (MainActivity) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.activity = null;
    }

    @Override
    public void onStart() {
        super.onStart();
        start();
    }

    @Override
    public void onPause() {
        super.onPause();
        stop();
    }
}
