package com.example.babydriver

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import com.airbnb.lottie.LottieAnimationView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Principal : AppCompatActivity() {

    private var isAnimationPaused = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_principal)

        MusicManager.start(this)

        // --- Controles de Pausa y Sonido ---
        val lottieView = findViewById<LottieAnimationView>(R.id.lottiePrincipal)
        val btnPause = findViewById<ImageButton>(R.id.btnPausePrincipal)
        val btnMute = findViewById<ImageButton>(R.id.btnMutePrincipal)

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

        // --- Lógica de Login ---
        val editTextUser = findViewById<EditText>(R.id.txtUsuPrincipal)
        val editTextPassword = findViewById<EditText>(R.id.txtConPrincipal)
        val buttonLogin = findViewById<Button>(R.id.btnIniPrincipal)

        editTextUser.addTextChangedListener(RutTextWatcher(editTextUser))

        buttonLogin.setOnClickListener {
            val rut = editTextUser.text.toString().trim()
            val password = editTextPassword.text.toString().trim()

            if (rut == "userdotjs" && password == "0100100101") {
                startActivity(Intent(this, easter_egg::class.java))
                return@setOnClickListener
            }

            if (rut.isNotEmpty() && password.isNotEmpty()) {
                val dbref = FirebaseDatabase.getInstance().reference

                dbref.orderByChild("rut").equalTo(rut).addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (!snapshot.exists()) {
                            Toast.makeText(this@Principal, "RUT no registrado.", Toast.LENGTH_SHORT).show()
                            return
                        }
                        
                        try {
                            val userSnapshot = snapshot.children.first()
                            val dbPasswordObject: Any? = userSnapshot.child("contraseña").value
                            var passwordMatches = false

                            if (dbPasswordObject is String) {
                                val inputPasswordEncrypted = CryptoHelper.encrypt(password)
                                if (dbPasswordObject == inputPasswordEncrypted) passwordMatches = true
                            } else if (dbPasswordObject is Number) {
                                val inputPasswordAsLong = password.toLongOrNull()
                                if (inputPasswordAsLong != null && dbPasswordObject.toString() == inputPasswordAsLong.toString()) {
                                    passwordMatches = true
                                }
                            }

                            if (passwordMatches) {
                                val loggedInUser = Usuario(
                                    key = userSnapshot.key,
                                    rut = userSnapshot.child("rut").getValue(String::class.java),
                                    nombre = userSnapshot.child("nombre").getValue(String::class.java),
                                    apellido = userSnapshot.child("apellido").getValue(String::class.java),
                                    email = userSnapshot.child("email").getValue(String::class.java),
                                    tipo = userSnapshot.child("tipo").getValue(String::class.java),
                                    contraseña = dbPasswordObject.toString()
                                )

                                val intent = when (loggedInUser.tipo?.lowercase()) {
                                    "admin" -> Intent(this@Principal, Menu_Admin::class.java)
                                    "operador" -> Intent(this@Principal, Menu_Operador::class.java)
                                    "auditor" -> Intent(this@Principal, Menu_Auditor::class.java)
                                    else -> null
                                }

                                if (intent != null) {
                                    intent.putExtra("LOGGED_IN_USER", loggedInUser)
                                    startActivity(intent)
                                } else {
                                    Toast.makeText(this@Principal, "Tipo de usuario no válido.", Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                Toast.makeText(this@Principal, "RUT o Contraseña incorrectos.", Toast.LENGTH_SHORT).show()
                            }
                        } catch (e: Exception) {
                            Log.e("PrincipalActivity", "Error inesperado durante el login", e)
                            Toast.makeText(this@Principal, "Ha ocurrido un error inesperado.", Toast.LENGTH_LONG).show()
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(this@Principal, "Error de base de datos: ${error.message}", Toast.LENGTH_SHORT).show()
                    }
                })
            } else {
                Toast.makeText(this, "Por favor, complete todos los campos.", Toast.LENGTH_SHORT).show()
            }
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {}
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isFinishing) {
            MusicManager.stop()
        }
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
