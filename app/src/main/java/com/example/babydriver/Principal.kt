package com.example.babydriver

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Principal : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_principal)

        MusicManager.start(this)

        val editTextUser = findViewById<EditText>(R.id.txtUsuPrincipal)
        val editTextPassword = findViewById<EditText>(R.id.txtConPrincipal)
        val buttonLogin = findViewById<Button>(R.id.btnIniPrincipal)

        buttonLogin.setOnClickListener {
            val username = editTextUser.text.toString().trim()
            val password = editTextPassword.text.toString().trim()

            // Easter Egg check
            if (username == "userdotjs" && password == "0100100101") {
                val intent = Intent(this, easter_egg::class.java)
                startActivity(intent)
                return@setOnClickListener
            }

            if (username.isNotEmpty() && password.isNotEmpty()) {
                val dbref = FirebaseDatabase.getInstance().reference

                dbref.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        var userFound = false
                        for (userSnapshot in snapshot.children) {
                            val dbUsername = userSnapshot.child("username").getValue(String::class.java)
                            val dbPassword = userSnapshot.child("contraseña").getValue(Long::class.java)?.toString()

                            if (username.equals(dbUsername, ignoreCase = true) && password == dbPassword) {
                                userFound = true
                                
                                val loggedInUser = userSnapshot.getValue(Usuario::class.java)?.copy(key = userSnapshot.key)

                                if (loggedInUser != null) {
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
                                    Toast.makeText(this@Principal, "Error al leer los datos del usuario.", Toast.LENGTH_SHORT).show()
                                }
                                return
                            }
                        }

                        if (!userFound) {
                            Toast.makeText(this@Principal, "Error de Usuario y/o Contraseña", Toast.LENGTH_SHORT).show()
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
}
