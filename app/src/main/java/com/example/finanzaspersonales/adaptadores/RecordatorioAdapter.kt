package com.example.finanzaspersonales.adaptadores

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.finanzaspersonales.R
import com.example.finanzaspersonales.entidades.Recordatorio
import java.text.SimpleDateFormat
import java.util.Locale

class RecordatorioAdapter(private val context: Context, private var recordatorios: List<Recordatorio>) : RecyclerView.Adapter<RecordatorioAdapter.ViewHolder>(){
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val tvFecha: TextView = view.findViewById(R.id.tvFecha)
        val tvDescripcion: TextView = view.findViewById(R.id.tvDescripcion)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_recordatorio, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int){
        val recordatorio = recordatorios[position]

        val formatoFecha = SimpleDateFormat("MMM dd", Locale.getDefault())
        val fechaItems = formatoFecha.format(recordatorio.fecha)

        holder.tvFecha.text = fechaItems
        holder.tvDescripcion.text = recordatorio.descripcion
    }

    override fun getItemCount(): Int {
        return recordatorios.size
    }

    fun actualizarLista(nuevaLista: List<Recordatorio>){
        recordatorios = nuevaLista
        notifyDataSetChanged()
    }

}