package com.example.babydriver

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.*

class VGestionUsuarios : AppCompatActivity() {

    private lateinit var db: DatabaseReference
    private lateinit var userRecyclerView: RecyclerView
    private lateinit var userList: ArrayList<Usuario>
    private var isAnimationPaused = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_v_gestion_usuarios)

        // --- Controles de Pausa y Sonido ---
        val lottieView = findViewById<LottieAnimationView>(R.id.lottieGestionusuarios)
        val btnPause = findViewById<ImageButton>(R.id.btnPauseGestionusuarios)
        val btnMute = findViewById<ImageButton>(R.id.btnMuteGestionusuarios)

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

        // --- Lógica de Gestión de Usuarios ---
        userRecyclerView = findViewById(R.id.rvGestionusuarios)
        userRecyclerView.layoutManager = LinearLayoutManager(this)
        userRecyclerView.setHasFixedSize(true)

        userList = arrayListOf<Usuario>()
        getUserData()

        val fabAgregarUsuario = findViewById<FloatingActionButton>(R.id.fabAgregarGestionusuarios)
        fabAgregarUsuario.setOnClickListener {
            val intent = Intent(this, VCrearUsuario::class.java)
            startActivity(intent)
        }

        val buttonVolver = findViewById<Button>(R.id.btnVolverGestionusuarios)
        buttonVolver.setOnClickListener {
            finish()
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {}
        })
    }

    private fun getUserData() {
        db = FirebaseDatabase.getInstance().reference

        db.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                userList.clear()
                if (snapshot.exists()) {
                    for (userSnap in snapshot.children) {
                        if (userSnap.key?.startsWith("user") == true) {
                            try {
                                val user = Usuario(
                                    key = userSnap.key,
                                    rut = userSnap.child("rut").getValue(String::class.java),
                                    nombre = userSnap.child("nombre").getValue(String::class.java),
                                    apellido = userSnap.child("apellido").getValue(String::class.java),
                                    email = userSnap.child("email").getValue(String::class.java),
                                    tipo = userSnap.child("tipo").getValue(String::class.java),
                                    contraseña = userSnap.child("contraseña").value.toString()
                                )
                                userList.add(user)
                            } catch (e: Exception) {
                                Log.e("VGestionUsuarios", "Error al procesar usuario: ${userSnap.key}", e)
                            }
                        }
                    }
                    val adapter = GestionUserAdapter(userList, ::onEditClick, ::onDeleteClick)
                    userRecyclerView.adapter = adapter
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@VGestionUsuarios, "Error al leer datos: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun onEditClick(usuario: Usuario) {
        val intent = Intent(this, VEditarUsuario::class.java)
        intent.putExtra("USER_DATA", usuario)
        startActivity(intent)
    }

    private fun onDeleteClick(usuario: Usuario) {
        if (usuario.key != null) {
            db.child(usuario.key).removeValue().addOnSuccessListener {
                Toast.makeText(this, "Usuario ${usuario.nombre} eliminado.", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener { 
                Toast.makeText(this, "Error al eliminar usuario.", Toast.LENGTH_SHORT).show()
            }
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
