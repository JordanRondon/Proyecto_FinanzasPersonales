package com.example.finanzaspersonales.adaptadores

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.finanzaspersonales.R
import com.example.finanzaspersonales.entidades.Recordatorio

class RecordatorioAdapter(private var recordatorios: List<Recordatorio>) : RecyclerView.Adapter<RecordatorioAdapter.RecordatorioViewHolder>(){
    inner class RecordatorioViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val descripcion: TextView = view.findViewById(R.id.tvDescripcion)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordatorioViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_recordatorio, parent, false)
        return RecordatorioViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecordatorioViewHolder, position: Int){
        val recordatorio = recordatorios[position]
        holder.descripcion.text = recordatorio.descripcion
    }

    override fun getItemCount(): Int = recordatorios.size

    fun actualizarLista(nuevaLista: List<Recordatorio>){
        recordatorios = nuevaLista
        notifyDataSetChanged()
    }

}