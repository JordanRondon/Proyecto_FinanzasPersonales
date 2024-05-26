package com.example.finanzaspersonales.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.finanzaspersonales.R
import com.example.finanzaspersonales.adaptadores.CategoriaAdapter
import com.example.finanzaspersonales.entidades.Categoria
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class Gastos : Fragment() {

    private lateinit var recycle_conteiner: RecyclerView
    private lateinit var categoria_adapter: CategoriaAdapter
    private val arrayListCategoria: ArrayList<Categoria> = ArrayList()

    private lateinit var database: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_gastos, container, false)

        recycle_conteiner = view.findViewById(R.id.recycle_conteiner)
        recycle_conteiner.layoutManager = LinearLayoutManager(context)

        categoria_adapter = CategoriaAdapter(arrayListCategoria)
        recycle_conteiner.adapter = categoria_adapter

        getCategoria()

        return view
    }

    private fun getCategoria() {
        database = FirebaseDatabase.getInstance().reference

        database.child("Categoria").get().addOnSuccessListener {
            arrayListCategoria.clear()
            if (it.exists()) {
                for (ds: DataSnapshot in it.children)
                    arrayListCategoria.add(
                        Categoria(
                            ds.child("id").value.toString().toInt(),
                            ds.child("nombre").value.toString()
                        )
                    )
                categoria_adapter.notifyDataSetChanged()
            }
        }.addOnFailureListener {
            Toast.makeText(context, "Algo salio mal", Toast.LENGTH_SHORT).show()
        }
    }
}