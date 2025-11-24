package com.example.babydriver

import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ManipularCircuito : AppCompatActivity() {

    private val databaseReference = FirebaseDatabase.getInstance().getReference("sensor")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manipular_circuito)

        val switchEstado = findViewById<SwitchMaterial>(R.id.swEstadoManipularcircuito)
        val tvEstado = findViewById<TextView>(R.id.tvEstadoManipularcircuito)
        val llDatos = findViewById<LinearLayout>(R.id.llDatosManipularcircuito)
        val tvDistancia = findViewById<TextView>(R.id.tvDistanciaManipularcircuito)
        val progressBar = findViewById<ProgressBar>(R.id.pbDistanciaManipularcircuito)
        val tvAlerta = findViewById<TextView>(R.id.tvAlertaManipularcircuito)
        val buttonVolver = findViewById<android.widget.Button>(R.id.btnVolverManipularcircuito)

        val sensorEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val distancia = snapshot.child("distancia").getValue(Float::class.java) ?: 0.0f
                actualizarInterfazUsuario(distancia, tvDistancia, progressBar, tvAlerta)
            }
            override fun onCancelled(error: DatabaseError) {}
        }

        switchEstado.setOnCheckedChangeListener { _, isChecked ->
            databaseReference.child("estado").setValue(isChecked)
            actualizarEstadoVisual(isChecked, tvEstado, llDatos, sensorEventListener)
        }

        databaseReference.child("estado").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val estadoActual = snapshot.getValue(Boolean::class.java) ?: false
                switchEstado.isChecked = estadoActual
                actualizarEstadoVisual(estadoActual, tvEstado, llDatos, sensorEventListener)
            }
            override fun onCancelled(error: DatabaseError) {}
        })

        buttonVolver.setOnClickListener { finish() }
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {}
        })
    }

    private fun actualizarEstadoVisual(isActivo: Boolean, tvEstado: TextView, llDatos: LinearLayout, listener: ValueEventListener) {
        if (isActivo) {
            tvEstado.text = "SISTEMA ACTIVO"
            tvEstado.setTextColor(Color.GREEN)
            llDatos.visibility = View.VISIBLE
            databaseReference.addValueEventListener(listener)
        } else {
            tvEstado.text = "SISTEMA APAGADO"
            tvEstado.setTextColor(Color.RED)
            llDatos.visibility = View.GONE
            databaseReference.removeEventListener(listener)
        }
    }

    private fun actualizarInterfazUsuario(distancia: Float, tv: TextView, pb: ProgressBar, alerta: TextView) {
        val distInt = distancia.toInt()
        tv.text = "Distancia Real: $distInt cm"
        pb.progress = (100 - distInt).coerceIn(0, 100)

        // CORREGIDO: Lógica de colores de ProgressBar actualizada
        val color = when {
            distInt > 50 -> {
                alerta.text = "Zona Segura"
                alerta.setTextColor(Color.GREEN)
                Color.GREEN
            }
            distInt > 25 -> {
                alerta.text = "Precaución"
                alerta.setTextColor(Color.YELLOW)
                Color.YELLOW
            }
            else -> {
                alerta.text = "¡PELIGRO!"
                alerta.setTextColor(Color.RED)
                Color.RED
            }
        }

        // Aplicar el color usando el método correcto según la versión de Android
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            pb.progressDrawable.colorFilter = BlendModeColorFilter(color, BlendMode.SRC_IN)
        } else {
            @Suppress("DEPRECATION")
            pb.progressDrawable.setColorFilter(color, PorterDuff.Mode.SRC_IN)
        }
    }
}