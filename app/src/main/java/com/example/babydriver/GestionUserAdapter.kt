package com.example.babydriver

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class GestionUserAdapter(
    private val userList: ArrayList<Usuario>,
    private val onEditClick: (Usuario) -> Unit,
    private val onDeleteClick: (Usuario) -> Unit
) : RecyclerView.Adapter<GestionUserAdapter.UserViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_usuario_gestion, parent, false)
        return UserViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val currentUser = userList[position]
        holder.bind(currentUser)
    }

    override fun getItemCount() = userList.size

    inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // CORREGIDO: Ahora son ImageButton
        private val tvNombre: TextView = itemView.findViewById(R.id.tvNombreItemusuario)
        private val tvCargo: TextView = itemView.findViewById(R.id.tvCargoItemusuario)
        private val btnEditar: ImageButton = itemView.findViewById(R.id.btnEditarItemusuario)
        private val btnEliminar: ImageButton = itemView.findViewById(R.id.btnEliminarItemusuario)

        fun bind(usuario: Usuario) {
            tvNombre.text = "${usuario.nombre} ${usuario.apellido}"
            tvCargo.text = "RUT: ${usuario.rut} | Cargo: ${usuario.tipo}"

            btnEditar.setOnClickListener { onEditClick(usuario) }
            btnEliminar.setOnClickListener { onDeleteClick(usuario) }
        }
    }
}
