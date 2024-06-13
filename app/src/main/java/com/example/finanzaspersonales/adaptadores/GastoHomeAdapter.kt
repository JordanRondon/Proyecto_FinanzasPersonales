package com.example.finanzaspersonales.adaptadores

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.finanzaspersonales.R
import com.example.finanzaspersonales.entidades.EntidadGasto
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener

class GastoHomeAdapter(
    private val arrayListCategoria: ArrayList<EntidadGasto>,
    private val database: DatabaseReference,
    private val contadorReference: DatabaseReference,
    private val categoriaReference: DatabaseReference
) :
    RecyclerView.Adapter<GastoHomeAdapter.ViewHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_gastos, viewGroup, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.onBind(arrayListCategoria[position])
        viewHolder.imagen.setOnClickListener {
            deleteCategoria(position, viewHolder.context)
        }

        getIconCategoria(viewHolder.icon, arrayListCategoria[position].categoriaID,viewHolder.context)
    }

    override fun getItemCount(): Int {
        return arrayListCategoria.size
    }

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        val context: Context = view.context
        val imagen: ImageView = view.findViewById(R.id.ivDelete)
        val icon : ImageView = view.findViewById(R.id.ivIcon)

        fun onBind(entidadGasto: EntidadGasto) {
            val nombre: TextView = view.findViewById(R.id.txt_nombre)
            val monto: TextView = view.findViewById(R.id.txt_monto)
            val fecha: TextView = view.findViewById(R.id.txt_fecha)


            nombre.text = entidadGasto.categoriaID
            monto.text = entidadGasto.monto.toString()
            fecha.text = entidadGasto.fechaRegistro
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

    private fun getIconCategoria(icon : ImageView, categoriaID : String, context: Context){
        categoriaReference.child(categoriaID).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(data: DataSnapshot) {
                if(data.exists()){
                    val iconCategoria = data.child("urlicono").getValue(String::class.java)

                    icon.setImageResource(context.resources.getIdentifier(iconCategoria, "drawable", context.packageName))
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("Firebase", "Error al obtener los datos", databaseError.toException())
            }
        })
    }

}