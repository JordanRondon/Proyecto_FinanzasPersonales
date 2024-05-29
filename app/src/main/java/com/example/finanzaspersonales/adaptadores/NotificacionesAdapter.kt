package com.example.finanzaspersonales.adaptadores

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.finanzaspersonales.R
import com.example.finanzaspersonales.entidades.Notificacion


class NotificacionesAdapter(private val arrayListNotificacion: List<Notificacion>):
    RecyclerView.Adapter<NotificacionesAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(layoutInflater.inflate(R.layout.item_notificaciones,parent,false))
    }

    override fun onBindViewHolder(holder: NotificacionesAdapter.ViewHolder, position: Int) {
        val item = arrayListNotificacion[position]
        holder.render(item)
    }

    override fun getItemCount(): Int = arrayListNotificacion.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ventana  = view.findViewById<TextView>(R.id.tvVentana)
        val texto  = view.findViewById<TextView>(R.id.tvDescripcion)
        val fecha = view.findViewById<TextView>(R.id.tvFecha)

        fun render(notificacionModel: Notificacion){
            ventana.text = notificacionModel.ventana
            texto.text = notificacionModel.texto
            fecha.text = notificacionModel.fecha
        }
    }

}