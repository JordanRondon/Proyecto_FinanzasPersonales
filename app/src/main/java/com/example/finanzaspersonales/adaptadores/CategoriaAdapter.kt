package com.example.finanzaspersonales.adaptadores

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.contentValuesOf
import androidx.recyclerview.widget.RecyclerView
import com.example.finanzaspersonales.R
import com.example.finanzaspersonales.entidades.Categoria
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class CategoriaAdapter(arrayListCategoria: ArrayList<Categoria>) :
    RecyclerView.Adapter<CategoriaAdapter.ViewHolder>() {

    private val listCategoria = arrayListCategoria
    private lateinit var database: DatabaseReference


    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_categoria, viewGroup, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.onBind(listCategoria[position])
        viewHolder.imagen.setOnClickListener {
            deleteCategoria(position, viewHolder.context)
        }
    }

    override fun getItemCount(): Int {
        return listCategoria.size
    }

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        val context: Context = view.context
        val imagen: ImageView = view.findViewById(R.id.ivDelete)
        fun onBind(categoria: Categoria) {
            val nombre: TextView = view.findViewById(R.id.txt_nombre)
            val monto: TextView = view.findViewById(R.id.txt_monto)

            nombre.text = categoria.nombre
            monto.text = categoria.monto.toString()
        }
    }

    private fun deleteCategoria(index: Int, context: Context) {
        database = FirebaseDatabase.getInstance().reference
        val user = FirebaseAuth.getInstance().currentUser!!.uid

        database.child("Gasto").child(user).child((index + 1).toString()).removeValue()

        Toast.makeText(context, "Gasto eliminado exitosamente", Toast.LENGTH_SHORT).show()
        listCategoria.removeAt(index)
        notifyDataSetChanged()
    }
}