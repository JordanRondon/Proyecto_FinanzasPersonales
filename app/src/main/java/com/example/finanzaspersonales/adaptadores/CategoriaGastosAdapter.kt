package com.example.finanzaspersonales.adaptadores

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.finanzaspersonales.R
import com.example.finanzaspersonales.entidades.CategoriaGastos


class CategoriaGastosAdapter(
    private val dataSet: ArrayList<CategoriaGastos>,
    private val context: Context
) :
    RecyclerView.Adapter<CategoriaGastosAdapter.ViewHolder>() {

    private var selectedPos = RecyclerView.NO_POSITION

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cardView: CardView = view.findViewById(R.id.cardView)
        val ivCategoria: ImageView = view.findViewById(R.id.ivCategoria)
        val txtNombreCategoria: TextView = view.findViewById(R.id.txtNombreCategoria)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_categoria_gastos, viewGroup, false)

        return ViewHolder(view)
    }

    override fun getItemCount() = dataSet.size


    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val categoria = dataSet.getOrNull(position)
        if (categoria != null) {
            viewHolder.txtNombreCategoria.text = categoria.nombre
            viewHolder.ivCategoria.setImageResource(
                context.resources.getIdentifier(
                    categoria.urlImage,
                    "drawable",
                    context.packageName
                )
            )
            viewHolder.itemView.setOnClickListener {
                notifyItemChanged(selectedPos)
                selectedPos = viewHolder.layoutPosition
                notifyItemChanged(selectedPos)
            }

            viewHolder.cardView.setBackgroundColor(
                if (selectedPos == position) Color.rgb(180, 180, 184) else Color.TRANSPARENT
            )
        }
    }


    fun getCategoriaSelected(): CategoriaGastos? {
        return if (selectedPos != RecyclerView.NO_POSITION) dataSet[selectedPos] else null
    }


}