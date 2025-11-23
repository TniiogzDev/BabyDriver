package com.example.babydriver

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.VideoView
import androidx.activity.OnBackPressedCallback

class easter_egg : AppCompatActivity() {
    private lateinit var videoView: VideoView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_easter_egg)

        videoView = findViewById(R.id.videoKeyboardcat)
        val videoPath = "android.resource://" + packageName + "/" + R.raw.keyboardcat
        val uri = Uri.parse(videoPath)
        videoView.setVideoURI(uri)

        videoView.setOnPreparedListener { mediaPlayer ->
            mediaPlayer.isLooping = true
            videoView.start()
        }

        val buttonVolver = findViewById<Button>(R.id.btnVolverEasteregg)
        buttonVolver.setOnClickListener {
            val intent = Intent(this, Principal::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {}
        })
    }

    override fun onResume() {
        super.onResume()
        MusicManager.pauseForEasterEgg()
        videoView.start()
    }

    override fun onPause() {
        super.onPause()
        MusicManager.resumeFromEasterEgg()
    }
}
