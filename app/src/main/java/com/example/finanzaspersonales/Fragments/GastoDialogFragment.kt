package com.example.finanzaspersonales.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.example.finanzaspersonales.R
import com.example.finanzaspersonales.entidades.EntidadGasto
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener

class GastoDialogFragment(
    private val entidadGasto: EntidadGasto,
    private val categoriaReference: DatabaseReference
) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_gasto, null)

        val categoria: TextView = view.findViewById(R.id.txt_categoria)
        val presupuesto: TextView = view.findViewById(R.id.txt_presupuesto)
        val montoGasto: TextView = view.findViewById(R.id.txt_monto_gasto)
        val fechaRegistro: TextView = view.findViewById(R.id.txt_fecha_gasto)
        val imagenGasto: ImageView = view.findViewById(R.id.ivCategoria)

        categoria.text = entidadGasto.categoriaID
        presupuesto.text = entidadGasto.presupuestoID
        montoGasto.text = entidadGasto.monto.toString()
        fechaRegistro.text = entidadGasto.fechaRegistro

        getIconCategoria(imagenGasto, entidadGasto.categoriaID, requireContext())

        return MaterialAlertDialogBuilder(requireContext())
            .setView(view)
            .create()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_gasto, container, false)
    }

    private fun getIconCategoria(icon: ImageView, categoriaID: String, context: Context) {
        categoriaReference.child(categoriaID).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(data: DataSnapshot) {
                if (data.exists()) {
                    val iconCategoria = data.child("urlicono").getValue(String::class.java)

                    icon.setImageResource(
                        context.resources.getIdentifier(
                            iconCategoria,
                            "drawable",
                            context.packageName
                        )
                    )
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("Firebase", "Error al obtener los datos", databaseError.toException())
            }
        })
    }
}
