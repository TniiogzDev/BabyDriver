
package com.example.babydriver

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AccionAdapter(private val acciones: List<Accion>) : RecyclerView.Adapter<AccionAdapter.AccionViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_accion, parent, false)
        return AccionViewHolder(view)
    }

    override fun onBindViewHolder(holder: AccionViewHolder, position: Int) {
        val accion = acciones[position]
        holder.tvAccion.text = accion.accion
        holder.tvFecha.text = accion.fecha
    }

    override fun getItemCount(): Int = acciones.size

    class AccionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvAccion: TextView = itemView.findViewById(R.id.tvAccionItem)
        val tvFecha: TextView = itemView.findViewById(R.id.tvFechaItem)
    }
}
