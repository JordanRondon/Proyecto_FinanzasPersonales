package com.example.finanzaspersonales.adaptadores
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.finanzaspersonales.Clases.Categoria
import com.example.finanzaspersonales.R

class CrudCategoriaAdapter (private val Categorias: List<Categoria>,private val listener: OnItemClickListener) : RecyclerView.Adapter<CrudCategoriaAdapter.CrudCategoriaViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CrudCategoriaViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.categoria_items, parent, false)
        return CrudCategoriaViewHolder(view)
    }
    interface OnItemClickListener {
        fun onOptionsButtonClick(nombreCategoria: String)
    }

    override fun onBindViewHolder(holder: CrudCategoriaViewHolder, position: Int) {
        val categoria = Categorias[position]
        var dictIconos: Map<String, Int> = mapOf()
        dictIconos = mapOf(
            "icono_agua" to R.drawable.icono_agua,"icono_comida" to R.drawable.icono_comida,
            "icono_antena" to R.drawable.icono_antena,"icono_barco" to R.drawable.icono_barco,
            "icono_bebe" to R.drawable.icono_bebe,"icono_bicicleta" to R.drawable.icono_bicicleta,
            "icono_boleta" to R.drawable.icono_boleta,"icono_reloj" to R.drawable.icono_reloj,
            "icono_bus" to R.drawable.icono_bus,"icono_carro2" to R.drawable.icono_carro2,
            "icono_casa" to R.drawable.icono_casa,"icono_casco_moto" to R.drawable.icono_casco_moto,
            "icono_comer_fuera" to R.drawable.icono_comer_fuera,"icono_comida_chatarra" to R.drawable.icono_comida_chatarra,
            "icono_computadora" to R.drawable.icono_computadora,"icono_corazon" to R.drawable.icono_corazon,
            "icono_diente" to R.drawable.icono_diente,"icono_ejercicio" to R.drawable.icono_ejercicio,
            "icono_entregas" to R.drawable.icono_entregas,"icono_fijo" to R.drawable.icono_fijo,
            "icono_fijo2" to R.drawable.icono_fijo2,"icono_fuego" to R.drawable.icono_fuego,
            "icono_gadget" to R.drawable.icono_gadget,"icono_gasolina" to R.drawable.icono_gasolina,
            "icono_gel" to R.drawable.icono_gel,"icono_graduacion" to R.drawable.icono_graduacion,
            "icono_grifo" to R.drawable.icono_grifo,"icono_hamburguesa" to R.drawable.icono_hamburguesa,
            "icono_libros" to R.drawable.icono_libros,"icono_limpieza" to R.drawable.icono_limpieza,
            "icono_luz" to R.drawable.icono_luz,"icono_maletin_primeros_aux" to R.drawable.icono_maletin_primeros_aux,
            "icono_mapa" to R.drawable.icono_mapa,"icono_mascota" to R.drawable.icono_mascota,
            "icono_medicina" to R.drawable.icono_medicina,"icono_mochila" to R.drawable.icono_mochila,
            "icono_perro" to R.drawable.icono_perro,"icono_play" to R.drawable.icono_play,
            "icono_raqueta" to R.drawable.icono_raqueta,"icono_red" to R.drawable.icono_red,
            "icono_salud" to R.drawable.icono_salud,"icono_timon" to R.drawable.icono_timon,
            "icono_tren" to R.drawable.icono_tren,"icono_viaje" to R.drawable.icono_viaje,
            "icono_videojuegos" to R.drawable.icono_videojuegos,"icono_vuelo" to R.drawable.icono_vuelo,
            "icono_vuelo2" to R.drawable.icono_vuelo2,"icono_wifi" to R.drawable.icono_wifi,
            "icono_xbox" to R.drawable.icono_xbox,"agua_icono" to R.drawable.agua_icono,
            "comida_icono2" to R.drawable.comida_icono,"luz_icono2" to R.drawable.luz_icono
        )
        holder.imageView.setImageResource(dictIconos[categoria.URLicono]?: R.drawable.moneda)
        holder.textViewNombre.text = categoria.nombre
        holder.textViewDescripcion.text = categoria.descripcion

        //
        //holder.botonOpciones.setOnClickListener {
        //    listener.onOptionsButtonClick(categoria.nombre)
        //}
    }

    override fun getItemCount(): Int {
        return Categorias.size
    }

    class CrudCategoriaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.img_icono_crud)
        val textViewNombre: TextView = itemView.findViewById(R.id.txt_nombre_categoria_crud)
        val textViewDescripcion: TextView = itemView.findViewById(R.id.txt_descripcion_crud)
        //val botonOpciones: ImageButton = itemView.findViewById(R.id.boton_categoria_opciones)

    }

}