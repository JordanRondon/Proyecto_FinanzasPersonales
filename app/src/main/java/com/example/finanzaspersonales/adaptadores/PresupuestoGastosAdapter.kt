package com.example.finanzaspersonales.adaptadores

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.example.finanzaspersonales.R
import com.example.finanzaspersonales.entidades.CategoriaGastos

class PresupuestoGastosAdapter(
    private val dataSet: ArrayList<CategoriaGastos>,
    private val context: Context,
    private val navController: NavController,
    private val closeBottomSheetCallback: () -> Unit
) :
    RecyclerView.Adapter<PresupuestoGastosAdapter.ViewHolder>() {

    private var selectedPos = RecyclerView.NO_POSITION

    private lateinit var presupuestoClickListener: PresupuestoClickListener

    interface PresupuestoClickListener {
        fun onPresupuestoClick(position: Int)
    }

    fun setPresupuestoClickListener(listener: PresupuestoClickListener) {
        this.presupuestoClickListener = listener
    }

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

            val resourceId = if (categoria.urlImage != null) {
                context.resources.getIdentifier(
                    categoria.urlImage,
                    "drawable",
                    context.packageName
                )
            } else {
                R.drawable.ic_downloading
            }

            viewHolder.ivCategoria.setImageResource(resourceId)

            viewHolder.itemView.setOnClickListener {
                notifyItemChanged(selectedPos)
                selectedPos = viewHolder.layoutPosition
                notifyItemChanged(selectedPos)

                if (selectedPos == 0) {
                    closeBottomSheetCallback()
                    navController.navigate(R.id.action_gastos_to_presupuestos)
                }
            }

            viewHolder.cardView.setBackgroundColor(
                if (selectedPos == position && selectedPos != 0) Color.rgb(
                    180,
                    180,
                    184
                ) else Color.TRANSPARENT
            )
        }
    }


    fun getCategoriaSelected(): CategoriaGastos? {
        return if (selectedPos != RecyclerView.NO_POSITION) dataSet[selectedPos] else null
    }
}