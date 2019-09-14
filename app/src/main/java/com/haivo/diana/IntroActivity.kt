package com.haivo.diana

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.widget.Toast

class IntroActivity : AppCompatActivity() {

    /*
        Launcher activity used for requesting permissions.
        If permissions are granted, move onto MainActivity.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // If microphone permissions are granted, open MainActivity.
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            openMainActivity()
        }

        // Set AppTheme to remove Splash Screen background.
        setTheme(R.style.AppTheme)

        setContentView(R.layout.activity_intro)

        findViewById<View>(R.id.intro_permission_button).setOnClickListener { requestPermissions() }
    }

    private fun openMainActivity() {
        val i = Intent(this, MainActivity::class.java)
        startActivity(i)
        finish()
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), 11)
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>,
                                            grantResults: IntArray) {
        when (requestCode) {
            11 -> {
                return if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, getString(R.string.intro_permission_denied_msg), Toast.LENGTH_LONG).show()
                } else {
                    openMainActivity()
                }
            }
        }
    }
}
