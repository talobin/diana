package com.haivo.diana

import android.graphics.PixelFormat
import android.os.Bundle
import android.view.Window
import android.view.WindowManager

import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set AppTheme to remove Splash Screen background.
        setTheme(R.style.AppTheme)

        setContentView(R.layout.activity_main)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        val window = window
        window.setFlags(WindowManager.LayoutParams.FLAG_DITHER, WindowManager.LayoutParams.FLAG_DITHER)
        window.setFormat(PixelFormat.RGBA_8888)
        window.setFormat(PixelFormat.TRANSLUCENT)
    }
}
