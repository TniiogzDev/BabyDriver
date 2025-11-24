package com.example.babydriver

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import com.airbnb.lottie.LottieAnimationView
import com.google.firebase.database.FirebaseDatabase

class VEditarUsuario : AppCompatActivity() {

    private lateinit var db: FirebaseDatabase
    private var isAnimationPaused = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_v_editar_usuario)

        db = FirebaseDatabase.getInstance()

        val usuario = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("USER_DATA", Usuario::class.java)
        } else {
            @Suppress("DEPRECATION") intent.getParcelableExtra<Usuario>("USER_DATA")
        }

        // --- CRASH FIX: Si el usuario es nulo, no podemos continuar ---
        if (usuario == null) {
            Toast.makeText(this, "Error: No se recibieron datos del usuario.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        // --- Controles de Pausa y Sonido ---
        val lottieView = findViewById<LottieAnimationView>(R.id.lottieEditarusuario)
        val btnPause = findViewById<ImageButton>(R.id.btnPauseEditarusuario)
        val btnMute = findViewById<ImageButton>(R.id.btnMuteEditarusuario)

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

        // --- Lógica de Edición de Usuario ---
        val txtRut = findViewById<EditText>(R.id.txtRutEditarusuario)
        val txtNombre = findViewById<EditText>(R.id.txtNomEditarusuario)
        val txtApellido = findViewById<EditText>(R.id.txtApeEditarusuario)
        val txtEmail = findViewById<EditText>(R.id.txtCorreoEditarusuario)
        val spinnerTipo = findViewById<Spinner>(R.id.spCargoEditarusuario)
        val btnGuardar = findViewById<Button>(R.id.btnGuardarEditarusuario)
        val btnVolver = findViewById<Button>(R.id.btnVolverEditarusuario)

        val tipos = arrayOf("operador", "admin", "auditor")
        val adapter = ArrayAdapter(this, R.layout.custom_spinner_item, tipos)
        adapter.setDropDownViewResource(R.layout.custom_spinner_item)
        spinnerTipo.adapter = adapter

        // --- CRASH FIX: Rellenar el formulario de forma segura ---
        txtRut.setText(usuario.rut ?: "")
        txtNombre.setText(usuario.nombre ?: "")
        txtApellido.setText(usuario.apellido ?: "")
        txtEmail.setText(usuario.email ?: "")
        val tipoPosition = tipos.indexOf(usuario.tipo?.lowercase())
        if (tipoPosition >= 0) {
            spinnerTipo.setSelection(tipoPosition)
        }

        btnGuardar.setOnClickListener {
            if (usuario.key.isNullOrEmpty()) {
                Toast.makeText(this, "Error: No se pudo identificar al usuario.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val updatedUserMap = hashMapOf<String, Any?>(
                "rut" to txtRut.text.toString(),
                "nombre" to txtNombre.text.toString(),
                "apellido" to txtApellido.text.toString(),
                "email" to txtEmail.text.toString(),
                "tipo" to spinnerTipo.selectedItem.toString()
            )

            db.reference.child(usuario.key).updateChildren(updatedUserMap)
                .addOnSuccessListener {
                    Toast.makeText(this, "Usuario actualizado exitosamente.", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener { 
                    Toast.makeText(this, "Error al actualizar el usuario.", Toast.LENGTH_SHORT).show()
                }
        }

        btnVolver.setOnClickListener {
            finish()
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
