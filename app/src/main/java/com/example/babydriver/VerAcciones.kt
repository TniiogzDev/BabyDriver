
package com.example.babydriver

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class VerAcciones : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AccionAdapter
    private val databaseReference = FirebaseDatabase.getInstance().getReference("log_acciones")
    private val accionesList = mutableListOf<Accion>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ver_acciones)

        val loggedInUser = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("LOGGED_IN_USER", Usuario::class.java)
        } else {
            @Suppress("DEPRECATION") intent.getParcelableExtra<Usuario>("LOGGED_IN_USER")
        }

        val tvNombreUsuario = findViewById<TextView>(R.id.tvNombreusuarioVeracciones)
        if (loggedInUser != null) {
            tvNombreUsuario.text = "Usuario: ${loggedInUser.username}"
        }

        recyclerView = findViewById(R.id.rvAcciones)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val buttonVolver = findViewById<Button>(R.id.btnVolverVeracciones)
        buttonVolver.setOnClickListener {
            finish()
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {}
        })

        cargarAcciones()
    }

    private fun cargarAcciones() {
        databaseReference.orderByKey().limitToLast(100).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                accionesList.clear()
                for (data in snapshot.children) {
                    val accion = data.getValue(Accion::class.java)
                    if (accion != null) {
                        accionesList.add(accion)
                    }
                }
                accionesList.reverse() // Para mostrar las más nuevas primero
                adapter = AccionAdapter(accionesList)
                recyclerView.adapter = adapter
            }

            override fun onCancelled(error: DatabaseError) {
                // Manejar error
            }
        })
    }
}
