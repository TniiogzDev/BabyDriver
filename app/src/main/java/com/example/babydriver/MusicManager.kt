package com.example.babydriver

import android.content.Context
import android.media.MediaPlayer

object MusicManager {
    private var mediaPlayer: MediaPlayer? = null
    var isMuted = false
        private set
    private var isForcePaused = false // Para la pausa específica del Easter Egg

    fun start(context: Context) {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(context.applicationContext, R.raw.background_music)
            mediaPlayer?.isLooping = true
            if (!isMuted) {
                mediaPlayer?.start()
            }
        }
    }

    fun pauseForEasterEgg() {
        if (mediaPlayer?.isPlaying == true) {
            mediaPlayer?.pause()
            isForcePaused = true
        }
    }

    fun resumeFromEasterEgg() {
        if (isForcePaused) {
            if (!isMuted) {
                mediaPlayer?.start()
            }
            isForcePaused = false
        }
    }

    fun onAppBackgrounded() {
        if (mediaPlayer?.isPlaying == true) {
            mediaPlayer?.pause()
        }
    }

    fun onAppForegrounded() {
        if (mediaPlayer?.isPlaying == false && !isForcePaused && !isMuted) {
            mediaPlayer?.start()
        }
    }

    fun toggleMute() {
        isMuted = !isMuted
        if (isMuted) {
            mediaPlayer?.setVolume(0f, 0f)
        } else {
            mediaPlayer?.setVolume(1f, 1f)
            // Si no estaba pausado a la fuerza y no estaba sonando, que empiece
            if (mediaPlayer?.isPlaying == false && !isForcePaused) {
                mediaPlayer?.start()
            }
        }
    }

    fun stop() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}
