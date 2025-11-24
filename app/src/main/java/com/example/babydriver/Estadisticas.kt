package com.example.babydriver

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Estadisticas : AppCompatActivity() {

    private val dbSensor = FirebaseDatabase.getInstance().getReference("sensor/distancia")
    private val dbAcciones = FirebaseDatabase.getInstance().getReference("log_acciones")

    private lateinit var tvDistanciaActual: TextView
    private lateinit var tvTotalAlertas: TextView
    private lateinit var tvPeligroCount: TextView
    private lateinit var tvPrecaucionCount: TextView
    private lateinit var tvSeguraCount: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_estadisticas)

        val loggedInUser = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("LOGGED_IN_USER", Usuario::class.java)
        } else {
            @Suppress("DEPRECATION") intent.getParcelableExtra<Usuario>("LOGGED_IN_USER")
        }

        val tvNombreUsuario = findViewById<TextView>(R.id.tvNombreusuarioEstadisticas)
        if (loggedInUser != null) {
            // CORREGIDO: Se usa .rut en lugar del .username que fue eliminado de la clase Usuario
            tvNombreUsuario.text = "Usuario: ${loggedInUser.rut}"
        }

        tvDistanciaActual = findViewById(R.id.tvDistanciaActualEstadisticas)
        tvTotalAlertas = findViewById(R.id.tvTotalAlertas)
        tvPeligroCount = findViewById(R.id.tvPeligroCount)
        tvPrecaucionCount = findViewById(R.id.tvPrecaucionCount)
        tvSeguraCount = findViewById(R.id.tvSeguraCount)

        val buttonVolver = findViewById<Button>(R.id.btnVolverEstadisticas)
        buttonVolver.setOnClickListener {
            finish()
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {}
        })

        cargarEstadisticas()
    }

    private fun cargarEstadisticas() {
        // 1. Cargar distancia actual
        dbSensor.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val distancia = snapshot.getValue(Float::class.java) ?: 0.0f
                tvDistanciaActual.text = "Distancia Actual: ${distancia.toInt()} cm"
            }
            override fun onCancelled(error: DatabaseError) {}
        })

        // 2. Cargar y procesar el log de acciones
        dbAcciones.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val total = snapshot.childrenCount
                var peligro = 0
                var precaucion = 0
                var segura = 0

                for (data in snapshot.children) {
                    val accion = data.child("accion").getValue(String::class.java) ?: ""
                    when (accion) {
                        "PELIGRO" -> peligro++
                        "Precaucion" -> precaucion++
                        "Zona Segura" -> segura++
                    }
                }

                tvTotalAlertas.text = "Total de Alertas: $total"
                tvPeligroCount.text = "- Peligro: $peligro"
                tvPrecaucionCount.text = "- Precaución: $precaucion"
                tvSeguraCount.text = "- Zona Segura: $segura"
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }
}
