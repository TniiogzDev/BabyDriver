package com.example.babydriver

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class VCrearUsuario : AppCompatActivity() {

    private lateinit var db: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_v_crear_usuario)

        db = FirebaseDatabase.getInstance()

        val spinner: Spinner = findViewById(R.id.spCargoCrearusuario)
        val cargos = arrayOf("Tipo de Usuario", "operador", "admin", "auditor")

        val adapter = ArrayAdapter(this, R.layout.custom_spinner_item, cargos)
        adapter.setDropDownViewResource(R.layout.custom_spinner_item)
        spinner.adapter = adapter

        val buttonRegistrar = findViewById<Button>(R.id.btnRegCrearusuario)
        buttonRegistrar.setOnClickListener {
            val username = findViewById<EditText>(R.id.txtRutCrearusuario).text.toString().trim()
            val nombre = findViewById<EditText>(R.id.txtNomCrearusuario).text.toString().trim()
            val apellido = findViewById<EditText>(R.id.txtApeCrearusuario).text.toString().trim()
            val email = findViewById<EditText>(R.id.txtCorreoCrearusuario).text.toString().trim()
            val passwordStr = findViewById<EditText>(R.id.txtConCrearusuario).text.toString().trim()
            val repeatPassword = findViewById<EditText>(R.id.txtRepconCrearusuario).text.toString().trim()
            val tipo = spinner.selectedItem.toString()

            if (username.isNotEmpty() && nombre.isNotEmpty() && apellido.isNotEmpty() && email.isNotEmpty() && passwordStr.isNotEmpty() && repeatPassword.isNotEmpty() && spinner.selectedItemPosition != 0) {
                if (passwordStr == repeatPassword) {
                    try {
                        val passwordLong = passwordStr.toLong()

                        val dbRef = db.reference // Apuntar a la raíz
                        dbRef.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                val userCount = snapshot.childrenCount
                                val newUserId = "user${String.format("%02d", userCount + 1)}" // Formato user04, user05...

                                val userMap = hashMapOf<String, Any>(
                                    "username" to username,
                                    "nombre" to nombre,
                                    "apellido" to apellido,
                                    "email" to email,
                                    "tipo" to tipo,
                                    "contraseña" to passwordLong
                                )

                                // Guardar en la raíz con el nuevo ID calculado
                                dbRef.child(newUserId).setValue(userMap)
                                    .addOnSuccessListener {
                                        Toast.makeText(this@VCrearUsuario, "Usuario registrado como $newUserId", Toast.LENGTH_SHORT).show()
                                        finish()
                                    }
                                    .addOnFailureListener { e ->
                                        Toast.makeText(this@VCrearUsuario, "Error al guardar datos: ${e.message}", Toast.LENGTH_SHORT).show()
                                    }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                Toast.makeText(this@VCrearUsuario, "Error al contar usuarios: ${error.message}", Toast.LENGTH_SHORT).show()
                            }
                        })

                    } catch (e: NumberFormatException) {
                        Toast.makeText(this, "La contraseña debe ser un número.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Las contraseñas no coinciden.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Por favor, complete todos los campos.", Toast.LENGTH_SHORT).show()
            }
        }

        val buttonVolver = findViewById<Button>(R.id.btnVolverCrearusuario)
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
