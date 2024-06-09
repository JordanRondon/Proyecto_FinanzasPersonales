package com.example.finanzaspersonales.Fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.finanzaspersonales.Home
import com.example.finanzaspersonales.R
import com.example.finanzaspersonales.adaptadores.CategoriaAdapter
import com.example.finanzaspersonales.entidades.Categoria
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class Gastos : Fragment() {

    private lateinit var recycle_conteiner: RecyclerView
    private lateinit var categoria_adapter: CategoriaAdapter
    private val arrayListCategoria: ArrayList<Categoria> = ArrayList()
    private lateinit var floating_action_button: FloatingActionButton

    private lateinit var database: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_gastos, container, false)

        recycle_conteiner = view.findViewById(R.id.recycle_conteiner)
        floating_action_button = view.findViewById(R.id.floating_action_button)
        database = FirebaseDatabase.getInstance().reference

        recycle_conteiner.layoutManager = LinearLayoutManager(context)
        categoria_adapter = CategoriaAdapter(arrayListCategoria)
        recycle_conteiner.adapter = categoria_adapter


        floating_action_button.setOnClickListener {
            SheetGastos().show(requireActivity().supportFragmentManager, "newTaskGastos")
        }

        getCategorias()


        return view
    }

    private fun getCategorias() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            database.child("Gasto").child(userId)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        arrayListCategoria.clear()
                        if (dataSnapshot.exists()) {
                            for (ds: DataSnapshot in dataSnapshot.children) {
                                val categoria = ds.getValue(Categoria::class.java)
                                categoria?.let {
                                    arrayListCategoria.add(it)
                                }
                            }
                            categoria_adapter.notifyDataSetChanged()
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        // Handle possible errors.
                    }
                })
        }
    }

}