package com.example.finanzaspersonales.adaptadores

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.finanzaspersonales.R

class CategoriaAdapter(arrayListCategoria: ArrayList<com.example.finanzaspersonales.entidades.Categoria>) :
    RecyclerView.Adapter<CategoriaAdapter.ViewHolder>() {

    private val listCategoria = arrayListCategoria

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_categoria, viewGroup, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemId.text = listCategoria[position].nombre.toString()
        viewHolder.itemNombre.text = listCategoria[position].monto.toString()
    }

    override fun getItemCount(): Int {
        return listCategoria.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var itemId: TextView = itemView.findViewById(R.id.txt_nombre)
        var itemNombre: TextView = itemView.findViewById(R.id.txt_monto)
    }
}