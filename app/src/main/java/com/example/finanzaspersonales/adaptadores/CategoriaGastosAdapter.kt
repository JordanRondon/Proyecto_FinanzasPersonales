package com.example.finanzaspersonales.adaptadores

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.finanzaspersonales.R
import com.example.finanzaspersonales.entidades.Categoria


class CategoriaGastosAdapter(private val dataSet: ArrayList<Categoria>) :
    RecyclerView.Adapter<CategoriaGastosAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivCategoria : ImageView = view.findViewById(R.id.ivCategoria)
        val txtNombreCategoria : TextView = view.findViewById(R.id.txtNombreCategoria)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_categoria_gastos, viewGroup, false)

        return ViewHolder(view)
    }

    override fun getItemCount() = dataSet.size


    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

    }


}