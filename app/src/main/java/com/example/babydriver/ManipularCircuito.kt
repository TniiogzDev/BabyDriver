package com.example.babydriver

import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.SeekBar
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import com.google.android.material.switchmaterial.SwitchMaterial

class ManipularCircuito : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manipular_circuito)

        val loggedInUser = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("LOGGED_IN_USER", Usuario::class.java)
        } else {
            @Suppress("DEPRECATION") intent.getParcelableExtra<Usuario>("LOGGED_IN_USER")
        }

        val tvNombreUsuario = findViewById<TextView>(R.id.tvNombreusuarioManipularcircuito)
        if (loggedInUser != null) {
            tvNombreUsuario.text = "Usuario: ${loggedInUser.username}"
        }

        val switchEstado = findViewById<SwitchMaterial>(R.id.swEstadoManipularcircuito)
        val tvEstado = findViewById<TextView>(R.id.tvEstadoManipularcircuito)
        val llDatos = findViewById<LinearLayout>(R.id.llDatosManipularcircuito)
        val seekBar = findViewById<SeekBar>(R.id.sbDistanciaManipularcircuito)
        val tvDistancia = findViewById<TextView>(R.id.tvDistanciaManipularcircuito)
        val progressBar = findViewById<ProgressBar>(R.id.pbDistanciaManipularcircuito)
        val tvAlerta = findViewById<TextView>(R.id.tvAlertaManipularcircuito)
        val buttonVolver = findViewById<Button>(R.id.btnVolverManipularcircuito)

        // Lógica del Switch
        switchEstado.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                tvEstado.text = "ENCENDIDO"
                tvEstado.setTextColor(Color.GREEN)
                llDatos.visibility = View.VISIBLE
            } else {
                tvEstado.text = "APAGADO"
                tvEstado.setTextColor(Color.RED)
                llDatos.visibility = View.GONE
            }
        }

        // Lógica del SeekBar para simular la distancia
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                // Actualizar el texto de la distancia
                tvDistancia.text = "Distancia: $progress cm"

                // Actualizar la barra de progreso (invertida)
                progressBar.progress = 100 - progress

                // Actualizar color y texto de alerta
                when (progress) {
                    in 50..100 -> {
                        progressBar.progressDrawable.setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_IN)
                        tvAlerta.text = "Lejos"
                        tvAlerta.setTextColor(Color.GREEN)
                    }
                    in 25..49 -> {
                        progressBar.progressDrawable.setColorFilter(Color.YELLOW, PorterDuff.Mode.SRC_IN)
                        tvAlerta.text = "Obstaculo detectado"
                        tvAlerta.setTextColor(Color.YELLOW)
                    }
                    else -> {
                        progressBar.progressDrawable.setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN)
                        tvAlerta.text = "Detente"
                        tvAlerta.setTextColor(Color.RED)
                    }
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

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
