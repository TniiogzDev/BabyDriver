package com.example.babydriver

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class UsuarioAdapter(private val context: Context, private val usuarios: List<Usuario>) : RecyclerView.Adapter<UsuarioAdapter.UsuarioViewHolder>() {

    private val masterPassword = "SsjJ56gi$;"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsuarioViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_usuario, parent, false)
        return UsuarioViewHolder(view)
    }

    override fun onBindViewHolder(holder: UsuarioViewHolder, position: Int) {
        val usuario = usuarios[position]
        holder.bind(usuario)
    }

    override fun getItemCount(): Int = usuarios.size

    inner class UsuarioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvRut: TextView = itemView.findViewById(R.id.tvUsuarioRut)
        private val tvNombre: TextView = itemView.findViewById(R.id.tvUsuarioNombre)
        private val tvPassword: TextView = itemView.findViewById(R.id.tvUsuarioPassword)
        private val btnVer: Button = itemView.findViewById(R.id.btnVerPassword)

        fun bind(usuario: Usuario) {
            tvRut.text = "RUT: ${usuario.rut ?: "N/A"}"
            tvNombre.text = "Nombre: ${usuario.nombre ?: ""} ${usuario.apellido ?: ""}"
            tvPassword.text = "Contraseña: ••••••••"

            btnVer.setOnClickListener {
                showMasterPasswordDialog(usuario)
            }
        }

        private fun showMasterPasswordDialog(usuario: Usuario) {
            val builder = AlertDialog.Builder(context)
            val inflater = LayoutInflater.from(context)
            val dialogView = inflater.inflate(R.layout.dialog_master_password, null)
            val passwordInput = dialogView.findViewById<EditText>(R.id.etMasterPassword)

            builder.setView(dialogView)
                .setTitle("Autenticación Requerida")
                .setPositiveButton("Confirmar") { dialog, _ ->
                    if (passwordInput.text.toString() == masterPassword) {
                        // --- LÓGICA MEJORADA ---
                        val storedPassword = usuario.contraseña ?: ""
                        
                        // 1. Intentar descifrar la contraseña
                        val decryptedPassword = CryptoHelper.decrypt(storedPassword)
                        
                        // 2. Si el descifrado falla (devuelve null), significa que es una contraseña antigua (numérica).
                        //    En ese caso, usamos el valor original. Si tiene éxito, usamos el valor descifrado.
                        val finalPasswordToShow = decryptedPassword ?: storedPassword

                        tvPassword.text = "Contraseña: $finalPasswordToShow"

                    } else {
                        Toast.makeText(context, "Contraseña maestra incorrecta", Toast.LENGTH_SHORT).show()
                    }
                    dialog.dismiss()
                }
                .setNegativeButton("Cancelar") { dialog, _ -> dialog.cancel() }
                .create()
                .show()
        }
    }
}
