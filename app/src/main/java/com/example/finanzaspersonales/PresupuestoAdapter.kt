package com.example.finanzaspersonales


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.finanzaspersonales.Clases.Presupuesto

class PresupuestoAdapter(private val presupuestos: List<Presupuesto>) : RecyclerView.Adapter<PresupuestoAdapter.PresupuestoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PresupuestoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.presupuesto_items_mispresupuestos, parent, false)
        return PresupuestoViewHolder(view)
    }

    override fun onBindViewHolder(holder: PresupuestoViewHolder, position: Int) {
        val presupuesto = presupuestos[position]
        holder.imageView.setImageResource(presupuesto.icono)
        holder.textViewNombre.text = presupuesto.nombre
        holder.textViewDetalles.text = presupuesto.detalles
    }

    override fun getItemCount(): Int {
        return presupuestos.size
    }

    class PresupuestoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView4)
        val textViewNombre: TextView = itemView.findViewById(R.id.textView2)
        val textViewDetalles: TextView = itemView.findViewById(R.id.textView6)
    }
}