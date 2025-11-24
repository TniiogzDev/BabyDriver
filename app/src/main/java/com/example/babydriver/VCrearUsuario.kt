package com.example.babydriver

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
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class VCrearUsuario : AppCompatActivity() {

    private val dbRef = FirebaseDatabase.getInstance().reference
    private var isAnimationPaused = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_v_crear_usuario)

        // --- Controles de Pausa y Sonido ---
        val lottieView = findViewById<LottieAnimationView>(R.id.lottieCrearusuario)
        val btnPause = findViewById<ImageButton>(R.id.btnPauseCrearusuario)
        val btnMute = findViewById<ImageButton>(R.id.btnMuteCrearusuario)

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

        // --- Lógica de Creación de Usuario ---
        val spinner: Spinner = findViewById(R.id.spCargoCrearusuario)
        val cargos = arrayOf("Tipo de Usuario", "operador", "admin", "auditor")
        val adapter = ArrayAdapter(this, R.layout.custom_spinner_item, cargos)
        adapter.setDropDownViewResource(R.layout.custom_spinner_item)
        spinner.adapter = adapter

        val txtRut = findViewById<EditText>(R.id.txtRutCrearusuario)
        txtRut.addTextChangedListener(RutTextWatcher(txtRut))

        val buttonRegistrar = findViewById<Button>(R.id.btnRegCrearusuario)
        buttonRegistrar.setOnClickListener {
            val rut = txtRut.text.toString().trim()
            val nombre = findViewById<EditText>(R.id.txtNomCrearusuario).text.toString().trim()
            val apellido = findViewById<EditText>(R.id.txtApeCrearusuario).text.toString().trim()
            val email = findViewById<EditText>(R.id.txtCorreoCrearusuario).text.toString().trim()
            val passwordStr = findViewById<EditText>(R.id.txtConCrearusuario).text.toString().trim()
            val repeatPassword = findViewById<EditText>(R.id.txtRepconCrearusuario).text.toString().trim()
            val tipo = spinner.selectedItem.toString()

            if (rut.isNotEmpty() && nombre.isNotEmpty() && apellido.isNotEmpty() && email.isNotEmpty() && passwordStr.isNotEmpty() && repeatPassword.isNotEmpty() && spinner.selectedItemPosition != 0) {
                if (passwordStr == repeatPassword) {
                    val encryptedPassword = CryptoHelper.encrypt(passwordStr)
                    if (encryptedPassword == null) {
                        Toast.makeText(this, "Error fatal al cifrar la contraseña.", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }

                    dbRef.orderByChild("rut").equalTo(rut).addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (snapshot.exists()) {
                                Toast.makeText(this@VCrearUsuario, "El RUT ya se encuentra registrado.", Toast.LENGTH_SHORT).show()
                            } else {
                                registrarNuevoUsuario(rut, nombre, apellido, email, tipo, encryptedPassword)
                            }
                        }
                        override fun onCancelled(error: DatabaseError) {
                            Toast.makeText(this@VCrearUsuario, "Error al verificar RUT: ${error.message}", Toast.LENGTH_SHORT).show()
                        }
                    })
                } else {
                    Toast.makeText(this, "Las contraseñas no coinciden.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Por favor, complete todos los campos.", Toast.LENGTH_SHORT).show()
            }
        }

        findViewById<Button>(R.id.btnVolverCrearusuario).setOnClickListener { finish() }
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {}
        })
    }

    private fun registrarNuevoUsuario(rut: String, nombre: String, apellido: String, email: String, tipo: String, encryptedPass: String) {
        dbRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val userCount = snapshot.children.filter { it.key?.startsWith("user") ?: false }.count()
                val newUserId = "user${String.format("%02d", userCount + 1)}"

                val userMap = hashMapOf<String, Any>(
                    "rut" to rut,
                    "nombre" to nombre,
                    "apellido" to apellido,
                    "email" to email,
                    "tipo" to tipo,
                    "contraseña" to encryptedPass
                )

                dbRef.child(newUserId).setValue(userMap).addOnSuccessListener {
                    Toast.makeText(this@VCrearUsuario, "Usuario registrado como $newUserId", Toast.LENGTH_SHORT).show()
                    finish()
                }.addOnFailureListener { e ->
                    Toast.makeText(this@VCrearUsuario, "Error al guardar: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@VCrearUsuario, "Error al contar usuarios: ${error.message}", Toast.LENGTH_SHORT).show()
            }
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
