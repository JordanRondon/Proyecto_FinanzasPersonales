package com.example.finanzaspersonales

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.example.finanzaspersonales.Clases.Categoria
import com.example.finanzaspersonales.Clases.Presupuesto_Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class PresupuestoAdapter(private val presupuestos: List<Presupuesto_Firebase>,private val navController: NavController
    ) : RecyclerView.Adapter<PresupuestoAdapter.PresupuestoViewHolder>() {
    private lateinit var database: DatabaseReference

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PresupuestoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.presupuesto_items_mispresupuestos, parent, false)
        return PresupuestoViewHolder(view)
    }

    override fun onBindViewHolder(holder: PresupuestoViewHolder, position: Int) {
        val presupuesto = presupuestos[position]
        val username = FirebaseAuth.getInstance().currentUser!!.uid
        val categoriaid = presupuesto.categoriaID

        loadCategoria(username, categoriaid) { categoria ->
            if (categoria != null) {
                holder.imageView.setImageResource(getIconResource(categoria.URLicono))
                holder.textViewDetalles.text = "Detalles"
            } else {
                holder.imageView.setImageResource(R.drawable.moneda)
                holder.textViewDetalles.text = "Detalles"
            }
        }
        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val bundle = Bundle().apply {
                putString("Presupuesto_id", presupuesto.nombre)
            }
            navController.navigate(R.id.action_presupuestos_to_detalle_presupuesto, bundle)


        }

        holder.textViewNombre.text = presupuesto.nombre
        holder.textfecha.text = presupuesto.fechaInicio
        val porcentaje_barra=((presupuesto.monto_actual/presupuesto.monto_total)*100).toInt()

        if(porcentaje_barra<=50 ){
            holder.imgesfera.setImageDrawable(
                ContextCompat.getDrawable(holder.itemView.context, R.drawable.esfera_indicador_presu2)
            )
        }
        else if(porcentaje_barra>50 && porcentaje_barra<=75){
            holder.imgesfera.setImageDrawable(
                ContextCompat.getDrawable(holder.itemView.context, R.drawable.esfera_indicador_presu3)
            )
        }
        else{
            holder.imgesfera.setImageDrawable(
                ContextCompat.getDrawable(holder.itemView.context, R.drawable.esfera_indicador_presu4)
            )
        }
        if(!presupuesto.estado){

            holder.imgesfera.setImageDrawable(
                ContextCompat.getDrawable(holder.itemView.context, R.drawable.esfera_indicador_presu)
            )

            holder.textViewNombre.alpha = 0.3f
            holder.textfecha.alpha = 0.3f
            holder.imageView.alpha=0.3f
            holder.textViewDetalles.alpha=0.3f

        }
        else{
            holder.textViewNombre.alpha = 1.0f
            holder.textfecha.alpha = 1.0f
            holder.imageView.alpha=1.0f
            holder.textViewDetalles.alpha=1.0f
        }
    }

    override fun getItemCount(): Int {
        return presupuestos.size
    }

    class PresupuestoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView4)
        val textViewNombre: TextView = itemView.findViewById(R.id.textView2)
        val textfecha: TextView = itemView.findViewById(R.id.txt_fecha)
        val textViewDetalles: TextView = itemView.findViewById(R.id.textView6)
        val imgesfera: ImageView = itemView.findViewById(R.id.esfera_presu)
    }

    private fun getIconResource(iconName: String): Int {
        val dictIconos: Map<String, Int> = mapOf(
            "icono_agua" to R.drawable.icono_agua, "icono_comida" to R.drawable.icono_comida,
            "icono_antena" to R.drawable.icono_antena, "icono_barco" to R.drawable.icono_barco,
            "icono_bebe" to R.drawable.icono_bebe, "icono_bicicleta" to R.drawable.icono_bicicleta,
            "icono_boleta" to R.drawable.icono_boleta, "icono_reloj" to R.drawable.icono_reloj,
            "icono_bus" to R.drawable.icono_bus, "icono_carro2" to R.drawable.icono_carro2,
            "icono_casa" to R.drawable.icono_casa, "icono_casco_moto" to R.drawable.icono_casco_moto,
            "icono_comer_fuera" to R.drawable.icono_comer_fuera, "icono_comida_chatarra" to R.drawable.icono_comida_chatarra,
            "icono_computadora" to R.drawable.icono_computadora, "icono_corazon" to R.drawable.icono_corazon,
            "icono_diente" to R.drawable.icono_diente, "icono_ejercicio" to R.drawable.icono_ejercicio,
            "icono_entregas" to R.drawable.icono_entregas, "icono_fijo" to R.drawable.icono_fijo,
            "icono_fijo2" to R.drawable.icono_fijo2, "icono_fuego" to R.drawable.icono_fuego,
            "icono_gadget" to R.drawable.icono_gadget, "icono_gasolina" to R.drawable.icono_gasolina,
            "icono_gel" to R.drawable.icono_gel, "icono_graduacion" to R.drawable.icono_graduacion,
            "icono_grifo" to R.drawable.icono_grifo, "icono_hamburguesa" to R.drawable.icono_hamburguesa,
            "icono_libros" to R.drawable.icono_libros, "icono_limpieza" to R.drawable.icono_limpieza,
            "icono_luz" to R.drawable.icono_luz, "icono_maletin_primeros_aux" to R.drawable.icono_maletin_primeros_aux,
            "icono_mapa" to R.drawable.icono_mapa, "icono_mascota" to R.drawable.icono_mascota,
            "icono_medicina" to R.drawable.icono_medicina, "icono_mochila" to R.drawable.icono_mochila,
            "icono_perro" to R.drawable.icono_perro, "icono_play" to R.drawable.icono_play,
            "icono_raqueta" to R.drawable.icono_raqueta, "icono_red" to R.drawable.icono_red,
            "icono_salud" to R.drawable.icono_salud, "icono_timon" to R.drawable.icono_timon,
            "icono_tren" to R.drawable.icono_tren, "icono_viaje" to R.drawable.icono_viaje,
            "icono_videojuegos" to R.drawable.icono_videojuegos, "icono_vuelo" to R.drawable.icono_vuelo,
            "icono_vuelo2" to R.drawable.icono_vuelo2, "icono_wifi" to R.drawable.icono_wifi,
            "icono_xbox" to R.drawable.icono_xbox, "agua_icono" to R.drawable.agua_icono,
            "comida_icono" to R.drawable.comida_icono, "luz_icono" to R.drawable.luz_icono
        )
        return dictIconos[iconName] ?: R.drawable.moneda
    }

    private fun loadCategoria(username: String, categoriaID: String, callback: (Categoria?) -> Unit) {
        database = FirebaseDatabase.getInstance().getReference("Categoria/$username/$categoriaID")

        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val categoria = snapshot.getValue(Categoria::class.java)
                callback(categoria)
            }

            override fun onCancelled(error: DatabaseError) {
                callback(null)
            }
        })
    }
}
