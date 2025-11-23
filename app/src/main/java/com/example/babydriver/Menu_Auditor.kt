package com.example.babydriver

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.OnBackPressedCallback

class Menu_Auditor : AppCompatActivity() {

    private var loggedInUser: Usuario? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_auditor)

        loggedInUser = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("LOGGED_IN_USER", Usuario::class.java)
        } else {
            @Suppress("DEPRECATION") intent.getParcelableExtra<Usuario>("LOGGED_IN_USER")
        }

        val tvTitulo = findViewById<TextView>(R.id.tvTituloMenuauditor)
        tvTitulo.text = "Bienvenido ${loggedInUser?.nombre ?: "Usuario"}"

        val btnMute = findViewById<ImageButton>(R.id.btnMuteMenuauditor)
        updateMuteButtonIcon(btnMute)

        btnMute.setOnClickListener {
            MusicManager.toggleMute()
            updateMuteButtonIcon(btnMute)
        }

        val buttonVerAcciones = findViewById<Button>(R.id.btnVeraccionesMenuauditor)
        val buttonVerEstadisticas = findViewById<Button>(R.id.btnVerEstadisticasMenuauditor)
        val buttonVerPerfil = findViewById<Button>(R.id.btnVerperfilMenuauditor)
        val buttonCerrarSesion = findViewById<Button>(R.id.btnCerrarsesionMenuauditor)


        buttonVerAcciones.setOnClickListener {
            val intent = Intent(this, VerAcciones::class.java)
            intent.putExtra("LOGGED_IN_USER", loggedInUser)
            startActivity(intent)
        }

        buttonVerEstadisticas.setOnClickListener {
            val intent = Intent(this, Estadisticas::class.java)
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
}
