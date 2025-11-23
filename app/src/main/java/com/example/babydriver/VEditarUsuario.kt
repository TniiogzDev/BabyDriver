package com.example.babydriver

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import com.google.firebase.database.FirebaseDatabase

class VEditarUsuario : AppCompatActivity() {

    private lateinit var db: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_v_editar_usuario)

        db = FirebaseDatabase.getInstance()

        // --- Recibir y mostrar los datos del usuario ---
        val usuario = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("USER_DATA", Usuario::class.java)
        } else {
            @Suppress("DEPRECATION") intent.getParcelableExtra<Usuario>("USER_DATA")
        }

        val txtUsername = findViewById<EditText>(R.id.txtRutEditarusuario)
        val txtNombre = findViewById<EditText>(R.id.txtNomEditarusuario)
        val txtApellido = findViewById<EditText>(R.id.txtApeEditarusuario)
        val txtEmail = findViewById<EditText>(R.id.txtCorreoEditarusuario)
        val spinnerTipo = findViewById<Spinner>(R.id.spCargoEditarusuario)
        val btnGuardar = findViewById<Button>(R.id.btnGuardarEditarusuario)
        val btnVolver = findViewById<Button>(R.id.btnVolverEditarusuario)

        // Configurar el Spinner
        val tipos = arrayOf("operador", "admin", "auditor")
        val adapter = ArrayAdapter(this, R.layout.custom_spinner_item, tipos)
        adapter.setDropDownViewResource(R.layout.custom_spinner_item)
        spinnerTipo.adapter = adapter

        // Rellenar el formulario con los datos del usuario
        if (usuario != null) {
            txtUsername.setText(usuario.username)
            txtNombre.setText(usuario.nombre)
            txtApellido.setText(usuario.apellido)
            txtEmail.setText(usuario.email)
            val tipoPosition = tipos.indexOf(usuario.tipo?.lowercase())
            if (tipoPosition >= 0) {
                spinnerTipo.setSelection(tipoPosition)
            }
        }

        // --- Lógica para guardar los cambios ---
        btnGuardar.setOnClickListener {
            if (usuario?.key == null) {
                Toast.makeText(this, "Error: No se pudo identificar al usuario.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val updatedUserMap = hashMapOf<String, Any?>(
                "username" to txtUsername.text.toString(),
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
}
