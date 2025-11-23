package com.example.babydriver

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class UserAdapter(
    private val userList: ArrayList<Usuario>,
    private val onEditClick: (Usuario) -> Unit,
    private val onDeleteClick: (Usuario) -> Unit
) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_usuario, parent, false)
        return UserViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val currentUser = userList[position]
        holder.tvNombre.text = "${currentUser.nombre} ${currentUser.apellido}"
        holder.tvCargo.text = currentUser.tipo?.replaceFirstChar { it.uppercase() }

        holder.btnEditar.setOnClickListener { onEditClick(currentUser) }
        holder.btnEliminar.setOnClickListener { onDeleteClick(currentUser) }
    }

    override fun getItemCount() = userList.size

    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNombre: TextView = itemView.findViewById(R.id.tvNombreItemusuario)
        val tvCargo: TextView = itemView.findViewById(R.id.tvCargoItemusuario)
        val btnEditar: Button = itemView.findViewById(R.id.btnEditarItemusuario)
        val btnEliminar: Button = itemView.findViewById(R.id.btnEliminarItemusuario)
    }
}
