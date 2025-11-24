package com.example.babydriver

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class VerUsuarios : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: UsuarioAdapter
    private val databaseReference = FirebaseDatabase.getInstance().reference
    private val userList = mutableListOf<Usuario>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ver_usuarios)

        recyclerView = findViewById(R.id.rvUsuarios)
        recyclerView.layoutManager = LinearLayoutManager(this)

        findViewById<Button>(R.id.btnVolverVerUsuarios).setOnClickListener {
            finish()
        }

        cargarUsuarios()
    }

    private fun cargarUsuarios() {
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                userList.clear()
                for (data in snapshot.children) {
                    if (data.key?.startsWith("user") == true) {
                        try {
                            // --- LÓGICA A PRUEBA DE CRASHES ---
                            val usuario = Usuario(
                                key = data.key,
                                rut = data.child("rut").getValue(String::class.java),
                                nombre = data.child("nombre").getValue(String::class.java),
                                apellido = data.child("apellido").getValue(String::class.java),
                                email = data.child("email").getValue(String::class.java),
                                tipo = data.child("tipo").getValue(String::class.java),
                                // Leer la contraseña como String, si es número no fallará, solo será null
                                contraseña = data.child("contraseña").value.toString()
                            )
                            userList.add(usuario)
                        } catch (e: Exception) {
                            Log.e("VerUsuarios", "Error procesando usuario ${data.key}", e)
                        }
                    }
                }
                adapter = UsuarioAdapter(this@VerUsuarios, userList)
                recyclerView.adapter = adapter
            }

            override fun onCancelled(error: DatabaseError) {
                // Manejar error si es necesario
            }
        })
    }
}
