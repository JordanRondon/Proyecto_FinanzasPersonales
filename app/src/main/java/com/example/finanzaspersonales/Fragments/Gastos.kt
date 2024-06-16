package com.example.finanzaspersonales.Fragments

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.finanzaspersonales.R
import com.example.finanzaspersonales.adaptadores.GastoHomeAdapter
import com.example.finanzaspersonales.entidades.EntidadGasto
import com.google.android.material.card.MaterialCardView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class Gastos : Fragment() {

    private lateinit var recycle_conteiner: RecyclerView
    private lateinit var categoria_adapter: GastoHomeAdapter
    private val arrayListCategoria: ArrayList<EntidadGasto> = ArrayList()
    private lateinit var floating_action_button: FloatingActionButton


    private lateinit var txtGastos: TextView
    private lateinit var ivImagen: ImageView
    private lateinit var txtMensaje1: TextView
    private lateinit var txtMensaje2: TextView

    //private lateinit var card: MaterialCardView


    private val username = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    private val database = FirebaseDatabase.getInstance().getReference("Gasto/$username")
    private val contadorReference = FirebaseDatabase.getInstance().getReference("Gasto/$username/contador/ultimo_gasto")
    private val categoriaReference = FirebaseDatabase.getInstance().getReference("Categoria/$username")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_gastos, container, false)

        recycle_conteiner = view.findViewById(R.id.recycle_conteiner)
        floating_action_button = view.findViewById(R.id.floating_action_button)
        txtGastos = view.findViewById(R.id.txtGastos)
        ivImagen = view.findViewById(R.id.ivImagen)
        txtMensaje1 = view.findViewById(R.id.txtMensaje1)
        txtMensaje2 = view.findViewById(R.id.txtMensaje2)
        //card = view.findViewById(R.id.card)


        recycle_conteiner.layoutManager = LinearLayoutManager(context)
        categoria_adapter =
            GastoHomeAdapter(
                arrayListCategoria,
                database,
                contadorReference,
                categoriaReference
                //card
            )
        recycle_conteiner.adapter = categoria_adapter


        floating_action_button.setOnClickListener {
            SheetGastos().show(requireActivity().supportFragmentManager, "newTaskGastos")
        }

        getGasto()


        return view
    }

    private fun getGasto() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                arrayListCategoria.clear()
                if (dataSnapshot.exists() && dataSnapshot.hasChildren()) {
                    txtGastos.visibility = View.VISIBLE
                    for (ds: DataSnapshot in dataSnapshot.children) {
                        if (ds.key != "contador") {
                            val categoriaID = ds.child("categoriaID").value.toString()
                            val presupuestoID = ds.child("presupuestoID").value.toString()
                            val monto = ds.child("monto").getValue(Float::class.java) ?: 0.0f
                            val fechaRegistro = ds.child("fechaRegistro").value.toString()

                            arrayListCategoria.add(
                                EntidadGasto(
                                    categoriaID,
                                    presupuestoID,
                                    monto,
                                    fechaRegistro
                                )
                            )

                        }
                    }
                    categoria_adapter.notifyDataSetChanged()
                    showImages(arrayListCategoria)
                }else{
                    txtGastos.visibility = View.INVISIBLE
                    ivImagen.visibility = View.VISIBLE
                    txtMensaje1.visibility = View.VISIBLE
                    txtMensaje2.visibility = View.VISIBLE
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle possible errors.
            }
        })

    }

    private fun showImages(arrayListCategoria: ArrayList<EntidadGasto>) {
        if (arrayListCategoria.isNotEmpty()) {
            txtGastos.visibility = View.VISIBLE
            ivImagen.visibility = View.INVISIBLE
            txtMensaje1.visibility = View.INVISIBLE
            txtMensaje2.visibility = View.INVISIBLE
        } else {
            txtGastos.visibility = View.INVISIBLE
            ivImagen.visibility = View.VISIBLE
            txtMensaje1.visibility = View.VISIBLE
            txtMensaje2.visibility = View.VISIBLE
        }
    }

}