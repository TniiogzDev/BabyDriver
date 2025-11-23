package com.example.babydriver

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.*

class VGestionUsuarios : AppCompatActivity() {

    private lateinit var db: DatabaseReference
    private lateinit var userRecyclerView: RecyclerView
    private lateinit var userList: ArrayList<Usuario>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_v_gestion_usuarios)

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
            finish() // Cierra la actividad actual y vuelve a la anterior
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {}
        })
    }

    private fun getUserData() {
        db = FirebaseDatabase.getInstance().getReference()

        db.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                userList.clear()
                if (snapshot.exists()) {
                    for (userSnap in snapshot.children) {
                        val key = userSnap.key!!
                        val userData = userSnap.getValue(Usuario::class.java)
                        if (userData != null) {
                            val userWithKey = userData.copy(key = key)
                            userList.add(userWithKey)
                        }
                    }
                    val adapter = UserAdapter(userList, ::onEditClick, ::onDeleteClick)
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
        // Enviar el objeto Usuario completo a la pantalla de edición
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
}
