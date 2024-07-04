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

class RecordatorioAdapter(
    private val context: Context,
    private var recordatorios: List<Pair<String, Recordatorio>>,
    private val onRecordatorioClick: (Recordatorio, String) -> Unit,
    private val onRecordatorioLongClick: (String) -> Unit
) : RecyclerView.Adapter<RecordatorioAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvFecha: TextView = view.findViewById(R.id.tvFecha)
        val tvDescripcion: TextView = view.findViewById(R.id.tvDescripcion)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_recordatorio, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val (recordatorioId, recordatorio) = recordatorios[position]

        val formatoFecha = SimpleDateFormat("MMM dd", Locale.getDefault())
        val fechaItems = formatoFecha.format(recordatorio.fecha)

        holder.tvFecha.text = fechaItems
        holder.tvDescripcion.text = recordatorio.descripcion

        holder.itemView.setOnClickListener {
            onRecordatorioClick(recordatorio, recordatorioId)
        }
        holder.itemView.setOnLongClickListener {
            onRecordatorioLongClick(recordatorioId)
            true
        }
    }

    override fun getItemCount(): Int {
        return recordatorios.size
    }

    fun actualizarLista(nuevaLista: List<Pair<String, Recordatorio>>) {
        recordatorios = nuevaLista
        notifyDataSetChanged()
    }

    fun limpiarLista() {
        this.recordatorios = emptyList()
        notifyDataSetChanged()
    }
}