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
import com.haivo.diana.MusicEarManager;
import com.haivo.diana.Util.Tuner;
import com.haivo.diana.Model.TunerResult;
import com.haivo.diana.databinding.FragmentTunerBinding;

public class MainFragment extends Fragment {

    private MainActivity activity;
    private FragmentTunerBinding binding;

    public MainFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void start() {
        if (MusicEarManager.isMicrophoneAvailable()) {
            MusicEarManager.getInstance().start(new Tuner.OnNoteFoundListener() {
                @Override
                public void onEvent(TunerResult note) {
                    Log.d("Hai",
                          note.getType() + "|" + note.getFrequencyLabel() + "|" + note.getPercentageLabel() + "|" + note
                              .getNoteLabelWithAugAndOctave() + "|" + note.statusText);
                }
            });
        } else if ((!MusicEarManager.getInstance().isRunning())) {
            Toast.makeText(this.getContext(), "Cannot access Mic", Toast.LENGTH_SHORT).show();
        }
    }

    public void stop() {
        MusicEarManager.getInstance().stop();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentTunerBinding.inflate(inflater, container, false);

        return binding.getRoot();
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
