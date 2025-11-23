package com.example.babydriver

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.OnBackPressedCallback

class Estadisticas : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_estadisticas)

        val loggedInUser = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("LOGGED_IN_USER", Usuario::class.java)
        } else {
            @Suppress("DEPRECATION") intent.getParcelableExtra<Usuario>("LOGGED_IN_USER")
        }

        val tvNombreUsuario = findViewById<TextView>(R.id.tvNombreusuarioEstadisticas)
        if (loggedInUser != null) {
            tvNombreUsuario.text = "Usuario: ${loggedInUser.username}"
        }

        val buttonVolver = findViewById<Button>(R.id.btnVolverEstadisticas)
        buttonVolver.setOnClickListener {
            finish() // Cierra la actividad actual y vuelve a la anterior
        }

        // Bloquear el gesto de retroceso del sistema
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // No hacer nada para deshabilitar el botón de retroceso
            }
        })
    }
}
