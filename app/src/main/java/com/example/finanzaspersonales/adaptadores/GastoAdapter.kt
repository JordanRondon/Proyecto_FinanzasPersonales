package com.example.finanzaspersonales

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.finanzaspersonales.entidades.EntidadGasto
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener

class GastoAdapter(
    private val context: Context,
    private val historalGastos: List<EntidadGasto>,
    private val database: DatabaseReference
): RecyclerView.Adapter<GastoAdapter.ViewHolder>() {

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
        val categoriaRef = database.child(item.categoriaId)

        categoriaRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val urlIcono = dataSnapshot.child("urlicono").getValue(String::class.java)
                    val resourceId = context.resources.getIdentifier(urlIcono, "drawable", context.packageName)

                    if (resourceId != 0) {
                        holder.ivIcono.setImageResource(resourceId);
                    } else {
                        holder.ivIcono.setImageResource(R.drawable.ic_launcher_foreground);
                    }
                }
            } override fun onCancelled(databaseError: DatabaseError) {
                Log.e("Firebase", "Error al obtener los datos", databaseError.toException())
            }
        })

        holder.tvCategoria.text = item.categoriaId.toString()
        holder.tvFecha.text = item.fechaGasto.toString()
        holder.tvValorSoles.text = item.valorGasto.toString()
    }

    override fun getItemCount() = historalGastos.size
}