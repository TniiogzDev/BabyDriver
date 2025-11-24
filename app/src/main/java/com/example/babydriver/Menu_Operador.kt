package com.example.babydriver

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import com.airbnb.lottie.LottieAnimationView

class Menu_Operador : AppCompatActivity() {

    private var loggedInUser: Usuario? = null
    private var isAnimationPaused = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_operador)

        loggedInUser = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("LOGGED_IN_USER", Usuario::class.java)
        } else {
            @Suppress("DEPRECATION") intent.getParcelableExtra<Usuario>("LOGGED_IN_USER")
        }

        if (loggedInUser == null) {
            Toast.makeText(this, "Error de sesión. Por favor, inicie sesión de nuevo.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        val tvTitulo = findViewById<TextView>(R.id.tvTituloMenuoperador)
        val nombreAMostrar = if (!loggedInUser?.nombre.isNullOrEmpty()) loggedInUser?.nombre else loggedInUser?.rut
        tvTitulo.text = "Bienvenido ${nombreAMostrar ?: "Usuario"}"

        // --- Control de Animación y Sonido ---
        val lottieView = findViewById<LottieAnimationView>(R.id.lottieMenuoperador)
        val btnPause = findViewById<ImageButton>(R.id.btnPauseMenuoperador)
        val btnMute = findViewById<ImageButton>(R.id.btnMuteMenuoperador)

        updateMuteButtonIcon(btnMute)
        updatePauseButtonIcon(btnPause)

        btnMute.setOnClickListener {
            MusicManager.toggleMute()
            updateMuteButtonIcon(btnMute)
        }

        btnPause.setOnClickListener {
            isAnimationPaused = !isAnimationPaused
            if (isAnimationPaused) {
                lottieView.pauseAnimation()
            } else {
                lottieView.resumeAnimation()
            }
            updatePauseButtonIcon(btnPause)
        }

        // --- Navegación ---
        val buttonManipularCircuito = findViewById<Button>(R.id.btnManipularcircuitoMenuoperador)
        val buttonVerPerfil = findViewById<Button>(R.id.btnVerperfilMenuoperador)
        val buttonCerrarSesion = findViewById<Button>(R.id.btnCerrarsesionMenuoperador)

        buttonManipularCircuito.setOnClickListener {
            val intent = Intent(this, ManipularCircuito::class.java)
            intent.putExtra("LOGGED_IN_USER", loggedInUser)
            startActivity(intent)
        }

        buttonVerPerfil.setOnClickListener {
            val intent = Intent(this, VerPerfil::class.java)
            intent.putExtra("LOGGED_IN_USER", loggedInUser)
            startActivity(intent)
        }

        buttonCerrarSesion.setOnClickListener {
            val intent = Intent(this, Principal::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {}
        })
    }

    private fun updateMuteButtonIcon(button: ImageButton) {
        if (MusicManager.isMuted) {
            button.setImageResource(android.R.drawable.ic_lock_silent_mode)
        } else {
            button.setImageResource(android.R.drawable.ic_lock_silent_mode_off)
        }
    }

    private fun updatePauseButtonIcon(button: ImageButton) {
        if (isAnimationPaused) {
            button.setImageResource(android.R.drawable.ic_media_play)
        } else {
            button.setImageResource(android.R.drawable.ic_media_pause)
        }
    }
}
