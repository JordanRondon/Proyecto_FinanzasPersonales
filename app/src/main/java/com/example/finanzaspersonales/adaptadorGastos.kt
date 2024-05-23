package com.example.finanzaspersonales

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class adaptadorGastos(
    private val context: Context,
    private val historalGastos: List<EntidadGastos>
): RecyclerView.Adapter<adaptadorGastos.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvCategoria: TextView = view.findViewById(R.id.tvCategoria)
        val tvFecha: TextView = view.findViewById(R.id.tvFecha)
        val tvValorSoles: TextView = view.findViewById(R.id.tvValorSoles)
        val ivIcono: ImageView = view.findViewById(R.id.ivIcono)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.elemento_gastos, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = historalGastos[position]

        /*if (item.valorMaquina == item.valorUsuario) {
            holder.ivLogo.setImageResource(imagenes[0])
        } else {
            holder.txtEstadoJuego.text = "PERDISTE"
        }*/

        holder.tvCategoria.text = item.nombreCategoria.toString()
        holder.tvFecha.text = item.fechaGasto.toString()
        holder.tvValorSoles.text = item.valorGasto.toString()
    }

    override fun getItemCount() = historalGastos.size
}