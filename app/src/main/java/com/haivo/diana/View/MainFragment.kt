package com.haivo.diana.View

import android.app.Fragment
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.haivo.diana.Core.MusicEar
import com.haivo.diana.Core.MusicFactory
import com.haivo.diana.MainActivity
import com.haivo.diana.Model.BaseNote
import com.haivo.diana.R
import com.haivo.diana.Util.Tuner

class MainFragment : Fragment() {

    private var activity: MainActivity? = null
    private var currentNote: String = ""
    private lateinit var presenter: MusicPresenter
    private lateinit var instrument: KalimbaView

    init {
        MusicFactory.setInstrument(BaseNote.Instrument.KALIMBA)
        MusicFactory.startRandom()
        currentNote = MusicFactory.nextNote?.noteLabel ?: ""
    }

    fun start() {
        if (MusicEar.isMicrophoneAvailable) {
            MusicEar.start(Tuner.OnNoteFoundListener { note ->
                Log.d("Hai",
                        note.type.toString() + "|" + note.frequencyLabel + "|" + note.percentageLabel + "|" + note
                                .noteLabelWithAugAndOctave + "|" + note.statusText)

                checkNote(note.noteLabelWithAugAndOctave)
                //Log.d("Hai", "Generated Note: " + MusicFactory.getInstance().getNextNote().getNoteLabel());
            })
        } else if (!MusicEar.isRunning) {
            Toast.makeText(this.context, "Cannot access Mic", Toast.LENGTH_SHORT).show()
        }
    }

    fun stop() {
        MusicEar.stop()
    }


    private fun checkNote(detectedNote: String?) {
        presenter.setDetectedNote(detectedNote!!)
        instrument.setDetectedNote(detectedNote)
        if (detectedNote == currentNote) {
            currentNote = MusicFactory.nextNote?.noteLabel ?: ""
            presenter.setTargetNote(currentNote)
            instrument.setTargetNote(currentNote)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val rootView = inflater.inflate(R.layout.fragment_main, container, false)
        presenter = rootView.findViewById(R.id.presenter)
        instrument = rootView.findViewById(R.id.kalimba)
        presenter.setTargetNote(currentNote)
        instrument.setTargetNote(currentNote)
        return rootView
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is AppCompatActivity) {
            this.activity = context as MainActivity
        }
    }

    override fun onDetach() {
        super.onDetach()
        this.activity = null
    }

    override fun onStart() {
        super.onStart()
        start()
    }

    override fun onPause() {
        super.onPause()
        stop()
    }
}
