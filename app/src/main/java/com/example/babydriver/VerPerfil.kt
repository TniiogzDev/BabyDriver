package com.example.babydriver

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.OnBackPressedCallback

class VerPerfil : AppCompatActivity() {

    private var loggedInUser: Usuario? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ver_perfil)

        loggedInUser = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("LOGGED_IN_USER", Usuario::class.java)
        } else {
            @Suppress("DEPRECATION") intent.getParcelableExtra<Usuario>("LOGGED_IN_USER")
        }

        // --- Referencias de la UI (CORREGIDAS para que coincidan con el XML) ---
        val tvTituloBienvenida = findViewById<TextView>(R.id.tvNombreusuarioVerperfil)
        val tvRut = findViewById<TextView>(R.id.tvValuerutVerperfil)
        val tvNombre = findViewById<TextView>(R.id.tvValuenombreVerperfil)
        val tvApellido = findViewById<TextView>(R.id.tvValueapellidoVerperfil)
        val tvEmail = findViewById<TextView>(R.id.tvValuecorreoVerperfil)
        val tvCargo = findViewById<TextView>(R.id.tvValuecargoVerperfil)
        val btnVolver = findViewById<Button>(R.id.btnVolverVerperfil)

        // Ocultar campo de contraseña que ya no se debe mostrar aquí
        findViewById<TextView>(R.id.tvLabelconVerperfil).visibility = View.GONE
        findViewById<TextView>(R.id.tvValueconVerperfil).visibility = View.GONE

        // --- Mostrar la información del usuario --- 
        loggedInUser?.let {
            tvTituloBienvenida.text = "Perfil de: ${it.nombre}"
            tvRut.text = ": ${it.rut}"
            tvNombre.text = ": ${it.nombre}"
            tvApellido.text = ": ${it.apellido}"
            tvEmail.text = ": ${it.email}"
            tvCargo.text = ": ${it.tipo}"
        }

        btnVolver.setOnClickListener {
            finish()
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {}
        })
    }
}