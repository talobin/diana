package com.haivo.diana;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

public class IntroActivity extends AppCompatActivity {

    /*
        Launcher activity used for requesting permissions.
        If permissions are granted, move onto MainActivity.
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // If microphone permissions are granted, open MainActivity.
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
            == PackageManager.PERMISSION_GRANTED) {
            openMainActivity();
        }

        // Set AppTheme to remove Splash Screen background.
        setTheme(R.style.AppTheme);

        setContentView(R.layout.activity_intro);

        findViewById(R.id.intro_permission_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestPermissions();
            }
        });
    }

    private void openMainActivity() {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.RECORD_AUDIO }, 11);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case 11: {

                if (grantResults.length == 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(this, getString(R.string.intro_permission_denied_msg), Toast.LENGTH_LONG).show();
                } else {
                    openMainActivity();
                }
                return;
            }
        }
    }
}
