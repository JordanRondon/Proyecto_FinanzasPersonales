package com.example.finanzaspersonales.adaptadores
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.finanzaspersonales.Clases.Categoria
import com.example.finanzaspersonales.R

class CrudCategoriaAdapter (private val Categorias: List<Categoria>) : RecyclerView.Adapter<CrudCategoriaAdapter.CrudCategoriaViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CrudCategoriaViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.categoria_items, parent, false)
        return CrudCategoriaViewHolder(view)
    }

    override fun onBindViewHolder(holder: CrudCategoriaViewHolder, position: Int) {
        val categoria = Categorias[position]
        holder.imageView.setImageResource(categoria.icono)
        holder.textViewNombre.text = categoria.nombre
    }

    override fun getItemCount(): Int {
        return Categorias.size
    }

    class CrudCategoriaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.img_icono_crud)
        val textViewNombre: TextView = itemView.findViewById(R.id.txt_nombre_categoria_crud)

    }

}