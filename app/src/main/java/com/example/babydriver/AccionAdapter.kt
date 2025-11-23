package com.example.babydriver

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AccionAdapter(private val accionList: ArrayList<Accion>) : RecyclerView.Adapter<AccionAdapter.AccionViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccionViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_accion, parent, false)
        return AccionViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: AccionViewHolder, position: Int) {
        val currentItem = accionList[position]
        holder.tvFecha.text = ": ${currentItem.fecha}"
        holder.tvHora.text = ": ${currentItem.hora}"
        holder.tvUsuario.text = ": ${currentItem.usuario}"
        holder.tvAccion.text = ": ${currentItem.accion}"
        holder.tvDistancia.text = ": ${currentItem.distancia}"
    }

    override fun getItemCount() = accionList.size

    class AccionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvFecha: TextView = itemView.findViewById(R.id.tvValuefechaItemaccion)
        val tvHora: TextView = itemView.findViewById(R.id.tvValuehoraItemaccion)
        val tvUsuario: TextView = itemView.findViewById(R.id.tvValueusuarioItemaccion)
        val tvAccion: TextView = itemView.findViewById(R.id.tvValueaccionItemaccion)
        val tvDistancia: TextView = itemView.findViewById(R.id.tvValuedistanciaItemaccion)
    }
}
