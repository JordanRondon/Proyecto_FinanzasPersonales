package com.example.finanzaspersonales.adaptadores

import android.content.Context
import android.media.Image
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
import com.example.finanzaspersonales.entidades.EntidadGasto
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class CategoriaAdapter(
    private val arrayListCategoria: ArrayList<EntidadGasto>,
    private val database: DatabaseReference,
    private val contadorReference: DatabaseReference
) :
    RecyclerView.Adapter<CategoriaAdapter.ViewHolder>() {


    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_categoria, viewGroup, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.onBind(arrayListCategoria[position])
        viewHolder.imagen.setOnClickListener {
            deleteCategoria(position, viewHolder.context)
        }
    }

    override fun getItemCount(): Int {
        return arrayListCategoria.size
    }

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        val context: Context = view.context
        val imagen: ImageView = view.findViewById(R.id.ivDelete)
        fun onBind(entidadGasto: EntidadGasto) {
            val icon : ImageView = view.findViewById(R.id.ivIcon)
            val nombre: TextView = view.findViewById(R.id.txt_nombre)
            val monto: TextView = view.findViewById(R.id.txt_monto)
            val fecha: TextView = view.findViewById(R.id.txt_fecha)


            nombre.text = entidadGasto.categoriaId
            monto.text = entidadGasto.valorGasto.toString()
            fecha.text = entidadGasto.fechaGasto
        }
    }

    private fun deleteCategoria(index: Int, context: Context) {
        database.child((index + 1).toString()).removeValue()
        decrementContador()
        Toast.makeText(context, "Gasto eliminado exitosamente", Toast.LENGTH_SHORT).show()
        arrayListCategoria.removeAt(index)
        notifyDataSetChanged()
    }

    private fun decrementContador(){
        contadorReference.get().addOnSuccessListener { data ->
            val contador = data.getValue(Int::class.java) ?: 0
            val contadorUpdate = contador - 1

            contadorReference.setValue(contadorUpdate)
        }
    }

}