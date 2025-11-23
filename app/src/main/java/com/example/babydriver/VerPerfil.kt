package com.example.babydriver

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.OnBackPressedCallback

class VerPerfil : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ver_perfil)

        val loggedInUser = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("LOGGED_IN_USER", Usuario::class.java)
        } else {
            @Suppress("DEPRECATION") intent.getParcelableExtra<Usuario>("LOGGED_IN_USER")
        }

        // Vincular vistas
        val tvNombreUsuario = findViewById<TextView>(R.id.tvNombreusuarioVerperfil)
        val tvUsername = findViewById<TextView>(R.id.tvValuerutVerperfil)
        val tvNombre = findViewById<TextView>(R.id.tvValuenombreVerperfil)
        val tvApellido = findViewById<TextView>(R.id.tvValueapellidoVerperfil)
        val tvCorreo = findViewById<TextView>(R.id.tvValuecorreoVerperfil)
        val tvCargo = findViewById<TextView>(R.id.tvValuecargoVerperfil)
        val tvContraseña = findViewById<TextView>(R.id.tvValueconVerperfil)

        // Mostrar datos del usuario
        if (loggedInUser != null) {
            tvNombreUsuario.text = "Usuario: ${loggedInUser.username}"
            tvUsername.text = ": ${loggedInUser.username}"
            tvNombre.text = ": ${loggedInUser.nombre}"
            tvApellido.text = ": ${loggedInUser.apellido}"
            tvCorreo.text = ": ${loggedInUser.email}"
            tvCargo.text = ": ${loggedInUser.tipo}"
            tvContraseña.text = ": ${loggedInUser.contraseña}"
        }

        val buttonVolver = findViewById<Button>(R.id.btnVolverVerperfil)
        buttonVolver.setOnClickListener {
            finish()
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {}
        })
    }
}
