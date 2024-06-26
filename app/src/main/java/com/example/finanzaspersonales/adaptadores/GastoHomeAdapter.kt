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
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.finanzaspersonales.R
import com.example.finanzaspersonales.dialogs.GastoDialogFragment
import com.example.finanzaspersonales.entidades.EntidadGasto
import com.google.android.material.card.MaterialCardView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class GastoHomeAdapter(
    private val arrayListCategoria: ArrayList<EntidadGasto>,
    private val database: DatabaseReference,

    private val contadorReference: DatabaseReference,
    private val categoriaReference: DatabaseReference

    //private val cardView: MaterialCardView
) :
    RecyclerView.Adapter<GastoHomeAdapter.ViewHolder>() {
    private lateinit var database2: DatabaseReference


    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_gastos, viewGroup, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.onBind(arrayListCategoria[position])

//        viewHolder.itemView.setOnClickListener {
//            viewHolder.bindCard(arrayListCategoria[viewHolder.layoutPosition])
//            getIconCategoria(viewHolder.imagenGasto, arrayListCategoria[viewHolder.layoutPosition].categoriaID, viewHolder.context)
//            cardView.visibility = View.VISIBLE
//        }

        viewHolder.itemView.setOnClickListener {
            val gastoDialog = GastoDialogFragment(arrayListCategoria[viewHolder.layoutPosition], categoriaReference)
            gastoDialog.show((viewHolder.context as AppCompatActivity).supportFragmentManager, "GastoDialog")
        }

        getIconCategoria(
            viewHolder.icon,
            arrayListCategoria[position].categoriaID,
            viewHolder.context
        )

    }

    override fun getItemCount(): Int {
        return arrayListCategoria.size
    }

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val context: Context = view.context
        val icon: ImageView = view.findViewById(R.id.ivIcon)
        //val imagenGasto : ImageView = cardView.findViewById(R.id.ivCategoria)

        fun onBind(entidadGasto: EntidadGasto) {
            val nombre: TextView = view.findViewById(R.id.txt_nombre)
            val monto: TextView = view.findViewById(R.id.txt_monto)
            val fecha: TextView = view.findViewById(R.id.txt_fecha)


            nombre.text = entidadGasto.categoriaID
            monto.text = entidadGasto.monto.toString()
            fecha.text = entidadGasto.fechaRegistro
        }

//        fun bindCard(entidadGasto: EntidadGasto) {
//            val categoria: TextView = cardView.findViewById(R.id.txt_categoria)
//            val presupuesto: TextView = cardView.findViewById(R.id.txt_presupuesto)
//            val montoGasto: TextView = cardView.findViewById(R.id.txt_monto_gasto)
//            val fechaRegistro: TextView = cardView.findViewById(R.id.txt_fecha_gasto)
//
//            categoria.text = entidadGasto.categoriaID
//            presupuesto.text = entidadGasto.presupuestoID
//            montoGasto.text = entidadGasto.monto.toString()
//            fechaRegistro.text = entidadGasto.fechaRegistro
//        }
    }

    interface OnDataRetrieved<T> {
        fun onSuccess(data: T)
        fun onFailure(error: String)
    }

    private fun deleteCategoria(index: Int, context: Context) {
        val idGasto = (index + 1).toString()

        obtener_id_presupuesto(idGasto, object : OnDataRetrieved<String?> {
            override fun onSuccess(id_presupuesto: String?) {
                obtener_monto_gasto(idGasto, object : OnDataRetrieved<Float?> {
                    override fun onSuccess(monto_gasto: Float?) {
                        if (id_presupuesto != null && monto_gasto != null) {
                            setGastoPresupuesto(monto_gasto, id_presupuesto)
                        }

                        database.child(idGasto).removeValue()
                        decrementContador()

                        Toast.makeText(context, "Gasto eliminado exitosamente", Toast.LENGTH_SHORT)
                            .show()
                        arrayListCategoria.removeAt(index)
                        notifyDataSetChanged()
                    }

                    override fun onFailure(error: String) {
                        Toast.makeText(
                            context,
                            "Error al obtener el monto: $error",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
            }

            override fun onFailure(error: String) {
                Toast.makeText(
                    context,
                    "Error al obtener el ID del presupuesto: $error",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }


    private fun decrementContador() {
        contadorReference.get().addOnSuccessListener { data ->
            val contador = data.getValue(Int::class.java) ?: 0
            val contadorUpdate = contador - 1

            contadorReference.setValue(contadorUpdate)
        }
    }

    private fun setGastoPresupuesto(Monto_gasto: Float, presuesto_id: String) {

        database2 = FirebaseDatabase.getInstance().reference
        val user = FirebaseAuth.getInstance().currentUser!!.uid
        val gasto_presupuesto =
            FirebaseDatabase.getInstance()
                .getReference("Presupuesto/$user/$presuesto_id/monto_actual")

        //obtiene el valor actual del monto actual
        gasto_presupuesto.get().addOnSuccessListener { data ->
            val monto_Actual = data.getValue(Float::class.java) ?: 0f
            val monto_presupuestoActualizado = monto_Actual - Monto_gasto
            //actualiza monto del dia
            gasto_presupuesto.setValue(monto_presupuestoActualizado)
                .addOnCompleteListener { tarea ->
                    if (!tarea.isSuccessful) {
                        println("Error al actualizar el valor: ${tarea.exception?.message}")
                    }
                }
        }.addOnFailureListener { exception ->
            println("Error al obtener el valor actual: ${exception.message}")
        }
    }

    private fun obtener_id_presupuesto(Id_gasto: String, callback: OnDataRetrieved<String?>) {
        database2 = FirebaseDatabase.getInstance().reference
        val user = FirebaseAuth.getInstance().currentUser!!.uid
        val presupuestoID =
            FirebaseDatabase.getInstance().getReference("Gasto/$user/$Id_gasto/presupuestoID")

        presupuestoID.get().addOnSuccessListener { data ->
            val id_presupuesto = data.getValue(String::class.java) ?: ""
            callback.onSuccess(id_presupuesto)
        }.addOnFailureListener { exception ->
            println("Error al obtener el valor actual: ${exception.message}")
            callback.onFailure(exception.message ?: "Error desconocido")
        }
    }

    private fun obtener_monto_gasto(Id_gasto: String, callback: OnDataRetrieved<Float?>) {
        database2 = FirebaseDatabase.getInstance().reference
        val user = FirebaseAuth.getInstance().currentUser!!.uid
        val presupuestoID =
            FirebaseDatabase.getInstance().getReference("Gasto/$user/$Id_gasto/monto")

        presupuestoID.get().addOnSuccessListener { data ->
            val monto_gasto = data.getValue(Float::class.java) ?: 0f
            callback.onSuccess(monto_gasto)
        }.addOnFailureListener { exception ->
            println("Error al obtener el valor actual: ${exception.message}")
            callback.onFailure(exception.message ?: "Error desconocido")
        }
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